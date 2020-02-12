package packingsolver;

import java.util.Comparator;

class SortByWidth implements Comparator<Rectangle> {

    /* */
    public int compare(Rectangle a, Rectangle b) {
        return Integer.compare(a.getWidth(), b.getWidth());
    }
}

class SortByWidthReverse extends SortByWidth {

    @Override
    public int compare(Rectangle a, Rectangle b) {
        return -1 * super.compare(a, b);
    }
}