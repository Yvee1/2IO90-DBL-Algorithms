
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;


/**
 *
 * @author Steven van den Broek
 */
public class MaxRectsSolver implements AlgorithmInterface {
    // Set F of free rectangles
    ArrayList<Rectangle> emptySpaces = new ArrayList<>();
    boolean rotationsAllowed;
    Rectangle container;
    // Reference to the array of rectangles that needs to be placed
    Rectangle[] rs;
    
    boolean debug = false;
    
    @Override
    public PackingSolution solve(PackingProblem pp){
        // set the rectangles
        rs = pp.getRectangles();
        rotationsAllowed = pp.getSettings().rotation;
        
        // sort DESCSS, sort by shorter side first, followed by longest side
        Arrays.sort(rs, Comparator.comparing(Rectangle::getShorterSide)
                                  .thenComparing(Rectangle::getLongerSide)
                                  .reversed());
        
        // begin with container with size of 'biggest' rectangle
        container = new Rectangle(0, 0, rs[0].getWidth(), rs[0].getHeight());
        emptySpaces.add(container);

        for (Rectangle r : rs){
            if(debug){
                System.out.println("-------------");
                System.out.print("Rectangle: ");
                System.out.println(r);
                System.out.println("== Empty spaces ==");
                for (Rectangle space : emptySpaces){
                    System.out.println(space);
                }
                System.out.println();
            }
            boolean fit = false;
            boolean shouldBeRotated = false;
            int bestFit = -1;
            int shortestLeftover = Integer.MAX_VALUE;
                    
            for (int i = 0; i < emptySpaces.size(); i++){
                Rectangle space = emptySpaces.get(i);
                Result result = fitsInto(r, space);
                if (result.fits){
                    fit = true;
                    int leftover;
                    if (rotationsAllowed){
                        leftover = Math.min( space.getWidth() - r.getWidth(),
                                   Math.min( space.getHeight() - r.getHeight(),
                                   Math.min( space.getWidth() - r.getHeight()
                                           , space.getHeight() - r.getWidth())) );
                    } else {
                        leftover = Math.min( space.getWidth() - r.getWidth()
                                           , space.getHeight() - r.getHeight() );
                    }
                    if (bestFit == -1 || leftover < shortestLeftover){
                        bestFit = i;
                        shouldBeRotated = result.shouldBeRotated;
                        shortestLeftover = leftover;
                    }
                }
            }
            
            if (fit){
                if (shouldBeRotated){
                    r.rotate();
                }
                packAndSplit(r, bestFit);
            } else {
                int rw = r.getWidth();
                int rh = r.getHeight();
                int cw = container.getWidth();
                int ch = container.getHeight();
                boolean canGrowUp = rw <= cw;
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
                
                packAndSplit(r, emptySpaces.size()-1);
            }
        }
        
        return new PackingSolution(pp);
    }
    
    private Result fitsInto(Rectangle r1, Rectangle r2){
        boolean fits = false;
        boolean shouldBeRotated = false;
        
        fits = r1.getWidth()  <= r2.getWidth()
            && r1.getHeight() <= r2.getHeight();
        if (!fits && rotationsAllowed){
            fits = r1.getHeight()  <= r2.getWidth()
                && r1.getWidth() <= r2.getHeight();
            shouldBeRotated = true;
        }
        return new Result(fits, shouldBeRotated);
    }
    
    private void packAndSplit(Rectangle r, int i){
        Rectangle space = emptySpaces.get(i);
        
        // pack r
        r.setPos(space.getX(), space.getY());
        if (debug){
            System.out.print("Placed ");
            System.out.print(r);
            System.out.print(" in ");
            System.out.println(space);
        }
        
        // split space
        Rectangle horizontalSpace = null;
        int horizontalSpaceRemaining = space.getWidth() - r.getWidth();
        if (horizontalSpaceRemaining > 0){
            horizontalSpace = new Rectangle(r.getHorizontalReach(), r.getY(),
                    horizontalSpaceRemaining, space.getHeight());
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
//            System.out.println("Added vertical and horizontal space!");
            emptySpaces.add(verticalSpace);
            emptySpaces.add(horizontalSpace);
        } else if (horizontalSpace != null) {
//            System.out.println("Added horizontal space!");
            emptySpaces.add(horizontalSpace);
        } else if (verticalSpace != null){
//            System.out.println("Added vertical space!");
            emptySpaces.add(verticalSpace);
        }
        
        updateEmptySpaces(r);
        removeDegeneracies();
    }
    
    private void updateEmptySpaces(Rectangle r){
        for (int i = emptySpaces.size()-1; i >= 0; i--){
            Rectangle space = emptySpaces.get(i);
            if (Rectangle.overlaps(r, space)){
                if (debug){
                    System.out.print(r);
                    System.out.print(" overlaps ");
                    System.out.println(space);
                }
                int spaceLeft = r.getX() - space.getX();
                int spaceRight = space.getHorizontalReach() - r.getHorizontalReach();
                int spaceTop = space.getVerticalReach() - r.getVerticalReach();
                int spaceBottom = r.getY() - space.getY();
                
                if (spaceLeft > 0){
                    emptySpaces.add(new Rectangle(space.getX(), space.getY(), spaceLeft, space.getHeight()));
                }
                if (spaceRight > 0){
                    emptySpaces.add(new Rectangle(r.getHorizontalReach(), space.getY(), spaceRight, space.getHeight()));
                }
                if (spaceTop > 0){
                    emptySpaces.add(new Rectangle(space.getX(), r.getVerticalReach(), space.getWidth(), spaceTop));
                }
                if (spaceBottom > 0){
                    emptySpaces.add(new Rectangle(space.getX(), space.getY(), space.getWidth(), spaceBottom));
                }
                
                emptySpaces.remove(i);
            }
        }
    }
    
    public void removeDegeneracies(){
        ArrayList<Integer> toRemove = new ArrayList<>();
        
        for (int i = emptySpaces.size() -1; i >= 0; i--){
            for (int j = emptySpaces.size() -1; j >= 0; j--){
                if(i != j && emptySpaces.get(i).isCoveredBy(emptySpaces.get(j))){
                    if (debug){
                        System.out.print("Degenerecy removed: ");
                        System.out.println(i);
                        System.out.print("Size: ");
                        System.out.println(emptySpaces.size());
                    }
                    toRemove.add(i);
                    break;
                }
            }
        }
        
        for (int i : toRemove){
            emptySpaces.remove(i);
        }
    }
    
    class Result {
        boolean fits;
        boolean shouldBeRotated;
        
        Result(boolean fits, boolean sbr){
            shouldBeRotated = sbr;
            this.fits = fits;
        }
    }
}
