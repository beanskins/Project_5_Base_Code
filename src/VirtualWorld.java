import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.Scanner;
import java.util.Random;

import processing.core.*;

public final class VirtualWorld extends PApplet
{
    private static final int TIMER_ACTION_PERIOD = 100;

    private static final int VIEW_WIDTH = 640;
    private static final int VIEW_HEIGHT = 480;
    private static final int TILE_WIDTH = 32;
    private static final int TILE_HEIGHT = 32;
    private static final int WORLD_WIDTH_SCALE = 2;
    private static final int WORLD_HEIGHT_SCALE = 2;

    private static final int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
    private static final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;
    private static final int WORLD_COLS = VIEW_COLS * WORLD_WIDTH_SCALE;
    private static final int WORLD_ROWS = VIEW_ROWS * WORLD_HEIGHT_SCALE;

    private static final String IMAGE_LIST_FILE_NAME = "imagelist";
    private static final String DEFAULT_IMAGE_NAME = "background_default";
    private static final int DEFAULT_IMAGE_COLOR = 0x808080;

    private static final String LOAD_FILE_NAME = "world.sav";

    private static final String FAST_FLAG = "-fast";
    private static final String FASTER_FLAG = "-faster";
    private static final String FASTEST_FLAG = "-fastest";
    private static final double FAST_SCALE = 0.5;
    private static final double FASTER_SCALE = 0.25;
    private static final double FASTEST_SCALE = 0.10;

    private static double timeScale = 1.0;

    private ImageStore imageStore;
    private WorldModel world;
    private WorldView view;
    private EventScheduler scheduler;

    private long nextTime;

    public void settings() {
        size(VIEW_WIDTH, VIEW_HEIGHT);
    }

    /*
       Processing entry point for "sketch" setup.
    */
    public void setup() {
        this.imageStore = new ImageStore(
                createImageColored(TILE_WIDTH, TILE_HEIGHT,
                                   DEFAULT_IMAGE_COLOR));
        this.world = new WorldModel(WORLD_ROWS, WORLD_COLS,
                                    createDefaultBackground(imageStore));
        this.view = new WorldView(VIEW_ROWS, VIEW_COLS, this, world, TILE_WIDTH,
                                  TILE_HEIGHT);
        this.scheduler = new EventScheduler(timeScale);

        loadImages(IMAGE_LIST_FILE_NAME, imageStore, this);
        loadWorld(world, LOAD_FILE_NAME, imageStore);

        scheduleActions(world, scheduler, imageStore);

        nextTime = System.currentTimeMillis() + TIMER_ACTION_PERIOD;

        iceMountain iceMountain = new iceMountain("iceMountain", new Point(16, 9), imageStore.getImageList("iceMountain"), 5);
        iceMountain.executeActivity(world, imageStore, scheduler);
        iceMountain.scheduleActions(scheduler, world, imageStore);
        world.addEntity(iceMountain);

        iceMountain iceMountain2 = new iceMountain("iceMountain", new Point(33, 7), imageStore.getImageList("iceMountain"), 5);
        iceMountain2.executeActivity(world, imageStore, scheduler);
        iceMountain2.scheduleActions(scheduler, world, imageStore);
        world.addEntity(iceMountain2);


    }

    public void draw() {
        long time = System.currentTimeMillis();
        if (time >= nextTime) {
            this.scheduler.updateOnTime(time);
            nextTime = time + TIMER_ACTION_PERIOD;
        }

        view.drawViewport();
    }

    public void keyPressed() {
        if (key == CODED) {
            int dx = 0;
            int dy = 0;

            switch (keyCode) {
                case UP:
                    dy = -1;
                    break;
                case DOWN:
                    dy = 1;
                    break;
                case LEFT:
                    dx = -1;
                    break;
                case RIGHT:
                    dx = 1;
                    break;
            }
            this.view.shiftView(dx, dy);
        }
    }

    private Background createDefaultBackground(ImageStore imageStore) {
        return new Background(imageStore.getImageList(DEFAULT_IMAGE_NAME));
    }

    private PImage createImageColored(int width, int height, int color) {
        PImage img = new PImage(width, height, RGB);
        img.loadPixels();
        for (int i = 0; i < img.pixels.length; i++) {
            img.pixels[i] = color;
        }
        img.updatePixels();
        return img;
    }

    private void loadImages(
            String filename, ImageStore imageStore, PApplet screen)
    {
        try {
            Scanner in = new Scanner(new File(filename));
            Functions.loadImages(in, imageStore, screen);
        }
        catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    private void loadWorld(
            WorldModel world, String filename, ImageStore imageStore)
    {
        try {
            Scanner in = new Scanner(new File(filename));
            Functions.load(in, world, imageStore);
        }
        catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    private void scheduleActions(
            WorldModel world, EventScheduler scheduler, ImageStore imageStore)
    {
        for (Entity entity : world.getEntities()) {
            if (entity instanceof ScheduleEntities) {
                ScheduleEntities ent = (ScheduleEntities) entity;
                ent.scheduleActions(scheduler, world, imageStore);
            }
        }
    }

    private static void parseCommandLine(String[] args) {
        for (String arg : args) {
            switch (arg) {
                case FAST_FLAG:
                    timeScale = Math.min(FAST_SCALE, timeScale);
                    break;
                case FASTER_FLAG:
                    timeScale = Math.min(FASTER_SCALE, timeScale);
                    break;
                case FASTEST_FLAG:
                    timeScale = Math.min(FASTEST_SCALE, timeScale);
                    break;
            }
        }
    }

    private Point mouseToPoint()
    {
        return new Point(mouseX/TILE_WIDTH + view.getViewport().getCol(), mouseY/TILE_WIDTH + view.getViewport().getRow());
    }

    public void mousePressed() {
        Point pressed = mouseToPoint();
        System.out.println(pressed.x + ", " + pressed.y);
        Random rand = new Random();

        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                Point affected = new Point(pressed.x + i, pressed.y + j);
                world.setBackground(affected, new Background(imageStore.getImageList("dirt")));
            }
        }

        for (int i = -1; i < 2; i++) { //adding randomness to top edge of the dirt
            Point outline = new Point(pressed.x + i, pressed.y - 2);
            int randomInt = rand.nextInt(3);
            if (randomInt == 1)
                world.setBackground(outline, new Background(imageStore.getImageList("dirt")));
        }

        for (int j = -1; j < 2; j++) { //adding randomness to right edge of the dirt
            Point outline = new Point(pressed.x + 2, pressed.y + j);
            int randomInt = rand.nextInt(3);
            if (randomInt == 1)
                world.setBackground(outline, new Background(imageStore.getImageList("dirt")));
        }

        for (int i = -1; i < 2; i++) { //adding randomness to bottom edge of the dirt
            Point outline = new Point(pressed.x + i, pressed.y + 2);
            int randomInt = rand.nextInt(3);
            if (randomInt == 1)
                world.setBackground(outline, new Background(imageStore.getImageList("dirt")));
        }

        for (int j = -1; j < 2; j++) { //adding randomness to left edge of the dirt
            Point outline = new Point(pressed.x - 2, pressed.y + j);
            int randomInt = rand.nextInt(3);
            if (randomInt == 1)
                world.setBackground(outline, new Background(imageStore.getImageList("dirt")));
        }

        if (!(world.isOccupied(pressed)) || world.getOccupant(pressed).get() instanceof MinerTypes ) {

            Optional<Point> open = world.findOpenAround(pressed);
            if (open.isPresent() && !(world.isOccupied(pressed))) {
                Point spawn = open.get();

                AnimatingEntities zombie = new FireZombie("fireZombie", spawn, imageStore.getImageList("fireZombie"), 1000, 200);
                world.addEntity(zombie);
                zombie.scheduleActions(scheduler,world,imageStore);
            }
            AnimatingEntities fire = Factory.createFire("id", pressed, imageStore.getImageList("fire"), 10, 10);
            fire.executeActivity(world, imageStore, scheduler);
            fire.scheduleActions(scheduler,world,imageStore);
            world.addEntity(fire);


        }
        redraw();
    }

    public static void main(String[] args) {
        parseCommandLine(args);
        PApplet.main(VirtualWorld.class);
    }
}
