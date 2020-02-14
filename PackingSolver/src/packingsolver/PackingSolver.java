package packingsolver;
import java.util.*;
class PackingSolver {
    static Scanner sc;

    static void run() {

        InputReader reader = new InputReader();

        PackingProblem p = reader.readProblem();

        AlgorithmInterface ai = new NFDH();

        PackingSolution sol = ai.solve(p);

        OutputPrinter printer = new OutputPrinter();

        printer.printSolution(sol);

    }

    public static void main(String args[]) {
        sc = new Scanner(System.in);
        run();
    }
}