// SimDisplay.java

package air.sim.gfx;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Properties;

/**
 * A renderer for Simulation. Relies on {@link ShapeHolder} for shape data.
 * 
 * @author Aaron Bell
 */

public class SimDisplay extends AnimationPanel implements SimUpdateListener {
    /** enables debugging mode. */
    public static boolean DEBUG = false;
    /** enables graphics-debugging mode. */
    public static boolean DEBUG_GFX = false;

    /** the properties file. */
    private String propFile = "SimDisplay.properties";
    /** the properties table. */
    private Properties props;

    /** the pen used for all rendering. */
    protected BasicStroke penStyle = new BasicStroke(1.0f);
    /** colour to clear the component with. */
    protected Color bgroundColor = Color.black;

    /** the square-side spacing of the background grid. */
    protected int GRID_SIZE = 100;

    /** if true, render the grid. */
    protected boolean renderGrid = true;
    /* grid colour. */
    protected Color gridColor = Color.green;

    private int gridx, gridy; // utility variables for drawGrid()

    /* Size of the sim area. */
    protected Dimension simArea;

    /**
     * Topleft coords of the viewport. The user's window on the worldspace begins at this point.
     */
    protected Point viewport;

    /** viewport limits. */
    protected int maxViewportX, maxViewportY;

    private Iterator objList; // for traversing the objects
    private boolean drew = false; // did the last obj actually render?
    private AffineTransform transform; // utility in renderObject

    /** the viewport tracks this object, it non-null. */
    protected SimObject trackObject;

    private RenderEntry[] renderList; // things to paint

    // Constructor
    // ---------------------------------------------------------------------------

    /**
     * @param worldSize
     *            the simulation area size.
     */

    public SimDisplay(Dimension worldSize) {
        // initially use buffer chosen by the system, usually best
        super(AnimationPanel.DEFAULT_BUFFER);

        setBackground(bgroundColor);

        simArea = new Dimension(worldSize);
        viewport = new Point(0, 0);
        transform = new AffineTransform();

        loadProperties();
    }

    // SimUpdateListener method
    // ---------------------------------------------------------------------------

    public void simUpdated(SimUpdateEvent sue) {
        renderList = sue.getRenderList();
    }

    // Viewport methods
    // ---------------------------------------------------------------------------

    /**
     * @param simObj
     *            the viewport will track this object.
     */

    public void trackObject(SimObject simObj) {
        trackObject = simObj;
    }

    /**
     * @param view
     *            move the viewport's top-left corner to this location.
     */

    protected void setViewPort(Point view) {
        viewport.setLocation(view);
        validateViewport();
    }

    /**
     * @param centre
     *            centre the viewport on this point (as close as possible).
     */

    protected void centreViewportOn(Point2D.Float centre) {
        viewport.x = (int) centre.x - (size.width >> 1);
        viewport.y = (int) centre.y - (size.height >> 1);

        validateViewport();
    }

    /** if the viewport is not contained by the sim area, correct it. */

    protected void validateViewport() {
        if (viewport.x < 0)
            viewport.x = 0;
        else if (viewport.x > maxViewportX)
            viewport.x = maxViewportX;
        if (viewport.y < 0)
            viewport.y = 0;
        else if (viewport.y > maxViewportY)
            viewport.y = maxViewportY;
    }

    // AnimationPanel's abstract methods: reset and renderFrame
    // ---------------------------------------------------------------------------

    public void reset() {
        if (DEBUG)
            System.out.println("SimDisplay: reset, display " + size.width + " x " + size.height);

        // check for panel size greater than sim area

        if (size.width > simArea.width && size.height > simArea.height) {
            setSize(new Dimension(simArea.width, simArea.height));
        }
        else if (size.width > simArea.width) {
            setSize(new Dimension(simArea.width, size.height));
        }
        else if (size.height > simArea.height) {
            setSize(new Dimension(size.width, simArea.height));
        }

        // update our viewport limits

        maxViewportX = simArea.width - size.width;
        maxViewportY = simArea.height - size.height;
        validateViewport();
    }

    public void renderFrame(Graphics2D gfx) {
        // **SimDisplay'll hafta be informed if trackObject is killed
        if (trackObject != null)
            centreViewportOn(trackObject.location);

        if (renderGrid)
            drawGrid(gfx);

        if (renderList == null)
            System.out.println("SimDisplay: null renderList");
        else {
            for (int i = 0; i < renderList.length; i++) {
                drew = renderObject(renderList[i], gfx);

                // ** need some slightly more informative output here
                if (DEBUG && !drew)
                    System.out.println("SimDisplay: object " + i + " declined render");
            }
        }
    }

    // Protected methods
    // ---------------------------------------------------------------------------

    /**
     * Renders a generic object to the display, if it's in the viewport.
     * 
     * @param obj
     *            the RenderEntry for displaying.
     * @param gfx
     *            the graphics object for the drawing surface.
     * @return true if this object was actually drawn.
     */

    protected boolean renderObject(RenderEntry obj, Graphics2D gfx) {
        transform.setToIdentity();
        transform.translate((obj.location.x - viewport.x), (obj.location.y - viewport.y));

        if (obj.bearing != 0)
            transform.rotate(Lookup.deg2rad[obj.bearing]);

        Shape aShape = null;

        // ** HACK until primitives are supported in RenderEntry
        // ** hack uses bearing for circle's radius

        if (obj.className.equals("Circle")) {
            aShape = new Ellipse2D.Float(-obj.bearing, -obj.bearing, obj.bearing << 1, obj.bearing << 1);
        }
        else {
            aShape = ShapeHolder.getShape(obj.className);
        }

        aShape = transform.createTransformedShape(aShape);

        // ** could be improved to Intersect2D.rect(loc +- radius, size)

        if (aShape.getBounds().intersects(0, 0, size.width, size.height)) {
            gfx.setColor(obj.color);
            gfx.draw(aShape);
            if (DEBUG_GFX) {
                gfx.setColor(Color.red);
                gfx.draw(aShape.getBounds());
            }
            return true;
        }
        else return false;
    }

    /** Renders a simple background pattern in the current viewport. */

    protected void drawGrid(Graphics2D gfx) {
        gfx.setColor(gridColor);

        for (gridy = (GRID_SIZE - (viewport.y % GRID_SIZE)); gridy < size.height; gridy += GRID_SIZE) {
            // for this row, traverse x axis

            for (gridx = (GRID_SIZE - (viewport.x % GRID_SIZE)); gridx < size.width; gridx += GRID_SIZE) {
                gfx.drawLine(gridx, gridy, gridx, gridy); // single pixel dot
            }
        }
    }

    /** Get display options from a file, if it exists. */

    private void loadProperties() {
        if (new File(propFile).exists()) {
            try {
                props = new Properties();
                props.load(new FileInputStream(propFile));

                String str = (String) props.get("drawGrid");
                if (str != null)
                    renderGrid = new Boolean(str).booleanValue();

                str = (String) props.get("gridSize");
                if (str != null)
                    GRID_SIZE = Integer.parseInt(str);

                str = (String) props.get("antiAlias");
                if (str != null)
                    super.setAntiAlias(new Boolean(str).booleanValue());

                str = (String) props.get("composite");
                if (str != null)
                    super.setComposite(new Boolean(str).booleanValue());

                str = (String) props.get("textAlias");
                if (str != null)
                    super.setTextAlias(new Boolean(str).booleanValue());

                str = (String) props.get("showPerf");
                if (str != null)
                    super.setPerfDisplay(new Boolean(str).booleanValue());

                System.out.println("SimDisplay: properties file loaded OK.");
            }
            catch (Exception e) {
                System.err.println("SimDisplay: problem with props: " + e.getMessage());
            }
        }
    }
}
