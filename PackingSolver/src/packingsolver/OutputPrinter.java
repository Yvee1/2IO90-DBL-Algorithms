package packingsolver;

import java.io.PrintStream;

public class OutputPrinter {

    private PrintStream stream;

    public OutputPrinter() {
        this.stream = System.out;
    }

    public OutputPrinter(PrintStream stream) {
        this.stream = stream;
    }

    public void printProblem(PackingProblem p) {
        stream.println(p.settings.toString());

        for (Rectangle r: p.getRectangles()) {
            stream.println(r.getSizeString());
        }
    }

    public void printSolution(PackingSolution sol) {
        stream.println("placement of rectangles");

        for (Rectangle r: sol.solution) {
            stream.println(r.getPositionString(false));
        }
    }

}
