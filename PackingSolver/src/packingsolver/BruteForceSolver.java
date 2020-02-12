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
     * Gets the optimal solution for an array of rectangles
     */
    public PackingSolution solve(PackingProblem p) {
        setVariables(p);

        return solution;
    }


    /**
     * Gather all the required data
     */
    private void setVariables(PackingProblem p) {
        rectangles = p.getRectangles();
        settings = p.getSettings();

        sortArray();

    }

    /**
     * Simple algorithm to sort an array based on the height of the objects. (HEAPSORT)
     */
    private void sortArray() {
        int size = rectangles.length();

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
            int temp = rectangles[0];
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
            int temp = rectangles[i];
            rectangles[i] = rectangles[largest];
            rectangles[largest] = temp;

            heapify(rectangles, heapSize, largest);
        }
    }










    /**
     *Check if a rectangle overlaps another rectangle
     */
    private boolean isValid(Rectangle r) {



        for (Rectangle rectangle : rectangles) {
            if (!areClear(r, rectangle)) {
                return false;
            }
        }
        return true;
    }

    private void setArea() {
        int h = 0;
        int w = 0;

        for (Rectangle r : rectangles) {
            h += r.getHeight();
            w += r.getWidth();
        }

        height = h;
        width = w;
    }

    /**
     *Returns true when two rectangles do not overlap each other
     */
    private boolean areClear(Rectangle r1, Rectangle r2) {

        /**
         *If one rectangle is completely to the right of one another return true
         */
        if (r1.getX() > (r2.getX() + r2.getWidth()) || r2.getX() > (r1.getX() + r1.getWidth())) {
            return true;
        }

        /**
         *If one rectangle is completely below the other return true
         */
        if ((r1.getY() + r1.getHeight()) < r2.getY() || (r2.getY() + r2.getHeight()) < r1.getY()) {
            return true;
        }

        return false;
    }
}