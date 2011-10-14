package air;

@SuppressWarnings("serial")
public class LogTest extends Sketch {
    boolean mouseMoved;

    public void setup() {
        size(300, 700);
    }

    public void draw() {
        if (mouseMoved) {
            log("mouse: %d", mouseY);
            log("value: %.4f", parseMouse());
            log("");
            mouseMoved = false;
        }
    }

    public void mouseMoved() {
        mouseMoved = true;
    }

    protected float parseMouse() {
        double steps = height;
        double maxExponent = Math.log10(10000);
        double oneStep = maxExponent / steps;
        double exponent = mouseY * oneStep;
        double value = Math.pow(10, exponent);
        value = value / 10;
        return (float) value;
    }

}
