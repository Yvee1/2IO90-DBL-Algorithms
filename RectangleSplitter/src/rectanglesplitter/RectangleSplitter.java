package rectanglesplitter;
import java.util.Random;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Steven van den Broek
 */
public class RectangleSplitter {
    private final static Random rand = new Random();
    private final static ArrayList<Rectangle> partition = new ArrayList();
    private static int width;
    private static int height;
    private static int n;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 3){
            throw new IllegalArgumentException("Provide width, height and number");
        }
        
        width = Integer.parseInt(args[0]);
        height = Integer.parseInt(args[1]);
        n = Integer.parseInt(args[2]);
        final Rectangle original = new Rectangle(0, 0, width, height);
        partition.add(original);
        
        for(int diff = n - partition.size(); diff > 0;){
            RectangleSplitter.doRandomSplit(partition, diff >= 4);
            diff = n - partition.size();
        }
        
        for (Rectangle r : partition){
            System.out.println(r);
        }
        
        RectangleSplitter.outputToFile(partition);
    }
    
    private static void outputToFile(ArrayList<Rectangle> rs) throws IOException {
        final String fileName = String.format("n%d-w%d-h%d.in", n, width, height);
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false));
        writer.append("container height: free");
        writer.newLine();
        writer.append("rotations allowed: no");
        writer.newLine();
        writer.append(String.format("number of rectangles: %d", rs.size()));
        for (Rectangle r : rs){
            writer.newLine();
            writer.append(r.getSize());
        }
        writer.close();
    }
    
    private static void doRandomSplit(ArrayList<Rectangle> partition, boolean room){
        final int removeIndex = rand.nextInt(partition.size());
        final Rectangle toSplit = partition.remove(removeIndex);
        for (Rectangle r : toSplit.split(room)){
            partition.add(r);
        }
    }
}
