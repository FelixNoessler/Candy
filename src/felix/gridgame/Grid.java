package felix.gridgame;

import java.util.Random;

public class Grid {

    private int points = 0;
    private int[][] grid;
    private int numberOfColors;
    private int x_size, y_size;
    private Random r = new Random();


    public Grid(int x_size, int y_size, int numberOfColors){
        this.x_size = x_size;
        this.y_size = y_size;
        this.numberOfColors = numberOfColors;
    }

    public int getPoints(){
        return this.points;
    }

    public void setPointsToZero(){
        this.points = 0;
    }

    public void setGrid(int value, int x, int y){
        grid[x][y] = value;
    }

    public int getGrid(int x, int y){
        return grid[x][y];
    }


    public void generateRandomArray(){
        grid = new int[x_size][y_size];

        for(int x_iterator = 0; x_iterator < x_size; x_iterator++){
            for(int y_iterator = 0; y_iterator < y_size; y_iterator++){
                grid[x_iterator][y_iterator] = r.nextInt(numberOfColors);
            }
        }
    }


    public boolean checkGrid(boolean mouseClick){
        // true = changes!, false = no changes
        if(checkGridHorizontal(mouseClick)) return true;
        return checkGridVertical(mouseClick);
    }


    private boolean checkGridHorizontal(boolean mouseClick) {
        int horizontalRepeats = 1;
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
                    horizontalRepeats++;
                } else horizontalRepeats = 1;

                if (horizontalRepeats == 3) {

                    //check for more of the same color in the row:
                    int i = 1;
                    while (true) {
                        //checks that we are in the grid
                        if ((x_iterator + i) > (x_size - 1)) break;

                        int nextNumber = grid[x_iterator + i][y_iterator];

                        if (nextNumber == actualNumber) horizontalRepeats++;
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
            if(horizontalRepeats > 3) x_end += horizontalRepeats-3;
            int x_start = (x_end - (horizontalRepeats-1));

            if(mouseClick) points += x_end - x_start + 1;

            removeFromRow(x_start, x_end, y_row);
            return true; // changes!
        }

        return false; // no change!
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



    private boolean checkGridVertical(boolean mouseClick) {
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

            return true; // changes!
        }
        return false; // no changes
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
}
