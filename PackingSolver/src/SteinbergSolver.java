import org.w3c.dom.css.Rect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class SteinbergSolver implements AlgorithmInterface {

    // Solver variables
    private int condition3variable_m;
    private int conditionm3variable_m;
    private int condition2variable_i;
    private int condition2variable_k;
    private int conditionm2variable_i;
    private int conditionm2variable_k;
    private int condition0variable_i;
    private List<Rectangle> rectangles;


    @Override
    public PackingSolution solve(PackingProblem p) {

        // Extract problem settings
        PackingSettings settings = p.getSettings();
        boolean strip = settings.getFixed();
        boolean rotate = settings.getRotation();
        int limit = settings.getMaxHeight();

        // Get problem
        rectangles = Arrays.asList(p.getRectangles());


        // Set the bounding box
        Rectangle boundingBox = getInitialBoundingBox(rectangles, strip, limit);
        subProblem(boundingBox, rectangles);
        return new PackingSolution(p);
    }

    private void subProblem(Rectangle boundingBox, List<Rectangle> list) {
        if (list.size() == 0) { return; }
        if (condition1(boundingBox, list)) { procedure1(boundingBox, list); }
        else if (conditionm1(boundingBox, list)) { procedurem1(boundingBox, list); }
        else if (condition3(boundingBox, list)) { procedure3(boundingBox, list); }
        else if (conditionm3(boundingBox, list)) { procedurem3(boundingBox, list); }
        else if (condition0(boundingBox, list)) { procedure0(boundingBox, list); }
        else if (condition2(boundingBox, list)) { procedure2(boundingBox, list); }
        else if (conditionm2(boundingBox, list)) { procedurem2(boundingBox, list); }
    }

    private boolean solvable(Rectangle boundingBox, List<Rectangle> list) {
        int u = boundingBox.getWidth();
        int v = boundingBox.getHeight();
        int a = getMaxWidth(list);
        int b = getMaxHeight(list);
        return getTotalArea(list) * 2 <= (u * v) - (Math.max((2 * a) - u, 0) * Math.max((2 * b) - v, 0)) && a <= u
                && b <= v;
    }

    /*
        Apply procedure 0
     */
    private void procedure0(Rectangle boundingBox, List<Rectangle> list) {
        List<Rectangle> l = new ArrayList<>(list);
        placeRelative(boundingBox, l.get(condition0variable_i), 0, 0);
        l.remove(condition0variable_i);
        if (l.size() == 0) { return; }
        Rectangle Q = new Rectangle(boundingBox.getWidth() - list.get(condition0variable_i).getWidth(),
                boundingBox.getHeight());
        placeRelative(boundingBox, Q, list.get(condition0variable_i).getWidth(), 0);
        subProblem(Q, l);
    }

    /*
        Check if C0 holds
     */
    private boolean condition0(Rectangle boundingBox, List<Rectangle> list) {
        int a = getMaxWidth(list);
        int b = getMaxHeight(list);
        if (a > boundingBox.getWidth() / 2 || b > boundingBox.getHeight() / 2) {
            return false;
        }
        int S = getTotalArea(list);
        int fuv = boundingBox.getWidth() * boundingBox.getHeight() / 4;
        for (int i = 0; i < list.size(); i++) {
            if (S - fuv <= list.get(i).getArea()) {
                condition0variable_i = i;
                return true;
            }
        }
        return false;
    }

    /*
        Apply procedure 2
     */
    private void procedure2(Rectangle boundingBox, List<Rectangle> list) {
        if (list.get(condition2variable_i).getWidth() < list.get(condition2variable_k).getWidth()) {
            int temp = condition2variable_i;
            condition2variable_i = condition2variable_k;
            condition2variable_k = temp;
        }
        Rectangle ri = list.get(condition2variable_i);
        Rectangle rk = list.get(condition2variable_k);
        placeRelative(boundingBox, ri, 0, 0);
        placeRelative(boundingBox, rk, 0, ri.getHeight());
        List<Rectangle> l = new ArrayList<>(list);
        l.remove(ri);
        l.remove(rk);
        if (l.size() == 0) { return; }
        Rectangle Q = new Rectangle(boundingBox.getWidth() - ri.getWidth(), boundingBox.getHeight());
        placeRelative(boundingBox, Q, ri.getWidth(), 0);
        subProblem(Q, l);
    }

    /*
        Apply procedure -2
     */
    private void procedurem2(Rectangle boundingBox, List<Rectangle> list) {
        if (list.get(conditionm2variable_i).getHeight() < list.get(conditionm2variable_k).getHeight()) {
            int temp = conditionm2variable_i;
            conditionm2variable_i = conditionm2variable_k;
            conditionm2variable_k = temp;
        }
        Rectangle ri = list.get(conditionm2variable_i);
        Rectangle rk = list.get(conditionm2variable_k);
        placeRelative(boundingBox, ri, 0, 0);
        placeRelative(boundingBox, rk, ri.getWidth(), 0);
        List<Rectangle> l = new ArrayList<>(list);
        l.remove(ri);
        l.remove(rk);
        if (l.size() == 0) { return; }
        Rectangle Q = new Rectangle(boundingBox.getWidth(), boundingBox.getHeight() - ri.getHeight());
        placeRelative(boundingBox, Q, 0, ri.getHeight());
        subProblem(Q, l);
    }

    /*
        Check if C2 holds
     */
    private boolean condition2(Rectangle boundingBox, List<Rectangle> list) {
        int a = getMaxWidth(list);
        int b = getMaxHeight(list);
        if (a > boundingBox.getWidth() / 2 || b > boundingBox.getHeight() / 2 || list.size() <= 1) {
            return false;
        }
        int u = boundingBox.getWidth();
        int v = boundingBox.getHeight();
        float fu = u / 4;
        float fv = v / 4;
        int area = getTotalArea(list);
        for (int i = 0; i < list.size(); i++) {
            Rectangle ri = list.get(i);
            if (ri.getWidth() < fu || ri.getHeight() < fv) {
                continue;
            }
            for (int k = 0; k < list.size(); k++) {
                if (i == k) {
                    continue;
                }
                Rectangle rk = list.get(k);
                if (rk.getWidth() < fu || rk.getHeight() < fv) {
                    continue;
                }
                if (2 * (area - ri.getArea() - rk.getArea()) <= (u - Math.max(ri.getWidth(), rk.getWidth())) * v) {
                    condition2variable_i = i;
                    condition2variable_k = k;
                    return true;
                }
            }
        }
        return false;
    }

    /*
        Check if C-2 holds
     */
    private boolean conditionm2(Rectangle boundingBox, List<Rectangle> list) {
        int a = getMaxWidth(list);
        int b = getMaxHeight(list);
        if (a > boundingBox.getWidth() / 2 || b > boundingBox.getHeight() / 2 || list.size() <= 1) {
            return false;
        }
        int u = boundingBox.getWidth();
        int v = boundingBox.getHeight();
        int fu = u / 4;
        int fv = v / 4;
        int area = getTotalArea(list);
        for (int i = 0; i < list.size(); i++) {
            Rectangle ri = list.get(i);
            if (ri.getWidth() < fu || ri.getHeight() < fv) {
                continue;
            }
            for (int k = 0; k < list.size(); k++) {
                if (i == k) {
                    continue;
                }
                Rectangle rk = list.get(k);
                if (rk.getWidth() < fu || rk.getHeight() < fv) {
                    continue;
                }
                if (2 * (area - ri.getArea() - rk.getArea()) <= (v - Math.max(ri.getHeight(), rk.getHeight())) * u) {
                    conditionm2variable_i = i;
                    conditionm2variable_k = k;
                    return true;
                }
            }
        }
        return false;
    }

    /*
        Apply procedure 3
     */
    private void procedure3(Rectangle boundingBox, List<Rectangle> list) {
        int Z = getTotalArea(list, condition3variable_m);
        int up = Math.max(boundingBox.getWidth() / 2, Math.round((float) 2 * Z / boundingBox.getHeight()));
        int upp = boundingBox.getWidth() - up;
        int v = boundingBox.getHeight();
        List<Rectangle> l1 = new ArrayList<>();
        List<Rectangle> l2 = new ArrayList<>(list);
        for (int i = 0; i <= condition3variable_m; i++) {
            l1.add(list.get(i));
        }
        l2.removeAll(l1);
        Rectangle Q1 = new Rectangle(up, v);
        placeRelative(boundingBox, Q1, 0, 0);
        Rectangle Q2 = new Rectangle(upp, v);
        placeRelative(boundingBox, Q2, up, 0);
        subProblem(Q1, l1);
        subProblem(Q2, l2);
    }

    /*
        Apply procedure -3
     */
    private void procedurem3(Rectangle boundingBox, List<Rectangle> list) {
        int Z = getTotalArea(list, conditionm3variable_m);
        int vp = Math.max(boundingBox.getHeight() / 2, Math.round((float) 2 * Z / boundingBox.getWidth()));
        int vpp = boundingBox.getHeight() - vp;
        int u = boundingBox.getWidth();
        List<Rectangle> l1 = new ArrayList<>();
        List<Rectangle> l2 = new ArrayList<>(list);
        for (int i = 0; i <= conditionm3variable_m; i++) {
            l1.add(list.get(i));
        }
        l2.removeAll(l1);
        Rectangle Q1 = new Rectangle(u, vp);
        placeRelative(boundingBox, Q1, 0, 0);
        Rectangle Q2 = new Rectangle(u, vpp);
        placeRelative(boundingBox, Q2, 0, vp);
        subProblem(Q1, l1);
        subProblem(Q2, l2);
    }

    /*
        Check if C3 holds
     */
    private boolean condition3(Rectangle boundingBox, List<Rectangle> list) {
        int a = getMaxWidth(list);
        int b = getMaxHeight(list);
        if (a > boundingBox.getWidth() / 2 || b > boundingBox.getHeight() / 2 || list.size() <= 1) {
            return false;
        }
        int S = getTotalArea(list);
        int bbArea = boundingBox.getArea();
        for (int m = 0; m + 1 < list.size(); m++) {
            if (S - (bbArea / 4) <= getTotalArea(list, m) && getTotalArea(list, m) <= bbArea * 3 / 8
                    && list.get(m + 1).getWidth() <= boundingBox.getWidth() / 4) {
                condition3variable_m = m;
                return true;
            }
        }
        return false;
    }

    /*
        Check if C-3 holds
     */
    private boolean conditionm3(Rectangle boundingBox, List<Rectangle> list) {
        int a = getMaxWidth(list);
        int b = getMaxHeight(list);
        if (a > boundingBox.getWidth() / 2 || b > boundingBox.getHeight() / 2 || list.size() <= 1) {
            return false;
        }
        int S = getTotalArea(list);
        int bbArea = boundingBox.getArea();
        for (int m = 0; m + 1 < list.size(); m++) {
            if (S - (bbArea / 4) <= getTotalArea(list, m) && getTotalArea(list, m) <= bbArea * 3 / 8
                    && list.get(m + 1).getHeight() <= boundingBox.getHeight() / 4) {
                conditionm3variable_m = m;
                return true;
            }
        }
        return false;
    }

    /*
        Check if C1 holds
     */
    private boolean condition1(Rectangle boundingBox, List<Rectangle> list) {
        int a = getMaxWidth(list);
        return a > (float) boundingBox.getWidth() / 2;
    }

    /*
        Check if C-1 holds
     */
    private boolean conditionm1(Rectangle boundingBox, List<Rectangle> list) {
        int b = getMaxHeight(list);
        return b > (float) boundingBox.getHeight() / 2;
    }

    /*
        Apply procedure 1
     */
    private void procedure1(Rectangle boundingBox, List<Rectangle> l) {
        // Get all rectangles wider than half the width of subproblem
        l.sort(Comparator.comparing(Rectangle::getWidth).reversed());
        int m = 0;
        while(m + 1 < l.size() && l.get(m+1).getWidth() >= boundingBox.getWidth() / 2) {
            m++;
        }
        // Stack them bottom to top on the left
        int h = 0;
        List<Rectangle> tempList = new ArrayList<>(l);
        for (int i = 0; i <= m; i++) {
            placeRelative(boundingBox, l.get(i), 0, h);
            h += l.get(i).getHeight();
            tempList.remove(l.get(i));
        }
        // If all rectangles placed, return
        if (tempList.size() == 0) {
            return;
        }
        // Else sort remaining rectangles by decreasing height
        tempList.sort(Comparator.comparing(Rectangle::getHeight).reversed());
        if (tempList.get(0).getHeight() <= boundingBox.getHeight() - h) {
            Rectangle Q = new Rectangle(boundingBox.getWidth(), boundingBox.getHeight() - h);
            placeRelative(boundingBox, Q, 0, h);
            subProblem(Q, tempList);
        } else {
            int n = 0;
            while(n + 1 < tempList.size() && tempList.get(n).getHeight() > boundingBox.getHeight() - h) {
                n++;
            }
            int w = 0;
            List<Rectangle> remainder = new ArrayList<>(tempList);
            for (int i = 0; i <= n; i++) {
                placeRelative(boundingBox, tempList.get(i), boundingBox.getHeight() - tempList.get(i).getHeight(),
                        boundingBox.getWidth() - tempList.get(i).getWidth() - w);
                w += tempList.get(i).getWidth();
                remainder.remove(tempList.get(i));
            }
            Rectangle Q = new Rectangle(boundingBox.getWidth() - w, boundingBox.getHeight() - h);
            placeRelative(boundingBox, Q, 0, h);
            subProblem(Q, remainder);
        }
    }

    /*
        Apply procedure -1
     */
    private void procedurem1(Rectangle boundingBox, List<Rectangle> l) {
        // Get all rectangles longer than half the height of subproblem
        l.sort(Comparator.comparing(Rectangle::getHeight).reversed());
        int m = 0;
        while(m + 1 < l.size() && l.get(m+1).getHeight() >= boundingBox.getHeight() / 2) {
            m++;
        }
        // Stack them left to right on the bottom
        int w = 0;
        List<Rectangle> tempList = new ArrayList<>(l);
        for (int i = 0; i <= m; i++) {
            placeRelative(boundingBox, l.get(i), w, 0);
            w += l.get(i).getWidth();
            tempList.remove(l.get(i));
        }
        // If all rectangles places, return
        if (tempList.size() == 0) {
            return;
        }
        // Else sort remaining rectangles by decreasing width
        tempList.sort(Comparator.comparing(Rectangle::getHeight).reversed());
        if (tempList.get(0).getWidth() <= boundingBox.getWidth() - w) {
            Rectangle Q = new Rectangle(boundingBox.getWidth() - w, boundingBox.getHeight());
            placeRelative(boundingBox, Q, w, 0);
            subProblem(Q, tempList);
        } else {
            int n = 0;
            while (n + 1 < tempList.size() && tempList.get(n).getWidth() > boundingBox.getWidth() - w) {
                n++;
            }
            int h = 0;
            List<Rectangle> remainder = new ArrayList<>(tempList);
            for (int i = 0; i <= n; i++) {
                placeRelative(boundingBox, tempList.get(i), boundingBox.getWidth() - tempList.get(i).getWidth(),
                        boundingBox.getHeight() - tempList.get(i).getHeight() - h);
                h += tempList.get(i).getHeight();
                remainder.remove(tempList.get(i));
            }
            Rectangle Q = new Rectangle(boundingBox.getWidth() - w, boundingBox.getHeight() - h);
            placeRelative(boundingBox, Q, w, 0);
            subProblem(Q, remainder);
        }
    }

    /*
        Places rectangle relative to left bottom of subproblem
     */
    private void placeRelative(Rectangle boundingBox, Rectangle r, int x, int y) {
        r.setPos(boundingBox.getX() + x, boundingBox.getY() + y);
    }

    /*
        Get the minimum bounding box such that the algo can solve
     */
    private Rectangle getInitialBoundingBox(List<Rectangle> rectangles, boolean strip, int limit) {
        Rectangle boundingBox = new Rectangle(0, 0);
        boundingBox.setPos(0, 0);
        // If we're solving a strip problem, only one dimension has to be minimized
        if (strip) {
            int v = limit;
            int u = getMaxWidth(rectangles);
            int S = getTotalArea(rectangles);
            int mHeight = getMaxHeight(rectangles);
            int mWidth = getMaxWidth(rectangles);
            while(!(2 * S <= (u * v) - (Math.max(2*mWidth - u, 0) * Math.max(2*mHeight - v, 0)))) {
                u++;
            }
            boundingBox.setWidth(u);
            boundingBox.setHeight(v);
        // Otherwise we're minimizing both width and height
        } else {
            int u = getMaxWidth(rectangles);
            int v = getMaxHeight(rectangles);
            int S = getTotalArea(rectangles);
            int mHeight = getMaxHeight(rectangles);
            int mWidth = getMaxWidth(rectangles);
            while(!(2 * S <= (u * v) - (Math.max(2*mWidth - u, 0) * Math.max(2*mHeight - v, 0)))) {
                v++;
                if(!(2 * S <= (u * v) - (Math.max(2*mWidth - u, 0) * Math.max(2*mHeight - v, 0)))) {
                    u++;
                }
            }
            boundingBox.setWidth(u);
            boundingBox.setHeight(v);
        }
        return boundingBox;
    }

    /*
        Get the sum of the areas of a list of rectangles
     */
    private int getTotalArea(List<Rectangle> rectangles) {
        int sum = 0;
        for (Rectangle r : rectangles) {
            sum += r.getArea();
        }
        return sum;
    }

    /*
        Get the sum of the areas of a list of rectangles up to m
     */
    private int getTotalArea(List<Rectangle> rectangles, int m) {
        int sum = 0;
        for (int i = 0; i <= m; i++) {
            sum += rectangles.get(i).getArea();
        }
        return sum;
    }

    /*
        Get the maximum encountered width in a list of rectangles
     */
    private int getMaxWidth(List<Rectangle> rectangles) {
        int m = 0;
        for (Rectangle r : rectangles) {
            if (r.getWidth() > m) {
                m = r.getWidth();
            }
        }
        return m;
    }

    /*
        Get the maximum encountered height in a list of rectangles
     */
    private int getMaxHeight(List<Rectangle> rectangles) {
        int m = 0;
        for (Rectangle r : rectangles) {
            if (r.getHeight() > m) {
                m = r.getHeight();
            }
        }
        return m;
    }

}
