import java.util.*;
//import Visualizer;

public class PackingSolver {

    static Scanner sc;

    /**
     * The primary function called by main
     */
    static void run() {

        // Read the problem from inpit
        InputReader reader = new InputReader();
        PackingProblem p = reader.readProblem();

        // Decide which algorithm to apply
        AlgorithmInterface ai;
        if (p.rectangles.length <= 4) {
           ai  = new SteinbergSolver();
        } else { ai = new SteinbergSolver(); }

        // Run the algorithm and time the operation time
        long startTime = System.currentTimeMillis();
        PackingSolution sol = ai.solve(p);
        long endTime = System.currentTimeMillis();
        long time = endTime - startTime;

        // Do some more stuff based on time left

        // Verify the solutios validity
        checkValidity(sol.problem.getRectangles(), p.getSettings());

        // Print the solution
        OutputPrinter printer = new OutputPrinter();
        printer.printSolution(sol);

        //Visualizer.visualize(sol);
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