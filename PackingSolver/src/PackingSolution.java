

public class PackingSolution {

    public int width, height;
    public PackingProblem problem;
    
    public Rectangle[] orderedRectangles = null;

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
        this.problem = p;
        this.width = width;
        this.height = height;
    }
    
    public PackingSolution(PackingProblem p, Rectangle[] orderedRectangles) {
        this(p);
        this.orderedRectangles = orderedRectangles;
    }

    public int area(){
        if (problem.settings.fixed){
            return width * problem.settings.maxHeight;
        } else {
            return width * height;
        }
    }
    
    public double density(){
        int usedArea = 0;
        for (Rectangle r : problem.getRectangles()){
            usedArea += r.getArea();
        }
        
        return (double) usedArea / area();
    }
    
    public boolean hasOverlap(){
        boolean hasOverlap = false;
        for (Rectangle r1 : problem.rectangles){
            for (Rectangle r2 : problem.rectangles){
                if (r1 == r2){ continue; }
                hasOverlap = Rectangle.overlaps(r1, r2) || hasOverlap;
            }
        }
        
        return hasOverlap;
    }
}