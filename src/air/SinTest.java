package air;

@SuppressWarnings("serial")
public class SinTest extends Sketch {
    public void setup() {
        size(360, 360);
        int halfHeight = height / 2;
        background(0);
        for (int i = 0; i < 360; i++) {
            double sin = Math.sin(Math.toRadians(i));

            int y = halfHeight + (int) (-sin * halfHeight); // invert to match 0..1 on Y axis
            stroke(255);
            point(i, y);

            y = halfHeight + (int) (sin * halfHeight);
            stroke(255, 0, 0);
            point(i, y);
        }
        System.out.println("white = sin(x)");
        System.out.println("red = -sin(x)");
    }
}
