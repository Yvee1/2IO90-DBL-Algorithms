import java.util.Comparator;

/**
 * This class represents an abstract sorting method for rectangles.
 */
public abstract class RectangleSorter implements Comparator<Rectangle> {

    public abstract int compare(Rectangle a, Rectangle b);
}