
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
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
            r[i].id = i;

            if (r[i].h > settings.maxHeight) { r[i].rotate(); }

            if (width > largestWidth) { largestWidth = width; }
            if (height > largestHeight) { largestHeight = height; }
        }

        PackingProblem p = new PackingProblem(settings, r);
        p.largestWidth = largestWidth;
        p.largestHeight = largestHeight;

        return p;
    }

    public PackingProblem readFile(File file) throws FileNotFoundException {
        Scanner sc = new Scanner(file);
        sc.hasNextLine();
        String[] firstLine = sc.nextLine().trim().split(" ");
        boolean fix = false;
        int height = Integer.MAX_VALUE;
        if (firstLine.length == 4) {
            fix = true;
            height = Integer.parseInt(firstLine[3]);
        }
        String secondLine = sc.nextLine();
        boolean rot = secondLine.contains("yes");
        String[] thirdLine = sc.nextLine().trim().split(" ");
        int n = Integer.parseInt(thirdLine[3]);
        List<Rectangle> r = new ArrayList<>();
        int largestWidth = 0;
        int largestHeight = 0;
        int id = 0;
        while (sc.hasNextLine()) {
            String[] line = sc.nextLine().trim().split("\\s+");
            for (int j = 0; j < line.length / 2; j++) {
                line[j * 2] = line[j * 2].trim();
                line[(j * 2) + 1] = line[(j * 2) + 1].trim();
                Rectangle rect = new Rectangle(Integer.parseInt(line[j * 2]), Integer.parseInt(line[(j * 2) + 1]));
                rect.id = id;
                r.add(rect);
                id++;
                if (rect.getWidth() > largestWidth) { largestWidth = rect.getWidth(); }
                if (rect.getHeight() > largestHeight) { largestHeight = rect.getHeight(); }
            }
        }
        PackingSettings settings = new PackingSettings();
        settings.setFixed(fix);
        settings.setRotation(rot);
        settings.setMaxHeight(height);
        settings.setRectangleCount(n);
        Rectangle[] rectangles = new Rectangle[r.size()];
        for (Rectangle rect : r) {
            rectangles[rect.id] = rect;
        }
        PackingProblem p = new PackingProblem(settings, rectangles);
        p.largestHeight = largestHeight;
        p.largestWidth = largestWidth;
        return p;
    }
}