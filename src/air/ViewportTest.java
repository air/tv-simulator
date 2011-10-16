package air;

import java.awt.Dimension;
import java.awt.Point;

import processing.core.PGraphics2D;

@SuppressWarnings("serial")
public class ViewportTest extends Sketch {
    Dimension world = new Dimension(1900, 1400);
    Dimension cameraSize = new Dimension(500, 500);

    final int GRID_SIZE = 100;
    final int gridColor = color(0, 255, 0);

    Camera camera = new Camera(world.width, world.height, cameraSize.width, cameraSize.height);

    OrbitingPoint target;
    Point lastTarget = new Point(1, 1);

    // bounce stuff
    Point velocity = new Point((int) random(2, 10), (int) random(1, 10));

    @Override
    public void setup() {
        size(cameraSize.width, cameraSize.height, PGraphics2D.P2D);
        background(0);
        stroke(255);

        int centreX = world.width / 2;
        int centreY = world.height / 2;
        target = new OrbitingPoint(centreX, centreY, centreX, centreY);
    }

    @Override
    public void draw() {
        showDebugAtIntervals();

        update();
        camera.centreCameraOn(target.x, target.y);
        translate(-camera.position.x, -camera.position.y);

        background(0);
        drawGrid();
        drawObjects();
    }

    protected void update() {
        lastTarget.setLocation(target);
        // updateBounce();
        target.update();
    }

    protected void updateBounce() {
        target.x = target.x + velocity.x;
        if (target.x >= world.width) {
            target.x = world.width;
            velocity.x = -velocity.x;
        }
        if (target.x < 0) {
            target.x = 0;
            velocity.x = -velocity.x;
        }

        target.y = target.y + velocity.y;
        if (target.y >= world.height) {
            target.y = world.height;
            velocity.y = -velocity.y;
        }
        if (target.y < 0) {
            target.y = 0;
            velocity.y = -velocity.y;
        }

    }

    protected void drawGrid() {
        for (int gridy = 0; gridy < world.height; gridy += GRID_SIZE) {
            for (int gridx = 0; gridx < world.width; gridx += GRID_SIZE) {
                point(gridx, gridy);
            }
        }
    }

    protected void drawObjects() {
        line(lastTarget.x, lastTarget.y, target.x, target.y);
    }
}
