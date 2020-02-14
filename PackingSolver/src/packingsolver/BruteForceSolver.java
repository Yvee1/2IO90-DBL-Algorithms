package packingsolver;
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
        setVariables(p);
        
        fitRectangles();

        for (int i = 0; i < maxWidth; i++) {
            if (solutionArray[i][0] != 1) {
                finalWidth = i + 1;
                break;
            }
        }
        
        copyInto(rectangles, rect);
        solution = new PackingSolution(p, finalWidth, containerHeight);

        for (int w = finalWidth; w >= maxRectangleWidth; w--) {
            maxWidth = w;
            while(solution.width*solution.height > w*containerHeight) {
                if (!doRectanglesFit(w, containerHeight)) {
                    containerHeight++;
                } else {
                    copyInto(rectangles, rect);
                    solution = new PackingSolution(p, w, containerHeight);
                }
            }           
        }
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
        
        return recursePlace(0);
    }
    
    private boolean recursePlace(int i) {
        if (i == rectangles.length) {
            return true;
        }
        
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
         * solution has the max height
         */
        containerHeight = rectangles[0].getHeight();

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
        
        
        for (int i = x; i < x + w; i++) {
            for (int j = y; j < y + h; j++) {
                if (solutionArray[i][j] == 1) {
                    return false;
                }
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
        for (int i =0; i < r1.length; i++) {
            r2[i] = new Rectangle(r1[i]);
        }
    }

}