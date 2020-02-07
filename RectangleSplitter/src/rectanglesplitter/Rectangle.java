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
        final boolean doOneLineSplit = rand.nextDouble() < 0.1;
        final boolean roomForEmbed = w > 2 && h > 2;
        final boolean roomForSplit = w > 1 && h > 1;
        
        if (!doOneLineSplit && embedPossible && roomForEmbed){
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
        int splitX = rand.nextInt(w-1) + 1;
        // inv: 0 < splitX < w
        Rectangle r1 = new Rectangle(x, y, splitX, h);
        Rectangle r2 = new Rectangle(x + splitX, y, w - splitX, h);
        return new Rectangle[] {r1, r2};
    }
    
    public Rectangle[] splitVertical(){
        int splitY = rand.nextInt(h-1) + 1;
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
        int xoff = rand.nextInt(w-2) + 1;
        int yoff = rand.nextInt(h-2) + 1;
        // xoff in [1, w-1] and yoff in [1, h-1]
        
        // width and height, not touching boundary
        int width = rand.nextInt(w-xoff-1) + 1;
        int height = rand.nextInt(h-yoff-1) + 1;
        // width in [1, w-xoff-1] and height in [1, h-yoff-1]
        
        Rectangle embedded = new Rectangle(x + xoff, y + yoff, width, height);
        
        // Whether bottom left corner line is horizontal or not
        boolean blHor = rand.nextBoolean();     
        
        // widths and heights of the rectangles rectangles
        int w1 = xoff + (blHor ? width : 0);
        int h1 = yoff + (blHor ? 0 : height);
        int w2 = w - xoff + (blHor ? width : 0);
        int h2 = yoff + (blHor ? height : 0);
        int w3 = w - xoff + (blHor ? 0 : width);
        int h3 = h - yoff + (blHor ? height : 0);
        int w4 = xoff + (blHor ? 0 : width);
        int h4 = h - yoff + (blHor ? 0 : height);
        
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
}
