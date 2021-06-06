import processing.core.PImage;

import java.util.List;

public abstract class MovingEntity extends AnimatingEntities {

    public MovingEntity (String id,
                         Point position,
                         List<PImage> images,
                         int actionPeriod,
                         int animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    protected abstract boolean moveTo(
            WorldModel world,
            Entity target,
            EventScheduler scheduler);

    protected abstract Point nextPosition(WorldModel world, Point destPos);

    protected void scheduleActions(
            EventScheduler scheduler,
            WorldModel world,
            ImageStore imageStore)
    {
        super.scheduleActions(scheduler, world, imageStore);
        scheduler.scheduleEvent(this,
                Factory.createAnimationAction(this, 0),
                this.getAnimationPeriod());
    }

}
