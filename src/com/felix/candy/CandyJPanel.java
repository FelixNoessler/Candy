package com.felix.candy;

import java.awt.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.*;

public class CandyJPanel extends JPanel {

    private int points = 0;
    private int[][] grid;
    private int[][] specialGrid;
    private int numberOfColors;

    private Random r = new Random();
    private int xSize, ySize;
    private boolean firstTime = true;
    private JLabel pointLabel;

    //special elements:
    private int elementToRemove;

    // saves the clicks of the mouse
    private ArrayList<Integer> xClicked = new ArrayList<>();
    private ArrayList<Integer> yClicked = new ArrayList<>();

    // saves the rgb values of the colors in an array
    private Color[] colorArray;
    private JPanel candyPanel;


    // TODO timer
    javax.swing.Timer t;

    javax.swing.Timer changePosTimer;
    int yClick1;
    int xClick1;
    int yClick2;
    int xClick2;
    boolean animation;
    boolean toBottom, toTop;
    boolean toRight, toLeft;
    int animationsCounter = 0;

    public CandyJPanel(int ySize, int xSize, int numberOfColors) {
        this.ySize = ySize;
        this.xSize = xSize;
        this.numberOfColors = numberOfColors;

        //TODO timer
        t = new javax.swing.Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                points++;


                System.out.println(points);
            }
        });

        t.setRepeats(true);
        //t.start();

        changePosTimer = new javax.swing.Timer(5, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(animationsCounter < 30){
                    animation = true;
                    animationsCounter++;
                    repaint();
                }else{
                    animation = false;
                    animationsCounter = 0;
                    //repaint();
                    changePosTimer.stop();
                }
            }
        });

        changePosTimer.setRepeats(true);

        // Label for the points (3 fields destroyed = 3 points)
        pointLabel = new JLabel("0");
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

//                    if(isFive()) {
//                        combineFive(elementToRemove);
//
//                        repaint();
//                        pointLabel.setText(String.valueOf(points));
//
//                        //empty clicked Arraylist to avoid not wanted clicks
//                        xClicked.clear();
//                        yClicked.clear();
//
//                        return;
//                    }

                    // if neighbours, than the function changes the position

                    boolean neighbours = changePosAndCheck(false);
                    //first click
                    xClick1 = xClicked.get(xClicked.size()-2);
                    yClick1 = yClicked.get(yClicked.size()-2);
                    //second click
                    xClick2 = xClicked.get(xClicked.size()-1);
                    yClick2 = yClicked.get(yClicked.size()-1);

                    toRight = xClick2 > xClick1;
                    toLeft = xClick2 < xClick1;
                    toTop = yClick2 < yClick1;
                    toBottom = yClick2 > yClick1;


                    //TODO changePosAndCheck(true);

//                    if(neighbours){

//
//                        if(removeAndCheckGrid(xClick1, xClick2, yClick1, yClick2, true)) {
//                            System.out.println("Change");
//                            repaint();
//                            pointLabel.setText(String.valueOf(points));
//
//                        } else{
//                            System.out.println("Changed back!");
//                            changePosAndCheck(true);
//                        }
//                    }

                    //empty clicked Arraylist to avoid not wanted clicks
                    xClicked.clear();
                    yClicked.clear();
                }
            }
            public void mousePressed(MouseEvent e){}
            public void mouseReleased(MouseEvent e){}
            public void mouseEntered(MouseEvent e){}
            public void mouseExited(MouseEvent e){}
        });

        int widthPanel = xSize * 30+20, heightPanel = ySize * 30+40;
        this.setPreferredSize(new Dimension(widthPanel, heightPanel));
        this.setLayout(new FlowLayout(FlowLayout.RIGHT,20,heightPanel-30));
        this.setToolTipText("Click!");
    }

    // getter/setter methods ..............
    public void setPoints(int points){this.points = points; }

    public void setFirstTime(boolean firstTime){ this.firstTime = firstTime; }

    public void setNumberOfColors(int numberOfColors){ this.numberOfColors = numberOfColors; }

    public void setPointLabel(String text){ this.pointLabel.setText(text); }

    public void setXYSize(int x, int y){ this.xSize = x; this.ySize = y; }
    // end of getter/setter methods...............

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(firstTime) {
            // do it only on startup
            animation = false;
            fillColorArray();
            generateRandomArray(ySize, xSize);
        }
        firstTime = false;

        drawGrid(g);

        //recheck the  grid:
        //if(removeAndCheckGrid(999, 999, 999, 999, false)) repaint();
    }

    private void drawGrid(Graphics g){
        int xCell = 0, yCell = 0;

        int yMax = 30* ySize +10;
        int xMax = 30* xSize +10;

        int xAni = 0, yAni = 0;
        Color aniCol = Color.WHITE;

        for (int y = 10; y < yMax; y += 30){
            for (int x = 10; x < xMax; x += 30) {

                int no = grid[yCell][xCell];
                int special = specialGrid[yCell][xCell];

                if(yClick2 == yCell & xClick2 == xCell & animation){

                    aniCol =  colorArray[no];

                    if(toBottom){
                        xAni = x;
                        yAni = y - 30 + animationsCounter;
                    }else if(toTop){
                        xAni = x;
                        yAni = y + 30 - animationsCounter;
                    }else if(toRight){
                        xAni = x - 30 + animationsCounter;
                        yAni = y;
                    }else if(toLeft){
                        xAni = x + 30 - animationsCounter;
                        yAni = y;
                    }
                }

                else if(special == 4 | special == 3){
                    // four in one row
                    drawFour(g, y, x, special, no);

                } else if(special == 5){
                    drawFive(g, y, x);

                } else {
                    // fill rectangle
                    g.setColor(colorArray[no]);
                    g.fillRect( x, y, 25, 25 );

                    // boundaries of the rectangle
                    g.setColor( Color.GRAY );
                    g.drawRect( x-1, y-1, 25, 25 );
                }

                xCell++;
            }
            xCell = 0;
            yCell++;
        }

        if(animation){
            g.setColor(aniCol);
            g.fillRect(xAni,yAni,25,25);
            g.setColor(Color.GRAY);
            g.drawRect(xAni-1, yAni-1, 25, 25);
        }
    }

    private void drawFour(Graphics g, int y, int x, int special, int no) {
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
    }

    private void drawFive(Graphics g, int y, int x) {
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
    }

    private void fillColorArray(){
        if(numberOfColors < 6){
            colorArray = new Color[7];
        }else{
            colorArray = new Color[numberOfColors+1];
        }

        colorArray[0] = new Color(255, 255, 255);
        colorArray[1] = new Color(255, 31, 48);
        colorArray[2] = new Color(39, 97, 39, 230);
        colorArray[3] = new Color(255, 92, 242);
        colorArray[4] = new Color(0, 91, 255);
        colorArray[5] = new Color(255, 137, 61);
        colorArray[6] = new Color(25, 176, 130);

        for(int i = 7; i < numberOfColors; i++){
            float red =   r.nextFloat();
            float green = (r.nextFloat() / 5f + 0.8f * i/numberOfColors);
            float blue =  (r.nextFloat() / 5f + 0.8f * i/numberOfColors);

            colorArray[i] = new Color(red, green, blue);
        }
    }

    private boolean changePosAndCheck(boolean back) {
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
            // change grid cells
            int save = grid[y1][x1];
            grid[y1][x1] = grid[y2][x2];
            grid[y2][x2] = save;

            // change specialGrid items
            save = specialGrid[y1][x1]; //gridCalculate.getSpecialGrid(y1,x1);
            specialGrid[y1][x1] = specialGrid[y2][x2];
            specialGrid[y2][x2] = save;

            changePosTimer.start();

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

        if(grid[y1][x1] == 5) {
            specialGrid[y1][x1] = 0;
            elementToRemove = grid[y2][x2];
            return true;

        } else if(specialGrid[y2][x2] == 5){
            specialGrid[y2][x2] = 0;
            elementToRemove = grid[y1][x1];
            return true;

        } else {
            return false;
        }
    }


    public void generateRandomArray(int ySize, int xSize){
        grid = new int[ySize][xSize];
        specialGrid = new int[ySize][xSize];

        for(int yIterator = 0; yIterator < grid.length; yIterator++){
            for(int xIterator = 0; xIterator < grid[yIterator].length; xIterator++){

                grid[yIterator][xIterator] = r.nextInt(numberOfColors) + 1;

                // set the special array to 0's
                specialGrid[yIterator][xIterator] = 0;
            }
        }
    }


    public boolean removeAndCheckGrid(int xClick1, int xClick2, int yClick1, int yClick2, boolean mouseClick){
        // true = changes!, false = no changes
        if(checkGridHorizontal(xClick1, xClick2, mouseClick)) return true;
        return checkGridVertical(yClick1, yClick2, mouseClick);
    }


    private boolean checkGridHorizontal(int xClick1, int xClick2, boolean mouseClick) {
        boolean isFour = false;
        boolean fourLine = false;
        int fourXPosition = 0;

        int horizontalRepeats = 1;
        int lastNumber;
        int actualNumber;
        int xEnd = 0, yRow = 0;
        boolean breakLoop = false;

        outerLoop:
        for (int yIterator = 0; yIterator < grid.length; yIterator++) {
            for (int xIterator = 0; xIterator < grid[yIterator].length; xIterator++) {

                actualNumber = grid[yIterator][xIterator];

                if (xIterator - 1 < 0) lastNumber = 999;
                else lastNumber = grid[yIterator][xIterator - 1];

                if (actualNumber != lastNumber){
                    horizontalRepeats = 1;
                    isFour = false;

                } else  {
                    horizontalRepeats++;

                    int special = specialGrid[yIterator][xIterator];
                    if(special == 4 | special == 3){
                        isFour = true;
                        fourXPosition = xIterator;

                        // sets it true for 4, false for 3
                        fourLine = special != 3;
                    }
                }
                if(horizontalRepeats == 3) {
                    //check for more of the same color in the row:
                    int i = 1;
                    while (true) {
                        //checks that we are in the grid
                        if ((xIterator + i) > (grid[0].length - 1)) break;

                        int nextNumber = grid[yIterator][xIterator + i];

                        if (nextNumber == actualNumber) horizontalRepeats++;
                        else break;
                        i++;
                    }

                    yRow = yIterator;
                    xEnd = xIterator;
                    breakLoop = true;
                    break outerLoop;
                }
            }
        }
        if(breakLoop) {
            if (horizontalRepeats > 3) xEnd += horizontalRepeats - 3;
            int xStart = (xEnd - (horizontalRepeats - 1));

            if (mouseClick) {
                points += xEnd - xStart + 1;
            }
            else {
                xClick1 = xStart + 1;
                xClick2 = xStart + 1;
            }

            if (isFour) {
                combineFour(yRow, fourLine, grid[yRow][xStart]);
                specialGrid[yRow][fourXPosition] = 0;
            } else {
                removeFromRow(xClick1, xClick2, xStart, xEnd, yRow);

            }

            return true; // changes!
        }

        return false; // no change!
    }


    private void removeFromRow(int xClick1, int xClick2, int xStart, int xEnd, int yRow)  {
        int dif = xEnd - xStart;
        int origColor = grid[yRow][xStart];

        for(int yToTop = yRow; yToTop >= 0; yToTop--){
            for(int xIterator = xStart; xIterator <= xEnd; xIterator++){
                if(yToTop == 0){

                    grid[yToTop][xIterator] = r.nextInt(numberOfColors) + 1;
                    specialGrid[yToTop][xIterator] = 0;
                }else {
                    grid[yToTop][xIterator] = grid[yToTop -1][xIterator];
                    specialGrid[yToTop][xIterator] = specialGrid[yToTop-1][xIterator];
                }
            }
        }

        int xSpecial = elementIn(xStart, xEnd, xClick1, xClick2);


        if((dif+1) == 4) {
            //  System.out.println("Four!!!");
            setSpecialElement(origColor, yRow, xSpecial,true, true);
        }
        else if((dif+2) >= 5) {
            // System.out.println("Five!!!");
            setSpecialElement(origColor, yRow,xSpecial, false, true);
        }

    }


    private boolean checkGridVertical(int yClick1, int yClick2, boolean mouseClick) {
        boolean isFour = false;
        boolean fourLine = true;
        int fourYPosition = 0;

        int verticalRepeats = 1;
        int lastNumber;
        int actualNumber;
        int xCol = 0, yEnd = 0;
        boolean breakLoop = false;

        outerLoop:
        for (int xIterator = 0; xIterator < grid[0].length; xIterator++) {
            for (int yIterator = 0; yIterator < grid.length; yIterator++) {
                actualNumber = grid[yIterator][xIterator];

                if (yIterator - 1 < 0) lastNumber = 999;
                else lastNumber = grid[yIterator-1][xIterator];

                if (actualNumber == lastNumber) {
                    verticalRepeats++;

                    int special = specialGrid[yIterator][xIterator];
                    if (special == 4 | special == 3) {
                        isFour = true;
                        fourYPosition = yIterator;

                        fourLine = special != 3;
                    }

                } else {
                    verticalRepeats = 1;
                    isFour = false;
                }

                if (verticalRepeats == 3) {

                    //check for more of the same color in the row:
                    int i = 1;

                    while (true) {
                        //checks that we are in the grid
                        if ((yIterator + i) > (grid.length - 1)) break;

                        int nextNumber = grid[yIterator+1][xIterator];

                        if (nextNumber == actualNumber) verticalRepeats++;
                        else break;
                        i++;
                    }

                    xCol = xIterator;
                    yEnd = yIterator;
                    breakLoop = true;
                    break outerLoop;
                }
            }
        }
        if(breakLoop) {
            if (verticalRepeats > 3) yEnd += verticalRepeats - 3;
            int yStart = (yEnd - (verticalRepeats - 1));

            if (mouseClick) points += yEnd - yStart + 1;

            if (!mouseClick) {
                yClick1 = yStart + 1;
                yClick2 = yStart + 1;
            }

            if (isFour) {
                int origElement = grid[yStart][xCol];
                combineFour(xCol, fourLine, origElement);
                specialGrid[fourYPosition][xCol] = 0;

            } else removeFromCol(yClick1, yClick2, yStart, yEnd, xCol);


            return true; // changes!
        }
        return false; // no changes
    }


    private void removeFromCol(int yClick1, int yClick2, int yStart, int yEnd, int xCol) {
        int dif = yEnd - yStart;
        int origColor = grid[yStart][xCol];

        for (int yToTop = yEnd; yToTop >= 0; yToTop--) {
            if ((yToTop - dif) >= 0) {
                grid[yToTop][xCol] = grid[yToTop - dif][xCol];
                specialGrid[yToTop][xCol] = specialGrid[yToTop - dif][xCol];
            } else {
                grid[yToTop][xCol] = 0;//r.nextInt(numberOfColors) + 1;
                specialGrid[yToTop][xCol] = 0;
            }
        }


        int ySpecial = elementIn(yStart, yEnd, yClick1, yClick2);

        if ((dif + 1) == 4) {
            // System.out.println("Four!!!");
            setSpecialElement(origColor, ySpecial, xCol, true, false);
        } else if ((dif + 2) >= 5) {
            // System.out.println("Five!!!");
            setSpecialElement(origColor, ySpecial, xCol, false, false);
        }
    }


    private int elementIn(int start, int end, int test1, int test2) {
        boolean isTest1 = false;

        for (int i = 0; i <= end; i++) {
            if ((start + i) == test1) {
                isTest1 = true;
                break;
            }
        }

        if (isTest1) return test1;
        else return test2;
    }

    private void setSpecialElement(int origElement, int y, int x, boolean isFour, boolean isLine) {
        // four elements in one row
        if (isFour && isLine) specialGrid[y][x] = 4;
            // four elements in one col
        else if(isFour) specialGrid[y][x] = 3;
            // five elements in one row/col
        else specialGrid[y][x] = 5;

        grid[y][x] = origElement;
    }


    public void combineFive(int elementToRemove){
        for(int xIterator = 0; xIterator < grid.length; xIterator++){
            for(int yIterator = 0; yIterator < grid[0].length; yIterator++){
                int el = grid[xIterator][yIterator];

                if(specialGrid[xIterator][yIterator] == 5){
                    specialGrid[xIterator][yIterator] = 0;
                }

                if(elementToRemove == el){
                    this.points++;
                    int yToTop = yIterator;
                    while(yToTop > 0){
                        grid[xIterator][yToTop] = grid[xIterator][yToTop-1];
                        yToTop--;
                    }

                    grid[xIterator][0] = r.nextInt(numberOfColors) + 1;
                }
            }
        }
    }


    public void combineFour(int rowOrCol, boolean isLine, int origColor){
        if(isLine){
            System.out.println("Remove line");
            for (int xIterator = 0; xIterator < grid.length; xIterator++) {
                for (int toTop = rowOrCol; toTop >= 0; toTop--) {
                    runFourFive(origColor,toTop, xIterator);

                    if (toTop != 0) {
                        this.grid[xIterator][toTop] = this.grid[xIterator][toTop - 1];
                    } else {
                        this.grid[xIterator][toTop] = this.r.nextInt(numberOfColors) + 1;
                    }
                    this.points++;
                }
            }
        } else {
            System.out.println("Remove column");
            for (int yIterator = 0; yIterator < this.grid[1].length; yIterator++) {
                this.points++;
                this.grid[rowOrCol][yIterator] = this.r.nextInt(numberOfColors) + 1;

                this.runFourFive(origColor, yIterator, rowOrCol);
            }
        }
    }

    private void runFourFive(int origColor, int y, int x) {
        System.out.println("four/five");
        if(specialGrid[y][x] == 3){
            specialGrid[y][x] = 0;
            combineFour(y, false, origColor);
        } else if(specialGrid[y][x] == 4){
            specialGrid[y][x] = 0;
            combineFour(x, true, origColor);
        }else if(specialGrid[y][x] == 5){
            specialGrid[y][x] = 0;
            combineFive(origColor);
        }
    }
}

