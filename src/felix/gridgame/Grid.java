package felix.gridgame;

import java.util.Random;

public class Grid {

    private int points = 0;
    private int[][] grid;
    private int[][] specialGrid;
    private int numberOfColors;
    private Random r = new Random();

    public Grid(int numberOfColors){
        this.numberOfColors = numberOfColors;
    }

    public int getPoints(){ return this.points; }

    public int getSpecialGrid(int x, int y){ return this.specialGrid[x][y];}

    public void setSpecialGrid(int element, int x, int y){ this.specialGrid[x][y] = element;}

    public void setPointsToZero(){ this.points = 0; }

    public void setGrid(int value, int x, int y){ grid[x][y] = value; }

    public int getGrid(int x, int y){ return grid[x][y]; }


    public void generateRandomArray(int x_size, int y_size){
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


    private boolean checkGridHorizontal(int xClick1, int xClick2, boolean mouseClick) {
        boolean isFour = false;
        boolean fourLine = false;
        int fourPosition = 0;

        int horizontalRepeats = 1;
        int lastNumber;
        int actualNumber;
        int x_col = 0, y_row = 0;
        boolean breakLoop = false;

        y_outerloop:
        for (int y_iterator = 0; y_iterator < grid[0].length; y_iterator++) {
            for (int x_iterator = 0; x_iterator < grid.length; x_iterator++) {

                actualNumber = grid[x_iterator][y_iterator];

                if (x_iterator - 1 < 0) lastNumber = 999;
                else lastNumber = grid[x_iterator - 1][y_iterator];

                if (actualNumber == lastNumber) {
                    horizontalRepeats++;

                    int special = specialGrid[x_iterator][y_iterator];
                    if(special == 4 | special == 3){
                        isFour = true;
                        fourPosition = x_iterator;

                        // sets it true for 4
                        fourLine = special != 3;
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
                        if ((x_iterator + i) > (grid.length - 1)) break;

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
                xClick1 = x_start + 1;
                xClick2 = x_start + 1;
            }

            if (isFour) {
                combineFour(y_row, fourLine);
                specialGrid[fourPosition][y_row] = 0;
            } else {
                removeFromRow(xClick1, xClick2, x_start, x_end, y_row);
            }

            return true; // changes!
        }

        return false; // no change!
    }


    private void removeFromRow(int xClick1, int xClick2, int x_start, int x_end, int row) {
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

        int xRemove = whichElementIsMoved(x_start, x_end, xClick1, xClick2);


        if((dif+1) == 4) {
            System.out.println("Four!!!");
            setSpecialElement(element, xRemove, row, true, true);
        }
        else if((dif+2) >= 5) {
            System.out.println("Five!!!");
            setSpecialElement(element, xRemove, row, false, true);
        }

    }


    private boolean checkGridVertical(int yClick1, int yClick2, boolean mouseClick) {
        boolean isFour = false;
        boolean fourLine = true;
        int fourPosition = 0;

        int verticalRepeats = 1;
        int lastNumber;
        int actualNumber;
        int x_col = 0, y_end = 0;
        boolean breakLoop = false;

        x_outerloop:
        for (int x_iterator = 0; x_iterator < grid.length; x_iterator++) {
            for (int y_iterator = 0; y_iterator < grid[0].length; y_iterator++) {
                actualNumber = grid[x_iterator][y_iterator];

                if (y_iterator - 1 < 0) lastNumber = 999;
                else lastNumber = grid[x_iterator][y_iterator-1];

                if (actualNumber == lastNumber) {
                    verticalRepeats++;

                    int special = specialGrid[x_iterator][y_iterator];
                    if (special == 4 | special == 3) {
                        isFour = true;
                        fourPosition = y_iterator;

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
                        if ((y_iterator + i) > (grid[0].length - 1)) break;

                        int nextNumber = grid[x_iterator][y_iterator+1];

                        if (nextNumber == actualNumber) verticalRepeats++;
                        else break;
                        i++;
                    }

                    x_col = x_iterator;
                    y_end = y_iterator;
                    breakLoop = true;
                    break x_outerloop;
                }
            }
        }
        if(breakLoop) {
            if (verticalRepeats > 3) y_end += verticalRepeats - 3;
            int y_start = (y_end - (verticalRepeats - 1));

            if (mouseClick) points += y_end - y_start + 1;

            if (!mouseClick) {
                yClick1 = y_start + 1;
                yClick2 = y_start + 1;
            }

            if (isFour) {
                combineFour(x_col, fourLine);
                specialGrid[x_col][fourPosition] = 0;

            } else removeFromCol(yClick1, yClick2, y_start, y_end, x_col);


            return true; // changes!
        }
        return false; // no changes
    }


    private void removeFromCol(int yClick1, int yClick2, int y_start, int y_end, int col) {
        int dif = y_end - y_start;
        int element = grid[col][y_start + 1];

        for (int y_iterator = y_end; y_iterator >= 0; y_iterator--) {
            if ((y_iterator - dif) >= 0) {
                grid[col][y_iterator] = grid[col][y_iterator - dif];
            } else {
                grid[col][y_iterator] = r.nextInt(numberOfColors);
            }
        }


        int yRemove = whichElementIsMoved(y_start, y_end, yClick1, yClick2);

        if ((dif + 1) == 4) {
            System.out.println("Four!!!");
            setSpecialElement(element, col, yRemove, true, false);
        } else if ((dif + 2) >= 5) {
            System.out.println("Five!!!");
            setSpecialElement(element, col, yRemove, false, false);
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

    private void setSpecialElement(int origElement, int x, int y, boolean isFour, boolean isLine) {
        // four elements in one row
        if (isFour && isLine) specialGrid[x][y] = 4;
        // four elements in one col
        else if(isFour) specialGrid[x][y] = 3;
        // five elements in one row/col
        else specialGrid[x][y] = 5;

        grid[x][y] = origElement;
    }

    public void combineFive(int elementToRemove){

        for(int x_iterator = 0; x_iterator < grid.length; x_iterator++){
            for(int y_iterator = 0; y_iterator < grid[0].length; y_iterator++){
                int el = grid[x_iterator][y_iterator];

                if(specialGrid[x_iterator][y_iterator] == 5){
                    specialGrid[x_iterator][y_iterator] = 0;
                }

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
            for (int i = 0; i < grid.length; i++) {
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
            for (int i = 0; i < grid[0].length; i++) {
                grid[i][rowOrCol] = r.nextInt(numberOfColors);
            }
        }

    }


}
