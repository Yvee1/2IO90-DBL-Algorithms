/**
 *
 * @author Steven van den Broek
 */

public class DownScaleSolver implements AlgorithmInterface {
    // Downscale such that all sides have height and width <= maxLength
    private final int maxLength = 16;
    
    @Override
    public PackingSolution solve(PackingProblem p) throws InterruptedException {
        int largestSide = Math.max(p.getLargestHeight(), p.getLargestWidth());
        
        //double scale = Math.min((double) maxLength / largestSide, 1.0);

        Fraction scale = new Fraction(maxLength, largestSide);

        /* If the scale is > 1, set it to 1. */
        if (scale.numerator > scale.denominator) {
            scale = Fraction.ONE;
        }

        /* With these conditions, ordinary brute-force can be ran. */
        if ((p.largestHeight < 3500 && p.largestWidth < 3500) ||
                (p.rectangles.length <= 10 && p.largestHeight < 5300 && p.largestWidth < 5300) ||
                (p.rectangles.length <= 4 && p.largestHeight < 8000 && p.largestWidth < 8000)) {
            scale = Fraction.ONE;
        }

        //System.out.format("Scale %f\n", scale);
        
        PackingProblem smallP = downScale(p, scale);
        AlgorithmInterface brute = new BruteForceSolver();

        PackingSolution smallSol = brute.solve(smallP);

        /* Invert the scale, to scale back up. */
        Fraction inv_scale = scale.inverse();

        for (Rectangle r: smallSol.problem.rectangles){
            ((DownScaledRectangle) r).original.setPos(
                    Fraction.mul_floor(inv_scale, r.getX()),
                    Fraction.mul_floor(inv_scale, r.getY()));
        }


        SteinbergSolver.shake(p);
        return new PackingSolution(p);
    }
    
    /**
     * Down scale problem, round up rectangles, rounding down fixed height
     * @param p The PackingProblem 
     * @param scale
     * @modifies nothing
     * @return 
     */
    private PackingProblem downScale(PackingProblem p, Fraction scale){
        Rectangle[] rs = p.rectangles;
        Rectangle[] downScaledRs = new Rectangle[rs.length];

        // Copy settings
        PackingSettings ps = new PackingSettings(p.settings);

        // Downscale max height, *rounds down*
        if(ps.fixed){
            int scaledMax = Fraction.mul_floor(scale, p.settings.maxHeight);
            ps.setMaxHeight(scaledMax);
            //System.out.format("Height limit: %d\n", ps.getMaxHeight());
        }

        for (int i = 0; i < rs.length; i++){
            downScaledRs[i] = new DownScaledRectangle(rs[i], scale);
            //System.out.println(downScaledRs[i]);
        }
        
        return new PackingProblem(ps, downScaledRs);
    }
    
    
    class DownScaledRectangle extends Rectangle {
        // reference to the original rectangle
        Rectangle original;
        
        DownScaledRectangle(DownScaledRectangle r){
            super(r);
            original = r.original;
        }
        
        DownScaledRectangle(Rectangle r, double scale){
            super(r);
            original = r;
            downScale(scale);
        }

        DownScaledRectangle(Rectangle r, Fraction scale){
            super(r);
            original = r;
            downScale(scale);
        }
        
        /**
        * Down scale rectangle, rounding up
        * @param scale The scale to use
        * @modifies this
        */
       private DownScaledRectangle downScale(double scale){
           setWidth((int) Math.ceil(getWidth() * scale));
           setHeight((int) Math.ceil(getHeight() * scale));
           return this;
       }

        private DownScaledRectangle downScale(Fraction scale){
            setWidth(Fraction.mul_ceil(scale, getWidth()));
            setHeight(Fraction.mul_ceil(scale, getHeight()));
            return this;
        }
       
       @Override
       public DownScaledRectangle clone(){
           return new DownScaledRectangle(this);
       }
    }
    
}

class Fraction {

    /**
     * A minimal fraction representation of 1.
     */
    public static final Fraction ONE = new Fraction(1, 1);

    int numerator, denominator;

    public Fraction(int a, int b) {
        int gcd = gcd(a, b);
        numerator = a / gcd; denominator = b / gcd;
    }

    /**
     * Return the inverse of the fraction f.
     * @return 1 / f
     */
    public Fraction inverse() {
        return new Fraction(denominator, numerator);
    }

    /**
     * Multiply a value with a fraction, and return the floor of the result.
     * @param s The fraction.
     * @param val The value to multiply s with.
     * @return floor(s * val)
     */
    static int mul_floor(Fraction s, int val) {

        /* Calculate the numerator of the result */
        int prod = s.numerator * val;

        /* It is prod = k * s.denominator + r */
        /* To get k, subtract r and divide by s.denominator. */
        //return (prod - (prod % s.denominator)) / s.denominator;
        return prod / s.denominator; // integer division = floor

    }

    /**
     * Multiply a value with a fraction, and return the ceiling of the result.
     * @param s The fraction.
     * @param val The value to multiply s with.
     * @return ceil(s * val)
     */
    static int mul_ceil(Fraction s, int val) {

        /* Calculate the numerator of the result */
        int prod = s.numerator * val;

        /* It is prod = k * s.denominator + r */
        /* When r == 0, k = res.numerator / res.denominator */
        /* When r != 0, take the floor and add 1. */
        int remainder = prod % s.denominator;
        int floor = prod / s.denominator;

        return remainder == 0 ? floor : floor + 1;

    }

    private static int gcd(int p, int q) {
        while (q != 0) {
            int temp = q;
            q = p % q;
            p = temp;
        }
        return p;
    }

}
