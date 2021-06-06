import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;


class AStarPathingStrategy
        implements PathingStrategy
{


    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors) {
        Comparator<Node> comp = (s1, s2) -> {
            if (s1.getFVal() < s2.getFVal())
                return -1;
            if (s1.getFVal() > s2.getFVal())
                return 1;
            return 0;
        };
        List<Point> path = new LinkedList<>();
        HashMap<Point, Node> openL = new HashMap<>();
        PriorityQueue<Node> openLpQueue = new PriorityQueue<>(10, comp);
        HashMap<Point, Node> closedL = new HashMap<>();

        Node current = new Node(start, 0, 0, null);
        openL.put(current.getPos(), current);
        openLpQueue.add(current);

        while (current != null && !withinReach.test(current.getPos(), end)) {

            Node tempCurr = current;
            List<Node> validNeighbors = potentialNeighbors.apply(current.getPos())
                    .filter(canPassThrough).filter(s -> !(closedL.containsKey(s)))
                    .map(s -> new Node(s, tempCurr.getGVal() + (int) tempCurr.getPos().dist(s), s.dist(end), tempCurr))
                    .collect(Collectors.toList());

            for (Node node : validNeighbors) {
                if (!(openL.containsKey(node.getPos()))) {
                    openL.put(node.getPos(), node);
                    openLpQueue.add(node);

                } else {
                    if (node.getGVal() < openL.get(node.getPos()).getGVal())
                        openL.get(node.getPos()).setGVal(node.getGVal());
                }
            }
            closedL.put(current.getPos(), current);
            current = openLpQueue.poll();
            if (current != null)
                openL.remove(current.getPos());
        }
        while (current != null && current.getPriorNode() != null) {
            path.add(current.getPos());
            current = current.getPriorNode();
        }
        Collections.reverse(path);
        return path;
    }
}
