import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
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
    private final static int maxWindowSize = 500;
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
        
        visualize(sr.readSolution());
    }
    
    public void paint(Graphics g) {
        for (Rectangle r : ps.problem.getRectangles()){
            if (r.x < 0){
                continue;
            }
            
            final float hue = rand.nextFloat();
            // Saturation between 0.3 and 0.5
            final float saturation = (rand.nextInt(5000) + 3000) / 10000f;
            final float luminance = 0.9f;
            final Color color = Color.getHSBColor(hue, saturation, luminance);
            
            Graphics2D g2 = (Graphics2D) g;
            AffineTransform oldAT = g2.getTransform();
            try {
                //Move the origin to bottom-left, flip y axis
                g2.scale(1.0, -1.0);
                g2.translate(0, -windowHeight);
                
                // coloured fill
                g.setColor(color);
                g.fillRect((int) (r.x * scaling), (int) (r.y * scaling),
                           (int) (r.w * scaling), (int) (r.h * scaling));
                // black stroke
                g.setColor(Color.BLACK);
                g.drawRect((int) (r.x * scaling), (int) (r.y * scaling),
                           (int) (r.w * scaling), (int) (r.h * scaling));

            }
            finally {
                  //restore
                  g2.setTransform(oldAT);
            }
        }
    }
    
    public static void visualize(PackingSolution ps_){
        ps = ps_;

        // Make proper size window
        final double aspectRatio = (double) ps.width / ps.height;
        if (aspectRatio > 1){
            windowWidth = maxWindowSize;
            windowHeight = (int) (windowWidth / aspectRatio);
        } else{
            windowHeight = maxWindowSize;
            windowWidth = (int) (windowHeight * aspectRatio);
        }
        
        scaling = (double) windowWidth / ps.width;
        
        // preparing the window
        JFrame frame = new JFrame("Rectangle Packing Solution");
        Canvas canvas = new Visualizer();
        canvas.setSize(windowWidth, windowHeight);
        frame.add(canvas);
        frame.pack();
        frame.setVisible(true);
    }
}
