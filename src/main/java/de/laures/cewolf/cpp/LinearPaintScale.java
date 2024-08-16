package de.laures.cewolf.cpp;

// based on the org.jfree.chart.renderer.PaintScale class

import org.jfree.chart.renderer.PaintScale;

import java.awt.Color;
import static java.awt.Color.BLACK;
import static java.awt.Color.WHITE;
import java.awt.Paint;
import java.io.Serializable;
import static java.lang.Double.doubleToLongBits;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

import org.jfree.util.PublicCloneable;

public class LinearPaintScale implements PaintScale, PublicCloneable, Serializable {

	static final long serialVersionUID = 4672130708268404476L;

    private double lowerBound = 0.0, upperBound = 1.0;;
    private Color lowerColor = BLACK, upperColor = WHITE;;

    public LinearPaintScale() {
		// do nothing - default values and colors are used
    }

    public LinearPaintScale (double lowerBound, Color lowerColor, double upperBound, Color upperColor) {
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;

		if (lowerColor != null)
			this.lowerColor = lowerColor;
		if (upperColor != null) 
			this.upperColor = upperColor;
    }

    public double getLowerBound() {
        return this.lowerBound;
    }

    public double getUpperBound() {
        return this.upperBound;
    }

    public Color getLowerColor() {
        return this.lowerColor;
    }

    public Color getUpperColor() {
        return this.upperColor;
    }

    public Paint getPaint (double value) {
        var v = max(value, lowerBound);
        v = min(v, upperBound);

        var vi = (v - lowerBound) / abs(upperBound - lowerBound);
		//System.out.println("value="+value+", v="+v+", vi="+vi);

        var r = (int) (lowerColor.getRed() + (upperColor.getRed() - lowerColor.getRed()) * vi);
        var g = (int) (lowerColor.getGreen() + (upperColor.getGreen() - lowerColor.getGreen()) * vi);
        var b = (int) (lowerColor.getBlue() + (upperColor.getBlue() - lowerColor.getBlue()) * vi);
		//System.out.println("r="+r+", g="+g+", b="+b);

        return new Color(r, g, b);
    }

    /**
     * Tests this <code>LinearPaintScale</code> instance for equality with an
     * arbitrary object.  This method returns <code>true</code> if and only if:
     * <ul>
     * <li><code>obj</code> is not <code>null</code>;</li>
     * <li><code>obj</code> is an instance of <code>LinearPaintScale</code>;</li>
     * </ul>
     *
     * @param obj  the object (<code>null</code> permitted).
     *
     * @return A boolean.
     */
	@Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LinearPaintScale)) {
            return false;
        }
        var that = (LinearPaintScale) obj;
        if (this.lowerBound != that.lowerBound) {
            return false;
        }
        if (this.upperBound != that.upperBound) {
            return false;
        }
        if (this.lowerColor != that.lowerColor) {
            return false;
        }
        if (this.upperColor != that.upperColor) {
            return false;
        }
        return true;
    }

	@Override
	public int hashCode() {
		return lowerColor.hashCode() + 37 * (upperColor.hashCode()
			+ 37 * ((int) doubleToLongBits(lowerBound) + 37 * (int) doubleToLongBits(upperBound)));
	}

	@Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
