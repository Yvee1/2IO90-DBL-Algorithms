
import java.util.Arrays;
import java.util.Comparator;

/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */

/**
 *
 * @author 20182300
 */
public class BSSF_DESCSS_Solver extends MaxRectsSolver implements AlgorithmInterface {
    @Override
    void sortRectangles(){
        // sort DESCSS: sort by shorter side first, followed by longest side
        Arrays.sort(rs, Comparator.comparing(Rectangle::getShorterSide)
                                  .thenComparing(Rectangle::getLongerSide)
                                  .reversed());
    }
    
    @Override
    BestFitResult findBestSpace(Rectangle r){
        boolean fit = false;
        boolean shouldBeRotated = false;
        int bestFit = -1;
        int shortestLeftover = Integer.MAX_VALUE;

        for (int i = 0; i < emptySpaces.size(); i++){
            Rectangle space = emptySpaces.get(i);
            FitResult result = fitsInto(r, space);
            if (result.fits){
                fit = true;
                int leftover;
                if (rotationsAllowed){
                    leftover = Math.min( space.getWidth() - r.getWidth(),
                               Math.min( space.getHeight() - r.getHeight(),
                               Math.min( space.getWidth() - r.getHeight()
                                       , space.getHeight() - r.getWidth())) );
                } else {
                    leftover = Math.min( space.getWidth() - r.getWidth()
                                       , space.getHeight() - r.getHeight() );
                }
                if (bestFit == -1 || leftover < shortestLeftover){
                    bestFit = i;
                    shouldBeRotated = result.shouldBeRotated;
                    shortestLeftover = leftover;
                }
            }
        }
        
        return new BestFitResult(fit, shouldBeRotated, bestFit);
    }
}
