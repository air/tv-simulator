package air;

import java.awt.Point;

@SuppressWarnings("serial")
public class OrbitingPoint extends Point {

    int count = 0;
    Point centre; // never changes after init, go around this
    Point radius;

    /** Starts at the bottom and goes anticlockwise */
    public OrbitingPoint(int centreX, int centreY, int radiusX, int radiusY) {
        super(centreX, centreY + radiusY);
        centre = new Point(centreX, centreY);
        radius = new Point(radiusX, radiusY);
    }

    /** TODO: SLOWWWWW */
    public void update() {
        int phase = count % 360; // basic sin, start at 0 on fast part
        double sin = Math.sin(Math.toRadians(phase));
        // x is centre plus sin scaling to the edges
        this.x = (int) (radius.x + (radius.x * sin));

        phase = 90 + (count % 360); // quarter turn out of phase
        sin = Math.sin(Math.toRadians(phase));
        // x is centre plus sin scaling to the edges
        this.y = (int) (radius.y + (radius.y * sin));

        count++;
    }
}
