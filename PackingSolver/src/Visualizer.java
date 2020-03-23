import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.WindowConstants;


/**
 *
 * @author Steven van den Broek
 */
public class Visualizer extends JPanel  {
    // color palette
    private final static Color[] palette = new Color[] 
    {Color.decode("#db4549"), Color.decode("#d1e1e1"), Color.decode("#3e6a90"),
        Color.decode("#2e3853"), Color.decode("#a3c9d3")};
    
    // maximum window size
    private final static int maxWindowSize = 500;
    
    private int boxHeight;
    private int boxWidth;
    private int windowWidth;
    private int windowHeight;
    
    // scaling that is done from original rectangle to rectangle that is drawn 
    private double scaling;
    
    // packing solution to draw
    static PackingSolution ps;
    // random number generator
    private static Random rand = new Random();
    
    private Rectangle[] rs;
    private ColoredRectangle[] coloredRectangles;
    private boolean debug = false;
    
    // currently when multiple windows are open they start synchronously,
    // this is intended but can be changed by making the started variable non-static.
    private static boolean started = false;
    private final boolean animate = true;
    private final double animationDuration = 5.0;
    
    private final boolean saveAnimation = false;
    // directory to store animation frame in
    private static String dir = "C:\\Users\\20182300\\OneDrive - TU Eindhoven\\Documents\\Uni\\Y2\\Q3\\2IO90\\animation";
    
    
    // amount of rectangles to be drawn (used for animation and debugging)
    private double count = -1;
    private int currentFrame = 0;
    
    private boolean dashedBox;
    private boolean oneToOne;
    
    public Visualizer(){
        
        if (!debug && !animate){
            count = Integer.MAX_VALUE;
        }
        
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
//        System.out.println(count);
        
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
                ColoredRectangle cr = coloredRectangles[i];
                Rectangle r = cr.r;
                if (r.x < 0){
                    continue;
                }

                // coloured fill
                g.setColor(cr.color);
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
        
        if (started){
            count += (double) rs.length / animationDuration / 60;
        }
    }
    
    public static void visualize(PackingSolution ps_, boolean oneToOne_, boolean dashedBox_){
        
        // preparing the window
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                
                Visualizer v = new Visualizer();
                
                v.dashedBox = dashedBox_;
                v.oneToOne = oneToOne_;
                v.ps = ps_;
                

                if (v.ps.orderedRectangles != null){
                    v.rs = ps.orderedRectangles;
                } else {
                    v.rs = ps.problem.getRectangles();
                }

                v.coloredRectangles = new ColoredRectangle[v.rs.length];
                for (int i = 0; i < v.rs.length; i++){
                    v.coloredRectangles[i] = new ColoredRectangle(v.rs[i], palette[rand.nextInt(palette.length)]);
                }

                v.boxWidth = ps.width;
                if (ps.problem.settings.fixed){
                    v.boxHeight = ps.problem.settings.getMaxHeight();
                } else{
                    v.boxHeight = ps.height;
                }

                if (v.oneToOne){
                    v.windowWidth = v.boxWidth;
                    v.windowHeight = v.boxHeight;
                    v.scaling = 1;
                } else {
                    // Make proper size window
                    final double aspectRatio = (double) v.boxWidth / v.boxHeight;
                    if (aspectRatio > 1){
                        v.windowWidth = maxWindowSize;
                        v.windowHeight = (int) (v.windowWidth / aspectRatio);
                    } else{
                        v.windowHeight = maxWindowSize;
                        v.windowWidth = (int) (v.windowHeight * aspectRatio);
                    }
                    v.scaling = (double) v.windowWidth / v.ps.width;
                }
        
                JFrame frame = new JFrame(String.format("area: %d, density: %.2f", ps_.area(), ps_.density() * 100));
                
                if (v.oneToOne) { frame.setMinimumSize(new Dimension(v.windowWidth+100, v.windowHeight+50)); }
                frame.setPreferredSize(new Dimension(v.windowWidth, v.windowHeight));
                frame.addMouseListener(new MouseAdapter(){
                            @Override
                            public void mouseClicked(MouseEvent e){
                                if (v.debug){
                                    v.count++;
                                    v.repaint();
                                }

                                if (v.animate){
                                    started = !started;
                                    v.count = -1;
                                    v.currentFrame = 0;
                                }
                            }
                });
                frame.add(v);
                frame.pack();
                
                frame.addComponentListener(new ComponentAdapter() {
                    public void componentResized(ComponentEvent componentEvent) {
                        if (!v.oneToOne){
                            Dimension d = frame.getContentPane().getSize();
                            v.windowWidth = d.width;
                            v.windowHeight = d.height;

                            double ratioH = (double) v.windowHeight / v.boxHeight;
                            double ratioW = (double) v.windowWidth / v.boxWidth;

                            v.scaling = Math.min(ratioW, ratioH);
                        }
                    }
                });
                
                if (v.animate){
                    // animation loop of ~60fps
                    ActionListener taskPerformer = new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            if (v.started && v.count <= 1.5 * v.rs.length){
                                frame.repaint();

                                if (v.saveAnimation){
                                    BufferedImage image = new BufferedImage(v.windowWidth, v.windowHeight, BufferedImage.TYPE_INT_RGB);
                                    Graphics2D graphics2D = image.createGraphics();
                                    v.paint(graphics2D);

                                    try {
                                        ImageIO.write(image,"png", new File(dir + "\\test" + String.format("%04d", (v.currentFrame++)) + ".png"));
                                    } catch(Exception exc) {
                                        System.out.println(exc);
                                    }
                                }
                            }
                        }
                    };
                    new Timer((int) 1000.0 / 60, taskPerformer).start();
                }
                frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                frame.setVisible(true);
            }
        });
        
        
    }
}

class ColoredRectangle {
    Rectangle r;
    Color color;
    
    ColoredRectangle(Rectangle r, Color color){
        this.r = r;
        this.color = color;
    }
}
