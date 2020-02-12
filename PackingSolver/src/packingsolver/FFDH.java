package packingsolver;

import java.util.Arrays;
import java.util.ArrayList;

/**
 * The class FFDH implements the First-Fit Decreasing Height algorithm.
 */
public class FFDH implements AlgorithmInterface {

    private ArrayList<Level> levels;

    public PackingSolution solve(PackingProblem p) {

        //TODO: find a way to preserve the original order of rectangles.

        /* Sort the rectangles by nondecreasing width. */
        Arrays.sort(p.getRectangles(), new SortByWidthReverse());

        /* Create the object where the 'levels' are kept track of. */
        levels = new ArrayList<>();

        /* Add the first level for the first rectangle. */
        levels.add(new Level(p.getRectangles()[0].getWidth(), 0));

        /* Fit all rectangles. */
        for (Rectangle r: p.getRectangles()) {

            Level l = firstFit(r, p.settings.maxHeight);

            /* If no level fits r, create a new one and add r to it. */
            if (l == null) {
                Level newLevel = new Level(r.getWidth(), getLastLevel().endPos);
                newLevel.add(r);
                levels.add(newLevel);
            } else {
                l.add(r);
            }
        }

        return new PackingSolution(getLastLevel().endPos, p.settings.maxHeight, p.getRectangles());
    }

    /**
     * Find the first level where r fits.
     * @param r The rectangle to use.
     * @return A Level l where r fits. If r does not fit in any level, null is returned.
     */
    private Level firstFit(Rectangle r, int maxHeight) {

        /* Test all levels to see if r fits. */
        for (Level l: levels) {
            if (l.fits(r, maxHeight)) {
                return l;
            }
        }

        return null;
    }

    private Level getLastLevel() { return levels.get(levels.size() - 1); }

}

class Level {
    int width;
    int height;
    int startPos;
    int endPos;

    public Level(int width, int startPos) {
        this.width = width;
        this.startPos = startPos;
        endPos = startPos + width;
    }

    /**
     * Determines whether r fits in the level.
     * @param r The rectangle to test for fitting.
     * @param maxHeight The maximum height of the level.
     * @return true <=> r fits in the level.
     */
    public boolean fits(Rectangle r, int maxHeight) {

        /*
            r fits if its width is at most the level's width,
            and the height with r does not exceed maxHeight
        */
        return (r.getWidth() <= this.width) && (this.height + r.getHeight() <= maxHeight);

    }

    /**
     * Virtually stacks r on top of the other rectangles on the level.
     * Also update the location of r.
     * @param r The rectangle to add.
     */
    public void add(Rectangle r) {

        r.setPos(startPos, height);

        this.height += r.getHeight();
    }
}
