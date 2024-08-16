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
 * --------------------
 * XYBlockRenderer.java
 * --------------------
 * (C) Copyright 2006-2008, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 05-Jul-2006 : Version 1 (DG);
 * 02-Feb-2007 : Added getPaintScale() method (DG);
 * 09-Mar-2007 : Fixed cloning (DG);
 * 03-Aug-2007 : Fix for bug 1766646 (DG);
 * 07-Apr-2008 : Added entity collection code (DG);
 * 22-Apr-2008 : Implemented PublicCloneable (DG);
 */

package de.laures.cewolf.jfree;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import static java.lang.Math.abs;
import static java.lang.Math.abs;
import static java.lang.Math.abs;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.min;
import static java.lang.Math.min;
import static java.lang.Math.min;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import static org.jfree.chart.plot.PlotOrientation.HORIZONTAL;
import static org.jfree.chart.plot.PlotOrientation.VERTICAL;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.renderer.xy.*;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;
import org.jfree.ui.RectangleAnchor;
import static org.jfree.ui.RectangleAnchor.BOTTOM;
import static org.jfree.ui.RectangleAnchor.BOTTOM_LEFT;
import static org.jfree.ui.RectangleAnchor.BOTTOM_RIGHT;
import static org.jfree.ui.RectangleAnchor.CENTER;
import static org.jfree.ui.RectangleAnchor.LEFT;
import static org.jfree.ui.RectangleAnchor.RIGHT;
import static org.jfree.ui.RectangleAnchor.TOP;
import static org.jfree.ui.RectangleAnchor.TOP_LEFT;
import static org.jfree.ui.RectangleAnchor.TOP_RIGHT;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.PublicCloneable;

/**
 * A renderer that represents data from an {@link XYZDataset} by drawing a
 * color block at each (x, y) point, where the color is a function of the z-value from the dataset.
 *
 * @since 1.0.4
 */
public class XYBlockRenderer extends AbstractXYItemRenderer implements PublicCloneable {

	static final long serialVersionUID = 3202750109790314414L;

    /**
     * The block width (defaults to 1.0).
     */
    private double blockWidth = 1.0;

    /**
     * The block height (defaults to 1.0).
     */
    private double blockHeight = 1.0;

    /**
     * The anchor point used to align each block to its (x, y) location.  The
     * default value is <code>RectangleAnchor.CENTER</code>.
     */
    private RectangleAnchor blockAnchor = CENTER;

    /** Temporary storage for the x-offset used to align the block anchor. */
    private double xOffset;

    /** Temporary storage for the y-offset used to align the block anchor. */
    private double yOffset;

    /** The paint scale. */
    private PaintScale paintScale;

    /**
     * Creates a new <code>XYBlockRenderer</code> instance with default attributes.
     */
    public XYBlockRenderer() {
        updateOffsets();
        this.paintScale = new LookupPaintScale();
    }

    /**
     * Returns the block width, in data/axis units.
     *
     * @return The block width.
     *
     * @see #setBlockWidth(double)
     */
    public double getBlockWidth() {
        return this.blockWidth;
    }

    /**
     * Sets the width of the blocks used to represent each data item and
     * sends a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param width  the new width, in data/axis units (must be > 0.0).
     *
     * @see #getBlockWidth()
     */
    public void setBlockWidth(double width) {
        if (width <= 0.0) {
            throw new IllegalArgumentException("The 'width' argument must be > 0.0");
        }
        this.blockWidth = width;
        updateOffsets();
        fireChangeEvent();
    }

    /**
     * Returns the block height, in data/axis units.
     *
     * @return The block height.
     *
     * @see #setBlockHeight(double)
     */
    public double getBlockHeight() {
        return this.blockHeight;
    }

    /**
     * Sets the height of the blocks used to represent each data item and
     * sends a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param height  the new height, in data/axis units (must be > 0.0).
     *
     * @see #getBlockHeight()
     */
    public void setBlockHeight(double height) {
        if (height <= 0.0) {
            throw new IllegalArgumentException("The 'height' argument must be > 0.0");
        }
        this.blockHeight = height;
        updateOffsets();
        fireChangeEvent();
    }

    /**
     * Returns the anchor point used to align a block at its (x, y) location.
     * The default values is {@link RectangleAnchor#CENTER}.
     *
     * @return The anchor point (never <code>null</code>).
     *
     * @see #setBlockAnchor(RectangleAnchor)
     */
    public RectangleAnchor getBlockAnchor() {
        return this.blockAnchor;
    }

    /**
     * Sets the anchor point used to align a block at its (x, y) location and
     * sends a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param anchor  the anchor.
     *
     * @see #getBlockAnchor()
     */
    public void setBlockAnchor(RectangleAnchor anchor) {
        if (anchor == null) {
            throw new IllegalArgumentException("Null 'anchor' argument.");
        }
        if (this.blockAnchor.equals(anchor)) {
            return;  // no change
        }
        this.blockAnchor = anchor;
        updateOffsets();
        fireChangeEvent();
    }

    /**
     * Returns the paint scale used by the renderer.
     *
     * @return The paint scale (never <code>null</code>).
     *
     * @see #setPaintScale(PaintScale)
     * @since 1.0.4
     */
    public PaintScale getPaintScale() {
        return this.paintScale;
    }

    /**
     * Sets the paint scale used by the renderer and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param scale  the scale (<code>null</code> not permitted).
     *
     * @see #getPaintScale()
     * @since 1.0.4
     */
    public void setPaintScale(PaintScale scale) {
        if (scale == null) {
            throw new IllegalArgumentException("Null 'scale' argument.");
        }
        this.paintScale = scale;
        fireChangeEvent();
    }

    /**
     * Updates the offsets to take into account the block width, height and anchor.
     */
    private void updateOffsets() {
        if (this.blockAnchor.equals(BOTTOM_LEFT)) {
            this.xOffset = 0.0;
            this.yOffset = 0.0;
        }
        else if (this.blockAnchor.equals(BOTTOM)) {
            this.xOffset = -this.blockWidth / 2.0;
            this.yOffset = 0.0;
        }
        else if (this.blockAnchor.equals(BOTTOM_RIGHT)) {
            this.xOffset = -this.blockWidth;
            this.yOffset = 0.0;
        }
        else if (this.blockAnchor.equals(LEFT)) {
            this.xOffset = 0.0;
            this.yOffset = -this.blockHeight / 2.0;
        }
        else if (this.blockAnchor.equals(CENTER)) {
            this.xOffset = -this.blockWidth / 2.0;
            this.yOffset = -this.blockHeight / 2.0;
        }
        else if (this.blockAnchor.equals(RIGHT)) {
            this.xOffset = -this.blockWidth;
            this.yOffset = -this.blockHeight / 2.0;
        }
        else if (this.blockAnchor.equals(TOP_LEFT)) {
            this.xOffset = 0.0;
            this.yOffset = -this.blockHeight;
        }
        else if (this.blockAnchor.equals(TOP)) {
            this.xOffset = -this.blockWidth / 2.0;
            this.yOffset = -this.blockHeight;
        }
        else if (this.blockAnchor.equals(TOP_RIGHT)) {
            this.xOffset = -this.blockWidth;
            this.yOffset = -this.blockHeight;
        }
    }

    /**
     * Returns the lower and upper bounds (range) of the x-values in the specified dataset.
     *
     * @param dataset  the dataset (<code>null</code> permitted).
     *
     * @return The range (<code>null</code> if the dataset is <code>null</code> or empty).
     *
     * @see #findRangeBounds(XYDataset)
     */
    public Range findDomainBounds(XYDataset dataset) {
        if (dataset != null) {
            var r = DatasetUtilities.findDomainBounds(dataset, false);
            if (r == null) {
                return null;
            } else {
                return new Range(r.getLowerBound() + this.xOffset,
                        r.getUpperBound() + this.blockWidth + this.xOffset);
            }
        } else {
            return null;
        }
    }

    /**
     * Returns the range of values the renderer requires to display all the
     * items from the specified dataset.
     *
     * @param dataset  the dataset (<code>null</code> permitted).
     *
     * @return The range (<code>null</code> if the dataset is <code>null</code> or empty).
     *
     * @see #findDomainBounds(XYDataset)
     */
    public Range findRangeBounds(XYDataset dataset) {
        if (dataset != null) {
            var r = DatasetUtilities.findRangeBounds(dataset, false);
            if (r == null) {
                return null;
            } else {
                return new Range(r.getLowerBound() + this.yOffset,
                        r.getUpperBound() + this.blockHeight + this.yOffset);
            }
        } else {
            return null;
        }
    }

    /**
     * Draws the block representing the specified item.
     *
     * @param g2  the graphics device.
     * @param state  the state.
     * @param dataArea  the data area.
     * @param info  the plot rendering info.
     * @param plot  the plot.
     * @param domainAxis  the x-axis.
     * @param rangeAxis  the y-axis.
     * @param dataset  the dataset.
     * @param series  the series index.
     * @param item  the item index.
     * @param crosshairState  the crosshair state.
     * @param pass  the pass index.
     */
    public void drawItem (Graphics2D g2, XYItemRendererState state,
            Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot,
            ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset,
            int series, int item, CrosshairState crosshairState, int pass) {

        var x = dataset.getXValue(series, item);
        var y = dataset.getYValue(series, item);
        var z = 0.0;
        if (dataset instanceof XYZDataset) {
            z = ((XYZDataset) dataset).getZValue(series, item);
        }
        var p = this.paintScale.getPaint(z);
        var xx0 = domainAxis.valueToJava2D(x + this.xOffset, dataArea, plot.getDomainAxisEdge());
        var yy0 = rangeAxis.valueToJava2D(y + this.yOffset, dataArea, plot.getRangeAxisEdge());
        var xx1 = domainAxis.valueToJava2D(x + this.blockWidth + this.xOffset, dataArea, plot.getDomainAxisEdge());
        var yy1 = rangeAxis.valueToJava2D(y + this.blockHeight + this.yOffset, dataArea, plot.getRangeAxisEdge());
        Rectangle2D block;
        var orientation = plot.getOrientation();
        if (orientation.equals(HORIZONTAL)) {
            block = new Rectangle2D.Double(min(yy0, yy1),
                    min(xx0, xx1), abs(yy1 - yy0), abs(xx0 - xx1));
        } else {
            block = new Rectangle2D.Double(min(xx0, xx1),
                    min(yy0, yy1), abs(xx1 - xx0), abs(yy1 - yy0));
        }
        g2.setPaint(p);
        g2.fill(block);
        g2.setStroke(new BasicStroke(1.0f));
        g2.draw(block);

// the following 12 lines of code are all that distinguish this class from the original in JFreeChart
		var domainAxisLocation = plot.getDomainAxisEdge();
		var rangeAxisLocation = plot.getRangeAxisEdge();
		var transX = domainAxis.valueToJava2D(x, dataArea, domainAxisLocation);
		var transY = rangeAxis.valueToJava2D(y, dataArea, rangeAxisLocation);

		if (isItemLabelVisible(series, item)) {
			if (orientation == VERTICAL) {
				drawItemLabel(g2, orientation, dataset, series, item, transX, transY, false);
			} else if (orientation == HORIZONTAL) {
				drawItemLabel(g2, orientation, dataset, series, item, transY, transX, false);
			}
		}

		var entities = state.getEntityCollection();
        if (entities != null) {
            addEntity(entities, block, dataset, series, item, 0.0, 0.0);
        }
    }

    /**
     * Returns a clone of this renderer.
     *
     * @return A clone of this renderer.
     *
     * @throws CloneNotSupportedException if there is a problem creating the clone.
     */
    public Object clone() throws CloneNotSupportedException {
        var clone = (XYBlockRenderer) super.clone();
        if (this.paintScale instanceof PublicCloneable) {
            var pc = (PublicCloneable) this.paintScale;
            clone.paintScale = (PaintScale) pc.clone();
        }
        return clone;
    }
}
