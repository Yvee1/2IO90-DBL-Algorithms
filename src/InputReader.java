import java.util.Scanner;

/**
 * InputReader reads input problems from stdin.
 */
class InputReader {

    private Scanner sc;

    /**
     * Create a new InputReader for stdin.
     */
    public InputReader() { sc = new Scanner(System.in); }

    /**
     * Parse a description of a packing problem from stdin.
     * @return A PackingProblem for the input.
     */
    public PackingProblem readProblem() {

        PackingSettings settings = new PackingSettings();

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
            r[i] = new Rectangle(sc.nextInt(), sc.nextInt());
        }

        return new PackingProblem(settings, r);
    }

}