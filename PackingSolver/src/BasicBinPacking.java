
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 *
 * @author Steven van den Broek
 */

// adapted from: https://codeincomplete.com/posts/bin-packing/
// and inspiration from: https://github.com/TeamHypersomnia/rectpack2D#algorithm
public class BasicBinPacking implements AlgorithmInterface {
    // Reference to the array of rectangles that needs to be placed
    Rectangle[] rs;
    boolean rotationsAllowed;
    boolean fixed;
    Rectangle container;
    ArrayList<Rectangle> emptySpaces = new ArrayList<>();
    
    @Override
    public PackingSolution solve(PackingProblem pp){        
        rs = pp.getRectangles();
        rotationsAllowed = pp.getSettings().rotation;
        fixed = pp.getSettings().fixed;
        // sort in decreasing 'size'
        Arrays.sort(rs, Comparator.comparing(Rectangle::getLongerSide).reversed());
        
        // initial rectangle size
        if (fixed){
            container = new Rectangle(0, 0, rs[0].getWidth(), pp.getSettings().getMaxHeight());
        } else {
            // begin with container with size of 'biggest' rectangle
            container = new Rectangle(0, 0, rs[0].getWidth(), rs[0].getHeight());
        }
        emptySpaces.add(container);
        
        for (Rectangle r : rs){
//            System.out.println("-------------");
//                System.out.print("Rectangle: ");
//                System.out.println(r);
//            for (Rectangle space : emptySpaces){
//                System.out.println(space);
//            }
//            System.out.println();
//            
            boolean fit = false;
            // iterate backwards
            for (int i = emptySpaces.size() - 1; i >= 0; i--){
                if (fitsInto(r, emptySpaces.get(i))){
                    fit = true;
                    packAndSplit(r, i);
                    break;
                }
            }

            if (!fit){
                // rotate if it doesn't fit in container otherwise
                if (fixed && r.getHeight() > container.getHeight() && rotationsAllowed){
                    r.rotate();
                } else if (fixed && r.getWidth() > r.getHeight() && r.getWidth() <= container.getHeight() && rotationsAllowed){
                    // rotate the rectangle upright if it fits that way
                    r.rotate();
                }
                
                int rw = r.getWidth();
                int rh = r.getHeight();
                int cw = container.getWidth();
                int ch = container.getHeight();
                boolean canGrowUp = rw <= cw && !fixed;
                boolean canGrowRight = rh <= ch;
                
                boolean shouldGrowUp = canGrowUp && 
                        (cw >= (ch + rh));
                boolean shouldGrowRight = canGrowRight && 
                        (ch >= (cw + rw));
                
                if (shouldGrowUp){
                    emptySpaces.add(new Rectangle(container.getX()
                            , container.getVerticalReach(), cw, rh));
                    container.h += rh;
                } else if (shouldGrowRight){
                    emptySpaces.add(new Rectangle(container.getHorizontalReach()
                            , container.getY(), rw, ch));
                    container.w += rw;
                } else if (canGrowUp){
                    emptySpaces.add(new Rectangle(container.getX()
                            , container.getVerticalReach(), cw, rh));
                    container.h += rh;
                } else {
                    emptySpaces.add(new Rectangle(container.getHorizontalReach()
                            , container.getY(), rw, ch));
                    container.w += rw;
                }
                
//                System.out.format("cu: %b, cr: %b\n", canGrowUp, canGrowRight, shouldGrowUp, shouldGrowRight);
//                System.out.println("New space");
//                System.out.println(emptySpaces.get(emptySpaces.size()-1));
                packAndSplit(r, emptySpaces.size()-1);
            }
        }
        
        SteinbergSolver.shake(pp);
        return new PackingSolution(pp);
    }
    
    private boolean fitsInto(Rectangle r1, Rectangle r2){
        boolean fitNormally = r1.getWidth()  <= r2.getWidth()
                           && r1.getHeight() <= r2.getHeight();
        
        if (fitNormally){
            return true;
        }
        
        if (rotationsAllowed){
            boolean fitRotated = r1.getHeight()  <= r2.getWidth()
                               && r1.getWidth() <= r2.getHeight();

            if (fitRotated){
                r1.rotate();
                return true;
            }
        }
        return false;
    }
    
    private void packAndSplit(Rectangle r, int i){
        Rectangle space = emptySpaces.get(i);
        
//        System.out.print("Space: ");
//        System.out.println(space);
        
        // pack r
        r.setPos(space.getX(), space.getY());
        
        // split space
        Rectangle horizontalSpace = null;
        int horizontalSpaceRemaining = space.getWidth() - r.getWidth();
        if (horizontalSpaceRemaining > 0){
            horizontalSpace = new Rectangle(r.getHorizontalReach(), r.getY(),
                    horizontalSpaceRemaining, r.getHeight());
        }
        
        Rectangle verticalSpace = null;
        int verticalSpaceRemaining = space.getHeight() - r.getHeight();
        if (verticalSpaceRemaining > 0){
            verticalSpace = new Rectangle(r.getX(), r.getVerticalReach(),
                    space.getWidth(), verticalSpaceRemaining);
        }
        
        emptySpaces.remove(i);
        
        // Smallest 'size' at the end of arraylist
        if (horizontalSpace != null && verticalSpace != null){
            if (horizontalSpace.getLongerSide() > verticalSpace.getLongerSide()){
//                System.out.print("Vertical space: ");
//                System.out.println(verticalSpace);
                emptySpaces.add(verticalSpace);
//                System.out.print("Horizontal space: ");
//                System.out.println(horizontalSpace);
                emptySpaces.add(horizontalSpace);
                
            } else {
                emptySpaces.add(verticalSpace);
                emptySpaces.add(horizontalSpace);
            }
        } else if (horizontalSpace != null) {
//            System.out.print("Horizontal space: ");
//            System.out.println(horizontalSpace);
            emptySpaces.add(horizontalSpace);
        } else if (verticalSpace != null){
//            System.out.print("Vertical space: ");
//            System.out.println(verticalSpace);
            emptySpaces.add(verticalSpace);
        }
    }
}
