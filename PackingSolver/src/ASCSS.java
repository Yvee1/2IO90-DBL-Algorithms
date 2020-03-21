
import java.util.Arrays;
import java.util.Comparator;

/**
 *
 * @author Steven van den Broek
 */
public class ASCSS implements MaxRectsSortingSubroutine {
    @Override
    public void sortRectangles(Rectangle[] rs){
        // sort ASCSS: sort by shorter side first, followed by longest side
        Arrays.sort(rs, Comparator.comparing(Rectangle::getShorterSide)
                                  .thenComparing(Rectangle::getLongerSide));
    }
}
