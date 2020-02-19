/**
 * The class FFDH implements the First-Fit Decreasing-Height algorithm.
 */
public class FFDH extends FDH {

    /**
     * Find the first level where r fits.
     *
     * @param r The rectangle to use.
     * @return A Level l where r fits. If r does not fit in any level, null is returned.
     */
    protected Level findFit(Rectangle r, int maxHeight) {

        /* Test all levels to see if r fits. */
        for (Level l : levels) {
            if (l.fits(r, maxHeight)) {
                return l;
            }
        }

        return null;
    }
}