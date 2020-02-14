import java.util.Scanner;

/**
 *
 * @author Steven van den Broek
 */
public class SolutionReader {
    private Scanner sc;
    private InputReader ir;

    /**
     * Create a new SolutionReader for stdin.
     */
    public SolutionReader() { 
        sc = new Scanner(System.in);
    }
    
    /**
     * Create a new SolutionReader with custom scanner.
     */
    public SolutionReader(Scanner sc) { 
        this.sc = sc;
    }

    /**
     * Parse a description of a packing problem from stdin.
     * @return A PackingProblem for the input.
     */
    public PackingSolution readSolution() {
        InputReader ir = new InputReader(sc);
        PackingProblem pp = ir.readProblem();
        
        /* Skip "placement of rectangles" */
        sc.next();
        sc.next();
        sc.next();
        
        final int n = pp.getSettings().getRectangleCount();
        final Rectangle[] rs = pp.getRectangles();
        
        int width = 0;
        int height = 0;
        
        for (int i = 0; i < n; i++){
            boolean rotated = false;
            if (pp.settings.rotation){
                final String word = sc.next();
                if (word.equals("yes")){
                    rotated = true;
                } else if (word.equals("no")){
                    rotated = false;
                }
            }
            
            final int x = sc.nextInt();
            final int y = sc.nextInt();
            
            if (x + rs[i].w > width){
                width = x + rs[i].w;
            }
            if (y + rs[i].h > height){
                height = y + rs[i].h;
            }
            
            rs[i].setPos(x, y);
            if (rotated){
                rs[i].rotate();
            }
        }
        
        PackingSolution ps = new PackingSolution(new PackingProblem(new PackingSettings(), rs), width, height);
        return ps;
    }
}
