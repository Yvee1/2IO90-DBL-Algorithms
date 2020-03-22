import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.PriorityQueue;

public class BestFitFast implements AlgorithmInterface {

    private PriorityQueue<SkylineSegment> skyline;
    private TreeSet<RectangleWrap> bst;
    private Rectangle usedRectangles[];

    /* Store the top segment of a rectangle. */
    private SkylineSegment topSegments[];

    @Override
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
        topSegments = new SkylineSegment[p.getRectangles().length];

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

            /* Find the left-bottom skyline segment. */
            SkylineSegment segment = skyline.poll();

            /* Set up search_rect to search for a rectangle of width w. */
            search_rect.orig.h = segment.len;
            RectangleWrap w = bst.floor(search_rect);

            /* If there is no best-fit rectangle, merge the segment with its leftmost neighbour. */
            if (w == null) {
                mergeSegments(segment);
                continue;
            }

            /* Set the placed rectangle's position. */
            w.orig.setPos(segment.x, segment.y);

            updateSkyline(w.orig, segment);

            /* Remove the rectangles from the bst. */
            bst.remove(w);
            if (p.settings.rotation) { bst.remove(w.other); }

            usedRectangles[index++] = w.orig;
        }

//        if (p.settings.rotation) {
//            postProcess(p);
//        }

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

        /* newSeg is the top segment of r. */
        topSegments[r.id] = newSeg;

        /* Add the new segment to the skyline. */
        skyline.add(newSeg);

    }


    /**
     * Merge segment with its lower neighbour.
     * @param segment The skyline segment to be merged.
     * @pre The skyline does not contain segment.
     */
    private void mergeSegments(SkylineSegment segment) {

        /* If both neighbours are at the same x-position, merge with both. */
        if (segment.bottom.x == segment.top.x) {
            segment.bottom.len += segment.len + segment.top.len;

            /* Link segment two above current segment to the 'new' segment. */
            /* segment.top.top always exists, due to the height being finite. */
            segment.bottom.top = segment.top.top;
            segment.top.top.bottom = segment.bottom;

            /* Remove the merged top segment. */
            skyline.remove(segment.top);

            return;
        }

        /* Prioritize bottom neighbour due to lower runtime. */
        if (segment.bottom.x < segment.top.x) {
            /* Merge the segment with its left neighbour. */
            segment.bottom.len += segment.len;
        } else {
            segment.top.len += segment.len;

            /* The right neighbour moves down. */
            segment.top.y = segment.y;

            /* Since the next segment now has a lower x-coordinate, remove and re-add from heap. */
            skyline.remove(segment.top);
            skyline.add(segment.top);

        }

        /* Remove segment from linked list. */
        segment.bottom.top = segment.top;
        segment.top.bottom = segment.bottom;
    }

    /**
     * When rotations are allowed, it may be possible to remove 'towers'.
     */
    private void postProcess(PackingProblem p) {

        /* Sort rectangles by decreasing 'top' edge height. */
        Arrays.sort(usedRectangles, new Comparator<Rectangle>() {
            @Override
            public int compare(Rectangle o1, Rectangle o2) {
                return -Integer.compare(o1.x + o1.w, o2.x + o2.w);
            }
        });

        int width = usedRectangles[0].x + usedRectangles[0].w;

        /* Loop over all rectangles. */
        for (int i = 0; i < usedRectangles.length; i++) {

            Rectangle r = usedRectangles[i];

            /* If the rectangle is higher than it is wide, rotating will increase the width.*/
            /* If the rectangle doesn't fit when rotated, we are also done. */
            /* If the right edge is positioned to the left of the current width, then there is a previously */
            /* rotated rectangle at the current width, hence we are done. */
            if (r.h >= r.w || r.w > p.settings.maxHeight || r.x+r.w < width) { return; }

            /* Save the rectangle's position if revert is needed. */
            int x_cache = r.x;
            int y_cache = r.y;

            /* Update the skyline. */
            SkylineSegment seg = topSegments[r.id];

            /* The skyline segment that was created on top of r might have been merged. */
            /* Then the segment is no longer in the skyline heap. */
            /* Fixing this requires keeping track of all rectangles a segment lies on top of, */
            /*  and update their topSegment when the segments are merged during the adding of a rectangle. */
            /* This provably increasing the running to O(n^2). */
            /* A compromise can be made by running until the failure condition is detected. */
            //assert(skyline.contains(seg));
            if (!skyline.contains(seg)) { return; }

            /* The same thing goes for the length of the segment. */
            if (seg.len != r.h) { return; }

            skyline.remove(seg);
            seg.x = r.x;
            skyline.add(seg);

            /* Rotate r. */
            r.rotate();

            /* Find the leftmost segment, and raise until r fits. */
            SkylineSegment left = skyline.poll();
            while (r.h > left.len) {
                mergeSegments(left);
                left = skyline.poll();
            }

            r.setPos(left.x, left.y);
            updateSkyline(r, left);

            /* Calculate the new width. */
            /*
            The new width is either the new right-edge of the refitted rectangle, or the right-edge of the next
            in the sorted list.
            */
            int newWidth;
            if (i == usedRectangles.length - 1) { newWidth = r.x + r.w; }
            else { newWidth = Math.max(usedRectangles[i+1].x + usedRectangles[i+1].w, r.x + r.w); }

            /* If no improvement was made, revert current rectangle and we are done. */
            if (newWidth > width) {
                r.setPos(x_cache, y_cache);
                r.rotate();
                return;
            }

            /* Update new width if improved. */
            width = newWidth;
        }

    }

}

/* Represents a Skyline interval */
class SkylineSegment {
    int x;
    int len;
    int y;
    Rectangle atop = null;

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