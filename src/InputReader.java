import java.util.Scanner;

class InputReader {

    private Scanner sc;

    public InputReader() { sc = new Scanner(System.in); }

    public PackingProblem readProblem() {

        PackingSettings settings = new PackingSettings();

        /* Skip "container height:" */
        sc.next();
        sc.next();

        if (sc.next().equals("fixed")) {
            settings.setFixed(true);
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

        for (int i = 0; i < n; i++) {
            r[i] = new Rectangle(sc.nextInt(), sc.nextInt());
        }

        return new PackingProblem(settings, r);
    }

}