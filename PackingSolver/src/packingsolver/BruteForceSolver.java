/*
A class to position a set of rectangles in a certain space such that they do not overlap and that they consume the least
amount of space using a brute force approach

@author Pim van Leeuwen
 */

class BruteForceSolver implements AlgorithmInterface {

    /*
    The variables needed to solve the problem
     */
    private PackingSettings settings;

    /*
    The rectangles in the grid
     */
    private Rectangle[] rectangles;

    /*
    the maximum height that we can archieve when we stack everything
     */
    private int maxHeight;

    /*
    the maximum width that we can archieve when we stack everything
     */
    private int maxWidth;

    private int width;

    private int height;

    private PackingSolution solution;

    /*
    Gets the optimal solution for an array of rectangles
     */
    public PackingSolution solve(PackingProblem p) {
        setVariables(p);

        place(0);
        return solution;
    }

    public void place(int i) {
        Rectangle r = rectangles[i];

        for (int x = 0; (x + r.getWidth()) <= maxWidth; x++) {
            r.setX(x);
            for (int y = 0; (y + r.getHeight()) <= maxHeight; y++) {
                r.setY(y);
                if(isValid(r) && i < (rectangles.length - 1)) {
                    place(i+1);
                } else if (isValid(r)) {
                    setArea();

                    if (solution == null) {
                        solution = new PackingSolution(width, height, rectangles);
                    } else if ((width*height) < (solution.w*solution.h)) {
                        solution = new PackingSolution(width, height, rectangles);
                    }
                }
            }
        }
    }

    /*
    Gather all the required data
     */
    private void setVariables(PackingProblem p) {
        rectangles = p.getRectangles();
        settings = p.getSettings();

        maxHeight = 0;
        maxWidth = 0;

        for (Rectangle rectangle : rectangles) {
            maxWidth += rectangle.getWidth();
            maxHeight += rectangle.getHeight();
        }
    }

    /*
    Check if a rectangle overlaps another rectangle
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

    /*
    Returns true when two rectangles do not overlap each other
     */
    private boolean areClear(Rectangle r1, Rectangle r2) {

        /*
        If one rectangle is completely to the right of one another return true
         */
        if (r1.getX() > (r2.getX() + r2.getWidth()) || r2.getX() > (r1.getX() + r1.getWidth())) {
            return true;
        }

        /*
        If one rectangle is completely below the other return true
         */
        if ((r1.getY() + r1.getHeight()) < r2.getY() || (r2.getY() + r2.getHeight()) < r1.getY()) {
            return true;
        }

        return false;
    }
}