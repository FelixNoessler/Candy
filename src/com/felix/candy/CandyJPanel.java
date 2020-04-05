package com.felix.candy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;


public class CandyJPanel extends JPanel implements Runnable {

    private int xSize, ySize;
    private int[][] grid;

    private int points;

    // saves the rgb values of the colors in an array
    private int numberOfColors;
    private Color[] colorArray;

    private Random r = new Random();

    private Drawer d;
    PositionChange p;
    private GridChecker c;

    // saves the clicks of the mouse
    private ArrayList<Integer> xClicked = new ArrayList<>();
    private ArrayList<Integer> yClicked = new ArrayList<>();


    private boolean clickAnimation;


    public CandyJPanel(int ySize, int xSize, int numberOfColors) {
        this.ySize = ySize;
        this.xSize = xSize;
        this.numberOfColors = numberOfColors;

        initPanel();
    }

    private void initPanel(){

        // Label for the points (3 fields destroyed = 3 points)
        JLabel pointLabel = new JLabel("0");
        this.add(pointLabel);
        this.points = 0;

        // MouseListener - looks for the clicks of the mouse
        this.addMouseListener(new MouseListener(){
            @Override
            public void mouseClicked(MouseEvent e) {
                int getX = (e.getX()-10) / 30;
                int getY = (e.getY()-10) / 30;
                //System.out.print("("+getX + "," + getY+") ");

                // saves the position into the Arraylist
                xClicked.add(getX);
                yClicked.add(getY);

            }
            public void mousePressed(MouseEvent e){}
            public void mouseReleased(MouseEvent e){}
            public void mouseEntered(MouseEvent e){}
            public void mouseExited(MouseEvent e){

            }
        });

        int widthPanel = xSize * 30+20, heightPanel = ySize * 30+40;
        this.setPreferredSize(new Dimension(widthPanel, heightPanel));
        this.setLayout(new FlowLayout(FlowLayout.RIGHT,20,heightPanel-30));
        this.setToolTipText("Click for changing the rectangles!");

        c = new GridChecker();
        d = new Drawer();
        d.fillColorArray();

        GameArray gArray = new GameArray();
        gArray.generateRandomArray(ySize, xSize);

        p = new PositionChange();
    }

    @Override
    public void run(){

        c.remove();

        boolean isRunning = true;

        while(isRunning){

            p.getClicks();
            p.clickAnimation();
            p.start();
            p.animation();

            repaint();

            try{
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        d.drawGrid(g);
    }

    private class GridChecker{
        int xStart, xEnd;
        int row, col;
        int yStart, yEnd;

        private boolean checkGrid(){
            if(checkHorizontal()) return true;
            return checkVertical();
        }

        private void remove(){
            while(checkGrid()){
                if(checkHorizontal()) remRow();
                if(checkVertical()) remCol();
            }
        }


        private boolean checkHorizontal(){
            int horizontalRepeats = 1;
            int lastNumber;
            int actualNumber;

            for (int yI = 0; yI < grid.length; yI++) {
                for (int xI = 0; xI < grid[yI].length; xI++) {
                    actualNumber = grid[yI][xI];

                    if (xI - 1 >= 0) lastNumber = grid[yI][xI - 1];
                    else lastNumber = -1;

                    if (actualNumber != lastNumber) horizontalRepeats = 1;
                    else horizontalRepeats++;


                    if (horizontalRepeats == 3) {
                        //check for more of the same color in the row:

                        for (int i = 1; true; i++) {
                            if ((xI + i) > (grid[0].length - 1)) break;

                            int nextNumber = grid[yI][xI + i];

                            if (nextNumber == actualNumber) horizontalRepeats++;
                            else break;
                        }

                        row = yI;
                        xEnd = xI;
                        if (horizontalRepeats > 3) xEnd += horizontalRepeats - 3;
                        xStart = (xEnd - (horizontalRepeats - 1));

                        return true;
                    }
                }
            }
            return false;
        }

        private void remRow() {
            for (int yToTop = row; yToTop >= 0; yToTop--) {
                for (int xIterator = xStart; xIterator <= xEnd; xIterator++) {
                    if (yToTop == 0) {
                        grid[yToTop][xIterator] = r.nextInt(numberOfColors) + 1;

                    } else {
                        grid[yToTop][xIterator] = grid[yToTop - 1][xIterator];
                    }
                }
            }
        }


        private boolean checkVertical(){
            int verticalRepeats = 1;
            int lastNumber;
            int actualNumber;

            for(int xI = 0; xI < grid[0].length; xI++) {
                for(int yI = 0; yI < grid.length; yI++) {
                    actualNumber = grid[yI][xI];

                    if (yI - 1 < 0) lastNumber = -1;
                    else lastNumber = grid[yI-1][xI];

                    if (actualNumber == lastNumber) verticalRepeats++;
                    else verticalRepeats = 1;

                    if (verticalRepeats == 3) {
                        //check for more of the same color in the row:

                        for(int i = 1; true; i++){
                            if ((yI + i) > (grid.length - 1)) break;

                            int nextNumber = grid[yI+1][xI];

                            if (nextNumber == actualNumber) verticalRepeats++;
                            else break;
                        }

                        col = xI;
                        yEnd = yI;
                        if (verticalRepeats > 3) yEnd += verticalRepeats - 3;
                        yStart = (xEnd - (verticalRepeats - 1));

                        return true; // changes!
                    }
                }
            }
            return false; // no changes
        }

        private void remCol() {
            int dif = yEnd - yStart;

            for (int yToTop = yEnd; yToTop >= 0; yToTop--) {
                if ((yToTop - dif) >= 0) {
                    grid[yToTop][col] = grid[yToTop - dif][col];

                } else {
                    grid[yToTop][col] = r.nextInt(numberOfColors) + 1;

                }
            }
        }
    }


    private class PositionChange {
        private int x1, y1;
        private int x2, y2;
        private boolean animation;
        private boolean turned;
        private int counter;
        private int[] direction;

        private PositionChange(){
            animation = false;
            turned = false;
            counter = 0;
            direction = new int[2];
        }

        private void getClicks() {
            if (xClicked.size() == 1 & yClicked.size() == 1) {
                x1 = xClicked.get(0);
                y1 = yClicked.get(0);
            } else if (xClicked.size() == 2 & yClicked.size() == 2) {
                x2 = xClicked.get(1);
                y2 = yClicked.get(1);

                if (!isNeighbour()) {
                    x1 = xClicked.get(1);
                    y1 = yClicked.get(1);

                    xClicked.clear();
                    yClicked.clear();

                    xClicked.add(x1);
                    yClicked.add(y2);
                }
            }
        }

        private void clickAnimation() {
            clickAnimation = xClicked.size() == 1 & yClicked.size() == 1;
        }

        private boolean isNeighbour() {
            boolean xChange = (x1 == (x2 + 1) |
                    x1 == (x2 - 1)) &&
                    y1 == y2;
            boolean yChange = (y1 == (y2 + 1) |
                    y1 == (y2 - 1)) &&
                    x1 == x2;

            return xChange | yChange;
        }

        private void change() {
            int save = grid[y1][x1];
            grid[y1][x1] = grid[y2][x2];
            grid[y2][x2] = save;

        }

        private void start() {
            if (xClicked.size() == 2 & yClicked.size() == 2) {

                change();
                animation = true;

                //toRight
                if (x2 > x1) direction[0] = 1;
                //toLeft
                else if (x2 < x1) direction[0] = 2;
                //toTop
                else if (y2 < y1) direction[0] = 3;
                //toBottom
                else if (y2 > y1) direction[0] = 4;

                xClicked.clear();
                yClicked.clear();

            }
        }

        private void animation() {
            if (animation) {
                if (counter < 30) {
                    counter += 2;
                } else if (counter == 30) {
                    counter = 0;

                    boolean explode = c.checkGrid();
                    if(explode) {
                        animation = false;
                        c.remove();
                    }
                    else{ //change back!
                        if(turned){
                            animation = false;
                            turned = false;
                        }else{
                            turned = true;
                            change();

                            switch(direction[0]){
                                case 1:
                                    direction[1] = 2;
                                    break;
                                case 2:
                                    direction[1] = 1;
                                    break;
                                case 3:
                                    direction[1] = 4;
                                    break;
                                case 4:
                                    direction[1] = 3;
                                    break;
                            }
                        }
                    }
                }
            }
        }
    }

    private class GameArray{
        private void generateRandomArray(int ySize, int xSize){
            grid = new int[ySize][xSize];

            for(int yIterator = 0; yIterator < grid.length; yIterator++){
                for(int xIterator = 0; xIterator < grid[yIterator].length; xIterator++){

                    grid[yIterator][xIterator] = r.nextInt(numberOfColors) + 1;

                }
            }
        }

    }

    private class Drawer {

        private void fillColorArray() {
            if (numberOfColors < 6) {
                colorArray = new Color[7];
            } else {
                colorArray = new Color[numberOfColors + 1];
            }

            colorArray[0] = new Color(255, 255, 255);
            colorArray[1] = new Color(255, 31, 48);
            colorArray[2] = new Color(39, 97, 39, 230);
            colorArray[3] = new Color(255, 92, 242);
            colorArray[4] = new Color(0, 91, 255);
            colorArray[5] = new Color(255, 137, 61);
            colorArray[6] = new Color(25, 176, 130);

            for (int i = 7; i < numberOfColors; i++) {
                float red = r.nextFloat();
                float green = (r.nextFloat() / 5f + 0.8f * i / numberOfColors);
                float blue = (r.nextFloat() / 5f + 0.8f * i / numberOfColors);

                colorArray[i] = new Color(red, green, blue);
            }
        }

        private void drawGrid(Graphics g) {

            Graphics2D g2d = (Graphics2D) g;

            int xCell = 0, yCell = 0;

            int yMax = 30 * ySize + 10;
            int xMax = 30 * xSize + 10;

            int xAni = 0, yAni = 0;
            Color aniCol = Color.WHITE;

            for (int y = 10; y < yMax; y += 30) {
                for (int x = 10; x < xMax; x += 30) {

                    int no = grid[yCell][xCell];
                    //int special = specialGrid[yCell][xCell];


                    boolean firstClick = p.y1 == yCell & p.x1 == xCell & clickAnimation;

                    boolean forward = p.y2 == yCell & p.x2 == xCell & !p.turned;
                    boolean back = p.y1 == yCell & p.x1 == xCell & p.turned;

                    boolean changePos = (forward | back) & p.animation;

                    if (firstClick | changePos) {
                        int move = 0;

                        if (changePos) move = p.counter;

                        aniCol = colorArray[no];

                        if (firstClick) {
                            xAni = x + 2;
                            yAni = y + 2;

                            g2d.setColor(aniCol);
                            g2d.fillRect(xAni, yAni, 21, 21);
                            g2d.setColor(Color.GRAY);
                            g2d.drawRect(xAni - 1, yAni - 1, 21, 21);

                        } else if (p.animation) {
                            int di = 0;
                            if(forward) di = p.direction[0];
                            else if (back) di = p.direction[1];
                            else System.out.println("error");

                            switch (di) {
                                case 1: //right
                                    xAni = x - 30 + move;
                                    yAni = y;
                                    break;
                                case 2: //left
                                    xAni = x + 30 - move;
                                    yAni = y;
                                    break;
                                case 3: //top
                                    xAni = x;
                                    yAni = y + 30 - move;
                                    break;
                                case 4: //bottom
                                    xAni = x;
                                    yAni = y - 30 + move;
                                    break;
                            }
                        }


                        if (changePos) {
                            g2d.setColor(aniCol);
                            g2d.fillRect(xAni, yAni, 25, 25);
                            g2d.setColor(Color.GRAY);
                            g2d.drawRect(xAni - 1, yAni - 1, 25, 25);
                        }
                    }

                    else {
                        // fill rectangle
                        g2d.setColor(colorArray[no]);
                        g2d.fillRect(x, y, 25, 25);

                        // boundaries of the rectangle
                        g2d.setColor(Color.GRAY);
                        g2d.drawRect(x - 1, y - 1, 25, 25);
                    }

                    xCell++;
                }
                xCell = 0;
                yCell++;
            }

            if (p.animation) {
                g2d.setColor(aniCol);
                g2d.fillRect(xAni, yAni, 25, 25);
                g2d.setColor(Color.GRAY);
                g2d.drawRect(xAni - 1, yAni - 1, 25, 25);
            }
        }

    }

}