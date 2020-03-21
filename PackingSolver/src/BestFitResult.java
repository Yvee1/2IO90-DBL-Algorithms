/**
 *
 * @author Steven van den Broek
 */
public class BestFitResult {
    boolean fits;
    boolean shouldBeRotated;
    int bestFit;

    BestFitResult(boolean fits, boolean sbr, int bf){
        shouldBeRotated = sbr;
        this.fits = fits;
        bestFit = bf;
    }
}