package com.felix.candy;
//hallo

// Huhu
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.Color;

public class CandyJFrame extends JFrame {
    public CandyJFrame() {
        // set the layout options
        setLayout(null);

        // default values
        final int[] x_cells = {12};
        final int[] y_cells = {10};
        int[] colors = {5};

        // create the JPanel
        CandyJPanel pan = new CandyJPanel(x_cells[0], y_cells[0], colors[0]);
        this.add(pan);

        // JTextField X_Size
        JTextField fieldXSize = new JTextField(String.valueOf(x_cells[0]), 10);
        fieldXSize.setBounds(240, y_cells[0] * 30 + 30, 50, 30);
        this.add(fieldXSize);

        // JTextField Y_Size
        JTextField fieldYSize = new JTextField(String.valueOf(y_cells[0]), 10);
        fieldYSize.setBounds(320, y_cells[0] * 30 + 30, 50, 30);
        this.add(fieldYSize);

        // Button for restart
        JButton button = new JButton("start");
        button.setBounds(10, y_cells[0] * 30 + 30, 100, 30);
        this.add(button);
        button.addActionListener(e -> {
            x_cells[0] = Integer.parseInt(fieldXSize.getText());
            y_cells[0] = Integer.parseInt(fieldYSize.getText());
            pan.setXYSize(x_cells[0], y_cells[0]);
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
        colSlider.setBounds(120, y_cells[0] * 30 + 30, 100, 50);
        colSlider.setPaintTicks(true);
        this.add(colSlider);
        colSlider.addChangeListener((ChangeEvent event) -> colors[0] = colSlider.getValue());

        // Options for the JFrame
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Candy");
        int widthFrame = x_cells[0] * 30 + 60;
        if(widthFrame < 380) widthFrame = 380;
        int heightFrame = y_cells[0] * 30 + 100;
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