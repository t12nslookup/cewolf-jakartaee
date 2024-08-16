/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2011, by Object Refinery Limited and Contributors.
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
 * [Oracle and Java are registered trademarks of Oracle and/or its affiliates. 
 * Other names may be trademarks of their respective owners.]
 *
 * --------------------------
 * DefaultHeatMapDataset.java
 * --------------------------
 * (C) Copyright 2009, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes:
 * --------
 * 28-Jan-2009 : Version 1 (DG);
 */

package de.laures.cewolf.jfree;

import java.io.Serializable;
import static java.lang.Double.isInfinite;
import static java.lang.Double.isNaN;
import static java.lang.Double.valueOf;
import static java.lang.System.arraycopy;
import java.util.Arrays;

import org.jfree.data.general.AbstractDataset;
import org.jfree.util.PublicCloneable;

/**
 * A default implementation of the {@link HeatMapDataset} interface.
 *
 * @since 1.0.13
 */
public class DefaultHeatMapDataset extends AbstractDataset implements HeatMapDataset, PublicCloneable {

	static final long serialVersionUID = -8354339976241421170L;

    /** The number of samples in this dataset for the x-dimension. */
    private int xSamples;

    /** The number of samples in this dataset for the y-dimension. */
    private int ySamples;

    /** The minimum x-value in the dataset. */
    private double minX;

    /** The maximum x-value in the dataset. */
    private double maxX;

    /** The minimum y-value in the dataset. */
    private double minY;

    /** The maximum y-value in the dataset. */
    private double maxY;

    /** Storage for the z-values. */
    private double[][] zValues;

    /**
     * Creates a new dataset where all the z-values are initially 0.  This is a fixed size array of z-values.
     *
     * @param xSamples  the number of x-values.
     * @param ySamples  the number of y-values
     * @param minX  the minimum x-value in the dataset.
     * @param maxX  the maximum x-value in the dataset.
     * @param minY  the minimum y-value in the dataset.
     * @param maxY  the maximum y-value in the dataset.
     */
    public DefaultHeatMapDataset(int xSamples, int ySamples, double minX, double maxX, double minY, double maxY) {
        if (xSamples < 1) {
            throw new IllegalArgumentException("Requires 'xSamples' > 0");
        }
        if (ySamples < 1) {
            throw new IllegalArgumentException("Requires 'ySamples' > 0");
        }
        if (isInfinite(minX) || isNaN(minX)) {
            throw new IllegalArgumentException("'minX' cannot be INF or NaN.");
        }
        if (isInfinite(maxX) || isNaN(maxX)) {
            throw new IllegalArgumentException("'maxX' cannot be INF or NaN.");
        }
        if (isInfinite(minY) || isNaN(minY)) {
            throw new IllegalArgumentException("'minY' cannot be INF or NaN.");
        }
        if (isInfinite(maxY) || isNaN(maxY)) {
            throw new IllegalArgumentException("'maxY' cannot be INF or NaN.");
        }

        this.xSamples = xSamples;
        this.ySamples = ySamples;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.zValues = new double[xSamples][];
        for (var x = 0; x < xSamples; x++) {
            this.zValues[x] = new double[ySamples];
        }
    }

    /**
     * Returns the number of x values across the width of the dataset.  The
     * values are evenly spaced between {@link #getMinimumXValue()} and {@link #getMaximumXValue()}.
     *
     * @return The number of x-values (always > 0).
     */
    public int getXSampleCount() {
        return this.xSamples;
    }

    /**
     * Returns the number of y values (or samples) for the dataset.  The
     * values are evenly spaced between {@link #getMinimumYValue()} and {@link #getMaximumYValue()}.
     *
     * @return The number of y-values (always > 0).
     */
    public int getYSampleCount() {
        return this.ySamples;
    }

    /**
     * Returns the lowest x-value represented in this dataset.  A requirement
     * of this interface is that this method must never return infinite or Double.NAN values.
     *
     * @return The lowest x-value represented in this dataset.
     */
    public double getMinimumXValue() {
        return this.minX;
    }

    /**
     * Returns the highest x-value represented in this dataset.  A requirement
     * of this interface is that this method must never return infinite or Double.NAN values.
     *
     * @return The highest x-value represented in this dataset.
     */
    public double getMaximumXValue() {
        return this.maxX;
    }

    /**
     * Returns the lowest y-value represented in this dataset.  A requirement
     * of this interface is that this method must never return infinite or Double.NAN values.
     *
     * @return The lowest y-value represented in this dataset.
     */
    public double getMinimumYValue() {
        return this.minY;
    }

    /**
     * Returns the highest y-value represented in this dataset.  A requirement
     * of this interface is that this method must never return infinite or Double.NAN values.
     *
     * @return The highest y-value represented in this dataset.
     */
    public double getMaximumYValue() {
        return this.maxY;
    }

    /**
     * A convenience method that returns the x-value for the given index.
     *
     * @param xIndex  the xIndex.
     *
     * @return The x-value.
     */
    public double getXValue(int xIndex) {
        var x = this.minX + (this.maxX - this.minX) * (xIndex / (double) this.xSamples);
        return x;
    }

    /**
     * A convenience method that returns the y-value for the given index.
     *
     * @param yIndex  the yIndex.
     *
     * @return The y-value.
     */
    public double getYValue(int yIndex) {
        var y = this.minY + (this.maxY - this.minY) * (yIndex / (double) this.ySamples);
        return y;
    }

    /**
     * Returns the z-value at the specified sample position in the dataset.
     * For a missing or unknown value, this method should return Double.NAN.
     *
     * @param xIndex  the position of the x sample in the dataset.
     * @param yIndex  the position of the y sample in the dataset.
     *
     * @return The z-value.
     */
    public double getZValue(int xIndex, int yIndex) {
        return this.zValues[xIndex][yIndex];
    }

    /**
     * Returns the z-value at the specified sample position in the dataset.
     * In this implementation, where the underlying values are stored in an
     * array of double primitives, you should avoid using this method and
     * use {@link #getZValue(int, int)} instead.
     *
     * @param xIndex  the position of the x sample in the dataset.
     * @param yIndex  the position of the y sample in the dataset.
     *
     * @return The z-value.
     */
    public Number getZ(int xIndex, int yIndex) {
        return getZValue(xIndex, yIndex);
    }

    /**
     * Updates a z-value in the dataset and sends a DatasetChangeEvent to all registered listeners.
     *
     * @param xIndex  the x-index.
     * @param yIndex  the y-index.
     * @param z  the new z-value.
     */
    public void setZValue(int xIndex, int yIndex, double z) {
        setZValue(xIndex, yIndex, z, true);
    }

    /**
     * Updates a z-value in the dataset and, if requested, sends a
     * DatasetChangeEvent to all registered listeners.
     *
     * @param xIndex  the x-index.
     * @param yIndex  the y-index.
     * @param z  the new z-value.
     * @param notify  notify listeners?
     */
    public void setZValue(int xIndex, int yIndex, double z, boolean notify) {
        this.zValues[xIndex][yIndex] = z;
        if (notify) {
            fireDatasetChanged();
        }
    }

    /**
     * Returns an independent copy of this dataset.
     *
     * @return A clone.
     *
     * @throws java.lang.CloneNotSupportedException
     */
    public Object clone() throws CloneNotSupportedException {
        var clone = (DefaultHeatMapDataset) super.clone();
        clone.zValues = clone(this.zValues);
        return clone;
    }

    /**
     * Tests two arrays for equality.  To be considered equal, the arrays must
     * have exactly the same dimensions, and the values in each array must also
     * match (two values that qre both NaN or both INF are considered equal in this test).
     *
     * @param a  the first array (<code>null</code> permitted).
     * @param b  the second array (<code>null</code> permitted).
     *
     * @return A boolean.
     *
     * @since 1.0.13
     */
    public static boolean equal(double[][] a, double[][] b) {
        if (a == null) {
            return (b == null);
        }
        if (b == null) {
            return false;  // already know 'a' isn't null
        }
        if (a.length != b.length) {
            return false;
        }
        for (var i = 0; i < a.length; i++) {
            if (!Arrays.equals(a[i], b[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a clone of the specified array.
     *
     * @param source  the source array (<code>null</code> not permitted).
     *
     * @return A clone of the array.
     *
     * @since 1.0.13
     */
    public static double[][] clone(double[][] source) {
        if (source == null) {
            throw new IllegalArgumentException("Null 'source' argument.");
        }
        var clone = new double[source.length][];
        for (var i = 0; i < source.length; i++) {
            if (source[i] != null) {
                var row = new double[source[i].length];
                arraycopy(source[i], 0, row, 0, source[i].length);
                clone[i] = row;
            }
        }
        return clone;
    }

}
