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
    private final GameArrays G;
    private final Animation A;

    public CandyJPanel(int ySize, int xSize, int numberOfColors) {
        this.YSIZE = ySize;
        this.XSIZE = xSize;
        this.NUMBERFORMS = numberOfColors;

        G = new GameArrays();
        D = new Drawer();
        C = new GridChecker();
        P = new PositionChange();
        A = new Animation();

        initPanel();
    }

    private void initPanel(){

        // Label for the points (3 fields destroyed = 3 points)
        //JLabel pointLabel = new JLabel("0");
        //this.add(pointLabel);

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

        //System.currentTimeMillis();


        while (isRunning) {

            P.getClicks();
            A.clickAnimation();
            P.start();
            A.changePosAnimation();

            repaint();

            try {
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

    private class GridChecker {
        private int xStart, xEnd;
        private int yStart, yEnd;
        private int row, col;
        private boolean isVertical;


        private boolean checkGrid(){
            // wrapper for the two functions
            if(checkHorizontal()) {
                isVertical = false;
                return true; // three or more ine one col
            } else if (checkVertical()) {
                isVertical = true;
                return true; // three or more in one row
            }
            return false; // could not find three or more in one row/col
        }

        private void remove() {
            // remove without animation (on startup)
            while (checkGrid()) {
                if (checkHorizontal()) remRow();
                if (checkVertical()) remCol();
            }
        }

        private boolean checkHorizontal() {
            // goes through the grid and finds places where 3 or more
            // of the same form/color are grouped together
            // changes the variables row, xStart, xEnd
            // set colorBombs for five in one row
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

                        if (horizontalRepeats >= 5) G.setColorBomb(row, clickInX(xStart, xEnd));

                        return true; // found 3 or more of the same form/color
                    }
                }
            }
            return false; // could not find 3 or more of the same form/color
        }

        private int clickInX(int x1, int x2) {
            for (int i = x1; i <= x2; i++) {
                if (i == P.x1) return P.x1;
                else if (i == P.x2) return P.x2;
            }
            // no click in range found!
            return x1;
        }

        private void remRow() {
            // changes the grid, removes three or more in one row
            for (int yToTop = row; yToTop >= 0; yToTop--) {
                for (int xI = xStart; xI <= xEnd; xI++) {
                    if (yToTop > 0) {
                        G.grid[yToTop][xI] = G.grid[yToTop - 1][xI];
                        G.specialGrid[yToTop][xI] = G.specialGrid[yToTop - 1][xI];

                    } else {
                        G.grid[0][xI] = R.nextInt(NUMBERFORMS) + 1;
                        G.specialGrid[0][xI] = 0;
                    }
                }
            }
        }


        private boolean checkVertical() {
            // goes through the grid and finds places where 3 or more
            // of the same form/color are grouped together in one column
            // changes the variables col, yStart, yEnd
            // set colorBombs for five in one column

            int verticalRepeats = 1;
            int lastNumber;
            int actualNumber;

            for (int xI = 0; xI < G.grid[0].length; xI++) {
                for (int yI = 0; yI < G.grid.length; yI++) {
                    actualNumber = G.grid[yI][xI];

                    if (yI - 1 < 0) lastNumber = -1;
                    else lastNumber = G.grid[yI - 1][xI];

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

                        if (verticalRepeats >= 5) G.setColorBomb(clickInY(yStart, yEnd), col);

                        return true; // changes!
                    }
                }
            }
            return false; // no changes
        }


        private int clickInY(int y1, int y2) {
            for (int i = y1; i <= y2; i++) {
                if (i == P.y1) return P.y1;
                else if (i == P.y2) return P.y2;
            }
            // no click in range found!
            return y1;
        }


        private void remCol() {
            // removes three or more forms in one column
            int dif = yEnd - yStart;
            for (int yToTop = yEnd; yToTop >= 0; yToTop--) {
                if ((yToTop - dif) >= 0) {
                    G.grid[yToTop][col] = G.grid[yToTop - dif][col];
                    G.specialGrid[yToTop][col] = G.specialGrid[yToTop - 1][col];

                } else {
                    G.grid[0][col] = R.nextInt(NUMBERFORMS) + 1;
                    G.specialGrid[0][col] = 0;
                }

            }
        }


    }

    private class Animation {

        private final int[] DIRECTION;
        private boolean clickAnimation;
        private boolean posAnimation;
        private boolean turned;
        private boolean explode;
        private int posCounter;
        private int explodeCounter;
        private int removeCounter;
        private boolean removeAni;


        private boolean remColAnimation;
        private boolean remRowAnimation;
        private int rowMove;

        private Animation() {
            posAnimation = false;
            turned = false;
            explode = false;
            posCounter = 0;
            DIRECTION = new int[2];

            explodeCounter = 0;
            removeCounter = 0;
            removeAni = false;

            remRowAnimation = false;
            remColAnimation = false;
        }

        private boolean isClickAnimation(int y, int x) {
            return P.y1 == y & P.x1 == x & clickAnimation;
        }

        private void clickAnimation() {
            clickAnimation = P.XCLICKED.size() == 1 & P.YCLICKED.size() == 1;
        }

        private boolean[] isChangePosAnimation(int y, int x) {
            boolean forward = P.y2 == y & P.x2 == x & !turned;
            boolean back = P.y1 == y & P.x1 == x & turned;

            return new boolean[]{(forward | back) & posAnimation, forward};
        }

        private void changePosAnimation() {
            if (posAnimation) {
                if (posCounter < 30) {
                    posCounter += 2;
                } else if (posCounter == 30) {
                    posCounter = 0;

                    explode = C.checkGrid();
                    if (explode) {
                        posAnimation = false;
                    } else { //change back!
                        if (turned) {
                            posAnimation = false;
                            turned = false;
                        } else {
                            turned = true;
                            P.change();

                            switch (DIRECTION[0]) {
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

        private boolean isRemoveAnimation(int y, int x) {
            boolean inX = false;
            for (int i = C.xStart; i <= C.xEnd; i++) {
                if (x == i) {
                    inX = true;
                    break;
                }
            }

            boolean inY = false;
            for (int i = C.row; i >= 0; i--) {
                if (y == i) {
                    inY = true;
                    break;
                }
            }

            boolean isRow = remRowAnimation & inY & inX;
            if (isRow) return true;


            inY = false;
            for (int i = C.yEnd; i >= 0; i--) {
                if (i == y) {
                    inY = true;
                    break;
                }
            }

            return remColAnimation & inY & x == C.col;
        }


        private void removeAnimation() {
            if (remRowAnimation) {
                countRemoveAnimation(true);
                return;
            } else if (remColAnimation) {
                countRemoveAnimation(false);
                return;
            }

            if (removeCounter == 10 & !C.checkGrid()) {
                removeAni = false;
                removeCounter = 0;
                return;
            } else if (removeCounter == 10) {
                removeCounter = 0;
                return;
            }
            if (removeCounter == 0) {
                if (C.checkHorizontal()) {
                    C.remRow();
                    rowMove = -30;
                    remRowAnimation = true;
                    return;
                } else if (C.checkVertical()) {
                    C.remCol();
                    rowMove = -30 * (C.yEnd - C.yStart + 1);
                    remColAnimation = true;
                    return;
                }
            }
            removeCounter++;
        }

        private void countRemoveAnimation(boolean row) {
            if (rowMove == 0) {
                if (row) remRowAnimation = false;
                else remColAnimation = false;
                return;
            }

            if (row) rowMove += 5;
            else rowMove += 10;
        }

    }


    private class PositionChange {
        private int x1, y1;
        private int x2, y2;

        private final ArrayList<Integer> XCLICKED = new ArrayList<>();
        private final ArrayList<Integer> YCLICKED = new ArrayList<>();


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

        private void start() {
            if (XCLICKED.size() == 2 & YCLICKED.size() == 2) {

                change();
                A.posAnimation = true;

                //toRight
                if (x2 > x1) A.DIRECTION[0] = 1;
                    //toLeft
                else if (x2 < x1) A.DIRECTION[0] = 2;
                    //toTop
                else if (y2 < y1) A.DIRECTION[0] = 3;
                    //toBottom
                else if (y2 > y1) A.DIRECTION[0] = 4;

                XCLICKED.clear();
                YCLICKED.clear();
            }
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

        private void change(){
            changeGrid();
            changeSpecial();
        }

        private void changeGrid() {
            int save = G.grid[y1][x1];
            G.grid[y1][x1] = G.grid[y2][x2];
            G.grid[y2][x2] = save;
        }

        private void changeSpecial(){
            int save = G.specialGrid[y1][x1];
            G.specialGrid[y1][x1] = G.specialGrid[y2][x2];
            G.specialGrid[y2][x2] = save;
        }

    }

    private class GameArrays {
        private int[][] grid;
        private int[][] specialGrid;
        private int[] geomArray;
        private Color[] colorArray;

        private GameArrays(){
            fillGridArray();
            fillSpecialArray();
            fillColorArray();
            fillGeomArray();
        }

        private void fillGridArray(){
            grid = new int[YSIZE][XSIZE];
            for(int yI = 0; yI < grid.length; yI++){
                for(int xI = 0; xI < grid[yI].length; xI++){
                    grid[yI][xI] = R.nextInt(NUMBERFORMS) + 1;
                }
            }
        }

        private void fillSpecialArray(){
            specialGrid = new int[YSIZE][XSIZE];
            for(int yI = 0; yI < grid.length; yI++){
                for(int xI = 0; xI < grid[yI].length; xI++){
                    specialGrid[yI][xI] = 0;
                }
            }
        }

        private void fillGeomArray(){
            geomArray = new int[NUMBERFORMS+1];

            for(int i = 0; i < NUMBERFORMS; i++){
                if (i < 5 ) geomArray[i] = i;
                else geomArray[i] = R.nextInt(5);
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

        private void setColorBomb(int y, int x) {
            G.specialGrid[y][x] = 5;
        }

        private boolean isColorBomb(int y, int x) {
            return specialGrid[y][x] == 5;
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


        private void clearFormList(){
            GFORMARRAYLIST.clear();
        }

        private void fillFormList() {
            int xCell = 0, yCell = 0;

            int yMax = 30 * YSIZE + 10;
            int xMax = 30 * XSIZE + 10;

            for (int y = 10; y < yMax; y += 30) {
                for (int x = 10; x < xMax; x += 30) {
                    int no = G.grid[yCell][xCell];
                    Color col = G.colorArray[no];

                    boolean firstClick = A.isClickAnimation(yCell, xCell);
                    boolean[] changePos = A.isChangePosAnimation(yCell, xCell);
                    boolean r1 = A.isRemoveAnimation(yCell, xCell);

                    if (G.isColorBomb(yCell, xCell)) no = -1;

                    if (firstClick) fillClick(x, y, col, no);
                    else if (changePos[0]) fillChangePos(x, y, changePos[1], col, no);
                    else if (r1) fillRem(x, y, col, no);
                    else GFORMARRAYLIST.add(new GeomForm(x, y, 20, 20, col, no));

                    xCell++;
                }
                xCell = 0;
                yCell++;
            }

            if (A.explode) explode();
            else if (A.removeAni) A.removeAnimation();
        }

        private void fillClick(int x, int y, Color col, int no) {
            x += 2;
            y += 2;
            GFORMARRAYLIST.add(new GeomForm(x, y, 16, 16, col, no));
        }

        private void fillChangePos(int x, int y, boolean forward, Color col, int no) {
            int di;
            if (forward) di = A.DIRECTION[0];
            else di = A.DIRECTION[1];

            int move = A.posCounter;

            switch (di) {
                case 1: // to right
                    x = x - 30 + move;
                    break;
                case 2: // to left
                    x = x + 30 - move;
                    break;
                case 3: // to top
                    y = y + 30 - move;
                    break;
                case 4: // to bottom
                    y = y - 30 + move;
                    break;
            }

            GFORMARRAYLIST.add(new GeomForm(x, y, 20, 20, col, no));
        }

        private void fillRem(int x, int y, Color col, int no) {
            y = y + A.rowMove;
            GFORMARRAYLIST.add(new GeomForm(x, y, 20, 20, col, no));
        }

        private void explode() {
            // make the forms black
            // after 10 steps calls removeAnimation() in fillFormList()
            // though removeAni = true

            if (A.explodeCounter == 10) {
                A.explodeCounter = 0;
                A.explode = false;
                A.removeAni = true;
            } else {
                A.explodeCounter++;

                if (C.isVertical) {
                    for (int i = C.yStart; i <= C.yEnd; i++) {
                        Color c1 = Color.black;
                        int n1 = G.grid[C.yStart][C.col];
                        if (G.isColorBomb(i, C.col)) {
                            c1 = Color.yellow;
                            n1 = -1;
                        }

                        GFORMARRAYLIST.add(new GeomForm(C.col * 30 + 10,
                                i * 30 + 10,
                                20,
                                20,
                                c1,
                                n1));
                    }
                } else {
                    for (int i = C.xStart; i <= C.xEnd; i++) {
                        Color c1 = Color.black;
                        int n1 = G.grid[C.row][C.xStart];
                        if (G.isColorBomb(C.row, i)) {
                            c1 = Color.yellow;
                            n1 = -1;
                        }

                        GFORMARRAYLIST.add(new GeomForm(i * 30 + 10,
                                C.row * 30 + 10,
                                20,
                                20,
                                c1,
                                n1));
                    }
                }
            }
        }


        private void drawForm(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (GeomForm i : GFORMARRAYLIST) {
                g2d.setPaint(i.COLOR);

                if (i.FORM == -1) {
                    g2d.setPaint(Color.black);
                    Rectangle2D rect = new Rectangle2D.Double(i.X, i.Y, i.WIDTH * 0.5, i.HEIGHT * 0.5);
                    g2d.fill(rect);

                    g2d.setPaint(i.COLOR);
                    rect = new Rectangle2D.Double(i.X + i.WIDTH * 0.5, i.Y, i.WIDTH * 0.5, i.HEIGHT * 0.5);
                    g2d.fill(rect);

                    g2d.setPaint(i.COLOR);
                    rect = new Rectangle2D.Double(i.X, i.Y + i.WIDTH * 0.5, i.WIDTH * 0.5, i.HEIGHT * 0.5);
                    g2d.fill(rect);

                    g2d.setPaint(Color.black);
                    rect = new Rectangle2D.Double(i.X + i.WIDTH * 0.5, i.Y + i.WIDTH * 0.5, i.WIDTH * 0.5, i.HEIGHT * 0.5);
                    g2d.fill(rect);

                } else if (G.geomArray[i.FORM] == 0) {
                    Ellipse2D circle = new Ellipse2D.Double(i.X, i.Y, i.WIDTH, i.HEIGHT);
                    g2d.fill(circle);
                    g2d.setColor(Color.gray);
                    g2d.draw(circle);

                } else if (G.geomArray[i.FORM] == 1) {
                    Rectangle2D rect = new Rectangle2D.Double(i.X, i.Y, i.WIDTH, i.HEIGHT);
                    g2d.fill(rect);
                    g2d.setPaint(Color.gray);
                    g2d.draw(rect);

                } else if (G.geomArray[i.FORM] == 2) {
                    int[] xTriangle = {i.X + i.WIDTH / 2, i.X + i.WIDTH, i.X};
                    int[] yTriangle = {i.Y, i.Y + i.HEIGHT, i.Y + i.HEIGHT};

                    Polygon poly = new Polygon(xTriangle, yTriangle, 3);
                    g2d.fillPolygon(poly);
                    g2d.setPaint(Color.gray);
                    g2d.drawPolygon(poly);

                } else if (G.geomArray[i.FORM] == 3) {
                    int[] xPoly = {i.X + i.WIDTH / 2, i.X + i.WIDTH, i.X + i.WIDTH / 2, i.X};
                    int[] yPoly = {i.Y, i.Y + i.HEIGHT / 2, i.Y + i.HEIGHT, i.Y + i.HEIGHT / 2};

                    Polygon poly = new Polygon(xPoly, yPoly, 4);
                    g2d.fillPolygon(poly);
                    g2d.setPaint(Color.gray);
                    g2d.drawPolygon(poly);

                } else if (G.geomArray[i.FORM] == 4) {
                    int[] xPoly = {i.X + i.WIDTH / 4, i.X + i.WIDTH * 3 / 4,
                            i.X + i.WIDTH, i.X + i.WIDTH, i.X, i.X};

                    int[] yPoly = {i.Y, i.Y, i.Y + i.HEIGHT / 4, i.Y + i.HEIGHT * 3 / 4,
                            i.Y + i.HEIGHT * 3 / 4, i.Y + i.HEIGHT / 4};

                    Polygon poly = new Polygon(xPoly, yPoly, 6);
                    g2d.fillPolygon(poly);
                    g2d.setPaint(Color.gray);
                    g2d.drawPolygon(poly);

                }
            }
        }

    }

}