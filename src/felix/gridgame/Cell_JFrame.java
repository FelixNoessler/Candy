package felix.gridgame;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.Color;

public class Cell_JFrame extends JFrame {
    public Cell_JFrame() {
        setLayout(null);

        final int[] x_cells = {15};
        final int[] y_cells = {15};
        int[] colors = {4};

        Cell_JPanel pan = new Cell_JPanel(x_cells[0], y_cells[0], colors[0]);
        this.add(pan);

        // JTextField X_Size
        JTextField fieldXSize = new JTextField("15", 10);
        fieldXSize.setBounds(240, y_cells[0] * 30 + 30, 50, 30);
        this.add(fieldXSize);

        // JTextField Y_Size
        JTextField fieldYSize = new JTextField("15", 10);
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
            pan.firstTime = true;
            pan.setPointsToZero();
            pan.setPointLabel("0");
            pan.repaint();
        });

        // Slider for the number of Colors
        JSlider colSlider = new JSlider(2, 20, 4);
        colSlider.setMinorTickSpacing(1);
        colSlider.setMajorTickSpacing(5);
        colSlider.setBounds(120, y_cells[0] * 30 + 30, 100, 50);
        colSlider.setPaintTicks(true);
        this.add(colSlider);
        colSlider.addChangeListener((ChangeEvent event) -> colors[0] = colSlider.getValue());

        // Options for the JFrame
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Cells");
        int width_frame = x_cells[0] * 30 + 40, height_frame = y_cells[0] * 30 + 100;
        this.setSize(width_frame, height_frame);
        this.setVisible(true);
        this.getContentPane().setBackground(Color.WHITE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);

    }

    public static void main(String [] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (UnsupportedLookAndFeelException | ClassNotFoundException |
                InstantiationException | IllegalAccessException e ) {
           e.getStackTrace();
        }

        new Cell_JFrame();
    }
}