
public class PackingProblem {

    /* The settings for this problem instance. */
    public PackingSettings settings;

    /* The rectangles for this problem instance. */
    public Rectangle[] rectangles;

    public int largestWidth;
    public int largestHeight;
    public int totalWidth;
    public int totalHeight;

    public PackingProblem(PackingSettings settings, Rectangle[] rectangles) {
        this.settings = settings;
        this.rectangles = rectangles;
    }
    
    public PackingProblem(PackingProblem p){
        Rectangle[] toCopy = p.getRectangles();
        Rectangle[] rs = new Rectangle[toCopy.length];
        for (int i = 0; i < toCopy.length; i++){
            rs[i] = toCopy[i].clone();
        }
        
        this.settings = p.settings;
        this.rectangles = rs;
        this.largestWidth = p.largestWidth;
        this.largestHeight = p.largestHeight;
        this.totalWidth = p.totalWidth;
        this.totalHeight = p.totalHeight;
    }

    public PackingSettings getSettings() { return this.settings; }
    public void setSettings(PackingSettings settings) { this.settings = settings; }

    public Rectangle[] getRectangles() { return this.rectangles; }
    public void setRectangles(Rectangle[] rectangles) { this.rectangles = rectangles; }

    public int getLargestWidth() { return this.largestWidth; }
    public int getLargestHeight() { return this.largestHeight; }
    
    public void reset(){
        for (Rectangle r : rectangles){
            r.setPos(-1, -1);
        }
    }

}