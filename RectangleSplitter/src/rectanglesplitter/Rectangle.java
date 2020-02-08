package rectanglesplitter;
import java.util.Random;

public class Rectangle {
    private final int x;
    private final int y;
    
    private final int w;
    private final int h;
    
    private final Random rand = new Random();
    
    Rectangle(int x, int y, int w, int h){
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }
    
    /*
    Subdivide the rectangle
    @param embedPossible whether there is room for 4 new rectangles
    */
    public Rectangle[] split(boolean embedPossible){
        // 30 percent of the time do a line split
        final boolean doEmbedding = rand.nextDouble() < 1;
        final boolean roomForEmbed = w > 2 && h > 2;
        final boolean roomForSplit = w > 1 && h > 1;
        
        if (doEmbedding && embedPossible && roomForEmbed){
            return embedRandom();
        }
        else if(roomForSplit){
            if(rand.nextBoolean()){
                return splitHorizontal();
            }
            else {
                return splitVertical();
            }
        }
        else{
            return new Rectangle[] {this};
        }
    }
    
    public Rectangle[] splitHorizontal(){
        int splitX = r(0, w);
        // inv: 0 < splitX < w
        Rectangle r1 = new Rectangle(x, y, splitX, h);
        Rectangle r2 = new Rectangle(x + splitX, y, w - splitX, h);
        return new Rectangle[] {r1, r2};
    }
    
    public Rectangle[] splitVertical(){
        int splitY = r(0, h);
        // inv: 0 < splitY < h
        Rectangle r1 = new Rectangle(x, y, splitY, w);
        Rectangle r2 = new Rectangle(x, y + splitY, w, h - splitY);
        return new Rectangle[] {r1, r2};
    }
    
    /*
    ---------------------------------------
    |                      |              |
    |          IV          |      III     |
    |-----------------------              |
    |                 |  V |              |
    |        I        --------------------|
    |                 |                   |
    |                 |        II         |
    ---------------------------------------
    
                      or  
    
    ---------------------------------------
    |                 |         III       |
    |        IV       |                   |
    |                 --------------------|
    |                 |  V |              |
    |-----------------------      II      |
    |          I           |              |
    |                      |              |
    ---------------------------------------
    */
    // Embed rectangle inside consequently generating 5 rectangles
    public Rectangle[] embedRandom(){
        // determine lower left corner coordinates, not touching boundary
        int xoff = r(0, w);
        int yoff = r(0, h);
        // xoff in (0, w) and yoff in (0, h)
        
        // width and height, not touching boundary
        int w5 = r(0, w-xoff);
        int h5 = r(0, h-yoff);
        // width in (0, w-xoff) and height in (0, h-yoff)
        
        Rectangle embedded = new Rectangle(x + xoff, y + yoff, w5, h5);
        
        // Whether bottom left corner line is horizontal or not
        boolean blHor = rand.nextBoolean();     
        
        // widths and heights of the rectangles rectangles
        int w1 = xoff + (blHor ? w5 : 0);
        int h1 = yoff + (blHor ? 0 : h5);
        int w2 = w - w1;
        int h2 = yoff + (blHor ? h5 : 0);
        int w3 = w - xoff - (blHor ? 0 : w5);
        int h3 = h - h2;
        int w4 = w - w3;
        int h4 = h - h1;
        
        Rectangle r1 = new Rectangle(x, y, w1, h1);
        Rectangle r2 = new Rectangle(x + w - w2, y, w2, h2);
        Rectangle r3 = new Rectangle(x + w - w3, y + h - h3, w3, h3);
        Rectangle r4 = new Rectangle(x, y + h - h4, w4, h4);
        return new Rectangle[] {r1, r2, r3, r4, embedded};
    }
    
    public String getSize(){
        return String.format("%d %d", w, h);
    }
    
    @Override
    public String toString(){
        return String.format("Rect at (%d, %d) with size (%d, %d)", x, y, w, h);
    }
    
    // returns random number in range (lower, higher)
    private int r(int lower, int higher) throws IllegalArgumentException {
//        System.out.println(String.format("lower: %d, higher: %d", lower, higher));
        if (lower == higher){
            return lower;
        }
        int result = rand.nextInt(higher-lower) + lower + 1;
        return result;
    }
}
