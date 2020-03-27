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

    private Random r = new Random();
    private int x_size, y_size;
    private int numberOfColors;
    private boolean firstTime = true;
    private JLabel pointLabel;

    // saves the clicks of the mouse
    private ArrayList<Integer> x_clicked = new ArrayList<>();
    private ArrayList<Integer> y_clicked = new ArrayList<>();

    // saves the rgb values of the colors in an array
    private Color[] colorArray;

    public Cell_JPanel(int x_size, int y_size, int numberOfColors) {
        this.x_size = x_size;
        this.y_size = y_size;
        this.numberOfColors = numberOfColors;

        // set the layout of the JPanel
        this.setLayout(null);
        this.setBackground(Color.WHITE);

        // Label for the points (3 fields destroyed = 3 points)
        pointLabel = new JLabel();
        pointLabel.setBounds(x_size*30+20,10,20,30);
        pointLabel.setText("0");
        this.add(pointLabel);

        // MouseListener - looks for the clicks of the mouse
        this.addMouseListener(new MouseListener(){
            @Override
            public void mouseClicked(MouseEvent e) {
                int getX = (e.getX()-10) / 30;
                int getY = (e.getY()-10) / 30;
                System.out.print("("+getX + "," + getY+") ");

                // saves the position into the Arraylist
                x_clicked.add(getX);
                y_clicked.add(getY);

                // to avoid to run with one click
                if(x_clicked.size() > 1){

                    // if neighbours, than the function changes the position
                    boolean neighbours = changePosition(false);

                    if(neighbours){
                        if(gridCalculate.checkGrid(true)) {
                            repaint();
                            pointLabel.setText(String.valueOf(gridCalculate.getPoints()));

                        } else{
                            // change back
                            System.out.println("No change (changed back)!");
                            changePosition(true);
                        }
                    }

                    //empty clicked Arraylist to avoid not wanted clicks
                    x_clicked.clear();
                    y_clicked.clear();
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

        // this.setDoubleBuffered(true); // buffer for painting
        int width_panel = x_size*30+40, height_panel = y_size*30+20;
        this.setSize(width_panel, height_panel);
    }

    // getter/setter methods ..............
    public void setPointsToZero() { gridCalculate.setPointsToZero();}

    public void setFirstTime(boolean firstTime){
        this.firstTime = firstTime;
    }

    public void setNumberOfColors(int numberOfColors){
        this.numberOfColors = numberOfColors;
    }

    public void setPointLabel(String text){
        this.pointLabel.setText(text);
    }

    public void setXYSize(int x, int y){ this.x_size = x; this.y_size = y; }
    // end of getter/setter methods...............

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(firstTime) {
            setColors();
            gridCalculate = new Grid(x_size, y_size, numberOfColors);
            gridCalculate.generateRandomArray();

        }
        firstTime = false;
        drawGrid(g);
        if(gridCalculate.checkGrid(false)) repaint();
    }

    private void drawGrid(Graphics g){
        int x_cell = 0, y_cell = 0;

        int x_max = 30*x_size +10, y_max = 30*y_size +10;

        for (int y = 10; y < y_max; y += 30){
            for (int x = 10; x < x_max; x += 30) {
                int no;
                no = gridCalculate.getGrid(x_cell, y_cell);

                if(gridCalculate.getSpecialGrid(x_cell, y_cell) == 4){

                    boolean drawWhite = false;
                    for(int i = 0; i < 5; i++){
                        if(drawWhite){
                            g.setColor(Color.WHITE);
                            drawWhite = false;
                        } else {
                            g.setColor(colorArray[no]);
                            drawWhite = true;
                        }

                        g.fillRect( x, y + (i*5), 25, 5);
                    }

                    g.setColor( Color.GRAY );
                    g.drawRect( x -1, y - 1, 25, 25 );

                } else if(gridCalculate.getSpecialGrid(x_cell, y_cell) == 5){

                    // draw black or orange color
                    boolean drawBlack = false;

                    for(int xFive = 0; xFive < 5; xFive++){
                        for(int yFive = 0; yFive < 5; yFive++){
                            if(drawBlack) {
                                g.setColor(Color.BLACK);
                                drawBlack = false;
                            }
                            else {
                                g.setColor(Color.ORANGE);
                                drawBlack = true;
                            }

                            g.fillRect( x + (xFive*5), y + (yFive*5), 5, 5);
                        }
                    }

                    // boundaries:
                    g.setColor( Color.GRAY );
                    g.drawRect( x -1, y - 1, 25, 25 );

                } else {

                    // fill rectangle
                    g.setColor(colorArray[no]);
                    g.fillRect( x, y, 25, 25 );

                    // boundaries of the rectangle
                    g.setColor( Color.GRAY );
                    g.drawRect( x -1, y - 1, 25, 25 );
                }

                x_cell++;
            }
            x_cell = 0;
            y_cell++;
        }
    }


    private void setColors(){
        colorArray = new Color[numberOfColors+2];
        for(int i = 0; i < numberOfColors; i++){

            float red =   r.nextFloat();
            float green = (r.nextFloat() / 5f + 0.8f * i/numberOfColors);
            float blue =  (r.nextFloat() / 5f + 0.8f * i/numberOfColors);

            colorArray[i] = new Color(red, green, blue);
        }
        // color for four elements
        colorArray[colorArray.length-2] = new Color(0, 0, 0);

        //color for five elements
        colorArray[colorArray.length-1] = new Color(255, 255, 255);
    }

    private boolean changePosition(boolean back){
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
        boolean x_change = (x1 == (x2+1) | x1 == (x2-1)) && y1 == y2;
        boolean y_change = (y1 == (y2+1) | y1 == (y2-1)) && x1 == x2;

        if(x_change | y_change){
            int save = gridCalculate.getGrid(x1,y1);

            gridCalculate.setGrid(gridCalculate.getGrid(x2,y2),x1,y1);
            gridCalculate.setGrid(save,x2,y2);

            return true;

        }else{
            System.out.println("Not neighbours, no change!");
            return false;
        }
    }
}

