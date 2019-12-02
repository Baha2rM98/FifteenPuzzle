package Node;

/**
 * @author baha2r
 * Date: 28/Nov/2019 10:38 PM
 **/

public class Node {

    // X coordination
    public int x;

    // Y coordination
    public int y;


    /**
     * Constructor
     *
     * @param x X coordination of a Node
     * @param y Y coordination of a Node
     */
    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }


    /**
     * Returns position of X
     *
     * @return Returns position of X
     */
    public int getX() {
        return x;
    }


    /**
     * Returns position of Y
     *
     * @return Returns position of Y
     */
    public int getY() {
        return y;
    }
}