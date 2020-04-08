package com.felix.candy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

public class CandyJPanel extends JPanel implements Runnable {

    private final int XSIZE, YSIZE;
    private final int NUMBERFORMS;

    private final Random R = new Random();

    private final Drawer D;
    private final PositionChange P;
    private final GridChecker C;
    private final GameArray G;

    public CandyJPanel(int ySize, int xSize, int numberOfColors) {
        this.YSIZE = ySize;
        this.XSIZE = xSize;
        this.NUMBERFORMS = numberOfColors;

        G = new GameArray();
        D = new Drawer();
        C = new GridChecker();
        P = new PositionChange();

        initPanel();
    }

    private void initPanel(){

        // Label for the points (3 fields destroyed = 3 points)
        JLabel pointLabel = new JLabel("0");
        this.add(pointLabel);
        int points = 0;

        // MouseListener - looks for the clicks of the mouse
        this.addMouseListener(new MouseListener(){
            @Override
            public void mouseClicked(MouseEvent e) {
                int getX = (e.getX()-10) / 30;
                int getY = (e.getY()-10) / 30;
                //System.out.print("("+getX + "," + getY+") ");

                // saves the position into the Arraylist
                P.XCLICKED.add(getX);
                P.YCLICKED.add(getY);
            }
            public void mousePressed(MouseEvent e){}
            public void mouseReleased(MouseEvent e){}
            public void mouseEntered(MouseEvent e){}
            public void mouseExited(MouseEvent e){}
        });

        int widthPanel = XSIZE * 30+20, heightPanel = YSIZE * 30+40;
        this.setPreferredSize(new Dimension(widthPanel, heightPanel));
        this.setLayout(new FlowLayout(FlowLayout.RIGHT,20,heightPanel-30));
        this.setToolTipText("Click for changing the rectangles!");
    }

    @Override
    public void run(){

        C.remove();

        boolean isRunning = true;

        while(isRunning){

            P.getClicks();
            P.clickAnimation();
            P.start();
            P.animation();

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

        D.clearFormList();
        D.fillFormList();
        D.drawForm(g);
    }

    private class GridChecker{
        private int xStart, xEnd;
        private int row, col;
        private int yStart, yEnd;
        private boolean isVertical;

        private boolean checkGrid(){
            if(checkHorizontal()) {
                isVertical = false;
                return true;
            }
            else if(checkVertical()){
                isVertical = true;
                return true;
            }
            return false;
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

            for (int yI = 0; yI < G.grid.length; yI++) {
                for (int xI = 0; xI < G.grid[yI].length; xI++) {
                    actualNumber = G.grid[yI][xI];

                    if (xI - 1 >= 0) lastNumber = G.grid[yI][xI - 1];
                    else lastNumber = -99;

                    if (actualNumber != lastNumber) horizontalRepeats = 1;
                    else horizontalRepeats++;

                    if (horizontalRepeats == 3) {

                        //check for more of the same color in the row:
                        for (int i = 1; true; i++) {
                            if ((xI + i) > (G.grid[0].length - 1)) break;

                            int nextNumber = G.grid[yI][xI + i];

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
                        G.grid[yToTop][xIterator] = R.nextInt(NUMBERFORMS) + 1;

                    } else {
                        G.grid[yToTop][xIterator] = G.grid[yToTop - 1][xIterator];
                    }
                }
            }
        }


        private boolean checkVertical(){
            int verticalRepeats = 1;
            int lastNumber;
            int actualNumber;

            for(int xI = 0; xI < G.grid[0].length; xI++) {
                for(int yI = 0; yI < G.grid.length; yI++) {
                    actualNumber = G.grid[yI][xI];

                    if (yI - 1 < 0) lastNumber = -1;
                    else lastNumber = G.grid[yI-1][xI];

                    if (actualNumber == lastNumber) verticalRepeats++;
                    else verticalRepeats = 1;


                    if (verticalRepeats == 3) {

                        //check for more of the same color in the col:
                        for(int i = 1; true; i++){
                            if ((yI + i) >= (G.grid.length)) break;

                            int nextNumber = G.grid[yI+i][xI];

                            if (nextNumber == actualNumber) verticalRepeats++;
                            else break;
                        }

                        col = xI;
                        yEnd = yI;
                        if (verticalRepeats > 3) yEnd += verticalRepeats - 3;
                        yStart = (yEnd - (verticalRepeats - 1));

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
                    G.grid[yToTop][col] = G.grid[yToTop - dif][col];

                } else {
                    G.grid[yToTop][col] = R.nextInt(NUMBERFORMS) + 1;

                }
            }
        }
    }


    private class PositionChange {
        private int x1, y1;
        private int x2, y2;
        private boolean clickAnimation;
        private boolean animation;
        private boolean turned;
        private boolean explode;
        private int counter;
        private final int[] DIRECTION;
        private final ArrayList<Integer> XCLICKED = new ArrayList<>();
        private final ArrayList<Integer> YCLICKED = new ArrayList<>();

        private PositionChange(){
            animation = false;
            turned = false;
            explode = false;
            counter = 0;
            DIRECTION = new int[2];
        }

        private void getClicks() {
            if (XCLICKED.size() == 1 & YCLICKED.size() == 1) {
                x1 = XCLICKED.get(0);
                y1 = YCLICKED.get(0);
            } else if (XCLICKED.size() == 2 & YCLICKED.size() == 2) {
                x2 = XCLICKED.get(1);
                y2 = YCLICKED.get(1);

                if (!isNeighbour()) {
                    x1 = XCLICKED.get(1);
                    y1 = YCLICKED.get(1);

                    XCLICKED.clear();
                    YCLICKED.clear();

                    XCLICKED.add(x1);
                    YCLICKED.add(y2);
                }
            }
        }

        private void clickAnimation() {
            clickAnimation = XCLICKED.size() == 1 & YCLICKED.size() == 1;
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
            int save = G.grid[y1][x1];
            G.grid[y1][x1] = G.grid[y2][x2];
            G.grid[y2][x2] = save;
        }

        private void start() {
            if (XCLICKED.size() == 2 & YCLICKED.size() == 2) {

                change();
                animation = true;

                //toRight
                if (x2 > x1) DIRECTION[0] = 1;
                //toLeft
                else if (x2 < x1) DIRECTION[0] = 2;
                //toTop
                else if (y2 < y1) DIRECTION[0] = 3;
                //toBottom
                else if (y2 > y1) DIRECTION[0] = 4;

                XCLICKED.clear();
                YCLICKED.clear();
            }
        }

        private void animation() {
            if (animation) {
                if (counter < 30) {
                    counter += 2;
                } else if (counter == 30) {
                    counter = 0;

                    explode = C.checkGrid();
                    if(explode) {
                        animation = false;
                    }
                    else{ //change back!
                        if(turned){
                            animation = false;
                            turned = false;
                        }else{
                            turned = true;
                            change();

                            switch(DIRECTION[0]){
                                case 1:
                                    DIRECTION[1] = 2;
                                    break;
                                case 2:
                                    DIRECTION[1] = 1;
                                    break;
                                case 3:
                                    DIRECTION[1] = 4;
                                    break;
                                case 4:
                                    DIRECTION[1] = 3;
                                    break;
                            }
                        }
                    }
                }
            }
        }
    }

    private class GameArray{
        private int[][] grid;

        private GameArray(){
            generateRandomArray();
        }

        private void generateRandomArray(){
            grid = new int[YSIZE][XSIZE];

            for(int yIterator = 0; yIterator < grid.length; yIterator++){
                for(int xIterator = 0; xIterator < grid[yIterator].length; xIterator++){

                    grid[yIterator][xIterator] = R.nextInt(NUMBERFORMS) + 1;

                }
            }
        }

    }

    private static class GeomForm {
        final int X;
        final int Y;
        final int WIDTH;
        final int HEIGHT;
        final Color COLOR;
        final int FORM;

        private GeomForm(int x, int y, int width, int height, Color color, int form) {
            this.X = x;
            this.Y = y;
            this.WIDTH = width;
            this.HEIGHT = height;
            this.COLOR = color;
            this.FORM = form;
        }
    }

    private class Drawer {

        private final ArrayList<GeomForm> GFORMARRAYLIST = new ArrayList<>();
        private int explodeCounter = 0;
        private int[] geomArray;
        private Color[] colorArray;


        private Drawer(){
            fillColorArray();
            fillGeomArray();
        }

        private void clearFormList(){
            GFORMARRAYLIST.clear();
        }

        private void drawForm(Graphics g){
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (GeomForm i : GFORMARRAYLIST) {
                g2d.setPaint(i.COLOR);

                if (geomArray[i.FORM] == 0){
                    Ellipse2D circle = new Ellipse2D.Double(i.X, i.Y, i.WIDTH, i.HEIGHT);
                    g2d.fill(circle);
                    g2d.setColor(Color.gray);
                    g2d.draw(circle);
                } else if (geomArray[i.FORM] == 1){
                    Rectangle2D rect = new Rectangle2D.Double(i.X, i.Y, i.WIDTH, i.HEIGHT);
                    g2d.fill(rect);
                    g2d.setPaint(Color.gray);
                    g2d.draw(rect);
                }
            }
        }

        private void fillGeomArray(){
            geomArray = new int[NUMBERFORMS +1];

            for(int i = 0; i < NUMBERFORMS; i++){
                geomArray[i] = R.nextInt(2);
            }
        }

        private void fillColorArray() {
            if (NUMBERFORMS < 6) {
                colorArray = new Color[7];
            } else {
                colorArray = new Color[NUMBERFORMS + 1];
            }

            colorArray[0] = new Color(255, 255, 255);
            colorArray[1] = new Color(255, 31, 48);
            colorArray[2] = new Color(39, 97, 39, 230);
            colorArray[3] = new Color(255, 92, 242);
            colorArray[4] = new Color(0, 91, 255);
            colorArray[5] = new Color(255, 137, 61);
            colorArray[6] = new Color(25, 176, 130);

            for (int i = 7; i < NUMBERFORMS; i++) {
                float red = R.nextFloat();
                float green = (R.nextFloat() / 5f + 0.8f * i / NUMBERFORMS);
                float blue = (R.nextFloat() / 5f + 0.8f * i / NUMBERFORMS);

                colorArray[i] = new Color(red, green, blue);
            }
        }

        private void explode(){
            if(explodeCounter == 10) {
                explodeCounter = 0;
                P.explode = false;
                C.remove();
            }else{
                explodeCounter++;

                if(C.isVertical){
                    for(int i = C.yStart; i <= C.yEnd; i++){
                        GFORMARRAYLIST.add(new GeomForm(C.col*30+10,
                                i*30+10,
                                20,
                                20,
                                Color.black,
                                G.grid[C.yStart][C.col]));
                    }
                }else{
                    for(int i = C.xStart; i <= C.xEnd; i++){
                        GFORMARRAYLIST.add(new GeomForm(i*30+10,
                                C.row*30+10,
                                20,
                                20,
                                Color.black,
                                G.grid[C.row][C.xStart]));
                    }
                }
            }
        }

        private void fillFormList() {
            int xCell = 0, yCell = 0;

            int yMax = 30 * YSIZE + 10;
            int xMax = 30 * XSIZE + 10;

            int xAni = 0, yAni = 0;
            Color aniCol = Color.yellow;
            int aniNo = -1;

            for (int y = 10; y < yMax; y += 30) {
                for (int x = 10; x < xMax; x += 30) {

                    int no = G.grid[yCell][xCell];

                    boolean firstClick = P.y1 == yCell & P.x1 == xCell & P.clickAnimation;

                    boolean forward = P.y2 == yCell & P.x2 == xCell & !P.turned;
                    boolean back = P.y1 == yCell & P.x1 == xCell & P.turned;

                    boolean changePos = (forward | back) & P.animation;

                    if (firstClick | changePos) {
                        int move = 0;

                        if (changePos) move = P.counter;

                        aniCol = colorArray[no];
                        aniNo = no;

                        if (firstClick) {
                            xAni = x + 2;
                            yAni = y + 2;

                            GFORMARRAYLIST.add(new GeomForm(xAni, yAni, 16,16, aniCol, no));

                        } else { // p.animation
                            int di = 0;
                            if(forward) di = P.DIRECTION[0];
                            else if (back) di = P.DIRECTION[1];
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
                            GFORMARRAYLIST.add(new GeomForm(xAni, yAni, 20,20, aniCol, no));
                        }
                    }

                    else {
                        // without animation
                        GFORMARRAYLIST.add(new GeomForm(x, y,20,20, colorArray[no], no));
                    }

                    xCell++;
                }
                xCell = 0;
                yCell++;
            }

            if (P.animation) {
                GFORMARRAYLIST.add(new GeomForm(xAni, yAni, 20,20, aniCol, aniNo));
            }

            if(P.explode) explode();
        }
    }
}