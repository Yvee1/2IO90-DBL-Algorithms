import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.PriorityQueue;

public class BestFitFast implements AlgorithmInterface {

    private PriorityQueue<SkylineSegment> skyline;
    private TreeSet<RectangleWrap> bst;
    private Rectangle usedRectangles[];

    public PackingSolution solve(PackingProblem p) {

        /* Create a min-heap with y as key. */
        skyline = new PriorityQueue<>(11, new SkylineSorterXY());

        /* Binary search tree for rectangles by width. */
        bst = new TreeSet<>(new Comparator<RectangleWrap>() {
            @Override
            public int compare(RectangleWrap a, RectangleWrap b) {

                if (a.orig.h == b.orig.h) {
                    if (a.orig.w == b.orig.w) { return Integer.compare(a.orig.id, b.orig.id); }
                    else { return -Integer.compare(a.orig.w, b.orig.w); }
                }
                else { return Integer.compare(a.orig.h, b.orig.h); }

            }
        });

        usedRectangles = new Rectangle[p.getRectangles().length];

        for (Rectangle r: p.getRectangles()) {

            RectangleWrap rw = new RectangleWrap(r);
            //r.rotate(); // turn fixed-height into fixed-width
            bst.add(rw);

            /* If rotations are allowed, also add the rotated variant. */
            if (p.settings.rotation) {
                RectangleWrap rrw = new RectangleWrap(r.clone().rotate());
                rw.other = rrw;
                rrw.other = rw;
                bst.add(rrw);
            }
        }

        /* Start and end of the skyline are at x = infinity. */
        final SkylineSegment start = new SkylineSegment(Integer.MAX_VALUE, 0, 0);
        final SkylineSegment end = new SkylineSegment(Integer.MAX_VALUE, p.settings.maxHeight,0);

        SkylineSegment initial = new SkylineSegment(0, 0, p.settings.maxHeight);
        initial.bottom = start;
        initial.top = end;

        start.top = initial;
        end.bottom = initial;

        skyline.add(initial);

        final RectangleWrap search_rect = new RectangleWrap(new Rectangle(0, 0));

        int index = 0;

        while (!bst.isEmpty()) {

            SkylineSegment segment = skyline.poll();

            /* Set up search_rect to search for a rectangle of width w. */
            search_rect.orig.h = segment.len;
            RectangleWrap w = bst.floor(search_rect);

            /* If there is no best-fit rectangle. */
            if (w == null) {
                mergeSegments(segment);
                continue;
            }

            w.orig.setPos(segment.x, segment.y);

            updateSkyline(w.orig, segment);

            /* Remove the rectangles from the bst. */
            bst.remove(w);
            if (p.settings.rotation) { bst.remove(w.other); }

            usedRectangles[index++] = w.orig;


        }

        p.rectangles = usedRectangles;

        return new PackingSolution(p);

    }

    /**
     * Updates the skyline.
     * @param r
     * @param segment
     */
    private void updateSkyline(Rectangle r, SkylineSegment segment) {

        SkylineSegment newSeg = new SkylineSegment(segment.x + r.w, segment.y, r.h);
        newSeg.bottom = segment.bottom;
        newSeg.top = segment.top;

        /* The only skyline segment that could possibly be added is on the right, so always merge left if needed. */
        if (newSeg.x == segment.bottom.x) {

            /* segment.bottom.bottom always exists due to segment.y never being height of left wall. */
            newSeg.bottom = segment.bottom.bottom;
            newSeg.y = segment.bottom.y;
            newSeg.len += segment.bottom.len;
            skyline.remove(segment.bottom);

        }

        /*  */
        if (r.h < segment.len) {
            /* Create a new segment for the leftover width. */
            SkylineSegment remaining = new SkylineSegment(segment.x, segment.y + r.h, segment.len - r.h);
            remaining.bottom = newSeg;
            remaining.top = segment.top;

            /* Fix pointers. */
            segment.top.bottom = remaining;
            newSeg.top = remaining;

            skyline.add(remaining);
        } else if (newSeg.x == segment.top.x) {
            /* Merge newSeg and segment.right */
            newSeg.len += segment.top.len;
            newSeg.top = segment.top.top;
            skyline.remove(segment.top);
        }

        /* Fix pointers. */
        newSeg.bottom.top = newSeg;
        newSeg.top.bottom = newSeg;

        /* Add the new segment to the skyline. */
        skyline.add(newSeg);

    }

    /**
     * Merge segment with its lower neighbour.
     * @param segment
     */
    private void mergeSegments(SkylineSegment segment) {

        /* If both neighbours are at the same x-position, merge with both. */
        if(segment.bottom.x == segment.x && segment.top.x == segment.x) {
            segment.bottom.len += segment.len + segment.top.len;

            /* Link segment two above current segment to the 'new' segment. */
            /* segment.top.top always exists, due to the height being finite. */
            segment.bottom.top = segment.top.top;
            segment.top.top.bottom = segment.bottom;

            return;
        }

        /* Prioritize bottom neighbour due to lower runtime. */
        if (segment.bottom.x <= segment.top.x) {
            /* Merge the segment with its left neighbour. */
            segment.bottom.len += segment.len;
        } else {
            segment.top.len += segment.len;

            /* The right neighbour moves to the left. */
            segment.top.y = segment.y;

            /* Since the next segment now has a lower x-coordinate, remove and re-add from heap. */
            skyline.remove(segment.top);
            skyline.add(segment.top);
        }

        /* Remove segment from linked list. */
        segment.bottom.top = segment.top;
        segment.top.bottom = segment.bottom;
    }

//    /**
//     * When rotations are allowed, it may be possible to remove 'towers'.
//     */
//    private void postProcess() {
//
//        /* Sort rectangles by decreasing 'top' edge height. */
//        Arrays.sort(usedRectangles, new Comparator<Rectangle>() {
//            @Override
//            public int compare(Rectangle o1, Rectangle o2) {
//                return -Integer.compare(o1.x + o1.w, o2.x + o2.w);
//            }
//        });
//
//
//        int totalWidth = usedRectangles[0].x + usedRectangles[0].w;
//
//        for (int i = 0; i < usedRectangles.length; i++) {
//
//            Rectangle cur = usedRectangles[i];
//
//            /* If the rectangle will not lead to an improvement, we are done. */
//            if (cur.w <= cur.h) { break; }
//
//            /* TODO: prove that this may be done. */
//            if (cur.x + cur.w < totalWidth) { break; }
//
//            if (cur.x + cur.w >= totalWidth) {
//                totalWidth = cur.x + cur.w;
//            }
//
//        }
//
//
//    }

}

/* Represents a Skyline interval */
 class SkylineSegment {
    int x;
    int len;
    int y;

    SkylineSegment top, bottom;

    public SkylineSegment(int x, int y, int len) {
        this.x = x;
        this.y = y;
        this.len = len;
    }
}

class SkylineSorterXY implements Comparator<SkylineSegment> {

     @Override
    public int compare(SkylineSegment a, SkylineSegment b) {
        int res = Integer.compare(a.x, b.x);

        return res == 0 ? Integer.compare(a.y, b.y) : res;
    }

}

class RectangleWrap {
     Rectangle orig;
     RectangleWrap other;

     public RectangleWrap(Rectangle orig) {
         this.orig = orig;
     }
}