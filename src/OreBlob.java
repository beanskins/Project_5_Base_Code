import processing.core.PImage;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;


public class OreBlob extends MovingEntity {

    private static final String QUAKE_KEY = "quake";

    public OreBlob(
            String id,
            Point position,
            List<PImage> images,
            int actionPeriod,
            int animationPeriod)
    {
        super(id, position, images, actionPeriod, animationPeriod);
    }


    public void executeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Entity> blobTarget =
                world.findNearest(this.getPosition(), Vein.class);
        long nextPeriod = this.getActionPeriod();

        if (blobTarget.isPresent()) {
            Vein tgt = (Vein)blobTarget.get();
            Point tgtPos = tgt.getPosition();

            if (this.moveTo(world, blobTarget.get(), scheduler)) {
                ScheduleEntities quake = Factory.createQuake(tgtPos,
                        imageStore.getImageList(QUAKE_KEY));

                world.addEntity(quake);
                nextPeriod += this.getActionPeriod();
                quake.scheduleActions(scheduler, world, imageStore);
            }
        }

        scheduler.scheduleEvent(this,
                Factory.createActivityAction(this, world, imageStore),
                nextPeriod);
    }



    public Point nextPosition(
            WorldModel world, Point destPos)
    {
        PathingStrategy strat = new AStarPathingStrategy();

        Predicate<Point> canPassThrough = (s1) -> world.withinBounds(s1) && !(world.getOccupant(s1).isPresent() && !(world.getOccupant(s1).get() instanceof Ore));

        BiPredicate<Point, Point> withinReach = (var1, var2) -> var1.adjacent(var2);

        List<Point> path = strat.computePath(this.getPosition(), destPos, canPassThrough, withinReach, PathingStrategy.CARDINAL_NEIGHBORS);

        if (path.size() > 0)
            return path.get(0);
        return this.getPosition();
    }

    public boolean moveTo(
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        if (this.getPosition().adjacent(target.getPosition())) {
            world.removeEntity(target);
            scheduler.unscheduleAllEvents(target);
            return true;
        }
        else {
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


}
