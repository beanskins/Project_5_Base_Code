import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class FireZombie extends MovingEntity {

    public FireZombie(
            String id,
            Point position,
            List<PImage> images,
            int actionPeriod,
            int animationPeriod){
        super(id, position, images, actionPeriod, animationPeriod);
    }

    protected Point nextPosition(
            WorldModel world, Point destPos)
    {
        PathingStrategy strat = new AStarPathingStrategy();

        Predicate<Point> canPassThrough = (s1) -> world.withinBounds(s1) && !(world.getOccupant(s1).isPresent() && !(world.getOccupant(s1).get() instanceof Fire));

        BiPredicate<Point, Point> withinReach = (var1, var2) -> var1.adjacent(var2);

        List<Point> path = strat.computePath(this.getPosition(), destPos,
                canPassThrough,
                withinReach,
                PathingStrategy.CARDINAL_NEIGHBORS);

        if (path.size() > 0)
            return path.get(0);
        return this.getPosition();

    }


    public boolean moveTo(
            WorldModel world,
            Entity target,
            EventScheduler scheduler) {
        if (this.getPosition().adjacent(target.getPosition())) {
            return true;
        } else {
            Point nextPos = this.nextPosition(world, target.getPosition());

            if (!this.getPosition().equals(nextPos)) {
                Optional<Entity> occupant = world.getOccupant(nextPos);
                if (occupant.isPresent()) {
                    scheduler.unscheduleAllEvents(occupant.get());
                }

                world.moveEntity(this, nextPos);
            }
            return false;
        }
    }

    public  void executeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Entity> target =
                world.findNearest(this.getPosition(), MinerTypes.class);
        if (target.isPresent() && this.moveTo(world, target.get(), scheduler)){

            MinerTypes tminer = (MinerTypes)target.get();

            FireZombie nZom = Factory.createFireZombie(this.getId(),tminer.getPosition(),
                    this.getImages(),
                    this.getActionPeriod(),
                    this.getAnimationPeriod());

            world.removeEntity(tminer);
            scheduler.unscheduleAllEvents(tminer);

            world.addEntity(nZom);
            nZom.scheduleActions(scheduler, world, imageStore);
        }
        scheduler.scheduleEvent(this,
                Factory.createActivityAction(this, world, imageStore),
                this.getActionPeriod());
    }


}
