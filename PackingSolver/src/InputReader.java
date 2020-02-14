
import java.util.Scanner;

/**
 * InputReader reads input problems from stdin.
 */
public class InputReader {

    private Scanner sc;

    /**
     * Create a new InputReader for stdin.
     */
    public InputReader() {
        sc = new Scanner(System.in);
    }

    /**
     * Create a new SolutionReader with custom scanner.
     */
    public InputReader(Scanner sc) {
        this.sc = sc;
    }

    /**
     * Parse a description of a packing problem from stdin.
     *
     * @return A PackingProblem for the input.
     */
    public PackingProblem readProblem() {

        PackingSettings settings = new PackingSettings();
        int largestWidth = -1;
        int largestHeight = -1;

        /* Skip "container height:" */
        sc.next();
        sc.next();

        settings.setFixed(sc.next().equals("fixed"));

        if (settings.getFixed()) {
            settings.setMaxHeight(sc.nextInt());
        }

        /* Skip "rotations allowed:" */
        sc.next();
        sc.next();

        settings.setRotation(sc.next().equals("yes"));

        /* Skip "number of rectangles:" */
        sc.next();
        sc.next();
        sc.next();

        int n = sc.nextInt();
        settings.setRectangleCount(n);

        Rectangle[] r = new Rectangle[n];

        /* Read rectangles. */
        for (int i = 0; i < n; i++) {
            int width = sc.nextInt();
            int height = sc.nextInt();
            r[i] = new Rectangle(width, height);

            if (width > largestWidth) { largestWidth = width; }
            if (height > largestHeight) { largestHeight = height; }
        }

        PackingProblem p = new PackingProblem(settings, r);
        p.largestWidth = largestWidth;
        p.largestHeight = largestHeight;

        return p;
    }
}