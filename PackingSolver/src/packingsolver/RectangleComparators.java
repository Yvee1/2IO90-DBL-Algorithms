package packingsolver;

import java.util.Comparator;

/**
 * This class represents an abstract sorting method for rectangles.
 */
abstract class RectangleSorter implements Comparator<Rectangle> {

    public abstract int compare(Rectangle a, Rectangle b);
}

/**
 * Comparator that sorts by nondecreasing width.
 */
class WidthSorter extends RectangleSorter {

    @Override
    public int compare(Rectangle a, Rectangle b) {
        return Integer.compare(a.getWidth(), b.getWidth());
    }
}

class HeightSorter extends RectangleSorter {

    @Override
    public int compare(Rectangle a, Rectangle b) {
        return Integer.compare(a.getHeight(), b.getHeight());
    }
}

/**
 * Sort in the reverse order of the supplied RectangleSorter.
 */
class ReverseSorter extends RectangleSorter {

    private RectangleSorter sorter;

    public ReverseSorter(RectangleSorter sorter) {
        super();
        this.sorter = sorter;
    }

    @Override
    public int compare(Rectangle a, Rectangle b) {
        return -1 * sorter.compare(a, b);
    }
}