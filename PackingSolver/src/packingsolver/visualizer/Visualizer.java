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
    private static int windowWidth;
    private static int windowHeight;
    private static double scaling;
    static PackingSolution ps;
    private Random rand = new Random();
    
    public static void main(String[] args) throws FileNotFoundException{
        SolutionReader sr;

        if (args.length == 1){
            Scanner sc = new Scanner(new File(args[0]));
            sr = new SolutionReader(sc);
        } else{
            sr = new SolutionReader();
        }
        ps = sr.readSolution();
        final double aspectRatio = ps.w / ps.h;
        windowWidth = 500;
        windowHeight = (int) (windowWidth / aspectRatio);
        scaling = (double) windowWidth / ps.w;
        
        JFrame frame = new JFrame("Rectangle Packing Solution");
        Canvas canvas = new Visualizer();
        canvas.setSize(windowWidth, windowHeight);
        frame.add(canvas);
        frame.pack();
        frame.setVisible(true);
    }
    
    public void paint(Graphics g) {
        
        
        System.out.println(scaling);
        for (Rectangle r : ps.solution){
            final float hue = rand.nextFloat();
            // Saturation between 0.1 and 0.3
            final float saturation = (rand.nextInt(2000) + 1000) / 10000f;
            final float luminance = 0.9f;
            final Color color = Color.getHSBColor(hue, saturation, luminance);
            g.setColor(color);
            g.fillRect((int) (r.x * scaling), (int) (r.y * scaling),
                       (int) (r.w * scaling), (int) (r.h * scaling));
        }
    }
}
