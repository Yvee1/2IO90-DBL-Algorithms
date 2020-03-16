
public class Rectangle implements Cloneable {
    public int w, h;
    public int x, y;
    public boolean rotated;
    public int id;

    /**
     * A rectangle with a specified width and height.
     */
    public Rectangle(int w, int h) {
        this.w = w;
        this.h = h;

        /* Set position to (-1, -1) to catch potential errors. */
        this.x = this.y = -1;
        this.rotated = false;
    }
    
    public Rectangle(int x, int y, int w, int h) {
        this.w = w;
        this.h = h;

        this.x = x;
        this.y = y;
        
        this.rotated = false;
    }

    public Rectangle(Rectangle r) {
        this.w = r.w;
        this.h = r.h;

        this.x = r.x;
        this.y = r.y;
        this.rotated = r.rotated;
        this.id = r.id;
    }

    @Override
    public Rectangle clone() {
        return new Rectangle(this);
    }

    public int getX() { return this.x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return this.y; }
    public void setY(int y) { this.y = y; }
    
    public void setPos(int x, int y){ this.x = x; this.y = y; };

    public int getHeight() { return this.h; }
    public void setHeight(int height) { this.h = height;}

    public int getWidth() { return this.w; }
    public void setWidth(int width) { this.w = width; }
    
    public int getShorterSide() { return Math.min(this.w, this.h); }
    public int getLongerSide() { return Math.max(this.w, this.h); }

    public int getVerticalReach() { return this.y + this.h; }
    public int getHorizontalReach() { return this.x + this.w; }

    public int getArea() {
        return this.w * this.h;
    }

    /**
     * Rotate the rectangle by swapping width and height.
     */
    public Rectangle rotate() {
        this.rotated = !this.rotated;
        int tmp = w;
        this.w = this.h;
        this.h = tmp;
        return this;
    }

    /**
     * Return the string describing the unrotated width and height.
     * @return the string describing the unrotated width and height.
     */
    public String getSizeString() {
        if (this.rotated) {
            return String.format("%d %d", this.h, this.w);
        } else {
            return String.format("%d %d", this.w, this.h);
        }
    }

    /**
     * Get a string representation of the rectangle's position.
     * @param rotation whether rotations are allowed.
     * @return The string representation of the rectangle's position.
     */
    public String getPositionString(boolean rotation) {
        if (rotation) {
            return String.format("%s %d %d", this.rotated ? "yes" : "no", x, y);
        } else {
            return String.format("%d %d", this.x, this.y);
        }
    }
    
    @Override
    public String toString(){
        return String.format("{pos: (%d, %d), size: (%d, %d)", x, y, w, h);
    }
    
    /**
     * Check whether the given rectangle is covered by the object.
     * @param other
     * @return whether this covers other
     */
    public boolean covers(Rectangle other){
        return (this.getVerticalReach() >= other.getVerticalReach() &&
                this.getHorizontalReach() >= other.getHorizontalReach() &&
                this.getX() <= other.getX() &&
                this.getY() <= other.getY());
    }
    
    /**
     * Reverse of covers
     * @param r
     * @return whether r covers this rectangle
     */
    public boolean isCoveredBy(Rectangle r){
        return r.covers(this);
    }
    
    /**
     * Returns whether two rectangles overlap
     * @param r1 First rectangle
     * @param r2 Second rectangle
     * @return whether r1 and r2 overlap
     */
    public static boolean overlaps(Rectangle r1, Rectangle r2){
        return ( r2.x < r1.getHorizontalReach() && r2.y < r1.getVerticalReach()
              && r1.x < r2.getHorizontalReach() && r1.y < r2.getVerticalReach());
    }

}