package air;

import java.awt.Dimension;
import java.awt.Point;

import processing.core.PGraphics2D;

public class ViewportTest extends Sketch {
    Dimension world = new Dimension(600, 300);
    Dimension cameraSize = new Dimension(600, 300);

    final int GRID_SIZE = 100;
    final int gridColor = color(0, 255, 0);

    Camera camera = new Camera(world.width, world.height, cameraSize.width, cameraSize.height);

    Point dummy = new Point(1, 1);
    Point velocity = new Point((int) random(2, 10), (int) random(1, 10));

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
        Point dot = camera.translateWorldToCamera(dummy.x, dummy.y);
        point(dot.x, dot.y);
    }
}
