/**
 * Sort in the reverse order of the supplied RectangleSorter.
 */
public class ReverseSorter extends RectangleSorter {

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