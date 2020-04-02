package com.felix.candy;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;

public class CandyJFrame extends JFrame {
    CandyJPanel gridPanel;

    public CandyJFrame() {
        this.setLayout(new FlowLayout());

        // default values
        final int[] yCells = {15};
        final int[] xCells = {15};
        int[] colors = {6};

        // JTextField xSize
        JTextField fieldXSize = new JTextField(String.valueOf(xCells[0]), 10);
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

            int width  = xCells[0] * 30 + 20;
            int height = yCells[0] * 30 + 40;

            this.remove(gridPanel);
            gridPanel.setLayout(new FlowLayout(FlowLayout.RIGHT,20,height-30));
            gridPanel.setPreferredSize(new Dimension(width,height));
            this.add(gridPanel);

            gridPanel.setXYSize(xCells[0], yCells[0]);
            gridPanel.setNumberOfColors(colors[0]);
            gridPanel.setFirstTime(true);
            gridPanel.setPoints(0);
            gridPanel.setPointLabel("0");
            gridPanel.repaint();

            this.setPreferredSize(new Dimension(width, height+150));
            this.setSize(new Dimension(width, height+150));
            this.pack();
        });


        // Slider for the number of Colors
        JSlider colSlider = new JSlider(2, 20, colors[0]);
        colSlider.setMinorTickSpacing(1);
        colSlider.setMajorTickSpacing(5);
        colSlider.setBounds(120, yCells[0] * 30 + 30, 100, 50);
        colSlider.setPaintTicks(true);
        colSlider.addChangeListener((ChangeEvent event) -> colors[0] = colSlider.getValue());
        this.add(colSlider);

        // create the JPanel
        gridPanel = new CandyJPanel(yCells[0], xCells[0], colors[0]);
        this.add(gridPanel);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        int widthFrame = xCells[0] * 30+20, heightFrame = yCells[0] * 30 + 190;
        this.setPreferredSize(new Dimension(widthFrame, heightFrame));

        this.setLocationRelativeTo(null);
        this.pack();
        this.setVisible(true);
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
    }
}