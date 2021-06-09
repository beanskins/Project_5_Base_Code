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
                    700, 100);

            world.addEntity(snowman);
            snowman.scheduleActions(scheduler, world, imageStore);

        }
        super.scheduleActions(scheduler, world, imageStore);


    }
}
