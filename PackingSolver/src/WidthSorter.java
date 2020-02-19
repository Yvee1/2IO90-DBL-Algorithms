/**
 * Comparator that sorts by nondecreasing width.
 */
public class WidthSorter extends RectangleSorter {

    @Override
    public int compare(Rectangle a, Rectangle b) {
        return Integer.compare(a.getWidth(), b.getWidth());
    }
}