
import java.util.ArrayList;

/**
 *
 * @author Steven van den Broek
 */
public class MaxRectsSolver implements AlgorithmInterface {
    // Set F of free rectangles
    ArrayList<Rectangle> emptySpaces;
    // whether rotations are allowed
    boolean rotationsAllowed;
    // whether height is fixed
    boolean fixed;
    // current bounding box
    Rectangle container;
    // Reference to the array of rectangles that needs to be placed
    Rectangle[] rs;
    // Rectangles ordered in the way they were placed
    Rectangle[] orderedRectangles;
    // Denotes we are at the nth rectangle
    int n = 0;
    
    boolean debug = false;
    
    MaxRectsSortingSubroutine mrss;
    MaxRectsHeuristicSubroutine mrhs;
    
    MaxRectsSolver(MaxRectsHeuristicSubroutine mrhs, MaxRectsSortingSubroutine mrss){
        this.mrss = mrss;
        this.mrhs = mrhs;
    }
    
    @Override
    public PackingSolution solve(PackingProblem pp){
        emptySpaces = new ArrayList<>();
        
        // set the rectangles
        rs = pp.getRectangles();
        orderedRectangles = new Rectangle[rs.length];
        
        fixed = pp.getSettings().fixed;
        rotationsAllowed = pp.getSettings().rotation;
        
        mrss.sortRectangles(rs);
        
        if (fixed){
            container = new Rectangle(0, 0, rs[0].getWidth(), pp.getSettings().getMaxHeight());
        } else {
            // begin with container with size of 'biggest' rectangle
            container = new Rectangle(0, 0, rs[0].getWidth(), rs[0].getHeight());
        }
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
            
            BestFitResult bfr = mrhs.findBestSpace(r, emptySpaces, rotationsAllowed);
            
            if (bfr.fits){
                if (bfr.shouldBeRotated){
                    r.rotate();
                }
                packAndSplit(r, bfr.bestFit);
            } else {
                // rotate if it doesn't fit in container otherwise
                if (fixed && r.getHeight() > container.getHeight()){
                    r.rotate();
                } else if (fixed && r.getWidth() > r.getHeight() && r.getWidth() <= container.getHeight()){
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
                
                packAndSplit(r, emptySpaces.size()-1);
            }
        }
        
        return new PackingSolution(pp, orderedRectangles);
    }
    
    public static boolean fitsInto(Rectangle r1, Rectangle r2){
        return r1.getWidth()  <= r2.getWidth()
            && r1.getHeight() <= r2.getHeight();
    }
    
    private void packAndSplit(Rectangle r, int i){
        Rectangle space = emptySpaces.get(i);
        
        // pack r
        r.setPos(space.getX(), space.getY());
        orderedRectangles[n++] = r;
        
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
    
    private void removeDegeneracies(){
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
}