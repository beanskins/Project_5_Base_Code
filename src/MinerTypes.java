import processing.core.PImage;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class MinerTypes extends MovingEntity {

    private final int resourceLimit;

    public MinerTypes(String id,
                      Point position,
                      List<PImage> images,
                      int resourceLimit,
                      int actionPeriod,
                      int animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
        this.resourceLimit = resourceLimit;
    }

    protected Point nextPosition(
            WorldModel world, Point destPos)
    {
        PathingStrategy strat = new AStarPathingStrategy();

        Predicate<Point> canPassThrough = (s1) -> world.withinBounds(s1) && !world.isOccupied(s1);

        BiPredicate<Point, Point> withinReach = (var1, var2) -> var1.adjacent(var2);

        List<Point> path = strat.computePath(this.getPosition(), destPos,
                canPassThrough,
                withinReach,
                PathingStrategy.CARDINAL_NEIGHBORS);

        if (path.size() > 0)
            return path.get(0);
        return this.getPosition();

    }

    protected int getResourceLimit() {
        return resourceLimit;


    }
}
