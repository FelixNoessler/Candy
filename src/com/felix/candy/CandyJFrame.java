package com.felix.candy;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.Color;

public class CandyJFrame extends JFrame {
    public CandyJFrame() {
        // set the layout options
        setLayout(null);

        // default values
        final int[] yCells = {20};
        final int[] xCells = {3};
        int[] colors = {3};

        // create the JPanel
        CandyJPanel pan = new CandyJPanel(yCells[0], xCells[0], colors[0]);
        this.add(pan);

        // JTextField X_Size
        JTextField fieldXSize = new JTextField(String.valueOf(xCells[0]), 10);
        fieldXSize.setBounds(240, yCells[0] * 30 + 30, 50, 30);
        this.add(fieldXSize);

        // JTextField Y_Size
        JTextField fieldYSize = new JTextField(String.valueOf(yCells[0]), 10);
        fieldYSize.setBounds(320, yCells[0] * 30 + 30, 50, 30);
        this.add(fieldYSize);

        // Button for restart
        JButton button = new JButton("start");
        button.setBounds(10, yCells[0] * 30 + 30, 100, 30);
        this.add(button);
        button.addActionListener(e -> {
            xCells[0] = Integer.parseInt(fieldXSize.getText());
            yCells[0] = Integer.parseInt(fieldYSize.getText());
            pan.setXYSize(xCells[0], yCells[0]);
            pan.setNumberOfColors(colors[0]);
            pan.setFirstTime(true);
            pan.setPointsToZero();
            pan.setPointLabel("0");
            pan.repaint();
        });

        // Slider for the number of Colors
        JSlider colSlider = new JSlider(2, 20, colors[0]);
        colSlider.setMinorTickSpacing(1);
        colSlider.setMajorTickSpacing(5);
        colSlider.setBounds(120, yCells[0] * 30 + 30, 100, 50);
        colSlider.setPaintTicks(true);
        this.add(colSlider);
        colSlider.addChangeListener((ChangeEvent event) -> colors[0] = colSlider.getValue());

        // Options for the JFrame
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Candy");
        int widthFrame = xCells[0] * 30 + 60;
        if(widthFrame < 380) widthFrame = 380;
        int heightFrame = yCells[0] * 30 + 100;
        this.setSize(widthFrame, heightFrame);
        this.setVisible(true);
        this.getContentPane().setBackground(Color.WHITE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
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
        new com.felix.candy.CandyJFrame();
    }
}