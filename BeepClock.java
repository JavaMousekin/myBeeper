import java.time.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BeepClock {
    private String needTime;
    private LocalDateTime currTime = LocalDateTime.now();
    private LocalDateTime beepTime;
    private ArrayList <LocalDateTime> signals;

    public LocalDateTime getCurrTime(){
        return currTime;
    }


    public LocalDateTime getBeepTime(){
        return beepTime;
    }

    public LocalTime getShortBeepTime(){
        return beepTime.toLocalTime();
    }

    public ArrayList<LocalDateTime> getSignals() {
        return signals;
    }

    public BeepClock(String needTime) {
        this.needTime = needTime;
    }

    private LocalTime timeReader(){
        int [] hourMin = new int[2];
        Pattern p = Pattern.compile("([0-1]?\\d|2[0-3]):[0-5]\\d");
        Matcher m = p.matcher(needTime);
        if (m.matches()){
            String [] hm = needTime.split(":");
            hourMin[0] = Integer.parseInt(hm[0]);
            hourMin[1] = Integer.parseInt(hm[1]);
        }else {
            return null;
        }
        return LocalTime.of(hourMin[0],hourMin[1]);
    }

    public void setter(){
        LocalTime transit = timeReader();
        LocalTime cTime = currTime.toLocalTime();
        if (transit.isBefore(cTime)){
            int value = currTime.getDayOfYear()+1;
            beepTime = LocalDateTime.of((LocalDate.ofYearDay(currTime.getYear(), value)), transit);
            //beepTime = LocalDateTime.of(currTime.getYear(),currTime.getMonth(),(currTime.getDayOfMonth()+1),transit.getHour(),transit.getMinute());
        }else {
            beepTime = LocalDateTime.of(currTime.toLocalDate(),transit);
            signals.add(LocalDateTime.of(currTime.toLocalDate(),transit));
        }
    }

    public static void main(String[] args) {
        BeepClock beepClock = new BeepClock("33:50");
        beepClock.setter();
        System.out.println(beepClock.getBeepTime());
    }
}
