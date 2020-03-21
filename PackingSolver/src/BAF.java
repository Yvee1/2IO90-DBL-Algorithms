
import java.util.ArrayList;

/**
 *
 * @author Steven van den Broek
 */
public class BAF implements MaxRectsHeuristicSubroutine {
    @Override
    public BestFitResult findBestSpace(Rectangle r, ArrayList<Rectangle> emptySpaces, boolean rotationsAllowed){
        boolean fit = false;
        boolean shouldBeRotated = false;
        int bestFit = -1;
        int minimalWastedArea = Integer.MAX_VALUE;

        for (int i = 0; i < emptySpaces.size(); i++){
            Rectangle space = emptySpaces.get(i);
            if(MaxRectsSolver.fitsInto(r, space)){
                fit = true;
                int wastedArea = space.getArea() - r.getArea();
                
                if (bestFit == -1 || wastedArea < minimalWastedArea){
                    bestFit = i;
                    shouldBeRotated = false;
                    minimalWastedArea = wastedArea;
                }
            }
            
            if (rotationsAllowed){
                r.rotate();
                if(MaxRectsSolver.fitsInto(r, space)){
                    fit = true;
                    int wastedArea = space.getArea() - r.getArea();
                    
                    if (bestFit == -1 || wastedArea < minimalWastedArea){
                        bestFit = i;
                        shouldBeRotated = true;
                        minimalWastedArea = wastedArea;
                    }
                }
                r.rotate();
            }
        }
        
        return new BestFitResult(fit, shouldBeRotated, bestFit);
    }
}
