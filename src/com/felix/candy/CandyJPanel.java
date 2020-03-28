package com.felix.candy;

import java.awt.Color;
import java.awt.Graphics;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.*;

public class CandyJPanel extends JPanel {

    CandyGrid gridCalculate;

    private Random r = new Random();
    private int xSize, ySize;
    private int numberOfColors;
    private boolean firstTime = true;
    private JLabel pointLabel;

    //special elements:
    private int elementToRemove;

    // saves the clicks of the mouse
    private ArrayList<Integer> xClicked = new ArrayList<>();
    private ArrayList<Integer> yClicked = new ArrayList<>();

    // saves the rgb values of the colors in an array
    private Color[] colorArray;

    public CandyJPanel(int ySize, int xSize, int numberOfColors) {
        this.ySize = ySize;
        this.xSize = xSize;

        this.numberOfColors = numberOfColors;

        // set the layout of the JPanel
        this.setLayout(null);
        this.setBackground(Color.WHITE);

        // Label for the points (3 fields destroyed = 3 points)
        pointLabel = new JLabel();
        pointLabel.setBounds(xSize *30+20,10,70,30);
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
                xClicked.add(getX);
                yClicked.add(getY);

                // to avoid to run with one click
                if(xClicked.size() > 1){

                    if(isFive()) {
                        gridCalculate.combineFive(elementToRemove);

                        repaint();
                        pointLabel.setText(String.valueOf(gridCalculate.getPoints()));

                        //empty clicked Arraylist to avoid not wanted clicks
                        xClicked.clear();
                        yClicked.clear();

                        return;
                    }

                    // if neighbours, than the function changes the position
                    boolean neighbours = changePosition(false);

                    if(neighbours){
                        int xClick1 = xClicked.get(xClicked.size()-1);
                        int yClick1 = yClicked.get(yClicked.size()-1);

                        int xClick2 = xClicked.get(xClicked.size()-2);
                        int yClick2 = yClicked.get(yClicked.size()-2);

                        if(gridCalculate.checkGrid(xClick1, xClick2, yClick1, yClick2, true)) {
                            repaint();
                            pointLabel.setText(String.valueOf(gridCalculate.getPoints()));

                        } else{
                            System.out.println("No change (changed back)!");
                            changePosition(true);
                        }
                    }

                    //empty clicked Arraylist to avoid not wanted clicks
                    xClicked.clear();
                    yClicked.clear();
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
        int width_panel = xSize *30+60, height_panel = ySize *30+20;
        this.setSize(width_panel, height_panel);
    }

    // getter/setter methods ..............
    public void setPointsToZero() { gridCalculate.setPointsToZero();}

    public void setFirstTime(boolean firstTime){ this.firstTime = firstTime; }

    public void setNumberOfColors(int numberOfColors){ this.numberOfColors = numberOfColors; }

    public void setPointLabel(String text){ this.pointLabel.setText(text); }

    public void setXYSize(int x, int y){ this.xSize = x; this.ySize = y; }
    // end of getter/setter methods...............

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(firstTime) {
            setColors();
            gridCalculate = new CandyGrid(numberOfColors);
            gridCalculate.generateRandomArray(ySize, xSize);

        }
        firstTime = false;
        drawGrid(g);
        if(gridCalculate.checkGrid(999, 999, 999, 999, false)) repaint();
    }

    private void drawGrid(Graphics g){
        int xCell = 0, yCell = 0;

        int yMax = 30* ySize +10;
        int xMax = 30* xSize +10;

        for (int y = 10; y < yMax; y += 30){
            for (int x = 10; x < xMax; x += 30) {
                int no;
                no = gridCalculate.getGrid(yCell, xCell);
                int special = gridCalculate.getSpecialGrid(yCell, xCell);

                if(special == 4 | special == 3){
                    // four in one row
                    boolean drawWhite = false;
                    for(int i = 0; i < 5; i++){
                        if(drawWhite){
                            g.setColor(Color.WHITE);
                            drawWhite = false;
                        } else {
                            g.setColor(colorArray[no]);
                            drawWhite = true;
                        }
                        if(special == 4){
                            g.fillRect( x , y + (i*5), 25, 5);
                        }else{
                            g.fillRect( x + (i*5), y , 5, 25);
                        }

                    }

                    g.setColor( Color.GRAY );
                    g.drawRect( x -1, y - 1, 25, 25 );

                } else if(special == 5){
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

                xCell++;
            }
            xCell = 0;
            yCell++;
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
            y1 = yClicked.get(yClicked.size()-1);
            y2 = yClicked.get(yClicked.size()-2);
            x1 = xClicked.get(xClicked.size()-1);
            x2 = xClicked.get(xClicked.size()-2);
        }else{
            y1 = yClicked.get(yClicked.size()-2);
            y2 = yClicked.get(yClicked.size()-1);
            x1 = xClicked.get(xClicked.size()-2);
            x2 = xClicked.get(xClicked.size()-1);
        }


        //change for neighbours only
        boolean xChange = (x1 == (x2+1) | x1 == (x2-1)) && y1 == y2;
        boolean yChange = (y1 == (y2+1) | y1 == (y2-1)) && x1 == x2;

        if(xChange | yChange){
            int save = gridCalculate.getGrid(y1,x1);

            gridCalculate.setGrid(gridCalculate.getGrid(y2,x2),y1,x1);
            gridCalculate.setGrid(save,y2,x2);

            int saveSpecial = gridCalculate.getSpecialGrid(y1,x1);
            gridCalculate.setSpecialGrid(gridCalculate.getSpecialGrid(y2,x2),y1,x1);
            gridCalculate.setSpecialGrid(saveSpecial,y2,x2);

            return true;

        }else{
            System.out.println("Not neighbours, no change!");
            return false;
        }
    }


    private boolean isFive(){
        int x1 = xClicked.get(xClicked.size()-1);
        int y1 = yClicked.get(yClicked.size()-1);

        int x2 = xClicked.get(xClicked.size()-2);
        int y2 = yClicked.get(yClicked.size()-2);

        if(gridCalculate.getSpecialGrid(y1,x1) == 5) {
            gridCalculate.setSpecialGrid(0, y1,x1);
            elementToRemove = gridCalculate.getGrid(y2,x2);
            return true;
        } else if(gridCalculate.getSpecialGrid(y2, x2) == 5){
            gridCalculate.setSpecialGrid(0, y2,x2);
            elementToRemove = gridCalculate.getGrid(y1,x1);
            return true;
        } else {
            return false;
        }
    }
}

