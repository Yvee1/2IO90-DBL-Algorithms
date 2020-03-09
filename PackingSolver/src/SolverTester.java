import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class SolverTester {

    // VUL HIER IN WELKE TEST CASES
    private String cases = "AH";
    // e.g. "AH" voor alle test cases onder "\AH\"
    // of "AH\\AH1.txt" voor alleen die case
    // Momenteel zorgen sommige consecutive squares voor overflows denk ik (negatieve areas)

    private AlgorithmInterface algorithm;
    private OutputPrinter printer;

    private String testCasesPath;
    private List<TestCase> testCases;

    public SolverTester(AlgorithmInterface ai) {
        algorithm = ai;
        printer = new OutputPrinter();
        testCasesPath = new File("").getAbsolutePath() + "\\Testcases\\";
        testCases = new ArrayList<>();
    }

    public void run() throws FileNotFoundException {
        addTestCase(cases);
        double avgDens = 0;
        int i = 1;
        for (TestCase tc : testCases) {
            long start = System.nanoTime();
            tc.run();
            long end = System.nanoTime();
            long dur = (end - start) / 1000000;
            System.out.print("(" + i + "/" + testCases.size() + ")  ");
            System.out.print("dt: " + dur + "ms  ");
            System.out.print("n: " + tc.getProblem().getSettings().getRectangleCount() + "  ");
            System.out.print("rot: " + tc.getProblem().getSettings().getRotation() + "  ");
            System.out.print("lim: " + (tc.getProblem().getSettings().getFixed() ? tc.getProblem().getSettings().getMaxHeight() : 0) + "  ");
            System.out.print("area: " + tc.getArea() + "  ");
            System.out.print("used: " + tc.getUsedSpace() + "  ");
            System.out.print("dens: " + tc.getDensity());
            System.out.println("     (" + tc.getPath() + ")");
            avgDens += tc.getDensity();
            i++;
        }
        avgDens /= testCases.size();
        System.out.println("Average Density: " + avgDens);
    }

    public void addTestCase(String path) throws FileNotFoundException {
        if (path.endsWith(".txt") || path.endsWith(".in")) {
            testCases.add(new TestCase(new File(testCasesPath + path), algorithm, path));
        } else {
            File dir;
            String p;
            if (path.endsWith("\\")) {
                p = path.substring(0, path.length() - 1);
            } else {
                p = path;
            }
            dir = new File(testCasesPath + p);
            for (String item : Objects.requireNonNull(dir.list())) {
                addTestCase(p + "\\" + item);
            }
        }
    }

    public static void main(String args[]) throws FileNotFoundException {
        SolverTester st = new SolverTester(new SteinbergSolver());
        st.run();
    }

    public class TestCase {

        private PackingProblem problem;
        private PackingSolution solution;
        private AlgorithmInterface algo;
        private String path;
        private int area;
        private int usedSpace;
        private double density;

        public TestCase(File file, AlgorithmInterface ai, String path) throws FileNotFoundException {
            this.path = path;
            algo = ai;
            InputReader reader = new InputReader();
            problem = reader.readFile(file);
        }

        public PackingProblem getProblem() {
            return problem;
        }

        public PackingSolution getSolution() {
            return solution;
        }

        public double getDensity() {
            return density;
        }

        public String getPath() {
            return path;
        }

        public void run() {
            solution = algo.solve(problem);
            density = calculateDensity(solution);
        }

        public int getArea() {
            return area;
        }

        public int getUsedSpace() {
            return usedSpace;
        }

        private double calculateDensity(PackingSolution sol) {
            area = sol.area();
            usedSpace = 0;
            for (Rectangle r : sol.problem.getRectangles()) {
                usedSpace += r.getArea();
            }
            return (double) usedSpace / area;
        }


    }

}
