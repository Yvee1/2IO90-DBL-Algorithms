package packingsolver;

import java.util.ArrayList;

public class PackingProblem {

    /* The settings for this problem instance. */
    public PackingSettings settings;

    /* The rectangles for this problem instance. */
    public ArrayList<Rectangle> rectangles;

    public PackingProblem(PackingSettings settings, ArrayList<Rectangle> rectangles) {
        this.settings = settings;
        this.rectangles = rectangles;
    }

    public PackingSettings getSettings() { return this.settings; }
    public void setSettings(PackingSettings settings) { this.settings = settings; }

    public ArrayList<Rectangle> getRectangles() { return this.rectangles; }
    public void setRectangles(ArrayList<Rectangle> rectangles) { this.rectangles = rectangles; }

}