
public class Space {
    public int w, h;
    public int x, y;
    public boolean rotated;

    /**
     * A rectangle with a specified width and height.
     */
    public Space(int w, int h) {
        this.w = w;
        this.h = h;

        /* Set position to (-1, -1) to catch potential errors. */
        this.x = this.y = -1;
        this.rotated = false;
    }

    public Space(Rectangle r) {
        this.w = r.w;
        this.h = r.h;

        this.x = r.x;
        this.y = r.y;
        this.rotated = r.rotated;
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

    public int getReach() { return this.y + this.h; }

    public int getArea() {
        return this.w * this.h;
    }

    /**
     * Rotate the rectangle by swapping width and height.
     */
    public void rotate() {
        this.rotated = !this.rotated;
        int tmp = w;
        this.w = this.h;
        this.h = tmp;
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

}