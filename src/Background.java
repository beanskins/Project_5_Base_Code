import java.util.List;

import processing.core.PImage;

public final class Background
{
    // private final String id;
    private final List<PImage> images;
    private int imageIndex;

    public Background(List<PImage> images) {
     //   this.id = id;
        this.images = images;
        this.imageIndex = 0;
    }

    public PImage getCurrentImage() {
        return this.images.get((this).imageIndex);
    }
}
