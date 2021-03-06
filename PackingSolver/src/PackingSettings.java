
public class PackingSettings {

    /* Whether the height is fixed. */
    public boolean fixed = false;

    /* Whether rotations are allowed. */
    public boolean rotation = false;

    /* The number of rectangles. */
    public int n = 0;

    /* The maximum height of the container. */
    public int maxHeight = Integer.MAX_VALUE;

    public PackingSettings() {}

    //this case is only used for the SolverTester
    public PackingSettings(boolean fixed, boolean rotation) {
        this.fixed = fixed;
        this.rotation = rotation;
    }
    

    public PackingSettings(boolean fixed, boolean rotation, int n) {
        this.fixed = fixed;
        this.rotation = rotation;
        this.n = n;
    }

    public PackingSettings(boolean fixed, int maxHeight, boolean rotation, int n) {
        this(fixed, rotation, n);
        this.maxHeight = maxHeight;
    }
    
    public PackingSettings(PackingSettings pp){
        fixed = pp.fixed;
        rotation = pp.rotation;
        n = pp.n;
        maxHeight = pp.maxHeight;
    }

    public boolean getFixed() { return this.fixed; }
    public void setFixed(boolean fixed) { this.fixed = fixed; }

    public boolean getRotation() { return this.rotation; }
    public void setRotation(boolean rotation) { this.rotation = rotation; }

    public int getRectangleCount() { return this.n; }
    public void setRectangleCount(int n) { this.n = n; }

    public int getMaxHeight() { return this.maxHeight; }
    public void setMaxHeight(int maxHeight) { this.maxHeight = maxHeight; }
    
    @Override
    public String toString(){
        return String.format("container height: %s\n"
                + "rotations allowed: %s\n"
                + "number of rectangles: %d", 
                fixed ? "fixed " + maxHeight : "free", rotation ? "yes" : "no", n);
    }
}