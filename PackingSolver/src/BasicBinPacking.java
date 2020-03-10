
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
    Rectangle container;
    ArrayList<Rectangle> emptySpaces = new ArrayList();
    
    @Override
    public PackingSolution solve(PackingProblem pp){
        if (pp.getSettings().fixed){
            throw new IllegalArgumentException("BasicPinPacking: not for strip packing");
        }
        
        rs = pp.getRectangles();
        rotationsAllowed = pp.getSettings().rotation;
        // sort in decreasing 'size'
        Arrays.sort(rs, Comparator.comparing(Rectangle::getMaxSide).reversed());
        
        // initial rectangle size
        container = new Rectangle(0, 0, rs[0].getWidth(), rs[0].getHeight());
        emptySpaces.add(container);
        
        for (Rectangle r : rs){
            System.out.println("-------------");
                System.out.print("Rectangle: ");
                System.out.println(r);
            for (Rectangle space : emptySpaces){
                System.out.println(space);
            }
            System.out.println();
            
            boolean fit = false;
            // iterate backwards
            for (int i = emptySpaces.size() - 1; i >= 0; i--){
                if (fitsInto(r, emptySpaces.get(i))){
                    fit = true;
                    packAndSplit(r, i);
                }
            }

            if (!fit){
                int rw = r.getWidth();
                int rh = r.getHeight();
                int cw = container.getWidth();
                int ch = container.getHeight();
                boolean canGrowUp = rw <= cw;
                boolean canGrowRight = rh <= ch;
                
                boolean growUp = canGrowUp;
                
//                boolean shouldGrowUp = canGrowUp && 
//                        (cw >= (ch + rh));
//                boolean shouldGrowRight = canGrowRight && 
//                        (ch >= (cw + rw));
                
                if (canGrowRight && canGrowUp){
                    int areaUp = (ch + rh) * cw;
                    int areaRight = (cw + rw) * ch;
                    
                    if (!rotationsAllowed){
                        growUp = areaUp < areaRight;
                    } else {
                        int areaUpR = (ch + rw) * cw;
                        int areaRightR = (cw + rh) * ch;
                        int minimal = Math.min(areaUp, Math.min(areaRight, Math.min(areaUpR, areaRightR)));
                        
                        if (areaUp == minimal){
                            growUp = true;
                        } else if (areaUpR == minimal){
                            r.rotate();
                            growUp = true;
                        } else if (areaRight == minimal){
                            growUp = false;
                        } else {
                            r.rotate();
                            growUp = false;
                        }
                    }
                    
                } 
                if (growUp){
                    emptySpaces.add(new Rectangle(container.getX()
                            , container.getVerticalReach(), cw, rh));
                    container.h += rh;
                } else {
                    emptySpaces.add(new Rectangle(container.getHorizontalReach()
                            , container.getY(), rw, ch));
                    container.w += rw;
                }
                
                System.out.format("cu: %b, cr: %b\n", canGrowUp, canGrowRight);
                System.out.println("New space");
                System.out.println(emptySpaces.get(emptySpaces.size()-1));
                packAndSplit(r, emptySpaces.size()-1);
            }
        }
        
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
                    r.getWidth(), verticalSpaceRemaining);
        }
        
        emptySpaces.remove(i);
        
        // Smallest 'size' at the end of arraylist
        if (horizontalSpace != null && verticalSpace != null){
            if (horizontalSpace.getMaxSide() > verticalSpace.getMaxSide()){
                emptySpaces.add(horizontalSpace);
                emptySpaces.add(verticalSpace);
            } else {
                emptySpaces.add(verticalSpace);
                emptySpaces.add(horizontalSpace);
            }
        } else if (horizontalSpace != null) {
            emptySpaces.add(horizontalSpace);
        } else if (verticalSpace != null){
            emptySpaces.add(verticalSpace);
        }
    }
}
