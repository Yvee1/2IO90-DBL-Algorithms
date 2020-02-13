/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package packingsolver.visualizer;
import packingsolver.PackingSolution;
import packingsolver.Rectangle;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;
import javax.swing.JFrame;


/**
 *
 * @author 20182300
 */
public class Visualizer extends Canvas  {
    // maximum window size
    private static int maxWindowSize = 1000;
    private static int windowWidth;
    private static int windowHeight;
    // scaling that is done from original rectangle to rectangle that is drawn 
    private static double scaling;
    // packing solution to draw
    static PackingSolution ps;
    // random number generator
    private Random rand = new Random();
    
    public static void main(String[] args) throws FileNotFoundException{
        SolutionReader sr;

        // read solution either from file or system.in
        if (args.length == 1){
            Scanner sc = new Scanner(new File(args[0]));
            sr = new SolutionReader(sc);
        } else{
            sr = new SolutionReader();
        }
        ps = sr.readSolution();
        
        // Make proper size window
        final double aspectRatio = (double) ps.w / ps.h;
        if (aspectRatio > 1){
            windowWidth = maxWindowSize;
            windowHeight = (int) (windowWidth / aspectRatio);
        } else{
            windowHeight = maxWindowSize;
            windowWidth = (int) (windowHeight * aspectRatio);
        }
        
        scaling = (double) windowWidth / ps.w;
        
        // preparing the window
        JFrame frame = new JFrame("Rectangle Packing Solution");
        Canvas canvas = new Visualizer();
        canvas.setSize(windowWidth, windowHeight);
        frame.add(canvas);
        frame.pack();
        frame.setVisible(true);
    }
    
    public void paint(Graphics g) {
        for (Rectangle r : ps.solution){
            final float hue = rand.nextFloat();
            // Saturation between 0.3 and 0.5
            final float saturation = (rand.nextInt(5000) + 3000) / 10000f;
            final float luminance = 0.9f;
            final Color color = Color.getHSBColor(hue, saturation, luminance);
            // coloured fill
            g.setColor(color);
            g.fillRect((int) (r.x * scaling), (int) (r.y * scaling),
                       (int) (r.w * scaling), (int) (r.h * scaling));
            // black stroke
            g.setColor(Color.BLACK);
            g.drawRect((int) (r.x * scaling), (int) (r.y * scaling),
                       (int) (r.w * scaling), (int) (r.h * scaling));
        }
    }
}
