import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class SteinbergSolver implements AlgorithmInterface {

    /*
        Variables set during condition check, to be used during procedure
     */
    private int condition3variable_m;   // Index to split rectangles on in procedure 3
    private int conditionm3variable_m;  // Index to split rectangles on in procedure -3
    private int condition2variable_i;   // One of two rectangles to place in procedure 2
    private int condition2variable_k;   // One of two rectangles to place in procedure 2
    private int conditionm2variable_i;  // One of two rectangles to place in procedure -2
    private int conditionm2variable_k;  // One of two rectangles to place in procedure -2
    private int condition0variable_i;   // The one rectangle to place in procedure 0

    // The rectangles in the problem
    List<Rect> rectangles;

    // The settings
    PackingSettings settings;

    @Override
    public PackingSolution solve(PackingProblem p) {

        // Extract problem settings
        settings = p.getSettings();
        boolean strip = settings.getFixed();
        boolean rotate = settings.getRotation();
        int limit = settings.getMaxHeight();

        // Convert to Rect object that supports (double) dimensions and coords
        rectangles = new ArrayList<>();
        for (Rectangle r : p.getRectangles()) {
            if (rotate && r.getHeight() > limit) {
                r.rotate();
            }
            rectangles.add(new Rect(r));
        }

        // Get the intitial bounding box to use Steinberg on
        Rect boundingBox = getInitialBoundingBox(rectangles, strip, limit);
        // Run Steinberg on the bounding box
        subProblem(boundingBox, new ArrayList<>(rectangles));

        // Convert all rectangles back to integer coords
        for (int i = 0; i < rectangles.size(); i++) {
            rectangles.get(i).toRectangle();
        }

        int fuck = 0;
        for (Rectangle r : p.getRectangles()) {
            if (r.getX() < 0) {
                r.setPos((int) (boundingBox.getWidth() + fuck), 0);
                fuck += r.getWidth();
            }
        }

        // "shake" the boundingbox to make more compact solution
        shake(p);

        // Return the solution
        return new PackingSolution(p);
    }

    /**
     * Metaphorically shakes the solution into a more compact one
     * tries to move all rectangles left and down over and over.
     * Then tries to shuffle the right most rectangles into the rest of
     * the solution as well.
     *
     * @param p the packing problem
     */
    private void shake(PackingProblem p) {
        List<Rectangle> rects = Arrays.asList(p.getRectangles().clone());
        // Get new bounding box after Steinberg ran
        int width = 0;
        int height = 0;
        for (Rectangle r : rects) {
            if (r.getX() + r.getWidth() > width) {
                width = r.getX() + r.getWidth();
            }
            if (r.getY() + r.getHeight() > height) {
                height = r.getY() + r.getHeight();
            }
        }
        // Move all rectangles left and then down, repeatedly
        for (int i = 0; i < 100; i++) {
            width = 0;
            height = 0;
            for (Rectangle r : rects) {
                if (r.getX() + r.getWidth() > width) {
                    width = r.getX() + r.getWidth();
                }
                if (r.getY() + r.getHeight() > height) {
                    height = r.getY() + r.getHeight();
                }
            }
            leftDown(rects, width, height);
        }
        // Move all rectangles up, left and then down, repeatedly
        for (int i = 0; i < 100; i++) {
            width = 0;
            height = 0;
            for (Rectangle r : rects) {
                if (r.getHorizontalReach() > width) {
                    width = r.getHorizontalReach();
                }
                if (r.getVerticalReach() > height) {
                    height = r.getVerticalReach();
                }
            }
            pullUp(rects, width, height);
            leftDown(rects, width, height);
        }
        // Pull everything left one final time
        pullLeft(rects, height);
    }

    /**
     * Pulls all rectangles in the list, up to the given height
     *
     * @param list the rectangles to pull
     * @param width the width of the current solution
     * @param height the height to which to pull the rectangles
     */
    private void pullUp(List<Rectangle> list, int width, int height) {
        list.sort(Comparator.comparing(Rectangle::getVerticalReach).reversed());
        int[] markers = new int[width];
        for (int i = 0; i < width; i++) { markers[i] = height; }
        for (Rectangle r : list) {
            up(r, markers, height);
        }
    }

    /**
     * Pulls rectangle r up to the applicable marker height or the given height
     *
     * @param r the rectangle to pull
     * @param markers the markers, indicating collision box
     * @param height the height of the current solution
     */
    private void up(Rectangle r, int[] markers, int height) {
        int left = r.getX();
        int right = left + r.getWidth();
        int mark = height;
        for (int i = left; i < right; i++) {
            if (markers[i] < mark) { mark = markers[i]; }
        }
        r.setY(mark - r.getHeight());
        for (int i = left; i < right; i++) {
            markers[i] = mark - r.getHeight();
        }
    }

    /**
     * Pulls all the rectangles in the list left and then down
     *
     * @param list the list of rectangles to be moved
     * @param width the width of the current solution
     * @param height the height of the current solution
     */
    private void leftDown(List<Rectangle> list, int width, int height) {
        pullLeft(list, height);
        pullDown(list, width);
    }

    /**
     * Pulls all the rectangles in the list as far down as possible
     *
     * @param list the list of rectangles to be moved
     * @param width the width of the current solution
     */
    private void pullDown(List<Rectangle> list, int width) {
        list.sort(Comparator.comparing(Rectangle::getY));
        int[] markers = new int[width];
        for (int i = 0; i < width; i++) { markers[i] = 0; }
        for (Rectangle r : list) {
            down(r, markers);
        }
    }

    /**
     * Pulls the rectangle r as far down as possible
     *
     * @param r the rectangle to move
     * @param markers the markers, indicating collision boxes
     */
    private void down(Rectangle r, int[] markers) {
        int left = r.getX();
        int right = left + r.getWidth();
        int mark = 0;
        for (int i = left; i < right; i++) {
            if (markers[i] > mark) { mark = markers[i]; }
        }
        r.setY(mark);
        for (int i = left; i < right; i++) {
            markers[i] = r.getVerticalReach();
        }
    }

    /**
     * Pulls all rectangles in the list as far left as possible
     *
     * @param list the list of rectangles to move
     * @param height the height of the current solution
     */
    private void pullLeft(List<Rectangle> list, int height) {
        list.sort(Comparator.comparing(Rectangle::getX));
        int[] markers = new int[height];
        for (int i = 0; i < height; i++) { markers[i] = 0; }
        for (Rectangle r : list) {
            left(r, markers);
        }
    }

    /**
     * Pulls the rectangle r as far left as possible
     *
     * @param r the rectangle to move
     * @param markers markers indicating collision boxes
     */
    private void left(Rectangle r, int[] markers) {
        int bottom = r.getY();
        int top = bottom + r.getHeight();
        int mark = 0;
        for (int i = bottom; i < top; i++) {
            if (markers[i] > mark) { mark = markers[i]; }
        }
        if (mark <= r.getX()) {
            r.setX(mark);
        } else {
            int x = 0;
        }
        for (int i = bottom; i < top; i++) {
            markers[i] = r.getHorizontalReach();
        }
    }

    /**
     * Solves a subproblem by checking for each procedure whether it is applicable and running it
     *
     * @param boundingBox boundingBox of current subproblem
     * @param list list of rectangles to be placed in the boundingbox
     */
    private void subProblem(Rect boundingBox, List<Rect> list) {
        if (list.size() == 0) { return; }
        if (boundingBox.getWidth() % 1 >= 0.9999) {
            boundingBox.setWidth(Math.ceil(boundingBox.getWidth()));
        }
        if (boundingBox.getHeight() % 1 >= 0.9999) {
            boundingBox.setHeight(Math.ceil(boundingBox.getHeight()));
        }
        list.sort(Comparator.comparing(Rect::getHeight).reversed());
        if (conditionm1(boundingBox, list)) { procedurem1(boundingBox, list); return;}
        else if (conditionm2(boundingBox, list)) { procedurem2(boundingBox, list); return;}
        else if (conditionm3(boundingBox, list)) { procedurem3(boundingBox, list); return;}
        list.sort(Comparator.comparing(Rect::getWidth).reversed());
        if (condition1(boundingBox, list)) { procedure1(boundingBox, list); return;}
        else if (condition2(boundingBox, list)) { procedure2(boundingBox, list); return;}
        else if (condition3(boundingBox, list)) { procedure3(boundingBox, list); return;}
        else if (condition0(boundingBox, list)) { procedure0(boundingBox, list); return;}
    }

    /**
     * Checks whether the given subproblem is solvable using Steinberg
     *
     * @param bb boundingbox
     * @param list list of rectangles to be placed in boundingbox
     * @return whether Steinberg can solve this
     */
    private boolean solvable(Rect bb, List<Rect> list) {
        double u = bb.getWidth();
        double v = bb.getHeight();
        double a = getMaxWidth(list);
        double b = getMaxHeight(list);
        double S = getTotalArea(list);
        double t1 = Math.max((2 * a) - u, 0);
        double t2 = Math.max((2 * b) - v, 0);
        return (a <= u && b <= v && 2 * S <= (u * v) - (t1 * t2));
    }

    /**
     * Places rectangle relative to its bounding box
     *
     * @param bb boundingbox
     * @param r the rectangle to be placed
     * @param x the x coord of rectangle inside the boundingbox
     * @param y the y coord of rectangle inside the boundingbox
     */
    private void place(Rect bb, Rect r, double x, double y) {
        r.setPos(bb.getX() + x, bb.getY() + y);
    }

    /**
     * Applies procedure 0:
     * Place the rectangle found in the condition check in the left bottom, and recurse on the
     * space to the right of it.
     *
     * @param boundingBox the boundingbox
     * @param list the rectangles left to place inside this boundingbox
     */
    private void procedure0(Rect boundingBox, List<Rect> list) {
        Rect r = list.get(condition0variable_i);
        place(boundingBox, r, 0, 0);
        list.remove(r);
        Rect bb = new Rect(boundingBox.getWidth() - r.getWidth(), boundingBox.getHeight());
        place(boundingBox, bb, r.getWidth(), 0);
        subProblem(bb, list);
    }

    /**
     * Applies procedure -3:
     * Divide the rectangles and the boundingbox into two seperate subproblems based on
     * the value of m found in the condition check and recurse on both
     *
     * @param boundingBox the bounding box
     * @param list rectangles to be placed
     */
    private void procedurem3(Rect boundingBox, List<Rect> list) {
        double Z = getTotalArea(list, conditionm3variable_m);
        double u = boundingBox.getWidth();
        double v = boundingBox.getHeight();
        double vp = Math.max(v / 2, 2 * Z / u);
        double vpp = v - vp;
        List<Rect> l1 = new ArrayList<>();
        List<Rect> l2 = new ArrayList<>(list);
        for (int i = 0; i <= conditionm3variable_m; i++) {
            l1.add(list.get(i));
            l2.remove(list.get(i));
        }
        Rect Q1 = new Rect(u, vp);
        Rect Q2 = new Rect(u, vpp);
        place(boundingBox, Q1, 0, 0);
        place(boundingBox, Q2, 0, vp);
        subProblem(Q1, l1);
        subProblem(Q2, l2);
    }

    /**
     * Applies procedure -2:
     * Places rectangle i and k in the left bottom like this [][] and recurse on the space above them
     * the value of i and k are calculated in the condition check
     *
     * @param boundingBox the bounding box
     * @param list rectangles to be placed
     */
    private void procedurem2(Rect boundingBox, List<Rect> list) {
        if (list.get(conditionm2variable_i).getHeight() < list.get(conditionm2variable_k).getHeight()) {
            int temp = conditionm2variable_i;
            conditionm2variable_i = conditionm2variable_k;
            conditionm2variable_k = temp;
        }
        Rect ri = list.get(conditionm2variable_i);
        Rect rk = list.get(conditionm2variable_k);
        place(boundingBox, ri, 0, 0);
        place(boundingBox, rk, ri.getWidth(), 0);
        list.remove(ri);
        list.remove(rk);
        if (list.size() == 0) { return; }
        Rect bb = new Rect(boundingBox.getWidth(), boundingBox.getHeight() - ri.getHeight());
        place(boundingBox, bb, 0, ri.getHeight());
        subProblem(bb, list);
    }

    /**
     * Applies procedure -1:
     * Place all rects with height greater than half the bounding box height in the left bottom corner
     * like this [][][]....., and then place more in the right top corner, recurse on the bottom right corner
     *
     * @param boundingBox the bounding box
     * @param list the rectangles yet to be placed in this subproblem
     */
    private void procedurem1(Rect boundingBox, List<Rect> list) {
        int m = 0;
        while (m + 1 < list.size() && list.get(m + 1).getHeight() >= boundingBox.getHeight() / 2) {
            m++;
        }
        List<Rect> list2 = new ArrayList<>(list);
        double w = 0;
        for (int i = 0; i <= m; i++) {
            place(boundingBox, list.get(i), w, 0);
            w += list.get(i).getWidth();
            list2.remove(list.get(i));
        }
        if (list2.size() == 0) { return; }
        list2.sort(Comparator.comparing(Rect::getWidth).reversed());
        if (list2.get(0).getWidth() <= boundingBox.getWidth() - w) {
            Rect bb = new Rect(boundingBox.getWidth() - w, boundingBox.getHeight());
            place(boundingBox, bb, w, 0);
            subProblem(bb, list2);
        } else {
            int n = 0;
            while (n + 1 < list2.size() && list2.get(n + 1).getWidth() > boundingBox.getWidth() - w) {
                n++;
            }
            List<Rect> list3 = new ArrayList<>(list2);
            double h = 0;
            for (int i = 0; i <= n; i++) {
                place(boundingBox, list2.get(i), boundingBox.getWidth() - list2.get(i).getWidth(),
                        boundingBox.getHeight() - h - list2.get(i).getHeight());
                h += list2.get(i).getHeight();
                list3.remove(list2.get(i));
            }
            if (list3.size() == 0) { return; }
            Rect bb = new Rect(boundingBox.getWidth() - w, boundingBox.getHeight() - h);
            place(boundingBox, bb, w, 0);
            subProblem(bb, list3);
        }
    }

    /**
     * Applies procedure 3:
     * Divide the sub problem into two vertical subproblems like [][] and recurse on both
     *
     * @param boundingBox the boundingbox
     * @param list the list of rectangles to be placed
     */
    private void procedure3(Rect boundingBox, List<Rect> list) {
        double Z = getTotalArea(list, condition3variable_m);
        double u = boundingBox.getWidth();
        double v = boundingBox.getHeight();
        double up = Math.max(u / 2, 2 * Z / v);
        double upp = u - up;
        List<Rect> l1 = new ArrayList<>();
        List<Rect> l2 = new ArrayList<>(list);
        for (int i = 0; i <= condition3variable_m; i++) {
            l1.add(list.get(i));
            l2.remove(list.get(i));
        }
        Rect Q1 = new Rect(up, v);
        Rect Q2 = new Rect(upp, v);
        place(boundingBox, Q1, 0, 0);
        place(boundingBox, Q2, up, 0);
        subProblem(Q1, l1);
        subProblem(Q2, l2);
    }

    /**
     * Applies procedure 2:
     * Place two rectangles and recurse on the right space
     *
     * @param boundingBox the boundingbox
     * @param list the list of rectangles to be placed
     */
    private void procedure2(Rect boundingBox, List<Rect> list) {
        if (list.get(condition2variable_i).getWidth() < list.get(condition2variable_k).getWidth()) {
            int temp = condition2variable_i;
            condition2variable_i = condition2variable_k;
            condition2variable_k = temp;
        }
        Rect ri = list.get(condition2variable_i);
        Rect rk = list.get(condition2variable_k);
        place(boundingBox, ri, 0, 0);
        place(boundingBox, rk, 0, ri.getHeight());
        list.remove(ri);
        list.remove(rk);
        if (list.size() == 0) { return; }
        Rect bb = new Rect(boundingBox.getWidth() - ri.getWidth(), boundingBox.getHeight());
        place(boundingBox, bb, ri.getWidth(), 0);
        subProblem(bb, list);
    }

    /**
     * Stack all rectangles with width greater than half the width of the bounding box in the left bottom,
     * let v' be the height of the bounding box minus the height of that stack. All remaining rectangles with height
     * greater than v' are stacked from right to left in the top right corner. Recurse on the remaining rectangles
     * and the space enclosed by the tops of the two stacks.
     *
     * @param boundingBox the boundingbox
     * @param list the rectangles to be placed
     */
    private void procedure1(Rect boundingBox, List<Rect> list) {
        int m = 0;
        while (m + 1 < list.size() && list.get(m + 1).getWidth() >= boundingBox.getWidth() / 2) {
            m++;
        }
        List<Rect> list2 = new ArrayList<>(list);
        double h = 0;
        for (int i = 0; i <= m; i++) {
            place(boundingBox, list.get(i), 0, h);
            h += list.get(i).getHeight();
            list2.remove(list.get(i));
        }
        if (list2.size() == 0) { return; }
        list2.sort(Comparator.comparing(Rect::getHeight).reversed());
        if (list2.get(0).getHeight() <= boundingBox.getHeight() - h) {
            Rect bb = new Rect(boundingBox.getWidth(), boundingBox.getHeight() - h);
            place(boundingBox, bb, 0, h);
            subProblem(bb, list2);
        } else {
            int n = 0;
            while (n + 1 < list2.size() && list2.get(n + 1).getHeight() > boundingBox.getHeight() - h) {
                n++;
            }
            List<Rect> list3 = new ArrayList<>(list2);
            double w = 0;
            for (int i = 0; i <= n; i++) {
                place(boundingBox, list2.get(i), boundingBox.getWidth() - w - list2.get(i).getWidth(),
                        boundingBox.getHeight() - list2.get(i).getHeight());
                w += list2.get(i).getWidth();
                list3.remove(list2.get(i));
            }
            if (list3.size() == 0) { return; }
            Rect bb = new Rect(boundingBox.getWidth() - w, boundingBox.getHeight() - h);
            place(boundingBox, bb, 0, h);
            subProblem(bb, list3);
        }
    }

    private boolean condition0(Rect boundingBox, List<Rect> list) {
        double u = boundingBox.getWidth();
        double v = boundingBox.getHeight();
        if (getMaxWidth(list) > u / 2 || getMaxHeight(list) > v / 2) { return false; }
        for (int i = 0; i < list.size(); i++) {
            if (getTotalArea(list) - (u * v / 4) <= list.get(i).getArea()) {
                condition0variable_i = i;
                return true;
            }
        }
        return false;
    }

    private boolean conditionm3(Rect boundingBox, List<Rect> list) {
        double u = boundingBox.getWidth();
        double v = boundingBox.getHeight();
        if (getMaxWidth(list) > u / 2 || getMaxHeight(list) > v / 2 || list.size() <= 1) { return false; }
        double S = getTotalArea(list);
        for (int m = 0; m < list.size() - 1; m++) {
            double area = getTotalArea(list, m);
            if (S - (u * v / 4) <= area && area <= u * v * 3 / 8 && list.get(m + 1).getHeight() <= v / 4) {
                conditionm3variable_m = m;
                return true;
            }
        }
        return false;
    }

    private boolean conditionm2(Rect boundingBox, List<Rect> list) {
        double u = boundingBox.getWidth();
        double v = boundingBox.getHeight();
        double S = getTotalArea(list);
        if (getMaxWidth(list) > u / 2 || getMaxHeight(list) > v / 2) { return false; }
        for (int i = 0; i < list.size(); i++) {
            Rect ri = list.get(i);
            if (ri.getWidth() < u / 4) { continue; }
            if (ri.getHeight() < v / 4) { continue; }
            for (int k = 0; k < list.size(); k++) {
                Rect rk = list.get(k);
                if (i == k) { continue; }
                if (rk.getWidth() < u / 4) { continue; }
                if (rk.getHeight() < v / 4) { continue; }
                if (2 * (S - ri.getArea() - rk.getArea()) <= (u - Math.max(ri.getHeight(), rk.getHeight())) * v) {
                    conditionm2variable_i = i;
                    conditionm2variable_k = k;
                    return true;
                }
            }
        }
        return false;
    }

    private boolean conditionm1(Rect boundingBox, List<Rect> list) {
        return getMaxHeight(list) >= boundingBox.getHeight() / 2;
    }

    private boolean condition3(Rect boundingBox, List<Rect> list) {
        double u = boundingBox.getWidth();
        double v = boundingBox.getHeight();
        if (getMaxWidth(list) > u / 2 || getMaxHeight(list) > v / 2) { return false; }
        double S = getTotalArea(list);
        for (int m = 0; m < list.size() - 1; m++) {
            double area = getTotalArea(list, m);
            if (S - (u * v / 4) <= area && area <= u * v * 3 / 8 && list.get(m + 1).getWidth() <= u / 4) {
                condition3variable_m = m;
                return true;
            }
        }
        return false;
    }

    private boolean condition2(Rect boundingBox, List<Rect> list) {
        double u = boundingBox.getWidth();
        double v = boundingBox.getHeight();
        double S = getTotalArea(list);
        if (getMaxWidth(list) > u / 2 || getMaxHeight(list) > v / 2 || list.size() <= 1) { return false; }
        for (int i = 0; i < list.size(); i++) {
            Rect ri = list.get(i);
            if (ri.getWidth() < u / 4) { continue; }
            if (ri.getHeight() < v / 4) { continue; }
            for (int k = 0; k < list.size(); k++) {
                Rect rk = list.get(k);
                if (i == k) { continue; }
                if (rk.getWidth() < u / 4) { continue; }
                if (rk.getHeight() < v / 4) { continue; }
                if (2 * (S - ri.getArea() - rk.getArea()) <= (u - Math.max(ri.getWidth(), rk.getWidth())) * v) {
                    condition2variable_i = i;
                    condition2variable_k = k;
                    return true;
                }
            }
        }
        return false;
    }

    private boolean condition1(Rect boundingBox, List<Rect> list) {
        return getMaxWidth(list) >= boundingBox.getWidth() / 2;
    }

    /*
        Get the minimum bounding box such that the algo can solve
     */
    private Rect getInitialBoundingBox(List<Rect> rectangles, boolean strip, int limit) {
        Rect boundingBox = new Rect(0, 0);
        boundingBox.setPos(0, 0);
        // If we're solving a strip problem, only one dimension has to be minimized
        if (strip) {
            double v = limit;
            double u = getMaxWidth(rectangles);
            double S = getTotalArea(rectangles);
            double mHeight = getMaxHeight(rectangles);
            double mWidth = getMaxWidth(rectangles);
            while(!(2 * S <= (u * v) - (Math.max(2*mWidth - u, 0) * Math.max(2*mHeight - v, 0)))) {
                u++;
            }
            boundingBox.setWidth(u);
            boundingBox.setHeight(v);
            // Otherwise we're minimizing both width and height
        } else {
            double u = getMaxWidth(rectangles);
            double v = getMaxHeight(rectangles);
            double S = getTotalArea(rectangles);
            double mHeight = getMaxHeight(rectangles);
            double mWidth = getMaxWidth(rectangles);
            while(!(2 * S <= (u * v) - (Math.max(2*mWidth - u, 0) * Math.max(2*mHeight - v, 0)))) {
                v++;
                if(!(2 * S <= (u * v) - (Math.max(2*mWidth - u, 0) * Math.max(2*mHeight - v, 0)))) {
                    u++;
                }
            }
            double i;
            double j;
            if (u < v) {
                i = u - 1;
                j = v + 1;
                while ((2 * S <= (i * j) - (Math.max(2*mWidth - i, 0) * Math.max(2*mHeight - j, 0)))) {
                    i--; j++;
                }
                i++; j--;
            } else {
                i = u + 1;
                j = v - 1;
                while ((2 * S <= (i * j) - (Math.max(2*mWidth - i, 0) * Math.max(2*mHeight - j, 0)))) {
                    i++; j--;
                }
                i--; j++;
            }
            if (i * j < u * v) {
                boundingBox.setWidth(i);
                boundingBox.setHeight(j);
            } else {
                boundingBox.setWidth(u);
                boundingBox.setHeight(v);
            }
        }
        return boundingBox;
    }

    /*
        Get the sum of the areas of a list of rectangles
     */
    private double getTotalArea(List<Rect> rectangles) {
        double sum = 0;
        for (Rect r : rectangles) {
            sum += r.getArea();
        }
        return sum;
    }

    /*
        Get the sum of the areas of a list of rectangles up to m
     */
    private double getTotalArea(List<Rect> rectangles, int m) {
        double sum = 0;
        for (int i = 0; i <= m; i++) {
            sum += rectangles.get(i).getArea();
        }
        return sum;
    }

    /*
        Get the maximum encountered width in a list of rectangles
     */
    private double getMaxWidth(List<Rect> rectangles) {
        double m = 0;
        for (Rect r : rectangles) {
            if (r.getWidth() > m) {
                m = r.getWidth();
            }
        }
        return m;
    }

    /*
        Get the maximum encountered height in a list of rectangles
     */
    private double getMaxHeight(List<Rect> rectangles) {
        double m = 0;
        for (Rect r : rectangles) {
            if (r.getHeight() > m) {
                m = r.getHeight();
            }
        }
        return m;
    }

    private int getMinWidth(List<Rectangle> rectangles) {
        int m = Integer.MAX_VALUE;
        for (Rectangle r : rectangles) {
            if (r.getWidth() < m) {
                m = r.getWidth();
            }
        }
        return m;
    }

    private int getMinHeight(List<Rectangle> rectangles) {
        int m = Integer.MAX_VALUE;
        for (Rectangle r : rectangles) {
            if (r.getHeight() < m) {
                m = r.getHeight();
            }
        }
        return m;
    }

    // Rectangle class that supports doubles (Necessary for this algorithm)
    public class Rect {

        private double x = -1;
        private double y = -1;
        private double width;
        private double height;
        private boolean rotated;
        private Rectangle r;

        public Rect(double width, double height) {
            this.width = width;
            this.height = height;
        }

        public Rect(Rectangle r) {
            this.width = r.getWidth();
            this.height = r.getHeight();
            this.x = r.getX();
            this.y = r.getY();
            this.r = r;
        }

        public void toRectangle() {
            this.r.setWidth((int) width);
            this.r.setHeight((int) height);
            this.r.setPos((int) Math.floor(x), (int) Math.floor(y));
        }

        public double getX() { return x; }
        public double getY() { return y; }
        public double getHeight() { return height; }
        public double getWidth() { return width; }
        public double getArea() { return width * height; }

        public void setPos(double x, double y) { this.x = x; this.y = y; }
        public void setWidth(double w) { this.width = w; }
        public void setHeight(double h) { this.height = h; }

    }

}
