import processing.core.PImage;
import java.util.List;


public abstract class Entity {

    private Point position;
    private final String id;
    private final List<PImage> images;
    private int imageIndex;

    public Entity (String id, Point position, List<PImage> images) {
        this.position = position;
        this.id = id;
        this.images = images;
    }

    protected Point getPosition() {
        return position;
    }

    protected PImage getCurrentImage() {
        return this.images.get((this).imageIndex);
    }

    protected void setPosition(Point pos) {
        this.position = pos;
    }

    protected int getImageIndex() {
        return imageIndex;
    }

    protected List<PImage> getImages() {
        return images;
    }

    protected void setImageIndex(int i) {
        this.imageIndex = i;
    }

    protected String getId() {
        return this.id;
    }
}
