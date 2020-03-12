
import java.util.Arrays;
/**
 A class to position a set of rectangles in a certain space such that they do not overlap and that they consume the least
 amount of space using a brute force approach

 @author Pim van Leeuwen and Tim van Ham
 */

class BruteForceSolver implements AlgorithmInterface {

    /**
     *The variables needed to solve the problem
     */
    private PackingSettings settings;

    /**
     *The rectangles in the grid
     */
    private Rectangle[] rect;
    
    /**
     * A copy of the rectangles to work in
     */
    private Rectangle[] rectangles;

    /**
     * The end result
     */
    private PackingSolution solution;

    /**
     * The height of the solution
     */
    private int containerHeight;

    /**
     * Array containing the solution
     */
    private int[][] solutionArray;
    
    /**
     * Array storing the bins for the wasted space computation
     */
    private int[] verBinArray;
    private int[] horBinArray;
    
    /**
     * Vectors used for the computation of wasted space
     */
    private int[] verBinVector;
    private int[] verRecVector;
    private int[] horBinVector;
    private int[] horRecVector;
    
    /**
     * Max rectangle width
     */
    private int maxRectangleWidth;
    
    /**
     * the maximum width
     */
    private int maxWidth;

    /**
     * the final width
     */
    private int finalWidth;

    /**
     * Gets the optimal solution for an array of rectangles
     */
    @Override
    public PackingSolution solve(PackingProblem p) {
        boolean done = false;
        
        setVariables(p);
        
        fitRectangles();

        for (int i = (maxWidth - 1); i >= 0; i--) {
            if (done) {
                break;
            }
            
            for (int j = 0; j < containerHeight; j++) {
                if (solutionArray[i][j] == 1) {
                    finalWidth = i + 1;
                    done = true;
                    break;
                }
            }
        }
        copyInto(rectangles, rect);
        solution = new PackingSolution(p, finalWidth, containerHeight);

        /**
         * Two different loops depending on whether or not this is fixed
        */ 
        for (int w = finalWidth; w >= maxRectangleWidth; w--) {
            maxWidth = w;
            while(solution.width*solution.height > w*containerHeight) {
                if (doRectanglesFit(w, containerHeight)) {
                    copyInto(rectangles, rect);
                    solution = new PackingSolution(p, w, containerHeight);
                } else {
                    if  (settings.getFixed()) {
                        break;
                    } else {
                        containerHeight++;
                    }
                }
            }           
        }
        System.out.println(solution.area());
        return solution;
    }

    /**
     * Check whether our rectangles fit inside the given space
     * @param w width of the container
     * @param h height of the container
     * @return whether our rectangles fit inside the given space
     */
    private boolean doRectanglesFit(int w, int h) {
        /**
         * Set the container appropriately
         */
        solutionArray = new int[w][h];
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                solutionArray[x][y] = 0;
            }
        }   
        
        boolean result = recursePlace(0);
       
        return result;
    }
    
    private boolean recursePlace(int i) {
        //System.out.println(i);
        if (i == rectangles.length) {            
            return true;
        }
        int totalArea = solutionArray.length * solutionArray[0].length;
        verBinArray = new int[(int) Math.ceil(totalArea / 2.0)];
        horBinArray = new int[(int) Math.ceil(totalArea / 2.0)];
        /**
         * Create vertical bins
         */
        int b = 0;
        int binSize = 0;
        boolean bin = false;
        //System.out.println(solutionArray[0][0]);
        for (int x = 0; x < solutionArray.length; x++) {        
            for (int y = 0; y < solutionArray[0].length; y++) {
                if (!bin && solutionArray[x][y] == 0) {
                    bin = true;
                    binSize++;
                    //System.out.println("A");                    
                }
                else if (bin && solutionArray[x][y] == 0) {                    
                    binSize++;
                    //System.out.println("B");
                }    
                else if (bin && solutionArray[x][y] == 1) {
                    verBinArray[b] = binSize;
                    bin = false;
                    binSize = 0;
                    b++;
                    //System.out.println("C");
                }
                //else {System.out.println("D");}
            }
            if (bin) {
                verBinArray[b] = binSize;
                bin = false;
                binSize = 0;
                b++;
            }
        }
        //System.out.println(Arrays.toString(verBinArray));
        /**
         * Construct the bin vector
         */
        verBinVector = new int[solutionArray[0].length];
        int binSum = 0;
        for (int k = 0; k < verBinArray.length; k++) {
            if (verBinArray[k] != 0) {
                verBinVector[verBinArray[k] - 1] = verBinVector[verBinArray[k] - 1] + verBinArray[k];
                binSum = binSum + verBinArray[k];
            }            
        }
        /**
         * Construct the element vector
         */        
        verRecVector = new int[solutionArray[0].length];
        int recSum = 0;
        for (int l = i; l < rectangles.length; l++) {
            verRecVector[rectangles[l].getHeight() - 1] = verRecVector[rectangles[l].getHeight() - 1] + rectangles[l].getArea();
            recSum = recSum + rectangles[l].getArea();
        }
        //System.out.println(recSum);
        //System.out.println(binSum);
        /**
         * Create horizontal bins
         */
        b = 0;
        binSize = 0;
        bin = false;
        for (int y = 0; y < solutionArray[0].length; y++) {        
            for (int x = 0; x < solutionArray.length; x++) {
                if (!bin && solutionArray[x][y] == 0) {
                    bin = true;
                    binSize++;                    
                }
                else if (bin && solutionArray[x][y] == 0) {                    
                    binSize++;
                }    
                else if (bin && solutionArray[x][y] == 1) {
                    horBinArray[b] = binSize;
                    bin = false;
                    binSize = 0;
                    b++;
                }
            }
            if (bin) {
                horBinArray[b] = binSize;
                bin = false;
                binSize = 0;
                b++;
            }
        }
        /**
         * Construct the bin vector
         */
        horBinVector = new int[solutionArray.length];
        binSum = 0;
        for (int k = 0; k < horBinArray.length; k++) {
            if (horBinArray[k] != 0) {
                horBinVector[horBinArray[k] - 1] = horBinVector[horBinArray[k] - 1] + horBinArray[k];
                binSum = binSum + horBinArray[k];
            }            
        }
        /**
         * Construct the element vector
         */        
        horRecVector = new int[solutionArray.length];
        recSum = 0;
        for (int l = i; l < rectangles.length; l++) {
            horRecVector[rectangles[l].getWidth() - 1] = horRecVector[rectangles[l].getWidth() - 1] + rectangles[l].getArea();
            recSum = recSum + rectangles[l].getArea();
        }
        //System.out.println(recSum);
        //System.out.println(binSum);
        /**
         * Compute a lower bound on the wasted space and investigate whether the subproblem is solvable
         */
        int ws1 = wastedSpace(verBinVector, verRecVector);
        int ws2 = wastedSpace(horBinVector, horRecVector);
        int ws = Math.max(ws1, ws2);
        if (recSum + ws > binSum) {
            //System.out.println("Pruning...");
            return false;
        }
        
        //System.out.println(Arrays.toString(verBinArray));
        //System.out.println(Arrays.toString(verBinVector));
        //System.out.println(Arrays.toString(verRecVector));
                
        if (i == 0) {
            for (int x = 0; x < Math.ceil(solutionArray.length / 2.0); x++) {
                for (int y = 0; y < Math.ceil(solutionArray[0].length / 2.0); y++) {             
                    if (rectanglePossible(rectangles[i], x, y)) {
                        placeRectangle(rectangles[i], x, y);
                        if (recursePlace(i+1)) {
                            return true;
                        }
                        unplaceRectangle(rectangles[i], x, y);
                    }
                }
            } 
        } else {
            for (int x = 0; x < solutionArray.length; x++) {
                for (int y = 0; y < solutionArray[0].length; y++) {             
                    if (rectanglePossible(rectangles[i], x, y)) {
                        placeRectangle(rectangles[i], x, y);
                        if (recursePlace(i+1)) {
                            return true;
                        }
                        unplaceRectangle(rectangles[i], x, y);
                    }
                }
            } 
        }
        return false;
    }

    /**
     * Gather all the required data
     */
    private void setVariables(PackingProblem p) {
        rect = p.getRectangles();
        rectangles = new Rectangle[rect.length];
        
        copyInto(rect, rectangles);
        
        settings = p.getSettings();
        maxWidth = 0;

        /**
         * set the maximum possible width
         */
        for (Rectangle r : rectangles) {
            maxWidth += r.getWidth();
        }
        
        /**
         * First sort on the width of the rectangles for the max rectangle width
         */
        Arrays.sort(rectangles, new ReverseSorter(new WidthSorter()));
        
        maxRectangleWidth = rectangles[0].getWidth();

        /**
         * First we sort on the height of the rectangles
         */
        Arrays.sort(rectangles, new ReverseSorter(new HeightSorter()));
        

        /**
         * solution has the max height, only if no fixed height
         */
        if (settings.getFixed()) {
            containerHeight = settings.getMaxHeight();
        } else {
            containerHeight = rectangles[0].getHeight();
        }

        /**
         * Set the container appropriately
         */
        solutionArray = new int[maxWidth][containerHeight];
        
        for (int x = 0; x < maxWidth; x++) {
            for (int y = 0; y < containerHeight; y++) {
                solutionArray[x][y] = 0;
            }
        }       


    }

    /**
     * Fits all the rectangles in the box as small as possible
     */
    private void fitRectangles() {
        for (int i = 0; i < rectangles.length; i++) {
            int posX = 0;
            int posY = containerHeight - 1;

            /**
             * find the first suitable x
             */
            
            while (true) {
                if (solutionArray[posX][posY] != 0) {
                    posX++;
                } else if (rectanglePossible(rectangles[i], posX, 
                        (containerHeight - rectangles[i].getHeight()))) {
                    break;
                } else {
                    posX++;
                }       
            }
            
            posY = containerHeight - rectangles[i].getHeight();

            /**
             * Check if we can place it lower
             */
            while (true) {
                if (posY > 0) {
                    if (solutionArray[posX][posY - 1] == 0) {
                        posY--;
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            }
            
            placeRectangle(rectangles[i], posX, posY);
            

        }
    }

    /**
     * Sets the rectangle in the grid at a certain place
     */
    private void placeRectangle(Rectangle r, int x, int y) {                      
        int height = r.getHeight();
        int width = r.getWidth();
        r.setX(x);
        r.setY(y);

        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {
                solutionArray[i][j] = 1;
            }
        }      
        
    }
    
    /**
     * unsets the rectangle in the grid at a certain place
     */
    private void unplaceRectangle(Rectangle r, int x, int y) {                      
        int height = r.getHeight();
        int width = r.getWidth();
        r.setX(0);
        r.setY(0);

        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {
                solutionArray[i][j] = 0;
            }
        }      
        
    }
    
    /**
     * check if we can place the rectangle
     * @param r the rectangle
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return 
     */
    private boolean rectanglePossible(Rectangle r, int x, int y) {
        int h = r.getHeight();
        int w = r.getWidth();
        
        if ((x + w > maxWidth) || (y + h > containerHeight)) {
            return false;
        }
        
        /*
        for (int i = x; i < x + w; i++) {
            for (int j = y; j < y + h; j++) {
                if (solutionArray[i][j] == 1) {
                    return false;
                }
            }
        }
        */
        
        /**
         * Check for collisions on the boundary
         * Placing rectangles in decreasing order of height ensures that no already placed rectangle is completely contained in the new one
         */
        
        for (int i = x; i < x + w; i++) {
            if (solutionArray[i][y] == 1 || solutionArray[i][y + h - 1] == 1) {
                return false;
            }
        }
        
        for (int j = y + 1; j < y + h - 1; j++) {
            if (solutionArray[x][j] == 1 || solutionArray[x + w - 1][j] == 1) {
                return false;
            }
        }
        
       
        return true;
    }
    
    /**
     * copies r1 into r2
     * @param r1
     * @param r2 
     */
    private void copyInto(Rectangle[] r1, Rectangle[] r2) {
        for (int i = 0; i < r1.length; i++) {
            r2[i] = new Rectangle(r1[i]);
        }
    }

    private int wastedSpace(int[] binVector, int[] areaVector) {
        int accWaste = 0;
        int carryoverArea = 0;
        for (int w = 0; w < binVector.length; w++) {
            if (binVector[w] > carryoverArea + areaVector[w]) {                
                accWaste = accWaste + binVector[w] - ( carryoverArea + areaVector[w] );
                carryoverArea = 0;                
            }
            else if (binVector[w] == carryoverArea + areaVector[w]) {
                carryoverArea = 0;
            }
            else if (binVector[w] < carryoverArea + areaVector[w]){
                carryoverArea = carryoverArea + areaVector[w] - binVector[w];
            }
        }        
        return accWaste;
    }

}