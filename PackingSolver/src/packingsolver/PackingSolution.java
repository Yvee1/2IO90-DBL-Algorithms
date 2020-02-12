package packingsolver;
public class PackingSolution {

    public int w, h;
    public Rectangle[] solution;

    public PackingSolution(Rectangle[] solution) { this.solution = solution; }

    public PackingSolution(int width, int height, Rectangle[] solution) {
        this(solution);
        this.w = width;
        this.h = height;
    }

}