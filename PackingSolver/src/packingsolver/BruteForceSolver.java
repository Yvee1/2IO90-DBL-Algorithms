package packingsolver;
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
        sortArray();

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
            
            System.out.println(posY);
            
            posY = containerHeight - rectangles[i].getHeight();

            /**
             * Check if we can place it lower
             */
            boolean tempB = true;
            while (tempB) {
                if (posY > 0) {
                    if (solutionArray[posX][posY - 1] == 0) {
                        posY--;
                    } else {
                        tempB = false;
                    }
                }
            }
            System.out.println(i);
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



    /**
     * Simple algorithm to sort an array based on the height of the objects. (HEAPSORT)
     */
    private void sortArray() {
        int size = rectangles.length;

        /**
         * build a heap
         */
        for (int i = size/2; i >= 0; i--) {
            heapify(size, i);
        }

        /**
         * and then we can recursively keep getting out the max element
         */
        for (int i = size - 1; i >=0; i--) {
            /**
             * the root is the largest height
             */
            Rectangle temp = rectangles[0];
            rectangles[0] = rectangles[i];
            rectangles[i] = temp;

            /**
             * and we will heap the remaining again
             */
            heapify(i, 0);
        }
    }

    /**
     * heapify, so we sort the elements according to a max-heap
     */
    private void heapify(int heapSize, int i) {
        int largest = i;
        int leftChild = 2*i + 1;
        int rightChild = 2*i + 2;

        /**
         * if right is larger move it down
         */
        if (rightChild < heapSize && rectangles[rightChild].getHeight() > rectangles[largest].getHeight()) {
            largest = rightChild;
        }

        /**
         * if left is larger move it down
         */
        if (leftChild < heapSize && rectangles[leftChild].getHeight() > rectangles[largest].getHeight()) {
            largest = leftChild;
        }

        /**
         * If the root is then not the largest element we swap and recurse
         */
        if (largest != i) {
            Rectangle temp = rectangles[i];
            rectangles[i] = rectangles[largest];
            rectangles[largest] = temp;

            heapify(heapSize, largest);
        }
    }
}