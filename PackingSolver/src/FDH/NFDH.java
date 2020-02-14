/**
 * The class NFDH implements the Next-Fit Decreasing-Height algorithm.
 */
public class NFDH extends FDH {

    protected Level findFit(Rectangle r, int maxHeight) {
        /* NFDH only tests the last level. */
        return getLastLevel().fits(r, maxHeight) ? getLastLevel() : null;
    }
}