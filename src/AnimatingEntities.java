import processing.core.PImage;

import java.util.List;

public abstract class AnimatingEntities extends ScheduleEntities {

    private final int animationPeriod;

    public AnimatingEntities(String id,
                             Point position,
                             List<PImage> images,
                             int actionPeriod,
                             int animationPeriod) {
        super(id, position, images, actionPeriod);
        this.animationPeriod = animationPeriod;
    }

    protected void nextImage() {
        this.setImageIndex((this.getImageIndex() + 1) % this.getImages().size());
    }

    protected int getAnimationPeriod() {
        return this.animationPeriod;
    }
}
