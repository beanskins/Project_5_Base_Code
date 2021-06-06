public class Node {
    private final Point pos;
    private int gVal;
    private final double fVal;
    private final double hVal;
    private final Node priorNode;

    public Node(Point pos, int gVal, double hVal, Node priorNode) {
        this.pos = pos;
        this.gVal = gVal;
        this.hVal = hVal;
        this.fVal = gVal + hVal;
        this.priorNode = priorNode;
    }

    public Point getPos() { return pos; }

    public int getGVal() {
        return gVal;
    }

    public void setGVal(int i) {
        this.gVal = i;
    }

    public double getFVal() {
        return fVal;
    }

    public Node getPriorNode() {
        return priorNode;
    }

    public boolean equals(Node o) {
        return pos.equals(o.pos);
    }

    public int hashCode() {
        return pos.hashCode();
    }

}
