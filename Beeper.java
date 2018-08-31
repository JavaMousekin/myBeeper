
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.sound.sampled.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.time.ZoneId;
import java.util.TimerTask;
import java.util.Timer;

public class Beeper {
    private static boolean userSleep;
    private static JTextField timePlace;
    private static JTextField notice;
    private static BeepClock bc;
    private static Clip myclip;
    private static File file = new File("C:\\Users\\Мария\\IdeaProjects\\beeper\\src\\wav\\audio-1.wav");

    private static void signalSetter(){
        userSleep = true;
        bc = new BeepClock(timePlace.getText());
        try {
            bc.setter();
        }catch (NullPointerException ex){
            notice.setText("Something goes wrong, try again");
            System.out.println(ex.getMessage());
        }
        interval();
        notice.setText("I'll wake up you at "+ bc.getShortBeepTime());
    }

    private static void sound(){
        try{
            //Получаем AudioInputStream
        //Вот тут могут полететь IOException и UnsupportedAudioFileException
        AudioInputStream ais = AudioSystem.getAudioInputStream(file);

        //Получаем реализацию интерфейса Clip
        //Может выкинуть LineUnavailableException
        myclip = AudioSystem.getClip();

        //Загружаем наш звуковой поток в Clip
        //Может выкинуть IOException и LineUnavailableException
        myclip.open(ais);
        ais.close();
    } catch (IOException | UnsupportedAudioFileException | LineUnavailableException exc) {
        exc.printStackTrace();}
            myclip.setFramePosition(0); //устанавливаем указатель на старт
            myclip.start(); //Поехали!!!

            //Если не запущено других потоков, то стоит подождать, пока клип не закончится
            //В GUI-приложениях следующие 3 строчки не понадобятся
            try {
                Thread.sleep(60000);
                myclip.stop(); //Останавливаем
                myclip.close(); //Закрываем
                userSleep = false;
            } catch (InterruptedException exc) {exc.printStackTrace();}
    }

    private static void interval(){
        long time = System.currentTimeMillis();
        long time1 = bc.getBeepTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long che = time1 - time;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                notice.setText("Beep!");
                sound();

            }
        }, che);
    }

    private static void audioChooser(){
        if(userSleep){
           myclip.stop();
           myclip.close();
        }
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("WAV Files", "wav");
        chooser.setFileFilter(filter);
        int ret = chooser.showDialog(null, "Open");
        if (ret == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();
            JPanel chooserPane = new JPanel();
            chooserPane.setVisible(true);
        }else chooser.cancelSelection();

        try {
        //Получаем AudioInputStream
        //Вот тут могут полететь IOException и UnsupportedAudioFileException
        AudioInputStream ais = AudioSystem.getAudioInputStream(file);

        //Получаем реализацию интерфейса Clip
        //Может выкинуть LineUnavailableException
        myclip = AudioSystem.getClip();

        //Загружаем наш звуковой поток в Clip
        //Может выкинуть IOException и LineUnavailableException
        myclip.open(ais);
        notice.setText("Beeper audio was changed");
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException exc) {
            exc.printStackTrace();
            notice.setText("Can't read your file try another one");
        }
    }

    private static void stoper(){
        myclip.stop(); //Останавливаем
        myclip.close();
        userSleep = false;
    }

    private static void createGUI() {
        timePlace = new JTextField("hh:mm");
        timePlace.setFocusable(false);
        timePlace.setPreferredSize(new Dimension(50, 40));
        timePlace.setHorizontalAlignment(SwingConstants.CENTER);
        timePlace.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                timePlace.setText("");
                timePlace.setFocusable(true);
            }
        });

        JButton setSignalButton = new JButton("Set Signal");
        setSignalButton.setPreferredSize(new Dimension(150, 50));
        setSignalButton.setHorizontalTextPosition(SwingConstants.CENTER);

        JButton setAudioButton = new JButton("Set Melody");
        setAudioButton.setPreferredSize(new Dimension(150, 50));
        setAudioButton.setHorizontalTextPosition(SwingConstants.CENTER);

        JButton stopButton = new JButton("Stop Signal");
        stopButton.setPreferredSize(new Dimension(150, 50));
        stopButton.setHorizontalTextPosition(SwingConstants.CENTER);
        stopButton.setBackground(Color.RED);

        notice = new JTextField("Type time you need");
        notice.setPreferredSize(new Dimension(400, 40));
        notice.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel beeperPane = new JPanel();
        beeperPane.setLayout(new BorderLayout());
        beeperPane.add(timePlace, BorderLayout.NORTH);
        beeperPane.add(setSignalButton, BorderLayout.WEST);
        beeperPane.add(setAudioButton, BorderLayout.CENTER);
        beeperPane.add(stopButton, BorderLayout.EAST);
        beeperPane.add(notice, BorderLayout.SOUTH);
        beeperPane.setBorder(new EmptyBorder(10,10,10,10));

        setSignalButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                signalSetter();
            }
        });

        setAudioButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                audioChooser();
            }
        });

        stopButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                stoper();
            }
        });

        JFrame frame = new JFrame("MyBeeper");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(beeperPane);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Beeper::createGUI);
    }
}
