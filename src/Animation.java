public class Animation implements Action {
    private final AnimatingEntities animEntity;
    private final WorldModel world;
    private final ImageStore imageStore;
    private final int repeatCount;

    public Animation (

            AnimatingEntities animEntity,
            WorldModel world,
            ImageStore imageStore,
            int repeatCount) {

        this.animEntity = animEntity;
        this.world = world;
        this.imageStore = imageStore;
        this.repeatCount = repeatCount;
    }

    public ImageStore getImageStore() {
        return imageStore;
    }

    public AnimatingEntities getEntity() {
        return animEntity;
    }

    public WorldModel getWorld() {
        return world;
    }

    public void executeAction(EventScheduler scheduler) {
        this.animEntity.nextImage();

        if (this.repeatCount != 1) {
            scheduler.scheduleEvent(this.animEntity,
                    Factory.createAnimationAction(this.animEntity,
                            Math.max(this.repeatCount - 1,
                                    0)),
                    this.animEntity.getAnimationPeriod());
        }
    }

}
