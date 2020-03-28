package com.felix.candy;

import java.util.Random;

public class CandyGrid {

    private int points = 0;
    private int[][] grid;
    private int[][] specialGrid;
    private int numberOfColors;
    private Random r = new Random();

    public CandyGrid(int numberOfColors){
        this.numberOfColors = numberOfColors;
    }

    public int getPoints(){ return this.points; }

    public int getSpecialGrid(int y, int x){ return this.specialGrid[y][x];}

    public void setSpecialGrid(int element, int y, int x){ this.specialGrid[y][x] = element;}

    public void setPointsToZero(){ this.points = 0; }

    public void setGrid(int value, int y, int x){ grid[y][x] = value; }

    public int getGrid(int y, int x){ return grid[y][x]; }


    public void generateRandomArray(int ySize, int xSize){
        grid = new int[ySize][xSize];
        specialGrid = new int[ySize][xSize];

        for(int yIterator = 0; yIterator < ySize; yIterator++){
            for(int xIterator = 0; xIterator < xSize; xIterator++){
                grid[yIterator][xIterator] = r.nextInt(numberOfColors);

                // set the special array to 0's
                specialGrid[yIterator][xIterator] = 0;
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
                combineFour(yRow, fourLine, grid[xStart][yRow]);
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
                int origElement = grid[xCol][yStart];
                combineFour(xCol, fourLine, origElement);
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
                    this.points++;
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


    public void combineFour(int rowOrCol, boolean isLine, int origColor){
        if(isLine){
            System.out.println("Remove line");
            for (int xIterator = 0; xIterator < grid.length; xIterator++) {
                for (int toTop = rowOrCol; toTop >= 0; toTop--) {
                    runFourFive(origColor, xIterator, toTop);

                    if (toTop != 0) {
                        grid[xIterator][toTop] = grid[xIterator][toTop - 1];
                    } else {
                        grid[xIterator][toTop] = r.nextInt(numberOfColors);
                        this.points++;
                    }
                }
            }
        } else {
            System.out.println("Remove column");
            for (int yIterator = 0; yIterator < grid[1].length; yIterator++) {
                this.points++;
                grid[rowOrCol][yIterator] = r.nextInt(numberOfColors);

                runFourFive(origColor, rowOrCol, yIterator);
            }
        }
    }

    private void runFourFive(int origColor, int i, int toTop) {
        if(specialGrid[i][toTop] == 3){
            specialGrid[i][toTop] = 0;
            combineFour(i, false, origColor);
        } else if(specialGrid[i][toTop] == 4){
            specialGrid[i][toTop] = 0;
            combineFour(toTop, true, origColor);
        }else if(specialGrid[i][toTop] == 5){
            specialGrid[i][toTop] = 0;
            combineFive(origColor);
        }
    }


}
