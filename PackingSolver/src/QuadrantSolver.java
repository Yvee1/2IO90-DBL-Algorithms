
import java.util.Arrays;
/**
 A class to position a set of rectangles in a certain space such that they do not overlap and that they consume the least
 amount of space using a brute force approach

 @author Pim van Leeuwen and Tim van Ham
 */

class QuadrantSolver implements AlgorithmInterface {

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
     * The maximum width of a cube
     */
    private int maxRectangleWidth;
    
    /**
     * The max possible height of the solution container
     */
    private int maxHeight;
    
    /**
     * The area of all rectangles combined
     */
    private int area;
    
    private int width;

    /**
     * Gets the optimal solution for an array of rectangles
     */
    @Override
    public PackingSolution solve(PackingProblem p) {
        
        setVariables(p);
       



        return solution;
    }

    /**
     * Gather all the required data
     */
    private void setVariables(PackingProblem p) {
        rect = p.getRectangles();
        rectangles = new Rectangle[rect.length];
        
        copyInto(rect, rectangles);
        
        settings = p.getSettings();
        
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
         * max height and area
         */
        maxHeight = 0;
        area = 0;
        for (Rectangle r : rectangles) {
            area += r.getHeight()*r.getWidth();
            maxHeight += r.getHeight();
        }
        
        width = Math.max((int)Math.ceil(Math.sqrt(area/0.95)), 
                maxRectangleWidth);

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