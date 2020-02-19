

public class PackingSolution {

    public int width, height;
    public PackingProblem problem;

    public PackingSolution(PackingProblem p) {
        this.problem = p;
        width = 0;
        height = 0;
        
        for (Rectangle r : p.getRectangles()){
            if (r.x + r.w > width){
                width = r.x + r.w;
            }
            if (r.y + r.h > height){
                height = r.y + r.h;
            }
        }
    }

    public PackingSolution(PackingProblem p, int width, int height) {
        this(p);
        this.width = width;
        this.height = height;
    }

}