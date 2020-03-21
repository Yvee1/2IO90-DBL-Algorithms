
import java.util.ArrayList;

/**
 *
 * @author Steven van den Broek
 */
public class BLSF implements MaxRectsHeuristicSubroutine {
    @Override
    public BestFitResult findBestSpace(Rectangle r, ArrayList<Rectangle> emptySpaces, boolean rotationsAllowed){
        boolean fit = false;
        boolean shouldBeRotated = false;
        int bestFit = -1;
        int shortestLeftover = Integer.MAX_VALUE;

        for (int i = 0; i < emptySpaces.size(); i++){
            Rectangle space = emptySpaces.get(i);
            if(MaxRectsSolver.fitsInto(r, space)){
                fit = true;
                int leftover = Math.max( space.getWidth()  - r.getWidth()
                                       , space.getHeight() - r.getHeight() );
                
                if (bestFit == -1 || leftover < shortestLeftover){
                    bestFit = i;
                    shouldBeRotated = false;
                    shortestLeftover = leftover;
                }
            }
            
            if (rotationsAllowed){
                r.rotate();
                if(MaxRectsSolver.fitsInto(r, space)){
                    fit = true;
                    int leftover = Math.max( space.getWidth()  - r.getWidth()
                                           , space.getHeight() - r.getHeight() );
                    
                    if (bestFit == -1 || leftover < shortestLeftover){
                        bestFit = i;
                        shouldBeRotated = true;
                        shortestLeftover = leftover;
                    }
                }
                r.rotate();
            }
        }
        
        return new BestFitResult(fit, shouldBeRotated, bestFit);
    }
}
