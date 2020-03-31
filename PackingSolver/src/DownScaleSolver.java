/**
 *
 * @author Steven van den Broek & Martijn Leus
 */

public class DownScaleSolver implements AlgorithmInterface {
    // Downscale such that all sides have height and width <= maxLength
    private final AlgorithmInterface brute = new BruteForceSolver();
    private int largestSide;
    
    // last down scaled fixed height calculated
    private int downScaledFixedHeight = -1;
    
    // original packing problem
    private PackingProblem p;
    
    private boolean debug = false;
    
    // may run up to 25 seconds
    private int maxRunTime = 25;
    private long endTime;
    
    DownScaleSolver(){}
    DownScaleSolver(boolean debug){ this.debug = debug; }
    
    @Override
    public PackingSolution solve(PackingProblem p) throws InterruptedException {
        if (p.rectangles.length > 25){
            return new PackingSolution(p);
        }
                
        endTime = System.currentTimeMillis() + maxRunTime * 1000;
        
        this.p = p;
        largestSide = Math.max(p.getLargestHeight(), p.getLargestWidth());
//        largestSide = Math.min(Math.max(p.getLargestHeight(), p.getLargestWidth()), p.settings.maxHeight);
        
        // whether brute force can solve it under ~25 seconds
        boolean runnable = false;
        if (p.rectangles.length == 25){
            if (p.settings.fixed){
                if (p.totalHeight < 1000 && p.totalWidth < 1000){
                    runnable = true;
                }
            } else {
                if (p.largestHeight < 5 && p.largestWidth < 5){
                    runnable = true;
                }
            }
        }
        
        if (p.rectangles.length == 10){
            if (p.settings.fixed){
                if (p.totalHeight < 1000 && p.totalWidth < 1000){
                    runnable = true;
                }
            } else {
                if (p.largestHeight < 10 && p.largestWidth < 10){
                    runnable = true;
                }
            }
        }
        
        if (p.rectangles.length == 6){
            if (p.settings.fixed){
                if (p.totalHeight < 5000 && p.totalWidth < 5000){
                    runnable = true;
                }
            } else {
                if (p.largestHeight < 30 && p.largestWidth < 30){
                    runnable = true;
                }
            }
        }
        
        if (p.rectangles.length == 4){
            if (p.settings.fixed){
                if (p.totalHeight < 10000 && p.totalWidth < 10000){
                    runnable = true;
                }
            } else {
                if (p.largestHeight < 40 && p.largestWidth < 40){
                    runnable = true;
                }
            }
        }
        
        // if no rotations and possible to solver, just run brute
        if (runnable && !p.settings.rotation){
            return brute.solve(p);
        } else if (runnable && p.settings.rotation) {
            // if possible to solve and rotations allowed
            // run normal scale brute force with different rotations
            return runRotationsWithDifferentMaxLengths(largestSide);
        } else if (p.rectangles.length <= 10 && p.settings.rotation){
            // if not possible to solve by brute force on normal scale
            // and rotations are allowed. Just start with maxLength 10
            return runRotationsWithDifferentMaxLengths(10);
        } else {
            // if no rotations allowed and not possible to solve by
            // brute force on normal scale. Start with maxLength=10 downscaling
            return runWithDifferentMaxLengths(10);
        }
    }
    
    private PackingSolution runWithRotations(PackingProblem p, int maxLength, long timeLeft) throws InterruptedException {
        int iterations = (int) Math.pow(2, p.rectangles.length);
        PackingSolution bestSolution = null;
        String bestSwitches = null;
        
        for (int i = 0; i < iterations && timeLeft > 0; i++, timeLeft = endTime - System.currentTimeMillis()){
            PackingProblem newP = new PackingProblem(p);
            String switches = Integer.toBinaryString(i);
            
            // whether it's possible to pack the rectangles
            boolean impossible = false;
            
            for (int j = 0; j < newP.rectangles.length; j++){
                if (j < switches.length() && switches.charAt(j) == '1'){
                    newP.rectangles[j].rotate();
                    if (newP.rectangles[j].getHeight() > newP.settings.maxHeight){
                        impossible = true;
                    }
                }
            }
            
            if (impossible){
                continue;
            }
            PackingSolution sol = run(newP, maxLength, timeLeft);
            
            if (debug){
                System.out.format("Rotation %s: %d\n", switches, sol.area());
            }
            
            if (bestSolution == null || sol.area() < bestSolution.area()){
                bestSolution = sol;
                bestSwitches = switches;
            }
        }
        
        if (debug){
            System.out.println("--------------------");
            System.out.format("Best rotations: %s with area %d\n", bestSwitches, bestSolution.area());
        }
        
        return bestSolution;
    }
    
    private PackingSolution runWithDifferentMaxLengths(int startLength) throws InterruptedException {
        PackingSolution bestSolution = null;
        int bestMaxLength = -1;

        long timeLeft = maxRunTime;
        
        for (int maxLength = startLength; timeLeft > 0; maxLength++, timeLeft = endTime - System.currentTimeMillis()){
            PackingSolution sol = run(new PackingProblem(p), maxLength, timeLeft);
                     
            if (debug){
                System.out.format("maxLength %d: %d\n", maxLength, sol.area());
            }
            
            if (bestSolution == null || sol.area() < bestSolution.area()){
                bestSolution = sol;
                bestMaxLength = maxLength;
            }
        }
        
        if (debug){
            System.out.println("--------------------");
            System.out.format("Best maxLength: %d with area %d\n", bestMaxLength, bestSolution.area());
        }
        
        return bestSolution;
    }
    
    private PackingSolution runRotationsWithDifferentMaxLengths(int startLength) throws InterruptedException {
        PackingSolution bestSolution = null;
        int bestMaxLength = -1;

        long timeLeft = maxRunTime;
        
        for (int maxLength = startLength; timeLeft > 0; maxLength++, timeLeft = endTime - System.currentTimeMillis()){
            PackingSolution sol = runWithRotations(new PackingProblem(p), maxLength, timeLeft);
                     
            if (debug){
                System.out.format("maxLength %d: %d\n", maxLength, sol.area());
            }
            
            if (bestSolution == null || sol.area() < bestSolution.area()){
                bestSolution = sol;
                bestMaxLength = maxLength;
            }
        }
        
        if (debug){
            System.out.println("--------------------");
            System.out.format("Best maxLength: %d with area %d\n", bestMaxLength, bestSolution.area());
        }
        
        return bestSolution;
    }
    
    private PackingSolution run(PackingProblem p, int maxLength, long timeLeft) throws InterruptedException {
        Fraction scale = new Fraction(maxLength, largestSide);

        /* If the scale is > 1, set it to 1. */
        if (scale.numerator > scale.denominator) {
            scale = Fraction.ONE;
        }
        
        PackingProblem smallP = downScale(p, scale);
        PackingSolution smallSol = new BruteForceSolver((int) (timeLeft / 1000)).solve(smallP);

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
            int scaledMax = Math.max(Fraction.mul_floor(scale, p.settings.maxHeight), 1);
            downScaledFixedHeight = scaledMax;
            ps.setMaxHeight(scaledMax);
//            System.out.format("Height limit: %d\n", ps.getMaxHeight());
        } else {
            downScaledFixedHeight = Integer.MAX_VALUE;
        }

        for (int i = 0; i < rs.length; i++){
            downScaledRs[i] = new DownScaledRectangle(rs[i], scale);
//            System.out.println(downScaledRs[i]);
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
           setHeight(Math.min((int) Math.ceil(getHeight() * scale), downScaledFixedHeight));
           return this;
       }

        private DownScaledRectangle downScale(Fraction scale){
            setWidth(Fraction.mul_ceil(scale, getWidth()));
            setHeight(Math.min(Fraction.mul_ceil(scale, getHeight()), downScaledFixedHeight));
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

    public String toString(){
        return String.format("%d / %d", numerator, denominator);
    }
}
