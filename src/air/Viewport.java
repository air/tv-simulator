package air;

import java.awt.Point;

/**
 * The viewport
 * 
 */
public class Viewport {
    /** Topleft coords of the viewport. The user's window on the worldspace begins at this point */
    protected Point viewport;

    /** viewport position cannot exceed these limits */
    protected int maxViewportX, maxViewportY;

    public Viewport(int maxX, int maxY) {
        maxViewportX = maxX;
        maxViewportY = maxY;
    }

    /** move the viewport's top-left corner to this location */
    protected void setViewPort(int x, int y) {
        if (x > maxViewportX) {
            log("can't set viewport to x " + x + ", capping at " + maxViewportX);
            x = maxViewportX;
        }
        else if (x < 0) {
            log("can't set viewport to x " + x + ", capping at 0");
            x = 0;
        }
        if (y > maxViewportY) {
            log("can't set viewport to y " + y + ", capping at " + maxViewportY);
            x = maxViewportY;
        }
        else if (y < 0) {
            log("can't set viewport to y " + y + ", capping at 0");
            y = 0;
        }
        viewport.setLocation(new Point(x, y));
    }

    protected void log(String msg) {
        System.out.println(msg);
    }
}
