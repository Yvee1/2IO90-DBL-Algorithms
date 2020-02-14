package packingsolver;
import java.util.*;
import packingsolver.visualizer.Visualizer;
class PackingSolver {
    static Scanner sc;

    static void run() {

        InputReader reader = new InputReader();

        PackingProblem p = reader.readProblem();

        AlgorithmInterface ai = new BruteForceSolver();

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