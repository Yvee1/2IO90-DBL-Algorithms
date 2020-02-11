package packingsolver;
import java.io.*;
import java.util.*;
class PackingSolver {
    static Scanner sc;

    static void run() {

        boolean fixed= false, rotation = false;
        int n, maxH = 0;

        int rectangles[][];
        
        sc.next();
        sc.next();
        
        if (sc.next().equals("fixed")) {
            fixed = true;
            maxH = sc.nextInt();
        }

        sc.next();
        sc.next();
        rotation = sc.next().equals("yes");

        sc.next();
        sc.next();
        sc.next();

        n = sc.nextInt();

        rectangles = new int[n][2];
        
        for (int i = 0; i < n; i++) {
            rectangles[i][0] = sc.nextInt();
            rectangles[i][1] = sc.nextInt();
        }

        if (fixed) {
            System.out.printf("container height: fixed %d\n", maxH);
        } else {
            System.out.println("container height: free");
        }

        System.out.printf("rotations allowed: %s\n", rotation ? "yes" : "no");
        System.out.printf("number of rectangles: %d\n", n);

        for (int i = 0; i < n; i++) {
            System.out.printf("%d %d\n", rectangles[i][0], rectangles[i][1]);
        }

        System.out.println("placement of rectangles");

        for (int i = 0; i < n; i++) {
            if (rotation) {
                System.out.print("no ");
            }
            System.out.println("0 0");
        }

    }

    public static void main(String args[]) {
        sc = new Scanner(System.in);
        run();
    }
}