import processing.core.PImage;

import java.util.List;

//ActiveEntity
public abstract class ScheduleEntities extends Entity {

    private final int actionPeriod;

    public ScheduleEntities(String id,
                            Point position,
                            List<PImage> images,
                            int actionPeriod) {
        super(id, position, images);
        this.actionPeriod = actionPeriod;
    }

    protected void scheduleActions(
            EventScheduler scheduler,
            WorldModel world,
            ImageStore imageStore)
    {
        scheduler.scheduleEvent(this,
                Factory.createActivityAction(this, world, imageStore),
                this.actionPeriod);
    }

    protected abstract void executeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler);

    protected int getActionPeriod() {
        return this.actionPeriod;
    }
}
