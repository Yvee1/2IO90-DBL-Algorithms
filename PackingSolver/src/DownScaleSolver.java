/**
 *
 * @author Steven van den Broek
 */

public class DownScaleSolver implements AlgorithmInterface {
    // Downscale such that all sides have height and width <= maxLength
    private final int maxLength = 10;
    
    @Override
    public PackingSolution solve(PackingProblem p){
        int largestSide = Math.max(p.getLargestHeight(), p.getLargestWidth());
        double scale = (double) maxLength / largestSide;
        System.out.format("Scale %f\n", scale);
        
        PackingProblem smallP = downScale(p, scale);
        AlgorithmInterface brute = new BruteForceSolver();
        try {
        brute.solve(smallP);
        } catch (InterruptedException e) {
            System.err.print(e);
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
            downScaledRs[i] = downScale(rs[i].clone(), scale);
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
    
    /**
     * Down scale rectangle, rounding up
     * @param r Rectangle to scale
     * @param scale The scale to use
     * @modifies r
     */
    private Rectangle downScale(Rectangle r, double scale){
        r.setWidth((int) Math.ceil(r.getWidth() * scale));
        r.setHeight((int) Math.ceil(r.getHeight() * scale));
        return r;
    }
    
    
}
