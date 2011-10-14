package air;

import java.awt.Dimension;

public class ViewportTest extends Sketch {
    Dimension canvas = new Dimension(1000, 1000);
    Dimension screen = new Dimension(500, 500);

    final int GRID_SIZE = 100;
    final int gridColor = color(0, 255, 0);

    Viewport viewport = new Viewport(canvas.width - screen.width, canvas.height - screen.height);

    @Override
    public void setup() {
        size(screen.width, screen.height);
    }

    @Override
    public void draw() {
        showDebugAtIntervals();

    }
}
