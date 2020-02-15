
import java.util.ArrayList;

public class IngalaSolver implements AlgorithmInterface {
    PackingProblem pp;
    
    // large constant
    final int C = 1000;
    
    // optimal height
    private int OPT;
    
    // fixed width
    private int W;
    
    // alpha in [1/3, 1/2), constant for determining 'tall' rectangles
    private final double alpha = 1/3;
    
    /* 
        0 < epsilon < alpha, 1/epsilon in N. 
        epsilon >= deltaH > muH > 0
        epsilon >= deltaW > muW > 0
    */
    private final double epsilon = 1/10; // currently a random value, prob. incorrect
    
    // split between medium / vertical rectangles
    private final double deltaH = 1/11; // incorrect value, but then it compiles
    
    // split between medium / horizontal rectangles
    // deltaW != deltaH
    private final double deltaW = epsilon * deltaH / 12;
    
    // split between small / medium rectangles (vertical direction)
    private final double muH = f(deltaH);
    
    // split between small / medium rectangles (horizontal direction) 
    // muW != muH
    private final double muW = epsilon * muH / 12;
    
    // a constant, used for rounding
    private final double gamma = epsilon * deltaH / 2;
    
    // represents Large, Tall, Vertical, Horizontal, Small, Medium rectangles
    enum Category {
        L, T, V, H, S, M
    }
    
    private CategorizedRectangle[] crs;
    private ArrayList<CategorizedRectangle> largeRs;
    private ArrayList<CategorizedRectangle> tallRs;
    private ArrayList<CategorizedRectangle> verticalRs;
    private ArrayList<CategorizedRectangle> horizontalRs;
    private ArrayList<CategorizedRectangle> smallRs;
//    private ArrayList<CategorizedRectangle> nonSmallRs;
//    private ArrayList<CategorizedRectangle> LTV;
    private ArrayList<CategorizedRectangle> mediumRs;
    // Rectangles in set A := {R is Medium | height in (muH*OPT, deltaH*OPT)}
    private ArrayList<CategorizedRectangle> A;
    // nonA = mediumRs - A
    private ArrayList<CategorizedRectangle> nonA;
    
    private double f(double x){
        return Math.pow(epsilon * x, C / (epsilon*x));
    }
    
    @Override
    public PackingSolution solve(PackingProblem p){
        this.pp = p;
        // set some variables
        W = p.getSettings().getMaxHeight();
        final Rectangle[] rs = p.getRectangles();
        
        // categorize rectangles
        crs = new CategorizedRectangle[rs.length];
        for (int i = 0; i < rs.length; i++){
            CategorizedRectangle cr = new CategorizedRectangle(rs[i]);
            crs[i] = cr;
            switch (cr.c){
                case L:
                    largeRs.add(cr);
                    break;
                case T:
                    tallRs.add(cr);
                    break;
                case V:
                    verticalRs.add(cr);
                    break;
                case H:
                    horizontalRs.add(cr);
                    break;
                case S:
                    smallRs.add(cr);
                    break;
                case M:
                    mediumRs.add(cr);
                    final int h = cr.getHeight();
                    if (h > muH * OPT && h < deltaH * OPT){
                        A.add(cr);
                    } else {
                        nonA.add(cr);
                    }
                    break;
                default:
                    throw new AssertionError(cr.c.name());
            }
         }
    
        packNonSmall();
        return new PackingSolution(p);
    }

    // packs the nonSmall rectangles...
    private void packNonSmall() {
        packMedium();
    }
    
    private void packMedium(){
        Box mHor = packInBMhor();
        Box mVer = packInBMver();
    }
    
    // pack certain medium rectangles in a box called B_M,hor
    private Box packInBMhor(){
        final int stripWidth = pp.getSettings().maxHeight;
        final int n = A.size();
        PackingSettings ps = new PackingSettings(true, stripWidth, false, n);
        Rectangle[] arrayA = new Rectangle[n];
        for (int i = 0; i < n; i++){
            arrayA[i] = A.get(i).r;
        }
        PackingProblem pp = new PackingProblem(ps, arrayA);
        AlgorithmInterface nfdh = new NFDH();
        PackingSolution sol = nfdh.solve(pp);
        return new Box(sol.height, sol.width, arrayA);
    }
    
    // pack certain medium rectangles in a box called B_M,ver
    private Box packInBMver(){
//         alpha * OPT not necessarily integer I think
        final int stripWidth = (int) alpha * OPT;
        final int n = nonA.size();
        PackingSettings ps = new PackingSettings(true, stripWidth, false, n);
        Rectangle[] arrayNonA = new Rectangle[n];
        for (int i = 0; i < n; i++){
            arrayNonA[i] = nonA.get(i).r;
        }
        PackingProblem pp = new PackingProblem(ps, arrayNonA);
        AlgorithmInterface nfdh = new NFDH();
        PackingSolution sol = nfdh.solve(pp);
        Box mVer = new Box(sol.height, sol.width, arrayNonA);
        mVer.rotate();
        return mVer;
    }
    
    class Box extends Rectangle {
        Rectangle[] rectangles;
        
        public Box(int w, int h, Rectangle[] rs){
            super(w, h);
            this.rectangles = rs;
        }
        
    }
    
    /*
    Rectangle that has a category associated with it.
    Furthermore, height and width are reversed to match the Ingala paper.
    */
    class CategorizedRectangle {
        public Category c;
        public Rectangle r;
        private int possiblyRoundedWidth;
        private int possiblyRoundedHeight;
        
        public CategorizedRectangle(Rectangle r){
            this.r = r;
            determineCategory();
            if (c.equals(Category.L) || c.equals(Category.T) || c.equals(Category.V)){
                possiblyRoundedWidth = roundUp(r.getWidth(), (int) (gamma * OPT));
                possiblyRoundedHeight = roundUp(r.getHeight(), (int) (gamma * OPT));
            } else{
                possiblyRoundedWidth = r.getWidth();
                possiblyRoundedHeight = r.getHeight();
            }
        }
        
        private int roundUp(int numToRound, int multiple){
            if (multiple == 0){
                return numToRound;
            }
            
            final int remainder = numToRound % multiple;
            if (remainder == 0){
                return numToRound;
            }
            
            return numToRound + multiple - remainder;
        }
        
        public int getWidth(){
            return possiblyRoundedHeight;
        }
        
        public int getActualWidth(){
            return r.getWidth();
        }
        
        public int getHeight(){
            return possiblyRoundedWidth;
        }
        
        public int getActualHeight(){
            return r.getHeight();
        }

        private void determineCategory(){
            final int w = getWidth();
            final int h = getHeight();
            
            // large
            if (h >= deltaH * OPT && w >= deltaW * W){
                c = Category.L;
            }
            // tall
            else if (h > alpha * OPT && w < deltaW * W){
                c = Category.T;
            }
            // vertical
            else if (h > deltaH * OPT && h < alpha * OPT && w <= muW * W){
                c = Category.V;
            } 
            // horizontal
            else if (h < muH * OPT && w >= deltaW * W){
                c = Category.H;
            }
            // small
            else if (h <= muH * OPT && w <= muW * W){
                c = Category.S;
            }
            // medium
            else {
                c = Category.M;
            }
        }
    }
}

