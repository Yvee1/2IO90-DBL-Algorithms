/**
 * Represents a level in the
 */
class Level {
    int width;
    int height;
    int startPos;
    int endPos;

    public Level(int width, int startPos) {
        this.width = width;
        this.startPos = startPos;
        endPos = startPos + width;
    }

    /**
     * Determines whether r fits in the level.
     * @param r The rectangle to test for fitting.
     * @param maxHeight The maximum height of the level.
     * @return true <=> r fits in the level.
     */
    public boolean fits(Rectangle r, int maxHeight) {

        /*
            r fits if its width is at most the level's width,
            and the height with r does not exceed maxHeight
        */
        return (r.getWidth() <= this.width) && (this.height + r.getHeight() <= maxHeight);

    }

    /**
     * Virtually stacks r on top of the other rectangles on the level.
     * Also update the location of r.
     * @param r The rectangle to add.
     */
    public void add(Rectangle r) {

        r.setPos(startPos, height);

        this.height += r.getHeight();
    }
}

