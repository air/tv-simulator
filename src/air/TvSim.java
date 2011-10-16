package air;

import java.awt.Dimension;

import processing.core.PImage;

/**
 * TODO zoom!
 */
@SuppressWarnings("serial")
public class TvSim extends Sketch {
    PImage image;

    Dimension world; // set by image size
    Dimension cameraSize = new Dimension(200, 200);
    Camera camera;

    float brightness;
    final int MAX_BRIGHTNESS = 2; // used to scale the brightness by mouse position
    int BRIGHTNESS_COEFF; // how much to scale the brightness based on sketch width

    boolean interference = false;
    // TODO there is a noiseDetail() method which we are probably simulating
    float perlin_coeff;
    int perlin_pos = 0;
    float perlin_value;
    float cell_perturb;
    int timesSameNoiseValueReturned = 0;
    final int RESET_PERLIN_AFTER_SAME_HITS = 100;
    final float MAX_PERLIN_COEFF = 3000f;
    final float MIN_PERLIN_COEFF = 0.001f;

    final int CELL_WIDTH = 5;
    final int CELL_HEIGHT = 4;
    final int PHOSPHOR_WIDTH = 1; // phosphor width * 3 must fit in cell width!
    final int PHOSPHOR_HEIGHT = 3;

    boolean redOn = true;
    boolean greenOn = true;
    boolean blueOn = true;
    boolean mouseMoved = false;

    @Override
    public void setup() {
        image = loadImage("rgbtest150.jpg");

        world = new Dimension(image.width * CELL_WIDTH, image.height * CELL_HEIGHT);
        size(cameraSize.width, cameraSize.height, P2D);
        camera = new Camera(world.width, world.height, cameraSize.width, cameraSize.height);

        noStroke();

        // for the width of the sketch to scale to the max brightness, we need to take the number of pixels
        // in the image and expand it out by the cell width
        BRIGHTNESS_COEFF = (image.width * CELL_WIDTH) / MAX_BRIGHTNESS;
        // set the initial brightness as if the mouse were at the right edge
        brightness = width / BRIGHTNESS_COEFF;

        background(0);
        drawRgb(CELL_WIDTH, CELL_HEIGHT);
    }

    @Override
    protected void showDebug() {

        super.showDebug();
        // log("perlin_pos " + perlin_pos);
        // log("perlin_value " + perlin_value);
        // log("cell_perturb " + cell_perturb);
        // log("perlin_coeff %.5f", perlin_coeff);
        // log("mouseY " + mouseY);
        // log("height " + height);
        // log("brightness " + brightness);
    }

    /**
     * We take the position in the perlin landscape - a simple incrementing int - and divide by this to pass into noise()
     * 
     * TODO: make a generic scaleWithMouse(xOrY, LOGARITHMIC, min, max)
     */
    protected float getPerlinCoeff() {
        int steps = height;
        int scale = 1000; // we want a minimum of 0.001 not 1, so scale by 1000
        double maxExponent = Math.log10(2000 * scale);
        double oneStep = maxExponent / steps;
        double exponent = (height - 1 - mouseY) * oneStep; // mouseY only reaches height-1
        double value = Math.pow(10, exponent);
        value = value / scale;
        return (float) value;
    }

    protected float getBrightnessCoeff() {
        return (float) mouseX / BRIGHTNESS_COEFF;
    }

    @Override
    public void draw() {
        showDebugAtIntervals();

        if (interference || mouseMoved) {
            brightness = getBrightnessCoeff();
            // background(0); // remove this if sizes are stable
            drawRgb(CELL_WIDTH, CELL_HEIGHT);
            mouseMoved = false;
        }
    }

    void drawRgb(int cellWidth, int cellHeight) {
        double cell_brightness;
        int loc;
        float r, g, b;
        int xp, yp;

        for (int y = 0; y < image.height; y++) {
            for (int x = 0; x < image.width; x++) {
                cell_brightness = brightness;
                if (interference) {
                    // move in perlin landscape
                    perlin_pos++;
                    perlin_coeff = getPerlinCoeff();
                    perlin_value = perlin_pos / perlin_coeff;

                    // normalize to 0.5-1.5 range
                    float new_cell_perturb = noise(perlin_value) + 0.5f;

                    // fix for noise() getting stuck
                    if (new_cell_perturb == cell_perturb && timesSameNoiseValueReturned > RESET_PERLIN_AFTER_SAME_HITS) {
                        perlin_pos = 0;
                        timesSameNoiseValueReturned = 0;
                    }
                    else if (new_cell_perturb == cell_perturb) {
                        timesSameNoiseValueReturned++;
                    }
                    else cell_perturb = new_cell_perturb;

                    // adjust this cell's brightness
                    cell_brightness = brightness * cell_perturb;
                }

                // get the image data in RGB
                loc = x + y * image.width;
                r = red(image.pixels[loc]);
                g = green(image.pixels[loc]);
                b = blue(image.pixels[loc]);

                // top left of our larger RGB cell
                xp = x * cellWidth;
                yp = y * cellHeight;
                if (redOn) {
                    fill((float) (r * cell_brightness), 0, 0);
                    rect(xp, yp, PHOSPHOR_WIDTH, PHOSPHOR_HEIGHT);
                }
                if (greenOn) {
                    fill(0, (float) (g * cell_brightness), 0);
                    rect(xp + PHOSPHOR_WIDTH, yp, PHOSPHOR_WIDTH, PHOSPHOR_HEIGHT);
                }
                if (blueOn) {
                    fill(0, 0, (float) (b * cell_brightness));
                    rect(xp + (2 * PHOSPHOR_WIDTH), yp, PHOSPHOR_WIDTH, PHOSPHOR_HEIGHT);
                }
            }
        }
    }

    public void keyReleased() {
        if (key == 'r')
            redOn = !redOn;
        else if (key == 'g')
            greenOn = !greenOn;
        else if (key == 'b')
            blueOn = !blueOn;
        else if (key == 'i')
            interference = !interference;
        else if (key == '1')
            changeChannel("rgbtest150.jpg");
        else if (key == '2')
            changeChannel("rgbgamut150.jpg");
        else if (key == '3')
            changeChannel("mondrian150.jpg");
        else if (key == '4')
            changeChannel("field150.jpg");
        background(0);
        drawRgb(CELL_WIDTH, CELL_HEIGHT);
    }

    public void mouseMoved() {
        mouseMoved = true;
    }

    void changeChannel(String file) {
        image = loadImage(file);
    }

}
