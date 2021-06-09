import processing.core.PImage;
import java.util.List;
import java.util.Optional;

public class Fire extends AnimatingEntities{

    private static final int fireSpeedBoost = 700;

    public Fire(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    public static int getFireSpeedBoost() {
        return fireSpeedBoost;
    }

    protected void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler)
    {
        Optional<Entity> target =
                world.findNearest(this.getPosition(), MinerTypes.class);

        if (target.isPresent() && this.getPosition().equals(target.get().getPosition())){
            MinerTypes tminer = (MinerTypes)target.get();

            Optional<Point> open = world.findOpenAround(this.getPosition());

            if (open.isPresent()) {
                Point spawn = open.get();
                MinerBurnt minerBurnt = Factory.createMinerBurnt(tminer.getId(), spawn,
                        imageStore.getImageList("burntMiner"),
                        tminer.getResourceLimit(),
                        tminer.getActionPeriod()-fireSpeedBoost,      //fire speed boost
                        tminer.getAnimationPeriod());

                world.removeEntity(tminer);
                scheduler.unscheduleAllEvents(tminer);

                world.addEntity(minerBurnt);
                minerBurnt.scheduleActions(scheduler, world, imageStore);
            }
            else{
                world.removeEntity(tminer);
                scheduler.unscheduleAllEvents(tminer);
            }
            scheduler.scheduleEvent(this,
                    Factory.createActivityAction(this, world, imageStore),
                    this.getActionPeriod());
        }
    }

    protected void scheduleActions(
            EventScheduler scheduler,
            WorldModel world,
            ImageStore imageStore)
    {
        scheduler.scheduleEvent(this,
                Factory.createActivityAction(this, world, imageStore),
                this.getActionPeriod());
        scheduler.scheduleEvent(this, Factory.createAnimationAction(this,
                0),
                this.getAnimationPeriod());
    }

}
