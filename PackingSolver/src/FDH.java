import java.util.Arrays;
import java.util.ArrayList;

/**
 * FDH represents an (...)-Fit Decreasing Height algorithm.
 */
public abstract class FDH implements AlgorithmInterface {

    protected ArrayList<Level> levels;

    public PackingSolution solve(PackingProblem p) {
        //TODO: find a way to preserve the original order of rectangles.

        /* Sort the rectangles by non-increasing width. */
        Arrays.sort(p.getRectangles(),
                new ReverseSorter(new WidthSorter()));

        /* Create the object where the 'levels' are kept track of. */
        levels = new ArrayList<>();

        /* Add the first level for the first rectangle. */
        levels.add(new Level(p.getRectangles()[0].getWidth(), 0));

        /* Fit all rectangles. */
        for (Rectangle r: p.getRectangles()) {

            Level l = findFit(r, p.settings.maxHeight);

            /* If no level fits r, create a new one and add r to it. */
            if (l == null) {
                Level newLevel = new Level(r.getWidth(), getLastLevel().endPos);
                newLevel.add(r);
                levels.add(newLevel);
            } else {
                l.add(r);
            }
        }

        return new PackingSolution(p, getLastLevel().endPos, p.settings.maxHeight);
    }

    /**
     * Find the level where r is to be fitted
     * @param r The rectangle that needs to be fitted
     * @param maxHeight The maximum height of any level
     * @return A level l where l.fits(r, maxHeight). If there is no level for r, null is returned.
     */
    abstract Level findFit(Rectangle r, int maxHeight);

    /**
     * Return the last level.
     * @return the Level l that is last in levels.
     */
    protected Level getLastLevel() { return levels.get(levels.size() - 1); }

}