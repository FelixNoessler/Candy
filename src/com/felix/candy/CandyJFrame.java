package com.felix.candy;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class CandyJFrame extends JFrame {
    CandyJPanel gridPanel;
    Thread thread1;

    public CandyJFrame() {
        this.setLayout(new FlowLayout());

        // default values
        final int[] yCells = {15};
        final int[] xCells = {15};
        int[] colors = {6};


        // JTextField xSize
        JTextField fieldXSize = new JTextField(String.valueOf(xCells[0]), 4);
        this.add(fieldXSize);


        // JTextField ySize
        JTextField fieldYSize = new JTextField(String.valueOf(yCells[0]), 4);
        this.add(fieldYSize);


        // Button for restart
        JButton startButton = new JButton("start");
        startButton.setBounds(10, yCells[0] * 30 + 30, 100, 30);
        this.add(startButton);
        startButton.addActionListener(e -> {
            xCells[0] = Integer.parseInt(fieldXSize.getText());
            yCells[0] = Integer.parseInt(fieldYSize.getText());

            int width  = xCells[0] * 30 + 20;
            int height = yCells[0] * 30 + 40;

            this.remove(gridPanel);
            gridPanel = new CandyJPanel(yCells[0], xCells[0], colors[0]);
            this.add(gridPanel);
            thread1 = new Thread(gridPanel, "new Thread");
            thread1.start();


            this.setPreferredSize(new Dimension(width, height+150));
            this.setSize(new Dimension(width, height+150));
            this.pack();
        });


        // Slider for the number of Colors
        JSlider colSlider = new JSlider(2, 20, colors[0]);
        colSlider.setMinorTickSpacing(1);
        colSlider.setMajorTickSpacing(5);
        colSlider.setPaintTicks(true);
        colSlider.addChangeListener((ChangeEvent event) -> colors[0] = colSlider.getValue());
        this.add(colSlider);

        // create the JPanel
        gridPanel = new CandyJPanel(yCells[0], xCells[0], colors[0]);
        this.add(gridPanel);
        thread1 = new Thread(gridPanel, "Thread 1");
        thread1.start();

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        int widthFrame = xCells[0] * 30+20, heightFrame = yCells[0] * 30 + 190;
        this.setPreferredSize(new Dimension(widthFrame, heightFrame));

        this.setLocationRelativeTo(null);
        this.pack();
        this.setVisible(true);
    }

    public static void main(String [] args) throws InvocationTargetException, InterruptedException {
        // set the look of the gui
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (UnsupportedLookAndFeelException | ClassNotFoundException |
                InstantiationException | IllegalAccessException e ) {
           e.getStackTrace();
        }

        // start the program

        EventQueue.invokeAndWait(new Runnable(){
            @Override
            public void run() {
                new CandyJFrame();
            }
        });
    }
}