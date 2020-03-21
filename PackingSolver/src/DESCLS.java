
import java.util.Arrays;
import java.util.Comparator;

/**
 *
 * @author Steven van den Broek
 */
public class DESCLS implements MaxRectsSortingSubroutine {
    @Override
    public void sortRectangles(Rectangle[] rs){
        // sort DESCLS: sort by longer side first, followed by sorter side
        Arrays.sort(rs, Comparator.comparing(Rectangle::getLongerSide)
                                  .thenComparing(Rectangle::getShorterSide)
                                  .reversed());
    }
}
