import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class iceMountain extends ScheduleEntities {

    public iceMountain(String id,
                       Point position,
                       List<PImage> images,
                       int actionPeriod) {
        super(id, position, images, actionPeriod);
    }

    public void executeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Point> openPt = world.findOpenAround(this.getPosition());

        if (openPt.isPresent()) {
            SnowMan snowman = Factory.createSnowMan("snowman",
                    openPt.get(),
                    imageStore.getImageList("snowman"),
                    5, 5);
            world.addEntity(snowman);
            snowman.scheduleActions(scheduler, world, imageStore);
            snowman.executeActivity(world, imageStore, scheduler);
        }
        super.scheduleActions(scheduler, world, imageStore);


    }
}
