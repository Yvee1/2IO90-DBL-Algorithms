
/**
 * Sort rectangles on a first attribute, and if those are equal, on a second attribute.
 */
public class LexicographicSorter extends RectangleSorter {

    protected RectangleSorter first;
    protected RectangleSorter second;

    public LexicographicSorter(RectangleSorter a, RectangleSorter b) {
        this.first = a; this.second = b;
    }

    @Override
    public int compare(Rectangle a, Rectangle b) {

        int res = first.compare(a, b);

        /* If a and b are equal by first, sort using second. */
        if (res == 0) {
            return second.compare(a, b);
        } else {
            return res;
        }

    }

}
