
import java.util.ArrayList;

public class IngalaSolver implements AlgorithmInterface {
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
    
    private double f(double x){
        return Math.pow(epsilon * x, C / (epsilon*x));
    }
    
    @Override
    public PackingSolution solve(PackingProblem p){
        // set some variables
        W = p.getSettings().getMaxHeight();
        final Rectangle[] rs = p.getRectangles();
        
        // categorize rectangles
        crs = new CategorizedRectangle[rs.length];
        for (int i = 0; i < rs.length; i++){
            crs[i] = new CategorizedRectangle(rs[i]);
        }
        
        // divide into small and nonSmall rectangles
        ArrayList<CategorizedRectangle> small = new ArrayList<>();
        ArrayList<CategorizedRectangle> nonSmall = new ArrayList<>();
        
        Pair<ArrayList<CategorizedRectangle>,
                 ArrayList<CategorizedRectangle>> rectangles = splitOnCategory(Category.S, crs);
        small = rectangles.a;
        nonSmall = rectangles.b;
        
        packNonSmall(nonSmall);
        return new PackingSolution(p);
    }
    
    // packs the nonSmall rectangles...
    private void packNonSmall(ArrayList<CategorizedRectangle> nonSmall) {
        ArrayList<CategorizedRectangle> medium = new ArrayList<>();
        ArrayList<CategorizedRectangle> nonMedium = new ArrayList<>();
        Pair<ArrayList<CategorizedRectangle>,
                 ArrayList<CategorizedRectangle>> rectangles = splitOnCategory(Category.M, nonSmall);
        medium = rectangles.a;
        nonMedium = rectangles.b;
    }
    
    /*
    Rectangle that has a category associated with it.
    Furthermore, height and width are reversed to match the Ingala paper.
    */
    class CategorizedRectangle extends Rectangle {
        public Category c;
        
        public CategorizedRectangle(int w, int h) {
            super(w, h);
        }
        
        public CategorizedRectangle(Rectangle r){
            super(r);
            determineCategory();
        }
        
        @Override
        public int getWidth(){
            return h;
        }
        
        @Override
        public int getHeight(){
            return w;
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
    
    private Pair<ArrayList<CategorizedRectangle>,
                 ArrayList<CategorizedRectangle>>
                                splitOnCategory(Category c, ArrayList<CategorizedRectangle> crs){
        ArrayList<CategorizedRectangle> ofCategory = new ArrayList<>();
        ArrayList<CategorizedRectangle> notOfCategory = new ArrayList<>();
        for (CategorizedRectangle cr : crs){
            if (cr.c.equals(Category.S)){
                ofCategory.add(cr);
            } else{
                notOfCategory.add(cr);
            }
        }
        
        return new Pair(ofCategory, notOfCategory);
    }
                                
    private Pair<ArrayList<CategorizedRectangle>,
                 ArrayList<CategorizedRectangle>>
                                splitOnCategory(Category c, CategorizedRectangle[] crs){
        ArrayList<CategorizedRectangle> ofCategory = new ArrayList<>();
        ArrayList<CategorizedRectangle> notOfCategory = new ArrayList<>();
        for (CategorizedRectangle cr : crs){
            if (cr.c.equals(Category.S)){
                ofCategory.add(cr);
            } else{
                notOfCategory.add(cr);
            }
        }
        
        return new Pair(ofCategory, notOfCategory);
    }                            
    
    class Pair<A, B> {
        public A a;
        public B b;
        
        public Pair(A a, B b){
            this.a = a;
            this.b = b;
        }
    }
}

