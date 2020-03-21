
import java.util.ArrayList;

/**
 *
 * @author Steven van den Broek
 */
interface MaxRectsHeuristicSubroutine {
    public BestFitResult findBestSpace(Rectangle r, ArrayList<Rectangle> emptySpaces, boolean rotationsAllowed);
}
