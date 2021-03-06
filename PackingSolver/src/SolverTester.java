import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class SolverTester {

    // STOP JE SOLVER IN MAIN()
    // VUL HIER IN WELKE TEST CASES
    private String cases = "";
    // Open visualizer als overlap of height limit exceeded is?
    private boolean visualizeOnInvalidSolution = false;
    // e.g. "" leeg voor alle test cases
    // e.g. "AH" voor alle test cases onder "\AH\"
    // of "AH, N" voor alle test cases onder "\AH\" en "\N\"
    // of "AH\\AH1.txt" voor alleen die case
    // of "AH\\AH1.txt, AH\\AH2.txt, AH\\AH3.txt" voor al die cases
    // Momenteel zorgen sommige consecutive squares voor overflows denk ik (negatieve areas)

    private AlgorithmInterface algorithm;
    private OutputPrinter printer;

    private String testCasesPath;
    private List<TestCase> testCases;

    public SolverTester(AlgorithmInterface ai) {
        algorithm = ai;
        printer = new OutputPrinter();
        testCasesPath = "D:\\2IO90-DBL-Algorithms\\Testcases\\";
        testCases = new ArrayList<>();
    }

    public void run() throws Exception {
        addTestCase(cases);
        double avgDens = 0;
        // the average time in ms
        long avgTime = 0;
        int i = 1;
        int j = 0;
        for (TestCase tc : testCases) {
            System.out.print("(" + tc.getPath() + ") ");
            System.out.print("(" + i + "/" + testCases.size() + ")  ");
            long start = System.nanoTime();
            tc.run();
            long end = System.nanoTime();
            long dur = (end - start) / 1000000;
            System.out.print("dt: " + dur + "ms  ");
            System.out.print("n: " + tc.getProblem().getSettings().getRectangleCount() + "  ");
            System.out.print("rot: " + tc.getProblem().getSettings().getRotation() + "  ");
            System.out.print("lim: " + (tc.getProblem().getSettings().getFixed() ? tc.getProblem().getSettings().getMaxHeight() : 0) + "  ");
            System.out.print("area: " + tc.getArea() + "  ");
            System.out.print("used: " + tc.getUsedSpace() + "  ");
            System.out.print("dens: " + tc.getDensity());
            if (visualizeOnInvalidSolution) {
                try {
                    checkValidity(Arrays.asList(tc.getSolution().problem.getRectangles()), tc.getSolution().problem.getSettings());
                } catch (Exception e) {
                    System.out.println(e);
                    Visualizer.visualize(tc.getSolution(),false, true);
                    break;
                }
            } else {
                checkValidity(Arrays.asList(tc.getSolution().problem.getRectangles()), tc.getSolution().problem.getSettings());
            }
            if (tc.getArea() < 0 || tc.getUsedSpace() < 0) {
                System.out.println("  (Weird results, ignored in total stats)  ");
                j++; i++;
                continue;
            }
            System.out.println("");
            avgDens += tc.getDensity();
            i++;
            avgTime += dur;
        }
        avgDens /= (i - j - 1);
        avgTime /= (i - j - 1);
        System.out.println("Average Density: " + avgDens);
        System.out.println("Average Time: " + avgTime + "ms");
        stop();
    }

    public void stop() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Visualize the following case (enter to skip or e.g.'35'):");
        String input = sc.next().trim();
        while (input.length() != 0) {
            System.out.println("Visualising " + testCases.get(Integer.parseInt(input) - 1).getPath());
            Visualizer.visualize(testCases.get(Integer.parseInt(input) - 1).getSolution(), false, true);
            System.out.println("Visualize the following case (enter to skip or e.g.'35'):");
            input = sc.next().trim();
        }
    }

    public void addTestCase(String path) throws FileNotFoundException {
        if (path.contains(",")) {
            String[] sets = path.replace(" ", "").split(",");
            for (String s : sets) {
                addTestCase(s);
            }
        } else if (path.endsWith(".txt") || path.endsWith(".in")) {
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
                if (p.length() > 0) {
                    addTestCase(p + "\\" + item);
                } else {
                    addTestCase(p + item);
                }
            }
        }
    }

    public void checkValidity(List<Rectangle> rects, PackingSettings settings) throws Exception {
        for (int i = 0; i < rects.size(); i++) {
            Rectangle r1 = rects.get(i);
            // Check if height limit is satisfied
            if (r1.getHeight() + r1.getY() > settings.maxHeight) {
                System.out.println();
                throw new Exception("MAX HEIGHT VIOLATED: Rectangle " + i + " at (" + r1.getX() + "," + r1.getY() + ") " +
                        "with dimensions (" + r1.getWidth() + "," + r1.getHeight() + "), lim=" + settings.maxHeight);
            }
            // For every pair of rectangles
            for (int j = 0; j < rects.size(); j++) {
                Rectangle r2 = rects.get(j);
                if (r1 == r2) { continue; }
                // Check if they overlap
                if (r1.getX() >= r2.getX() + r2.getWidth() || r2.getX() >= r1.getX() + r1.getWidth()) {
                    continue;
                }
                if (r1.getY() + r1.getHeight() <= r2.getY() || r2.getY() + r2.getHeight() <= r1.getY()) {
                    continue;
                }
                System.out.println();
                throw new Exception("OVERLAP DETECTED: Rectangles " + i + ", " + j + "." + "i = (" + r1.getX() + ","
                + r1.getY() + ")(" + r1.getWidth() + "," + r1.getHeight() + "), j = ("  + r1.getX() + ","
                        + r2.getY() + ")(" + r2.getWidth() + "," + r2.getHeight() + ")");
            }
        }
    }

    public static void main(String args[]) throws Exception {
        SolverTester st = new SolverTester(new BestFitFast());
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
            
            //possible adapt the problem to fit certain cases
//            problem.setSettings(new PackingSettings(false, true));
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
            try {
                solution = algo.solve(problem);
            } catch (InterruptedException e) {
                
            }
            
            density = calculateDensity(solution);
        }

        public int getArea() {
            return area;
        }

        public int getUsedSpace() {
            return usedSpace;
        }

        private double calculateDensity(PackingSolution sol) {
            area = (int) sol.area();
            usedSpace = 0;
            for (Rectangle r : sol.problem.getRectangles()) {
                usedSpace += r.getArea();
            }
            return (double) usedSpace / area;
        }


    }

}
