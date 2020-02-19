public class HeightSorter extends RectangleSorter {

    @Override
    public int compare(Rectangle a, Rectangle b) {
        return Integer.compare(a.getHeight(), b.getHeight());
    }
}