
import java.util.ArrayList;

public class IngalaSolver implements AlgorithmInterface {
    PackingProblem pp;
    
    // large constant
    final int C = 3;
    
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
    private final double epsilon = (double) 1/4; // currently a random value, prob. incorrect
    
    // split between medium / vertical rectangles
    private final double deltaH = (double) 1/3; // incorrect value, but then it compiles
    
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
    private ArrayList<CategorizedRectangle> largeRs = new ArrayList<>();
    private ArrayList<CategorizedRectangle> tallRs= new ArrayList<>();
    private ArrayList<CategorizedRectangle> verticalRs= new ArrayList<>();
    private ArrayList<CategorizedRectangle> horizontalRs= new ArrayList<>();
    private ArrayList<CategorizedRectangle> smallRs= new ArrayList<>();
    private ArrayList<CategorizedRectangle> mediumRs =  new ArrayList<>();
    // Rectangles in set A := {R is Medium | height in (muH*OPT, deltaH*OPT)}
    private ArrayList<CategorizedRectangle> A = new ArrayList<>();
    // nonA = mediumRs - A
    private ArrayList<CategorizedRectangle> nonA = new ArrayList<>();
    
    private double f(double x){
        return Math.pow(epsilon * x, C / (epsilon*x));
    }
    
    @Override
    public PackingSolution solve(PackingProblem p){
        // lower bound
        OPT = p.largestWidth;
        this.pp = p;
        // set some variables
        W = p.getSettings().getMaxHeight();
        final Rectangle[] rs = p.getRectangles();
        
        
        System.out.println("Large when h >= " + deltaH * OPT + ", w >= " + deltaW * W);
        System.out.println("Small when h <= " + muH * OPT + ", w <= " + muW * W);
        //h <= muH * OPT && w <= muW * W
        
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
        if (n == 0){
            return new Box(0, 0, new Rectangle[]{});
        }
        System.out.println(n);
        PackingSettings ps = new PackingSettings(true, stripWidth, false, n);
        Rectangle[] arrayA = new Rectangle[n];
        for (int i = 0; i < n; i++){
            arrayA[i] = A.get(i).r;
            System.out.println(A.get(i).r);
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
        if (n == 0){
            return new Box(0, 0, new Rectangle[]{});
        }
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
        mVer.translate(n, n);
        return mVer;
    }
    
    class Box extends Rectangle {
        Rectangle[] rectangles;
        
        public Box(int w, int h, Rectangle[] rs){
            super(w, h);
            this.rectangles = rs;
        }
        
        @Override
        public void rotate(){
            super.rotate();
            for (Rectangle r : rectangles){
                r.rotate();
            }
        }
        
        public void translate(int dx, int dy){
            for (Rectangle r : rectangles){
                r.setPos(r.getWidth() + dx, r.getHeight() + dy);
            }
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
            return r.getHeight();
        }
        
        public int getHeight(){
            return possiblyRoundedWidth;
        }
        
        public int getActualHeight(){
            return r.getWidth();
        }

        private void determineCategory(){
            final int w = getActualWidth();
            final int h = getActualHeight();
            System.out.println("Width " + w + ", height: " + h);
            
            // large
            if (h >= deltaH * OPT && w >= deltaW * W){
                System.out.println("Large");
                c = Category.L;
            }
            // tall
            else if (h > alpha * OPT && w < deltaW * W){
                System.out.println("Tall");
                c = Category.T;
            }
            // vertical
            else if (h > deltaH * OPT && h < alpha * OPT && w <= muW * W){
                System.out.println("Vertical");
                c = Category.V;
            } 
            // horizontal
            else if (h < muH * OPT && w >= deltaW * W){
                System.out.println("Horizontal");
                c = Category.H;
            }
            // small
            else if (h <= muH * OPT && w <= muW * W){
                System.out.println("Small");
                c = Category.S;
            }
            // medium
            else {
                System.out.println("Medium");
                c = Category.M;
            }
        }
    }
}

