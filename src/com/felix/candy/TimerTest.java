package com.felix.candy;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class TimerTest extends JPanel {
    private static final int TIMER_DELAY = 16;
    private static final double Y_VELOCITY = 0.05;
    private double dY = 0.0;
    private Timer timer = new Timer(TIMER_DELAY, new TimerListener());

    boolean first;

    public TimerTest() {
        timer.start();
        first=true;
    }

    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        g.setColor(Color.black);
        g.fillRect(50, (int) dY, 50, 50);


        if(first){
            g.setColor(Color.red);
            g.fillRect(80, 40, 50, 50);
            first=false;
        }

    }

    private class TimerListener implements ActionListener {
        private long prevTime;

        @Override
        public void actionPerformed(ActionEvent e) {
            if (prevTime == 0L) {
                repaint();
                prevTime = System.currentTimeMillis();
            } else {
                long currentTime = System.currentTimeMillis();
                long deltaTime = currentTime - prevTime;
                double deltaY = Y_VELOCITY * deltaTime;
                dY += deltaY;
                prevTime = currentTime;
                repaint();
            }

        }
    }

    public static void main(String[] args) {

        TimerTest drawPanel = new TimerTest();

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setVisible(true);
        frame.add(drawPanel);
    }
}