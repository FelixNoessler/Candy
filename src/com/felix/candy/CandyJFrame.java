package com.felix.candy;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;

public class CandyJFrame extends JFrame {

    private JLabel pointLabel1;

    public CandyJFrame() {
        this.setLayout(new FlowLayout());

        // default values
        final int[] yCells = {5};
        final int[] xCells = {5};
        int[] colors = {4};

        // create the JPanel
        CandyJPanel gridPanel = new CandyJPanel(yCells[0], xCells[0], colors[0]);
        this.add(gridPanel);

        JPanel asd = new JPanel();
        this.add(asd);

        pointLabel1 = new JLabel("0");
        this.add(pointLabel1);


        // JTextField xSize
        JTextField fieldXSize = new JTextField(String.valueOf(xCells[0]), 10);
        //fieldXSize.setBounds(240, yCells[0] * 30 + 30, 50, 30);
        fieldXSize.setPreferredSize(new Dimension(20,20));
        this.add(fieldXSize);


        // JTextField ySize
        JTextField fieldYSize = new JTextField(String.valueOf(yCells[0]), 10);
        //fieldYSize.setBounds(320, yCells[0] * 30 + 30, 50, 30);
        this.add(fieldYSize);


        // Button for restart
        JButton startButton = new JButton("start");
        startButton.setBounds(10, yCells[0] * 30 + 30, 100, 30);
        this.add(startButton);
        startButton.addActionListener(e -> {
            xCells[0] = Integer.parseInt(fieldXSize.getText());
            yCells[0] = Integer.parseInt(fieldYSize.getText());
            gridPanel.setXYSize(xCells[0], yCells[0]);
            gridPanel.setNumberOfColors(colors[0]);
            gridPanel.setFirstTime(true);
            gridPanel.setPointsToZero();
            gridPanel.setPointLabel("0");
            this.pointLabel1.setText("0");
            gridPanel.repaint();
        });


        // Slider for the number of Colors
        JSlider colSlider = new JSlider(2, 20, colors[0]);
        colSlider.setMinorTickSpacing(1);
        colSlider.setMajorTickSpacing(5);
        colSlider.setBounds(120, yCells[0] * 30 + 30, 100, 50);
        colSlider.setPaintTicks(true);
        colSlider.addChangeListener((ChangeEvent event) -> colors[0] = colSlider.getValue());
        this.add(colSlider);


        int widthFrame = xCells[0] * 30 + 60;
        if(widthFrame < 380) widthFrame = 380;
        int heightFrame = yCells[0] * 30 + 100;
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(widthFrame, heightFrame));

        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.pack();
        this.setVisible(true);

    }

    public void setPointLabel1(String points){
        this.pointLabel1.setText(points);
    }

    public static void main(String [] args) {
        // set the look of the gui
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        }
//        catch (UnsupportedLookAndFeelException | ClassNotFoundException |
//                InstantiationException | IllegalAccessException e ) {
//           e.getStackTrace();
//        }

        // start the program

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new com.felix.candy.CandyJFrame();
            }
        });
        //
    }
}