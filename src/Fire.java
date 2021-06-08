import processing.core.PImage;
import java.util.List;
import java.util.Optional;

public class Fire extends AnimatingEntities{

    public Fire(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    protected void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler)
    {
        Optional<Entity> target =
                world.findNearest(this.getPosition(), MinerTypes.class);

        if (this.getPosition() == target.get().getPosition()){
            MinerTypes tminer = (MinerTypes)target.get();

            MinerBurnt miner = Factory.createMinerBurnt(tminer.getId(),tminer.getPosition(),
                    tminer.getImages(),         //get burnt images
                    tminer.getResourceLimit(),
                    tminer.getActionPeriod(),
                    tminer.getAnimationPeriod());

            world.removeEntity(tminer);
            scheduler.unscheduleAllEvents(tminer);
            world.addEntity(miner);
            miner.scheduleActions(scheduler, world, imageStore);
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
                10),
                this.getAnimationPeriod());
    }

}
