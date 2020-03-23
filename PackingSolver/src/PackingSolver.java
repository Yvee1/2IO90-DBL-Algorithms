import java.io.File;
import java.util.*;
//import Visualizer;

public class PackingSolver {

    static Scanner sc;

    /**
     * The primary function called by main
     */
    static void run() {
        boolean multipleSolvers = false;
        boolean debug = true;

        // Read the problem from input
        InputReader reader = new InputReader();
        PackingProblem p = reader.readProblem();

        PackingSolution solution;

        if (multipleSolvers){
            solution = new CompoundSolver(debug).solve(p);
        } else {
            // Decide which algorithm to apply
            AlgorithmInterface ai;
//            ai = new MaxRectsSolver(new BSSF(), new DESCSS());
            ai = new GlobalMaxRectsSolver();
        
            solution = ai.solve(p);
        }

        // Verify the solutios validity
        if (debug){
            checkValidity(solution.problem.getRectangles(), p.getSettings());
        }

        /* Sort rectangles by id. */
        Arrays.sort(solution.problem.getRectangles(), (Rectangle a, Rectangle b) -> Integer.compare(a.id, b.id));

        // Print the solution
        OutputPrinter printer = new OutputPrinter();
        if (!debug){
            printer.printSolution(solution);
        }

        if (debug){
            Visualizer.visualize(solution, false, true);
        }
    }

    /**
     * Checks the validity of a solution by checking for overlaps and checking if every
     * rectangle adheres to the height limit
     *
     * @param rects the rectangles to run the checks on
     * @param settings the settings applicable to these rectangles
     */
    public static void checkValidity(Rectangle[] rects, PackingSettings settings) {
        // For every rectangle
        for (int i = 0; i < rects.length; i++) {
            Rectangle r1 = rects[i];
            // Check if height limit is satisfied
            if (r1.getHeight() + r1.getY() > settings.maxHeight) {
                System.out.println("MAX HEIGHT VIOLATED: " + i);
            }
            // For every pair of rectangles
            for (int j = 0; j < rects.length; j++) {
                Rectangle r2 = rects[j];
                if (r1 == r2) { continue; }
                // Check if they overlap
                if (r1.getX() >= r2.getX() + r2.getWidth() || r2.getX() >= r1.getX() + r1.getWidth()) {
                    continue;
                }
                if (r1.getY() + r1.getHeight() <= r2.getY() || r2.getY() + r2.getHeight() <= r1.getY()) {
                    continue;
                }
                System.out.println("OVERLAP DETECTED: (" + i + "," + j + ")");
            }
        }
    }

    public static void main(String args[]) {
        sc = new Scanner(System.in);
        run();
    }
}