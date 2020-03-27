package felix.gridgame;

import java.util.Random;

public class Grid {

    private int points = 0;
    private int[][] grid;
    private int[][] specialGrid;
    private int numberOfColors;
    private int x_size, y_size;
    private Random r = new Random();


    public Grid(int x_size, int y_size, int numberOfColors){
        this.x_size = x_size;
        this.y_size = y_size;
        this.numberOfColors = numberOfColors;
    }

    public int getPoints(){ return this.points; }

    public int getSpecialGrid(int x, int y){ return this.specialGrid[x][y];}

    public void setSpecialGrid(int element, int x, int y){ this.specialGrid[x][y] = element;}

    public void setPointsToZero(){ this.points = 0; }

    public void setGrid(int value, int x, int y){ grid[x][y] = value; }

    public int getGrid(int x, int y){ return grid[x][y]; }


    public void generateRandomArray(){
        grid = new int[x_size][y_size];

        // set the special array to 0's
        specialGrid = new int[x_size][y_size];

        for(int x_iterator = 0; x_iterator < x_size; x_iterator++){
            for(int y_iterator = 0; y_iterator < y_size; y_iterator++){
                grid[x_iterator][y_iterator] = r.nextInt(numberOfColors);
                specialGrid[x_iterator][y_iterator] = 0;
            }
        }
    }


    public boolean checkGrid(int x1, int x2, int y1, int y2, boolean mouseClick){
        // true = changes!, false = no changes
        if(checkGridHorizontal(x1, x2, mouseClick)) return true;
        return checkGridVertical(y1, y2, mouseClick);
    }


    private boolean checkGridHorizontal(int x1, int x2, boolean mouseClick) {
        boolean isFour = false;
        int fourPosition = 0;

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

                    if(specialGrid[x_iterator][y_iterator] == 4){
                        isFour = true;
                        fourPosition = x_iterator;
                    }

                } else {
                    horizontalRepeats = 1;
                    isFour = false;
                }

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
            if (horizontalRepeats > 3) x_end += horizontalRepeats - 3;
            int x_start = (x_end - (horizontalRepeats - 1));

            if (mouseClick) points += x_end - x_start + 1;

            if (!mouseClick) {
                x1 = x_start + 1;
                x2 = x_start + 1;
            }

            if (isFour) {
                combineFour(y_row, false);
                specialGrid[fourPosition][y_row] = 0;
            } else {
                removeFromRow(x1, x2, x_start, x_end, y_row);
            }

            return true; // changes!
        }

        return false; // no change!
    }


    private void removeFromRow(int click1, int click2, int x_start, int x_end, int row) {
        int dif = x_end - x_start;
        int element = grid[x_start+1][row];

        for(int y_iterator = row; y_iterator >= 0; y_iterator--){
            for(int x_iterator = x_start; x_iterator <= x_end; x_iterator++){
                if(y_iterator == 0){
                    grid[x_iterator][y_iterator] = r.nextInt(numberOfColors);
                }else {
                    grid[x_iterator][y_iterator] = grid[x_iterator][y_iterator-1];
                }
            }
        }

        int xRemove = whichElementIsMoved(x_start, x_end, click1, click2);


        if((dif+1) == 4) {
            System.out.println("Four!!!");
            setSpecialElement(element, xRemove, row, true);
        }
        else if((dif+2) >= 5) {
            System.out.println("Five!!!");
            setSpecialElement(element, xRemove, row, false);
        }

    }


    private boolean checkGridVertical(int y1, int y2, boolean mouseClick) {
        boolean isFour = false;
        int fourPosition = 0;

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
                    if (specialGrid[x_iterator][y_iterator] == 4) {
                        isFour = true;
                        fourPosition = y_iterator;
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

            if (verticalRepeats > 3) y_end += verticalRepeats - 3;
            int y_start = (y_end - (verticalRepeats - 1));

            if (mouseClick) points += y_end - y_start + 1;

            if (!mouseClick) {
                y1 = y_start + 1;
                y2 = y_start + 1;
            }

            if (isFour) {
                combineFour(x_col, true);
                specialGrid[x_col][fourPosition] = 0;

            } else removeFromCol(y1, y2, y_start, y_end, x_col);


            return true; // changes!
        }
        return false; // no changes
    }


    private void removeFromCol(int click1, int click2, int y_start, int y_end, int col) {
        int dif = y_end - y_start;
        int element = grid[col][y_start + 1];

        for (int y_iterator = y_end; y_iterator >= 0; y_iterator--) {
            if ((y_iterator - dif) >= 0) {
                grid[col][y_iterator] = grid[col][y_iterator - dif];
            } else {
                grid[col][y_iterator] = r.nextInt(numberOfColors);
            }
        }


        int yRemove = whichElementIsMoved(y_start, y_end, click1, click2);

        if ((dif + 1) == 4) {
            System.out.println("Four!!!");
            setSpecialElement(element, col, yRemove, true);
        } else if ((dif + 2) >= 5) {
            System.out.println("Five!!!");
            setSpecialElement(element, col, yRemove, false);
        }
    }

    private int whichElementIsMoved(int start, int end, int test1, int test2) {
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

    private void setSpecialElement(int element, int x, int y, boolean isFour) {
        // four Elements in one row/col
        if (isFour) specialGrid[x][y] = 4;

            // five elements in one row/col
        else specialGrid[x][y] = 5;

        grid[x][y] = element;
    }

    public void combineFive(int elementToRemove){

        for(int x_iterator = 0; x_iterator < x_size; x_iterator++){
            for(int y_iterator = 0; y_iterator < y_size; y_iterator++){
                int el = grid[x_iterator][y_iterator];

                if(elementToRemove == el){
                    int yToTop = y_iterator;
                    while(yToTop > 0){
                        grid[x_iterator][yToTop] = grid[x_iterator][yToTop-1];
                        yToTop--;
                    }

                    grid[x_iterator][0] = r.nextInt(numberOfColors);
                }
            }
        }
    }

    public void combineFour(int rowOrCol, boolean isLine){
        if(isLine){
            // remove line
            System.out.println("Remove line");
            for (int i = 0; i < x_size; i++) {
                for (int toTop = rowOrCol; toTop >= 0; toTop--) {
                    if (toTop != 0) {
                        grid[i][toTop] = grid[i][toTop - 1];
                    } else {
                        grid[i][toTop] = r.nextInt(numberOfColors);
                    }
                }
            }
        } else {
            // remove column
            System.out.println("Remove column");
            for (int i = 0; i < y_size; i++) {
                grid[i][rowOrCol] = r.nextInt(numberOfColors);
            }
        }

    }


}
