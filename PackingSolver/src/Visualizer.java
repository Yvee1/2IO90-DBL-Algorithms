import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;


/**
 *
 * @author 20182300
 */
public class Visualizer extends JPanel  {
    private final Color[] palette = new Color[] {Color.decode("#db4549"), Color.decode("#d1e1e1"), Color.decode("#3e6a90"), Color.decode("#2e3853"), Color.decode("#a3c9d3")};
    // maximum window size
    private final static int maxWindowSize = 500;
    private static int boxHeight;
    private static int boxWidth;
    private static int windowWidth;
    private static int windowHeight;
    // scaling that is done from original rectangle to rectangle that is drawn 
    private static double scaling;
    // packing solution to draw
    static PackingSolution ps;
    // random number generator
    private Random rand = new Random();
    
    private static Rectangle[] rs;
    private boolean debug = false;
    private int count = 0;
    
    private static boolean dashedBox;
    private static boolean oneToOne;
    
    public Visualizer(){
        if (!debug){
            count = Integer.MAX_VALUE;
        }
        
        setPreferredSize(new Dimension(windowWidth, windowHeight));
        addMouseListener(new MouseAdapter(){
                    @Override
                    public void mouseClicked(MouseEvent e){
                        if (debug){
                            count++;
                        }
                        repaint();
                    }
        });
    }
    
    public static void main(String[] args) throws FileNotFoundException{
        SolutionReader sr;

        // read solution either from file or system.in
        if (args.length == 1){
            Scanner sc = new Scanner(new File(args[0]));
            sr = new SolutionReader(sc);
        } else{
            sr = new SolutionReader();
        }
        
        visualize(sr.readSolution(), false, true);
    }
    
    @Override
    public void paintComponent(Graphics g) {    
        Graphics2D g2 = (Graphics2D) g;
        AffineTransform oldAT = g2.getTransform();
        try {
            //Move the origin to bottom-left, flip y axis
            g2.scale(1.0, -1.0);
            g2.translate(0, -windowHeight);

            g.setColor(Color.WHITE);
            g.fillRect(0, 0, windowWidth, windowHeight);
//            g.setColor(Color.decode("#F7F2DF"));
            g.fillRect(0, 0, (int) (ps.width * scaling), (int) (boxHeight * scaling));
            
            //creates a copy of the Graphics instance
            Graphics2D g2d = (Graphics2D) g.create();

            if (dashedBox){
                //set the stroke of the copy, not the original 
                Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
                g2d.setStroke(dashed);
                g2d.setColor(Color.BLACK);
                g2d.drawRect(0, 0, (int) (ps.width * scaling), (int) (boxHeight * scaling));
                //gets rid of the copy
                g2d.dispose();
            }
            
            
//            for (Rectangle r : ps.problem.getRectangles()){
            
            for (int i = 0; i <= count && i < rs.length; i++){
                Rectangle r = rs[i];
                if (r.x < 0){
                    continue;
                }

    //            final float hue = rand.nextFloat();
                // Saturation between 0.3 and 0.5
    //            final float saturation = (rand.nextInt(5000) + 3000) / 10000f;
    //            final float luminance = 0.9f;
    //            final Color color = Color.getHSBColor(hue, saturation, luminance);
                final Color color = palette[rand.nextInt(palette.length)];



                    // coloured fill
                    g.setColor(color);
                    g.fillRect((int) (r.x * scaling), (int) (r.y * scaling),
                               (int) (r.w * scaling), (int) (r.h * scaling));
                    // black stroke
                    g.setColor(Color.BLACK);
                    float minSize = Math.min(r.w * (float) scaling, r.h * (float) scaling);
                    float strokeWidth = Math.min(0.01f * minSize, 1.5f);
                    g2.setStroke(new BasicStroke(strokeWidth));
                    g.drawRect((int) (r.x * scaling), (int) (r.y * scaling),
                               (int) (r.w * scaling), (int) (r.h * scaling));

                }
        }
        finally {
              //restore
              g2.setTransform(oldAT);
        }
        
    }
    
    public static void visualize(PackingSolution ps_, boolean oneToOne_, boolean dashedBox_){
        dashedBox = dashedBox_;
        oneToOne = oneToOne_;
        ps = ps_;
        rs = ps.problem.getRectangles();
        
        boxWidth = ps.width;
        if (ps.problem.settings.fixed){
            boxHeight = ps.problem.settings.getMaxHeight();
        } else{
            boxHeight = ps.height;
        }
        
        if (oneToOne){
            windowWidth = boxWidth;
            windowHeight = boxHeight;
            scaling = 1;
        } else {
            // Make proper size window
            final double aspectRatio = (double) boxWidth / boxHeight;
            if (aspectRatio > 1){
                windowWidth = maxWindowSize;
                windowHeight = (int) (windowWidth / aspectRatio);
            } else{
                windowHeight = maxWindowSize;
                windowWidth = (int) (windowHeight * aspectRatio);
            }
            scaling = (double) windowWidth / ps.width;
        }
        
        // preparing the window
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame(String.format("area: %d, density: %.2f", ps.area(), ps.density() * 100));
                
                frame.add(new Visualizer());
                frame.pack();
                frame.addComponentListener(new ComponentAdapter() {
                    public void componentResized(ComponentEvent componentEvent) {
                        if (!oneToOne){
                            Dimension d = frame.getContentPane().getSize();
                            windowWidth = d.width;
                            windowHeight = d.height;

                            double ratioH = (double) windowHeight / boxHeight;
                            double ratioW = (double) windowWidth / boxWidth;

                            scaling = Math.min(ratioW, ratioH);
                        }
                    }
                });
                frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }
}
