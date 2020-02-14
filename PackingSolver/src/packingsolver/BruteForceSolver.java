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
     * the maximum width
     */
    private int maxWidth;

    /**
     * the final width
     */
    private int finalWidth;
    
    /**
     * the final height
     */
    private int finalHeight;

    /**
     * Gets the optimal solution for an array of rectangles
     */
    @Override
    public PackingSolution solve(PackingProblem p) {
        System.out.println("started");
        setVariables(p);

        fitRectangles();

        for (int i = 0; i < maxWidth; i++) {
            if (solutionArray[i][0] != 1) {
                finalWidth = i + 1;
                break;
            }
        }

        solution = new PackingSolution(finalWidth, containerHeight, rectangles);

        return solution;
    }


    /**
     * Gather all the required data
     */
    private void setVariables(PackingProblem p) {
        rectangles = p.getRectangles();
        settings = p.getSettings();
        maxWidth = 0;

        /**
         * set the maximum possible width
         */
        for (Rectangle r : rectangles) {
            maxWidth += r.getWidth();
        }

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
            while ((solutionArray[posX][posY] != 0) &&
                    (solutionArray[posX][containerHeight - rectangles[i].getHeight()] != 1)) {
                posX++;
            }
            
            posY = containerHeight - rectangles[i].getHeight();

            /**
             * Check if we can place it lower
             */
            boolean tempB = true;
            while (tempB) {
                System.out.println(posY);
                if (posY > 0) {
                    if (solutionArray[posX][posY - 1] == 0) {
                        posY--;
                    } 
                } else {
                    tempB = false;
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
            for (int j = y; j < j + height; j++) {
                solutionArray[i][j] = 1;
            }
        }
    }

}