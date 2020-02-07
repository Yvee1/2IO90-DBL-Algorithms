class Rectangle {
    public int w, h;
    public int x, y;
    public boolean rotated;

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

    public int getX() { return this.x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return this.y; }
    public void setY(int y) { this.y = y; }

    /**
     * Rotate the rectangle by swapping width and height.
     */
    public void rotate() {
        this.rotated = !this.rotated;
        int tmp = w;
        this.w = this.h;
        this.h = tmp;
    }
}