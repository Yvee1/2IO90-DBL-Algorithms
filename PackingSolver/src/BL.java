
import java.util.ArrayList;

/**
 *
 * @author Steven van den Broek
 */
public class BL implements MaxRectsHeuristicSubroutine {
    @Override
    public BestFitResult findBestSpace(Rectangle r, ArrayList<Rectangle> emptySpaces, boolean rotationsAllowed){
        boolean fit = false;
        boolean shouldBeRotated = false;
        int bestFit = -1;
        int smallestX = Integer.MAX_VALUE;
        // smallest vertical reach
        int smallestVR = Integer.MAX_VALUE;

        for (int i = 0; i < emptySpaces.size(); i++){
            Rectangle space = emptySpaces.get(i);
            if(MaxRectsSolver.fitsInto(r, space)){
                fit = true;
                
                if (bestFit == -1 || r.getVerticalReach() < smallestVR || (r.getVerticalReach() == smallestVR && r.getX() < smallestX)){
                    bestFit = i;
                    shouldBeRotated = false;
                    smallestVR = r.getVerticalReach();
                    smallestX = r.getX();
                }
            }
            
            if (rotationsAllowed){
                r.rotate();
                if(MaxRectsSolver.fitsInto(r, space)){
                    fit = true;

                    if (bestFit == -1 || r.getVerticalReach() < smallestVR || (r.getVerticalReach() == smallestVR && r.getX() < smallestX)){
                        bestFit = i;
                        shouldBeRotated = true;
                        smallestVR = r.getVerticalReach();
                        smallestX = r.getX();
                    }
                }
                r.rotate();
            }
        }
        
        return new BestFitResult(fit, shouldBeRotated, bestFit);
    }
}
