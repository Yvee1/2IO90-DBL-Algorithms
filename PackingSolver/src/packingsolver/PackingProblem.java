package packingsolver;
public class PackingProblem {

    /* The settings for this problem instance. */
    public PackingSettings settings;

    /* The rectangles for this problem instance. */
    public Rectangle[] rectangles;

    public PackingProblem(PackingSettings settings, Rectangle[] rectangles) {
        this.settings = settings;
        this.rectangles = rectangles;
    }

    public PackingSettings getSettings() { return this.settings; }
    public void setSettings(PackingSettings settings) { this.settings = settings; }

    public Rectangle[] getRectangles() { return this.rectangles; }
    public void setRectangles(Rectangle[] rectangles) { this.rectangles = rectangles; }

}