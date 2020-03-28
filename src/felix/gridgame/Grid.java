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
        specialGrid = new int[x_size][y_size];

        for(int xIterator = 0; xIterator < x_size; xIterator++){
            for(int yIterator = 0; yIterator < y_size; yIterator++){
                grid[xIterator][yIterator] = r.nextInt(numberOfColors);

                // set the special array to 0's
                specialGrid[xIterator][yIterator] = 0;
            }
        }
    }


    public boolean checkGrid(int xClick1, int xClick2, int yClick1, int yClick2, boolean mouseClick){
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

        y_outerloop:
        for (int yIterator = 0; yIterator < grid[0].length; yIterator++) {
            for (int xIterator = 0; xIterator < grid.length; xIterator++) {

                actualNumber = grid[xIterator][yIterator];

                if (xIterator - 1 < 0) lastNumber = 999;
                else lastNumber = grid[xIterator - 1][yIterator];

                if (actualNumber != lastNumber){
                    horizontalRepeats = 1;
                    isFour = false;

                } else  {
                    horizontalRepeats++;

                    int special = specialGrid[xIterator][yIterator];
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
                        if ((xIterator + i) > (grid.length - 1)) break;

                        int nextNumber = grid[xIterator + i][yIterator];

                        if (nextNumber == actualNumber) horizontalRepeats++;
                        else break;
                        i++;
                    }

                    xEnd = xIterator;
                    yRow = yIterator;
                    breakLoop = true;
                    break y_outerloop;
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
                combineFour(yRow, fourLine);
                specialGrid[fourXPosition][yRow] = 0;
            } else {
                removeFromRow(xClick1, xClick2, xStart, xEnd, yRow);
            }

            return true; // changes!
        }

        return false; // no change!
    }


    private void removeFromRow(int xClick1, int xClick2, int xStart, int xEnd, int yRow) {
        int dif = xEnd - xStart;
        int origColor = grid[xStart+1][yRow];

        for(int yIterator = yRow; yIterator >= 0; yIterator--){
            for(int xIterator = xStart; xIterator <= xEnd; xIterator++){
                if(yIterator == 0){
                    grid[xIterator][yIterator] = r.nextInt(numberOfColors);
                    specialGrid[xIterator][yIterator] = 0;
                }else {
                    grid[xIterator][yIterator] = grid[xIterator][yIterator -1];
                    specialGrid[xIterator][yIterator] = specialGrid[xIterator][yIterator -1];
                }
            }

        }

        int xSpecial = whichElementIsMoved(xStart, xEnd, xClick1, xClick2);


        if((dif+1) == 4) {
            System.out.println("Four!!!");
            setSpecialElement(origColor, xSpecial, yRow, true, true);
        }
        else if((dif+2) >= 5) {
            System.out.println("Five!!!");
            setSpecialElement(origColor, xSpecial, yRow, false, true);
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

        x_outerloop:
        for (int xIterator = 0; xIterator < grid.length; xIterator++) {
            for (int yIterator = 0; yIterator < grid[0].length; yIterator++) {
                actualNumber = grid[xIterator][yIterator];

                if (yIterator - 1 < 0) lastNumber = 999;
                else lastNumber = grid[xIterator][yIterator-1];

                if (actualNumber == lastNumber) {
                    verticalRepeats++;

                    int special = specialGrid[xIterator][yIterator];
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
                        if ((yIterator + i) > (grid[0].length - 1)) break;

                        int nextNumber = grid[xIterator][yIterator+1];

                        if (nextNumber == actualNumber) verticalRepeats++;
                        else break;
                        i++;
                    }

                    xCol = xIterator;
                    yEnd = yIterator;
                    breakLoop = true;
                    break x_outerloop;
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
                combineFour(xCol, fourLine);
                specialGrid[xCol][fourYPosition] = 0;

            } else removeFromCol(yClick1, yClick2, yStart, yEnd, xCol);


            return true; // changes!
        }
        return false; // no changes
    }


    private void removeFromCol(int yClick1, int yClick2, int yStart, int yEnd, int xCol) {
        int dif = yEnd - yStart;
        int origColor = grid[xCol][yStart];

        for (int y_iterator = yEnd; y_iterator >= 0; y_iterator--) {
            if ((y_iterator - dif) >= 0) {
                grid[xCol][y_iterator] = grid[xCol][y_iterator - dif];
                specialGrid[xCol][y_iterator] = specialGrid[xCol][y_iterator - dif];
            } else {
                grid[xCol][y_iterator] = r.nextInt(numberOfColors);
                specialGrid[xCol][y_iterator] = 0;
            }
        }


        int ySpecial = whichElementIsMoved(yStart, yEnd, yClick1, yClick2);

        if ((dif + 1) == 4) {
            System.out.println("Four!!!");
            setSpecialElement(origColor, xCol, ySpecial, true, false);
        } else if ((dif + 2) >= 5) {
            System.out.println("Five!!!");
            setSpecialElement(origColor, xCol, ySpecial, false, false);
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

        for(int xIterator = 0; xIterator < grid.length; xIterator++){
            for(int yIterator = 0; yIterator < grid[0].length; yIterator++){
                int el = grid[xIterator][yIterator];

                if(specialGrid[xIterator][yIterator] == 5){
                    specialGrid[xIterator][yIterator] = 0;
                }

                if(elementToRemove == el){
                    int yToTop = yIterator;
                    while(yToTop > 0){
                        grid[xIterator][yToTop] = grid[xIterator][yToTop-1];
                        yToTop--;
                    }

                    grid[xIterator][0] = r.nextInt(numberOfColors);
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
