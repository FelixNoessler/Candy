package felix.gridgame;

import java.awt.Color;
import java.awt.Graphics;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.*;

public class Cell_JPanel extends JPanel {
    private static final Random r = new Random();

    private int x_size = 15, y_size = 15;

    public boolean firstTime = true;

    private int[][] grid;

    private boolean removed = false;

    private int points = 0;
    private JLabel pointLabel;
    private int numberOfColors = 4;

    private ArrayList<Integer> x_clicked = new ArrayList<>();
    private ArrayList<Integer> y_clicked = new ArrayList<>();

    private Color[] colorArray;

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

    public Cell_JPanel() {
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
                    checkGrid(true);
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

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(firstTime) {
            setColors();
            generateRandomArray();
        }
        firstTime = false;
        drawGrid(g);
        checkGrid(false);
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
                int save = grid[x1][y1];

                grid[x1][y1] = grid[x2][y2];
                grid[x2][y2] = save;
                repaint();
            }
        }

    }

    private void checkGrid(boolean mouseClick){
        checkGridHorizontal(mouseClick);
        checkGridVertical(mouseClick);
    }


    private void checkGridHorizontal(boolean mouseClick) {
        int horiz = 1;
        int lastNumber;
        int actualNumber;
        int x_col = 0, y_row = 0;
        boolean breakLoop = false;

        y_outerloop:
        for (int y_iterator = 0; y_iterator < y_size; y_iterator++) {
            for (int x_iterator = 0; x_iterator < x_size; x_iterator++) {

                actualNumber = grid[x_iterator][y_iterator];

                if (x_iterator - 1 < 0) lastNumber = 999;
                else lastNumber = grid[x_iterator - 1][y_iterator];

                if (actualNumber == lastNumber) {
                    horiz++;
                } else horiz = 1;

                if (horiz == 3) {

                    //check for more of the same color in the row:
                    int i = 1;
                    while (true) {
                        //checks that we are in the grid
                        if ((x_iterator + i) > (x_size - 1)) break;

                        int nextNumber = grid[x_iterator + i][y_iterator];

                        if (nextNumber == actualNumber) horiz++;
                        else break;
                        i++;
                    }

                    x_col = x_iterator;
                    y_row = y_iterator;
                    breakLoop = true;
                    break y_outerloop;
                }
            }
        }
        if(breakLoop) {
            int x_end = x_col;
            if(horiz > 3) x_end += horiz-3;
            int x_start = (x_end - (horiz-1));

            if(mouseClick) points += x_end - x_start + 1;
            removeFromRow(x_start, x_end, y_row);
            removed = true;
            repaint();
        }
    }


    private void checkGridVertical(boolean mouseClick) {
        int verticalRepeats = 1;
        int lastNumber;
        int actualNumber;
        int x_col = 0, y_row = 0;
        boolean breakLoop = false;

        x_outerloop:
        for (int x_iterator = 0; x_iterator < x_size; x_iterator++) {
            for (int y_iterator = 0; y_iterator < y_size; y_iterator++) {
                actualNumber = grid[x_iterator][y_iterator];

                if (y_iterator - 1 < 0) lastNumber = 999;
                else lastNumber = grid[x_iterator][y_iterator-1];

                if (actualNumber == lastNumber) {
                    verticalRepeats++;
                } else verticalRepeats = 1;

                if (verticalRepeats == 3) {

                    //check for more of the same color in the row:
                    int i = 1;

                    while (true) {
                        //checks that we are in the grid
                        if ((y_iterator + i) > (y_size - 1)) break;

                        int nextNumber = grid[x_iterator][y_iterator+1];

                        if (nextNumber == actualNumber) verticalRepeats++;
                        else break;
                        i++;
                    }

                    x_col = x_iterator;
                    y_row = y_iterator;
                    breakLoop = true;
                    break x_outerloop;
                }
            }
        }
        if(breakLoop) {
            int y_end = y_row;

            if(verticalRepeats > 3) y_end += verticalRepeats-3;
            int y_start = (y_end - (verticalRepeats-1));

            if(mouseClick) points += y_end - y_start + 1;

            removeFromCol(y_start, y_end, x_col);
            removed = true;
            repaint();
        }
    }


    private void removeFromRow(int x_start, int x_end, int row) {
        for(int y_iterator = row; y_iterator >= 0; y_iterator--){
            for(int x_iterator = x_start; x_iterator <= x_end; x_iterator++){
                if(y_iterator == 0){
                    grid[x_iterator][y_iterator] = r.nextInt(numberOfColors);
                }else {
                    grid[x_iterator][y_iterator] = grid[x_iterator][y_iterator-1];
                }
            }
        }

        if((x_end-x_start) >= 5) grid[x_start+1][row] = 0;

    }


    private void removeFromCol(int y_start, int y_end, int col){
        int dif = y_end - y_start;
        for(int y_iterator = y_end; y_iterator >= 0; y_iterator--){
            if( (y_iterator-dif) >= 0){
                grid[col][y_iterator] = grid[col][y_iterator-dif];
            }
            else{
                grid[col][y_iterator] = r.nextInt(numberOfColors);
            }
        }
    }


    private void drawGrid(Graphics g){
        int x_cell = 0, y_cell = 0;

        int x_max = 30*x_size +10, y_max = 30*y_size +10;

        for (int y = 10; y < y_max; y += 30){
            for (int x = 10; x < x_max; x += 30) {
                int no = grid[x_cell][y_cell];
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

    private void generateRandomArray(){
        grid = new int[x_size][y_size];

        for(int x_iterator = 0; x_iterator < x_size; x_iterator++){
            for(int y_iterator = 0; y_iterator < y_size; y_iterator++){
                grid[x_iterator][y_iterator] = r.nextInt(numberOfColors);
            }
        }
    }
}

