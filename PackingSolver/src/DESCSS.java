import java.util.Arrays;
import java.util.Comparator;

/**
 *
 * @author Steven van den Broek
 */
public class DESCSS implements MaxRectsSortingSubroutine {
    @Override
    public void sortRectangles(Rectangle[] rs){
        // sort DESCSS: sort by shorter side first, followed by longest side
        Arrays.sort(rs, Comparator.comparing(Rectangle::getShorterSide)
                                  .thenComparing(Rectangle::getLongerSide)
                                  .reversed());
    }
}
