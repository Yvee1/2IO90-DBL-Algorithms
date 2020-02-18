
import java.util.*;

//import Visualizer;
public class PackingSolver {
    static Scanner sc;

    static void run() {

        InputReader reader = new InputReader();

        PackingProblem p = reader.readProblem();


        AlgorithmInterface ai;

        if (p.rectangles.length <= 4) {
           ai  = new SteinbergSolver();
        } else { ai = new SteinbergSolver(); }

        PackingSolution sol = ai.solve(p);

        OutputPrinter printer = new OutputPrinter();

        Rectangle[] rects = sol.problem.getRectangles();
        for (int i = 0; i < rects.length; i++) {
            Rectangle r1 = rects[i];
            if (r1.getHeight() + r1.getY() > sol.problem.getSettings().maxHeight) {
                System.out.println("MAX HEIGHT VIOLATED: " + i);
            }
            for (int j = 0; j < rects.length; j++) {
                Rectangle r2 = rects[j];
                if (r1 == r2) { continue; }
                if (r1.getX() >= r2.getX() + r2.getWidth() || r2.getX() >= r1.getX() + r1.getWidth()) {
                    continue;
                }
                if (r1.getY() + r1.getHeight() <= r2.getY() || r2.getY() + r2.getHeight() <= r1.getY()) {
                    continue;
                }
                System.out.println("OVERLAP DETECTED: (" + i + "," + j + ")");
            }
        }

        printer.printSolution(sol);
        //Visualizer.visualize(sol);
    }

    public static void main(String args[]) {
        sc = new Scanner(System.in);
        run();
    }
}