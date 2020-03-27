package felix.gridgame;

import java.awt.Color;
import java.awt.Graphics;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.*;

public class Cell_JPanel extends JPanel {

    Grid gridCalculate;

    private static final Random r = new Random();

    private int x_size, y_size;
    private int numberOfColors;

    public boolean firstTime = true;


    private boolean removed = false;

    private int points = 0;
    private JLabel pointLabel;


    private ArrayList<Integer> x_clicked = new ArrayList<>();
    private ArrayList<Integer> y_clicked = new ArrayList<>();

    private Color[] colorArray;

    public Cell_JPanel(int x_size, int y_size, int numberOfColors) {
        this.x_size = x_size;
        this.y_size = y_size;
        this.numberOfColors = numberOfColors;

        this.setBackground(Color.WHITE);
        this.setLayout(null);

        pointLabel = new JLabel();
        pointLabel.setBounds(x_size*30+20,10,20,30);
        pointLabel.setText(String.valueOf(points));
        this.add(pointLabel);

        this.addMouseListener(new MouseListener(){
            @Override
            public void mouseClicked(MouseEvent e) {
                int getX = (e.getX()-10) / 30;
                int getY = (e.getY()-10) / 30;

                x_clicked.add(getX);
                y_clicked.add(getY);

                if(x_clicked.size() > 1){
                    removed = false;
                    changePosition(false);
                    gridCalculate.checkGrid(true);
                    pointLabel.setText(String.valueOf(points));

                    if(!removed){
                        changePosition(true);
                    } else {
                        //empty clicked Arraylist to avoid not wanted clicks
                        x_clicked.clear();
                        y_clicked.clear();
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e){}

            @Override
            public void mouseReleased(MouseEvent e){}

            @Override
            public void mouseEntered(MouseEvent e){}

            @Override
            public void mouseExited(MouseEvent e){}
        });

        this.setDoubleBuffered(true);

        int width_panel = x_size*30+40, height_panel = y_size*30+20;
        this.setSize(width_panel, height_panel);
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setNumberOfColors(int numberOfColors){
        this.numberOfColors = numberOfColors;
    }

    public void setPointLabel(String text){
        this.pointLabel.setText(text);
    }

    public void setXYSize(int x, int y){
        this.x_size = x;
        this.y_size = y;
    }


    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(firstTime) {
            setColors();
            gridCalculate = new Grid(x_size, y_size, numberOfColors);
            gridCalculate.generateRandomArray();

        }
        firstTime = false;
        drawGrid(g);
        gridCalculate.checkGrid(false);
    }

    private void drawGrid(Graphics g){
        int x_cell = 0, y_cell = 0;

        int x_max = 30*x_size +10, y_max = 30*y_size +10;

        for (int y = 10; y < y_max; y += 30){
            for (int x = 10; x < x_max; x += 30) {
                int no;
                no = gridCalculate.getGrid(x_cell, y_cell);
                g.setColor(colorArray[no]);
                g.fillRect( x, y, 25, 25 );
                g.setColor( Color.GRAY );
                g.drawRect( x -1, y - 1, 25, 25 );
                x_cell++;
            }
            x_cell = 0;
            y_cell++;
        }
    }


    private void setColors(){
        colorArray = new Color[numberOfColors];
        for(int i = 0; i < numberOfColors; i++){

            float red =   r.nextFloat();
            float green = (r.nextFloat() / 5f + 0.8f * i/numberOfColors);
            float blue =  (r.nextFloat() / 5f + 0.8f * i/numberOfColors);

            colorArray[i] = new Color(red, green, blue);
        }
    }

    private void changePosition(boolean back){
        int x1, x2, y1, y2;
        if(!back){
            x1 = x_clicked.get(x_clicked.size()-1);
            x2 = x_clicked.get(x_clicked.size()-2);
            y1 = y_clicked.get(y_clicked.size()-1);
            y2 = y_clicked.get(y_clicked.size()-2);
        }else{
            x1 = x_clicked.get(x_clicked.size()-2);
            x2 = x_clicked.get(x_clicked.size()-1);
            y1 = y_clicked.get(y_clicked.size()-2);
            y2 = y_clicked.get(y_clicked.size()-1);
        }

        //change for neighbours only
        if(x1 == x2 | x1 == (x2+1) | x1 == (x2-1)){
            if(y1 == y2 | y1 == (y2+1) | y1 == (y2-1)) {
                int save = gridCalculate.getGrid(x1,y1);

                gridCalculate.setGrid(gridCalculate.getGrid(x1,y1),x1,y1);
                gridCalculate.setGrid(save,x1,y1);
                repaint();
            }
        }

    }
}

