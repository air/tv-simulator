package air;

import java.awt.Dimension;
import java.awt.Point;

import processing.core.PGraphics2D;

@SuppressWarnings("serial")
public class ViewportTest extends Sketch {
    Dimension world = new Dimension(1600, 1600);
    Dimension cameraSize = new Dimension(800, 800);

    final int GRID_SIZE = 100;
    final int gridColor = color(0, 255, 0);

    Camera camera = new Camera(world.width, world.height, cameraSize.width, cameraSize.height);

    Point dummy = new Point(1, 1);
    Point lastDummy = new Point(1, 1);

    // bounce stuff
    Point velocity = new Point((int) random(2, 10), (int) random(1, 10));
    // sine stuff
    int count = 0;

    @Override
    public void setup() {
        size(cameraSize.width, cameraSize.height, PGraphics2D.P2D);
        background(0);
        stroke(255);
    }

    @Override
    public void draw() {
        showDebugAtIntervals();

        update();
        camera.centreCameraOn(dummy.x, dummy.y);
        background(0);
        drawGrid();
        drawObjects();
    }

    protected void update() {
        lastDummy.setLocation(dummy);
        // updateBounce();
        updateSineWave();
    }

    protected void updateSineWave() {
        // y will ease in starting at 0
        int phase = 90 + (count % 360); // start at 1 on the curved part
        double sin = Math.sin(Math.toRadians(phase)); // range 1 to -1
        double mult = 1 - sin; // range 0 to 2
        mult = mult / 2; // range 0 to 1
        dummy.y = (int) (mult * world.height);

        // x starts at midpoint and eases up to the edges
        phase = count % 360; // basic sin, start at 0 on fast part
        sin = Math.sin(Math.toRadians(phase));
        // x is midpoint plus sin scaling to the edges
        dummy.x = (int) ((world.width / 2) + ((world.width / 2) * sin));

        count++;
    }

    protected void updateBounce() {
        dummy.x = dummy.x + velocity.x;
        if (dummy.x >= world.width) {
            dummy.x = world.width;
            velocity.x = -velocity.x;
        }
        if (dummy.x < 0) {
            dummy.x = 0;
            velocity.x = -velocity.x;
        }

        dummy.y = dummy.y + velocity.y;
        if (dummy.y >= world.height) {
            dummy.y = world.height;
            velocity.y = -velocity.y;
        }
        if (dummy.y < 0) {
            dummy.y = 0;
            velocity.y = -velocity.y;
        }

    }

    protected void drawGrid() {
        Point dot;
        for (int gridy = 0; gridy < world.height; gridy += GRID_SIZE) {
            for (int gridx = 0; gridx < world.width; gridx += GRID_SIZE) {
                dot = camera.translateWorldToCamera(gridx, gridy);
                point(dot.x, dot.y);
            }
        }
    }

    protected void drawObjects() {
        Point from = camera.translateWorldToCamera(lastDummy.x, lastDummy.y);
        Point to = camera.translateWorldToCamera(dummy.x, dummy.y);
        line(from.x, from.y, to.x, to.y);
    }
}
