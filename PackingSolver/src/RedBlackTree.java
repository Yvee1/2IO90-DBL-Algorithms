import java.util.Comparator;

public class RedBlackTree<E> {

    private final RedBlackNode<E> sentinel = new RedBlackNode<>(null, Colour.BLACK, null);
    private final Comparator<E> comp;

    RedBlackNode<E> root = sentinel;

    public RedBlackTree(Comparator<E> comp) {
        this.comp = comp;
    }

    /**
     * Insert a node with data into the tree.
     * @param data The data to be inserted.
     * @return The node with the data.
     */
    public RedBlackNode<E> insert(E data) {
        RedBlackNode<E> z = new RedBlackNode<E>(data, Colour.RED, sentinel);
        _insert(z);
        return z;
    }

    /**
     * Delete the (a) node containing data.
     * @param data The data to be deleted.
     */
    public void delete(E data) {
        RedBlackNode<E> z = find(data);

        delete(z);
    }

    /**
     * Find a node which contains data.
     * @param data The data to be found.
     * @return The node which contains data.
     */
    private RedBlackNode<E> find(E data) {
        return null;
    }

    /**
     * Insert a node into the red-black tree.
     * @param z The node to be inserted.
     */
    private void _insert(RedBlackNode<E> z) {
        RedBlackNode<E> y = sentinel;
        RedBlackNode<E> x = root;

        while (x != sentinel) {
            y = x;
            if (comp.compare(z.data, x.data) < 0) {
                x = x.left;
            } else {
                x = x.right;
            }
        }

        z.p = y;

        if (y == sentinel) {
            root = z;
        }
        else if (comp.compare(z.data, y.data) < 0) {
            y.left = z;
        } else {
            y.right = z;
        }

        _fixRedBlackInsert(z);
    }

    /**
     * Delete a node z from the red-black tree.
     * @param z The node to be deleted.
     */
    public void delete(RedBlackNode<E> z) {
        final boolean left_child = z.p.left == z;

        /* z has two children. */
        if (z.left != sentinel && z.right != sentinel) {
            RedBlackNode<E> y = _successor(z);
            z.data = y.data;
            delete(y);
        }

        /* z has only a left child. */
        else if (z.left != sentinel) {
            if (left_child) { z.p.left = z.left; }
            else { z.p.right = z.left; }
            z.left.p = z.p;
        }

        /* z has only a right child. */
        else if (z.right != sentinel) {
            if (left_child) { z.p.left = z.right; }
            else { z.p.right = z.right; }
            z.right.p = z.p;
        }

        /* z has no children. */
        else {
            if (left_child) { z.p.left = sentinel; }
            else { z.p.right = sentinel; }
        }
    }

    /**
     * Find the successor of x
     * @param x The node of which the successor needs to be found.
     * @return
     */
    private RedBlackNode<E> _successor(RedBlackNode<E> x) {

        if (x.right != sentinel) {
            return _treeMinimum(x.right);
        }

        RedBlackNode<E> y = x.p;

        while (y != sentinel && x == y.right) {
            x = y;
            y = x.p;
        }
        return y;
    }

    /**
     * Find the minimum element of the subtree rooted at x.
     * @param x The root of the subtre.
     * @return The minimum element of the subtree.
     */
    private RedBlackNode<E> _treeMinimum(RedBlackNode<E> x) {
        while (x.left != sentinel) {
            x = x.left;
        }
        return x;
    }

    /**
     * Rotate left around z.
     * @param z The node to be rotated around.
     */
    private void _rotateLeft(RedBlackNode<E> z) {
        RedBlackNode<E> z_new = z.right;
        RedBlackNode<E> p = z.p;

        z.right = z_new.left;
        z_new.left = z;
        z.p = z_new;

        if (z.right != sentinel) {
            z.right.p = z;
        }

        if (p != sentinel) {
            if (z == p.left) { p.left = z_new; }
            else { p.right = z_new; }
        }

        z_new.p = p;
    }

    /**
     * Rotate right around z.
     * @param z The node to be rotated around.
     */
    private void _rotateRight(RedBlackNode<E> z) {
        RedBlackNode<E> z_new = z.left;
        RedBlackNode<E> p = z.p;

        z.left = z_new.right;
        z_new.right = z;
        z.p = z_new;

        if(z.left != sentinel) {
            z.left.p = z;
        }

        if (p != sentinel) {
            if (z == p.left) { p.left = z_new; }
            else { p.right = z_new; }
        }

        z_new.p = p;

    }

    /**
     * Find the uncle of z.
     * @param z The node for which the uncle needs to be found.
     * @return The uncle of z.
     */
    private RedBlackNode<E> _uncle(RedBlackNode<E> z) {
        if (z.p == z.p.p.left) { return z.p.p.right; }
        else { return z.p.p.left; }
    }

    /**
     * Fix the red-black properties after insertion of z.
     * @param z The node that was inserted.
     */
    private void _fixRedBlackInsert(RedBlackNode<E> z) {

        /* Case 1 */
        if (z.p == sentinel) { z.colour = Colour.BLACK; return; }

        /* Case 2 */
        else if (z.p.colour == Colour.BLACK) { /* Do nothing */ return; }

        RedBlackNode<E> uncle = _uncle(z);

        /* Case 3 */
        if (uncle != sentinel && uncle.colour == Colour.RED) {
            z.p.colour = Colour.BLACK;
            uncle.colour = Colour.BLACK;
            z.p.p.colour = Colour.RED;
            _fixRedBlackInsert(z.p.p);
            return;
        }

        /* Case 4 */
        if (z == z.p.right && z.p == z.p.p.left) {
            _rotateLeft(z.p);
            z = z.left;
        }
        else if (z == z.p.left && z.p == z.p.p.right) {
            _rotateRight(z.p);
            z = z.right;
        }

        RedBlackNode<E> p = z.p;
        RedBlackNode<E> gp = z.p.p;

        if (z == p.left) { _rotateRight(gp); }
        else { _rotateLeft(gp); }

        p.colour = Colour.BLACK;
        gp.colour = Colour.RED;

    }

}

class RedBlackNode<E> {
    Colour colour;
    E data;

    RedBlackNode<E> p;
    RedBlackNode<E> left, right;

    public RedBlackNode(E data) {
        this.data = data;
    }

    public RedBlackNode(E data, Colour colour, RedBlackNode<E> sentinel) {
        this(data);
        this.colour = colour;
        this.left = this.right = sentinel;
    }
}

enum Colour {
    BLACK,
    RED,
}
