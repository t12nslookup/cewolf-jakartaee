/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2008, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * -----------------
 * WaferMapPlot.java
 * -----------------
 *
 * (C) Copyright 2003-2008, by Robert Redburn and Contributors.
 *
 * Original Author:  Robert Redburn;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * Changes
 * -------
 * 25-Nov-2003 : Version 1 contributed by Robert Redburn (DG);
 * 05-May-2005 : Updated draw() method parameters (DG);
 * 10-Jun-2005 : Changed private --> protected for drawChipGrid(), drawWaferEdge() and getWafterEdge() (DG);
 * 16-Jun-2005 : Added default constructor and setDataset() method (DG);
 */

package de.laures.cewolf.jfree;

import java.awt.*;
import static java.awt.Color.black;
import static java.awt.Color.lightGray;
import static java.awt.Color.white;
import static java.awt.Font.PLAIN;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.*;
import static java.awt.geom.Arc2D.OPEN;
import java.io.Serializable;
import static java.lang.Math.floor;
import static java.lang.String.valueOf;
import java.util.ResourceBundle;

import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.event.RendererChangeListener;
import org.jfree.chart.plot.*;
import static org.jfree.chart.plot.PlotOrientation.HORIZONTAL;
import static org.jfree.chart.plot.PlotOrientation.VERTICAL;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.WaferMapDataset;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ObjectUtilities;
import static org.jfree.util.ObjectUtilities.equal;

/**
 * A wafer map plot.
 */
public class WaferMapPlot extends Plot implements RendererChangeListener {

    /** For serialization. */
    private static final long serialVersionUID = 4668320403707308155L;

    /** The plot orientation.
     *  vertical = notch down
     *  horizontal = notch right
     */
    private PlotOrientation orientation;

    /** The dataset. */
    private WaferMapDataset dataset;

	/** whether or not to show the cell values */
	private boolean showCellValues = false;

    /**
     * Object responsible for drawing the visual representation of each point on the plot.
     */
    private WaferMapRenderer renderer;

    /**
     * Creates a new plot with no dataset.
     */
    public WaferMapPlot() {
        this(null);
    }

    /**
     * Creates a new plot.
     *
     * @param dataset  the dataset (<code>null</code> permitted).
     */
    public WaferMapPlot(WaferMapDataset dataset) {
        this(dataset, null);
    }

    /**
     * Creates a new plot.
     *
     * @param dataset  the dataset (<code>null</code> permitted).
     * @param renderer  the renderer (<code>null</code> permitted).
     */
    public WaferMapPlot(WaferMapDataset dataset, WaferMapRenderer renderer) {
        super();

        this.orientation = VERTICAL;

        this.dataset = dataset;
        if (dataset != null) {
            dataset.addChangeListener(this);
        }

        this.renderer = renderer;
        if (renderer != null) {
            renderer.setPlot(this);
            renderer.addChangeListener(this);
        }
    }

	public void setShowCellValues (boolean showCellValues) {
		this.showCellValues = showCellValues;
	}

    /**
     * Returns the plot type as a string.
     *
     * @return A short string describing the type of plot.
     */
    public String getPlotType() {
        return ("WMAP_Plot");
    }

    /**
     * Returns the dataset
     *
     * @return The dataset (possibly <code>null</code>).
     */
    public WaferMapDataset getDataset() {
        return this.dataset;
    }

    /**
     * Sets the dataset used by the plot and sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param dataset  the dataset (<code>null</code> permitted).
     */
    public void setDataset(WaferMapDataset dataset) {
        // if there is an existing dataset, remove the plot from the list of change listeners...
        if (this.dataset != null) {
            this.dataset.removeChangeListener(this);
        }

        // set the new dataset, and register the chart as a change listener...
        this.dataset = dataset;
        if (dataset != null) {
            setDatasetGroup(dataset.getGroup());
            dataset.addChangeListener(this);
        }

        // send a dataset change event to self to trigger plot change event
        datasetChanged(new DatasetChangeEvent(this, dataset));
    }

    /**
     * Returns the renderer for the primary dataset.
     *
     * @return The renderer (possibly <code>null</code>).
     *
     * @see #setRenderer(WaferMapRenderer)
     */
    public WaferMapRenderer getRenderer() {
        return this.renderer;
    }

    /**
     * Sets the item renderer, and notifies all listeners of a change to the plot.
	 * If the renderer is set to <code>null</code>, no chart will be drawn.
     *
     * @param renderer  the new renderer (<code>null</code> permitted).
     */
    public void setRenderer(WaferMapRenderer renderer) {
        if (this.renderer != null) {
            this.renderer.removeChangeListener(this);
        }
        this.renderer = renderer;
        if (renderer != null) {
            renderer.setPlot(this);
        }
        fireChangeEvent();
    }

    /**
     * Draws the wafermap view.
     *
     * @param g2  the graphics device.
     * @param area  the plot area.
     * @param anchor  the anchor point (<code>null</code> permitted).
     * @param state  the plot state.
     * @param info  the plot rendering info.
     */
    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState state, PlotRenderingInfo info) {

        // if the plot area is too small, just return...
        var b1 = (area.getWidth() <= MINIMUM_WIDTH_TO_DRAW);
        var b2 = (area.getHeight() <= MINIMUM_HEIGHT_TO_DRAW);
        if (b1 || b2) {
            return;
        }

        // record the plot area...
        if (info != null) {
            info.setPlotArea(area);
        }

        // adjust the drawing area for the plot insets (if any)...
        var insets = getInsets();
        insets.trim(area);

        drawChipGrid(g2, area);
        drawWaferEdge(g2, area);
    }

    /**
     * Calculates and draws the chip locations on the wafer.
     *
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     */
    protected void drawChipGrid(Graphics2D g2, Rectangle2D plotArea) {

        var savedClip = g2.getClip();
        g2.setClip(getWaferEdge(plotArea));
        Rectangle2D chip = new Rectangle2D.Double();
        var xchips = 35;
        var ychips = 20;
        var space = 1d;
        if (this.dataset != null) {
            xchips = this.dataset.getMaxChipX() + 2;
            ychips = this.dataset.getMaxChipY() + 2;
            space = this.dataset.getChipSpace();
        }
        var startX = plotArea.getX();
        var startY = plotArea.getY();
        var chipWidth = 1d;
        var chipHeight = 1d;
        if (plotArea.getWidth() != plotArea.getHeight()) {
            var major = 0d;
            var minor = 0d;
            if (plotArea.getWidth() > plotArea.getHeight()) {
                major = plotArea.getWidth();
                minor = plotArea.getHeight();
            } else {
                major = plotArea.getHeight();
                minor = plotArea.getWidth();
            }
            //set upperLeft point
            if (plotArea.getWidth() == minor) { // x is minor
                startY += (major - minor) / 2;
                chipWidth = (plotArea.getWidth() - (space * xchips - 1)) / xchips;
                chipHeight = (plotArea.getWidth() - (space * ychips - 1)) / ychips;
            } else { // y is minor
                startX += (major - minor) / 2;
                chipWidth = (plotArea.getHeight() - (space * xchips - 1)) / xchips;
                chipHeight = (plotArea.getHeight() - (space * ychips - 1)) / ychips;
            }
        }

        for (var x = 1; x <= xchips; x++) {
            var upperLeftX = (startX - chipWidth) + (chipWidth * x) + (space * (x - 1));
            for (var y = 1; y <= ychips; y++) {
                var upperLeftY = (startY - chipHeight) + (chipHeight * y) + (space * (y - 1));
                chip.setFrame(upperLeftX, upperLeftY, chipWidth, chipHeight);
                g2.setColor(white);
                if (this.dataset.getChipValue(x - 1, ychips - y - 1) != null) {
                    g2.setPaint( this.renderer.getChipColor( this.dataset.getChipValue(x - 1, ychips - y - 1)));
                }
                g2.fill(chip);
                g2.setColor(lightGray);
                g2.draw(chip);
				if (showCellValues) {
					var chipValueString = " ";
					var chipValue = this.dataset.getChipValue(x - 1, ychips - y - 1);
					if (chipValue != null) {
						g2.setPaint(this.renderer.getChipColor(chipValue));
						chipValueString = valueOf(chipValue.intValue());
						g2.fill(chip);
						g2.setColor(lightGray);
						g2.draw(chip);
						g2.setColor(black);
						var bounds = chip.getBounds2D();
						var fontSize = (int) (floor(bounds.getHeight() / 2));
						var font = new Font("SansSerif", PLAIN, fontSize);
						var frc = g2.getFontRenderContext();
						var layout = new TextLayout(chipValueString, font, frc);
						var xPos = (float) (bounds.getMinX() + ((bounds.getMaxX() - bounds.getMinX()) / 10.0f));
						var yPos = (float) (bounds.getMinY() + ((bounds.getMaxY() - bounds.getMinY()) * 2.0f / 3.0f));
						layout.draw(g2, xPos, yPos);
					}
				}
            }
        }
        g2.setClip(savedClip);
    }

    /**
     * Calculates the location of the waferedge.
     *
     * @param plotArea  the plot area.
     *
     * @return The wafer edge.
     */
    protected Ellipse2D getWaferEdge(Rectangle2D plotArea) {
        Ellipse2D edge = new Ellipse2D.Double();
        var diameter = plotArea.getWidth();
        var upperLeftX = plotArea.getX();
        var upperLeftY = plotArea.getY();
        //get major dimension
        if (plotArea.getWidth() != plotArea.getHeight()) {
            var major = 0d;
            var minor = 0d;
            if (plotArea.getWidth() > plotArea.getHeight()) {
                major = plotArea.getWidth();
                minor = plotArea.getHeight();
            } else {
                major = plotArea.getHeight();
                minor = plotArea.getWidth();
            }
            //ellipse diameter is the minor dimension
            diameter = minor;
            //set upperLeft point
            if (plotArea.getWidth() == minor) { // x is minor
                upperLeftY = plotArea.getY() + (major - minor) / 2;
            } else { // y is minor
                upperLeftX = plotArea.getX() + (major - minor) / 2;
            }
        }
        edge.setFrame(upperLeftX, upperLeftY, diameter, diameter);
        return edge;
    }

    /**
     * Draws the waferedge, including the notch.
     *
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     */
    protected void drawWaferEdge(Graphics2D g2, Rectangle2D plotArea) {
        // draw the wafer
        var waferEdge = getWaferEdge(plotArea);
        g2.setColor(black);
        g2.draw(waferEdge);
        // calculate and draw the notch
        // horizontal orientation is considered notch right
        // vertical orientation is considered notch down
        Arc2D notch = null;
        var waferFrame = waferEdge.getFrame();
        var notchDiameter = waferFrame.getWidth() * 0.04;
        if (this.orientation == HORIZONTAL) {
            Rectangle2D notchFrame = new Rectangle2D.Double(
                    waferFrame.getX() + waferFrame.getWidth()
                    - (notchDiameter / 2), waferFrame.getY()
                    + (waferFrame.getHeight() / 2) - (notchDiameter / 2),
                    notchDiameter, notchDiameter
                );
            notch = new Arc2D.Double(notchFrame, 90d, 180d, OPEN);
        } else {
            Rectangle2D notchFrame = new Rectangle2D.Double(
                    waferFrame.getX() + (waferFrame.getWidth() / 2)
                    - (notchDiameter / 2), waferFrame.getY()
                    + waferFrame.getHeight() - (notchDiameter / 2),
                    notchDiameter, notchDiameter
                );
            notch = new Arc2D.Double(notchFrame, 0d, 180d, OPEN);
        }
        g2.setColor(white);
        g2.fill(notch);
        g2.setColor(black);
        g2.draw(notch);
    }

    /**
     * Return the legend items from the renderer.
     *
     * @return The legend items.
     */
    public LegendItemCollection getLegendItems() {
        return this.renderer.getLegendCollection();
    }

    /**
	 * Implements the RendererChangeListener interface
	 *
     * Notifies all registered listeners of a renderer change.
     *
     * @param event  the event.
     */
    public void rendererChanged(RendererChangeEvent event) {
        fireChangeEvent();
    }

    /**
     * Tests this plot for equality with another object. The plot's dataset is not considered in the test.
     *
     * @param obj  the object (<code>null</code> permitted).
     *
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof WaferMapPlot)) {
            return false;
        }
        var that = (WaferMapPlot) obj;
        if (!super.equals(obj)) {
            return false;
        }
        if (!equal(this.orientation, that.orientation)) {
            return false;
        }
        if (this.showCellValues != that.showCellValues) {
            return false;
        }
        if (this.renderer != that.renderer) {
            return false;
        }
        return true;
    }

	public int hashCode() {
		assert false : "hashCode not designed";
		return 42; // any arbitrary constant will do 
	}
}
