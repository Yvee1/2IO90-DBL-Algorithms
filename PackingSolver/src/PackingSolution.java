

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

    public long area(){
        if (problem.settings.fixed){
            return ((long) width) * problem.settings.maxHeight;
        } else {
            return ((long) width) * height;
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

    public boolean isValid() {
        Rectangle[] l = problem.rectangles;
        for (int i = 0; i < l.length; i++) {

            if (l[i].y + l[i].h > problem.settings.maxHeight) { return false; }

            /* The solvers shouldn't have any overlap. */
//            for (int j = i+1; j < l.length; j++) {
//                if (Rectangle.overlaps(l[i], l[j])) { return false; }
//            }

        }
        return true;
    }
}