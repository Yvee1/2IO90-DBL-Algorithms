import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class SteinbergSolver implements AlgorithmInterface {

    // Interprocedural variables
    private int condition3variable_m;
    private int conditionm3variable_m;
    private int condition2variable_i;
    private int condition2variable_k;
    private int conditionm2variable_i;
    private int conditionm2variable_k;
    private int condition0variable_i;
    List<Rect> rectangles;

    @Override
    public PackingSolution solve(PackingProblem p) {

        // Extract problem settings
        PackingSettings settings = p.getSettings();
        boolean strip = settings.getFixed();
        boolean rotate = settings.getRotation();
        int limit = settings.getMaxHeight();

        rectangles = new ArrayList<>();
        for (Rectangle r : p.getRectangles()) {
            rectangles.add(new Rect(r));
        }

        Rect boundingBox = getInitialBoundingBox(rectangles, strip, limit);
        subProblem(boundingBox, new ArrayList<>(rectangles));

        Rectangle[] solutionRects = new Rectangle[p.getRectangles().length];
        for (int i = 0; i < solutionRects.length; i++) {
            solutionRects[i] = rectangles.get(i).toRectangle();
        }

        pullLeft(solutionRects);

        PackingProblem solution = new PackingProblem(settings, solutionRects);

        return new PackingSolution(solution);
    }

    private void pullLeft(Rectangle[] recs) {
        List<Rectangle> list = Arrays.asList(recs);
        list.sort(Comparator.comparing(Rectangle::getX));
        int step = getMinWidth(list) * 100;
        for (int i = 0; i < list.size(); i++) {
            Boolean col = false;
            Rectangle r1 = list.get(i);
            while (!col && r1.getX() > 1) {
                for (int j = 0; j < i; j++) {
                    if (checkCollision(list.get(j), r1, step)) {
                        col = true;
                        break;
                    }
                }
                if (!col) {
                    r1.setX(r1.getX() - step);
                }
            }
        }
    }

    private boolean checkCollision(Rectangle left, Rectangle right, int step) {
        if (left.getX() < right.getX() + right.getWidth() - step &&
                left.getX() + left.getWidth() > right.getX() - step &&
                left.getY() < right.getY() + right.getHeight() &&
                left.getY() + left.getHeight() > right.getY()) {
            return true;
        }
        return false;
    }

    private void subProblem(Rect boundingBox, List<Rect> list) {
        if (list.size() == 0) { return; }
        list.sort(Comparator.comparing(Rect::getWidth).reversed());
        if (condition1(boundingBox, list)) { procedure1(boundingBox, list); return;}
        else if (condition2(boundingBox, list)) { procedure2(boundingBox, list); return;}
        else if (condition3(boundingBox, list)) { procedure3(boundingBox, list); return;}
        list.sort(Comparator.comparing(Rect::getHeight).reversed());
        if (conditionm1(boundingBox, list)) { procedurem1(boundingBox, list); return;}
        else if (conditionm2(boundingBox, list)) { procedurem2(boundingBox, list); return;}
        else if (conditionm3(boundingBox, list)) { procedurem3(boundingBox, list); return;}
        else if (condition0(boundingBox, list)) { procedure0(boundingBox, list); return;}
    }

    /*
        Is the given subproblem solvable with this algo
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

    private void place(Rect bb, Rect r, double x, double y) {
        r.setPos(bb.getX() + x, bb.getY() + y);
    }

    private void procedure0(Rect boundingBox, List<Rect> list) {
        Rect r = list.get(condition0variable_i);
        place(boundingBox, r, 0, 0);
        list.remove(r);
        Rect bb = new Rect(boundingBox.getWidth() - r.getWidth(), boundingBox.getHeight());
        place(boundingBox, bb, r.getWidth(), 0);
        subProblem(bb, list);
    }

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
            bb.setPos(w, 0);
            subProblem(bb, list3);
        }
    }

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
            bb.setPos(0, h);
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

    // Rectangle class that supports doubles (Necessary for this algorithm)
    public class Rect {

        private double x = -1;
        private double y = -1;
        private double width;
        private double height;
        private boolean rotated;

        public Rect(double width, double height) {
            this.width = width;
            this.height = height;
        }

        public Rect(Rectangle r) {
            this.width = r.getWidth();
            this.height = r.getHeight();
            this.x = r.getX();
            this.y = r.getY();
        }

        public Rectangle toRectangle() {
            Rectangle r = new Rectangle((int) width, (int) height);
            r.setPos((int) Math.floor(x), (int) Math.floor(y));
            return r;
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
