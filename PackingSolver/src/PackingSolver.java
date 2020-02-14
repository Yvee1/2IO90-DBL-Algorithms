
import java.util.*;

//import Visualizer;
class PackingSolver {
    static Scanner sc;

    static void run() {

        InputReader reader = new InputReader();

        PackingProblem p = reader.readProblem();


        AlgorithmInterface ai;

        if (p.rectangles.length <= 4) {
            ai  = new BruteForceSolver();
        } else { ai = new SteinbergSolver(); }

        PackingSolution sol = ai.solve(p);

        OutputPrinter printer = new OutputPrinter();

        printer.printSolution(sol);
        Visualizer.visualize(sol);
    }

    public static void main(String args[]) {
        sc = new Scanner(System.in);
        run();
    }
}