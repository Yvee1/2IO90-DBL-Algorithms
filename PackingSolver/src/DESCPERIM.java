import java.util.Arrays;
import java.util.Comparator;

/**
 *
 * @author Steven van den Broek
 */
public class DESCPERIM implements MaxRectsSortingSubroutine {
    @Override
    public void sortRectangles(Rectangle[] rs){
        // sort DESCA: sort by area
        Arrays.sort(rs, Comparator.comparing(Rectangle::getPerimeter)
                                  .reversed());
    }
}