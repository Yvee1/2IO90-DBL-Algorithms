/**
 *
 * @author Steven van den Broek
 */

public class DownScaleSolver implements AlgorithmInterface {
    // Downscale such that all sides have height and width <= maxLength
    private final int maxLength = 10;
    
    @Override
    public PackingSolution solve(PackingProblem p) throws InterruptedException {
        int largestSide = Math.max(p.getLargestHeight(), p.getLargestWidth());
        double scale = (double) maxLength / largestSide;
        System.out.format("Scale %f\n", scale);
        
        PackingProblem smallP = downScale(p, scale);
        AlgorithmInterface brute = new BruteForceSolver();

        PackingSolution smallSol = brute.solve(smallP);

        for (Rectangle r: smallSol.problem.rectangles){
            ((DownScaledRectangle) r).original.setPos((int) (r.getX() / scale), (int) (r.getY() / scale));
        }
        
        return new PackingSolution(p);
    }
    
    /**
     * Down scale problem, round up rectangles, rounding down fixed height
     * @param p The PackingProblem 
     * @param scale
     * @modifies nothing
     * @return 
     */
    private PackingProblem downScale(PackingProblem p, double scale){
        Rectangle[] rs = p.rectangles;
        Rectangle[] downScaledRs = new Rectangle[rs.length];
        
        for (int i = 0; i < rs.length; i++){
            downScaledRs[i] = new DownScaledRectangle(rs[i], scale);
            System.out.println(downScaledRs[i]);
        }
        
        // Copy settings
        PackingSettings ps = new PackingSettings(p.settings);
        
        // Downscale max height, *rounds down*
        if(ps.fixed){
            ps.setMaxHeight((int) (p.settings.maxHeight * scale));
            System.out.format("Height limit: %d\n", ps.getMaxHeight());
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
       
       @Override
       public DownScaledRectangle clone(){
           return new DownScaledRectangle(this);
       }
    }
    
}
