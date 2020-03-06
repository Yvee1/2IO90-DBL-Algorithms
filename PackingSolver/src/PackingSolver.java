import java.util.*;
//import Visualizer;

public class PackingSolver {

    static Scanner sc;

    /**
     * The primary function called by main
     */
    static void run() {
        boolean multipleSolvers = true;

        // Read the problem from inpit
        InputReader reader = new InputReader();
        PackingProblem p = reader.readProblem();
        long bigBang = System.currentTimeMillis();
        
        // Array of solvers to use in order of non-increasing running-time
        AlgorithmInterface[] solvers;

        // Decide which algorithm to apply
        AlgorithmInterface ai;
        if (p.rectangles.length <= 4) {
            ai = new BruteForceSolver();

            solvers = new AlgorithmInterface[1];
            solvers[0] = ai;
            
        } else { 
            ai = new SteinbergSolver(); 
            solvers = new AlgorithmInterface[]
            {new SteinbergSolver(), new FFDH(), new NFDH()};
        }

        
        PackingSolution bestSolution = null;
        
        // Do some more stuff based on time left
        if (multipleSolvers){
            PackingProblem[] ps = new PackingProblem[solvers.length];
            ps[0] = p;
            for (int i = 1; i < solvers.length; i++){
                ps[i] = new PackingProblem(p);
            }
            // seconds used for previous algorithm
            double secondsUsed = 0;
            // seconds left till total time reaches 30
            double secondsLeft = 30 - (System.currentTimeMillis() - bigBang) / 1000;
            
            for (int i = 0; i < solvers.length && secondsLeft > secondsUsed; i++){
                long startTime = System.currentTimeMillis();
                PackingSolution sol = solvers[i].solve(ps[i]);
                long endTime = System.currentTimeMillis();
                long time = endTime - startTime;
                
                secondsUsed = (double) time / 1000;
                secondsLeft = 30 - secondsUsed;
               
//                System.out.println();
//                System.out.println(solvers[i].getClass().getName());
//                System.out.println(sol.area());
                
                if (bestSolution == null || sol.area() < bestSolution.area()){
                    bestSolution = sol;
                }
            }
        } else {
            // Run the algorithm and time the operation time
            long startTime = System.currentTimeMillis();
            bestSolution = ai.solve(p);
            long endTime = System.currentTimeMillis();
            long time = endTime - startTime;
        }

        // Verify the solutios validity
        checkValidity(bestSolution.problem.getRectangles(), p.getSettings());

        // Print the solution
        OutputPrinter printer = new OutputPrinter();
        printer.printSolution(bestSolution);
//        System.out.println("------");
//        System.out.println("Best solution");
//        System.out.println(bestSolution.area());

        Visualizer.visualize(bestSolution);
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