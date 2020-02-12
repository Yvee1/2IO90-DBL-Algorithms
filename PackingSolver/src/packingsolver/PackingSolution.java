package packingsolver;

import java.util.ArrayList;

public class PackingSolution {
    
    public int w, h;
    public ArrayList<Rectangle> solution;

    public PackingSolution(ArrayList<Rectangle> solution) { this.solution = solution; }

    public PackingSolution(int width, int height, ArrayList<Rectangle> solution) {
        this(solution);
        this.w = width;
        this.h = height;
    }

}