package packingsolver;
public class PackingProblem {

    /* The settings for this problem instance. */
    public PackingSettings settings;

    /* The rectangles for this problem instance. */
    public Rectangle[] rectangles;

    /* Backup of the original order of the rectangles. */
    private Rectangle[] initialOrderRectangle;

    public int largestWidth;
    public int largestHeight;

    public PackingProblem(PackingSettings settings, Rectangle[] rectangles) {
        this.settings = settings;
        this.rectangles = rectangles;

        /* clone() is a shallow copy, so Rectangle pointers are idential. */
        this.initialOrderRectangle = this.rectangles.clone();
    }

    public PackingSettings getSettings() { return this.settings; }
    public void setSettings(PackingSettings settings) { this.settings = settings; }

    public Rectangle[] getRectangles() { return this.rectangles; }
    public void setRectangles(Rectangle[] rectangles) { this.rectangles = rectangles; }

    public int getLargestWidth() { return this.largestWidth; }
    public int getLargestHeight() { return this.largestHeight; }

    public Rectangle[] getIdentityOrderRectangles() { return this.initialOrderRectangle; }

}