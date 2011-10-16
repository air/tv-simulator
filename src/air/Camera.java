package air;

import java.awt.Dimension;
import java.awt.Point;

public class Camera {
    /** topleft coords of the viewport */
    public Point position = new Point(0, 0);

    protected Dimension size = new Dimension(0, 0);
    protected Dimension world = new Dimension(0, 0);
    protected Point maxPosition = new Point(0, 0);

    public Camera(int worldWidth, int worldHeight, int cameraWidth, int cameraHeight) {
        world.width = worldWidth;
        world.height = worldHeight;
        size.width = cameraWidth;
        size.height = cameraHeight;
        maxPosition = new Point(world.width - size.width, world.height - size.height);
    }

    public void centreCameraOn(int x, int y) {
        int targetX = (int) (x - (size.width / 2));
        int targetY = (int) (y - (size.height / 2));
        setViewport(targetX, targetY);
    }

    /** use translate(-camera.position.x, camera.position.y) */
    @Deprecated
    public Point translateWorldToCamera(int x, int y) {
        // transform.translate((obj.location.x - viewport.x), (obj.location.y - viewport.y)); // original code
        return new Point(x - position.x, y - position.y);
    }

    /** move the viewport's top-left corner to this location */
    protected void setViewport(int x, int y) {
        if (x > maxPosition.x) {
            // log("can't set viewport to x " + x + ", capping at " + maxPosition.x);
            x = maxPosition.x;
        }
        else if (x < 0) {
            // log("can't set viewport to x " + x + ", capping at 0");
            x = 0;
        }
        if (y > maxPosition.y) {
            // log("can't set viewport to y " + y + ", capping at " + maxPosition.y);
            y = maxPosition.y;
        }
        else if (y < 0) {
            // log("can't set viewport to y " + y + ", capping at 0");
            y = 0;
        }
        position.x = x;
        position.y = y;
    }

    protected void log(String msg) {
        System.out.println(msg);
    }
}
