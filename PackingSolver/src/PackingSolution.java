

public class PackingSolution {

    public int width, height;
    public PackingProblem problem;

    public PackingSolution(PackingProblem p) { this.problem = p; }

    public PackingSolution(PackingProblem p, int width, int height) {
        this(p);
        this.width = width;
        this.height = height;
    }

}