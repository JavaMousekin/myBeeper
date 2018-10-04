
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.*;
import java.io.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.time.ZoneId;
import java.util.TimerTask;
import java.util.Timer;

public class Beeper {
    private static boolean userSleep = false;
    private static JTextField timePlace;
    private static JTextField notice;
    private static BeepClock bc;
    private static Player myplayer;
    private static File file = new File("src\\wav\\audio-1.mp3");

    private static void signalSetter() {
        if (userSleep) {
            myplayer.close();
        }
        bc = new BeepClock(timePlace.getText());
        try {
            bc.setter();
        } catch (NullPointerException ex) {
            notice.setText("Something goes wrong, try again");
            ex.printStackTrace();
        }
        interval();
        notice.setText("I'll wake up you at " + bc.getShortBeepTime());
        userSleep = true;
    }

    private static void soundMP3() {
        try {
            FileInputStream f = new FileInputStream(file);
            myplayer = new Player(f);
            myplayer.play(6000);
            myplayer.close();
            userSleep = false;
        } catch (FileNotFoundException| JavaLayerException e) {
            e.printStackTrace();
        }
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
                soundMP3();
            }
        }, che);
    }

    private static void audioChooser(){
        if(userSleep){
           myplayer.close();
        }
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("MP3 Files", "mp3");
        chooser.setFileFilter(filter);
        int ret = chooser.showDialog(null, "Open");
        if (ret == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();
            JPanel chooserPane = new JPanel();
            chooserPane.setVisible(true);
        }else chooser.cancelSelection();

        try {
            FileInputStream f = new FileInputStream(file);
            myplayer = new Player(f);
            notice.setText("Beeper audio was changed");
            
        }catch (JavaLayerException e) {
            e.printStackTrace();
            notice.setText("Can't read your file try another one");

        }catch (FileNotFoundException e) {
            e.printStackTrace();
            notice.setText("Can't find your file try another one");
        }
    }

    private static void stoper(){
        myplayer.close();
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
                timePlace.setText(":");
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
