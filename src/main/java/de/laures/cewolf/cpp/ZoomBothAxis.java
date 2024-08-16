package de.laures.cewolf.cpp;

import java.io.Serializable;
import java.util.Map;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import de.laures.cewolf.ChartPostProcessor;
import static java.lang.Double.NaN;
import static java.lang.Double.isNaN;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.lang.Integer.parseInt;
import static java.lang.Integer.parseInt;
import static java.lang.Integer.parseInt;
import static java.lang.Integer.parseInt;
import static java.lang.Integer.parseInt;
import static java.lang.Integer.parseInt;
import static java.lang.Integer.parseInt;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.*;

/**
* A postprocessor for altering the domain and range of a chart, essentially creating a way to "zoom into" a chart.
* The zooming cabilities for the range (y) axis works for all plots.<BR>
* The capabilities for the domain (x) axis work only for x/y and timeseries plots (currently only has capabilities to define hours) only, not category plots.<BR>
* (Note: in order for the set range capabilities to work properly, it is recommended that the "rangeIncludesZero" be set to false using a VisualEnhancer postprocessor.)<BR>
* The following parameters can be used:
* <BR><b>lowerRangeVal</b>  optional, default value produced by chart; the lowest value that will be graphed on the range (y) axis
* <BR><b>upperRangeVal</b> optional; default value produced by chart; the highest value that will be graphed on the range (y) axis
* <BR><b>lowerDomainValN</b> optional; default value produced by chart; the lowest value that will be graphed on the domain (x) axis for a numerical plot (xy plot)
* <BR><b>upperDomainValN</b> optional; default value produced by chart; the highest value that will be graphed on the domain (x) axis for a numerical plot (xy plot)
* <BR><b>lowerDomainValD</b> optional; default value produced by chart; the minimum time that will be graphed on the domain (x) axis for a time scale graph (timeseries plot)
* <BR><b>upperDomainValD</b> optional; default value produced by chart; the maximum time that will be graphed on the domain (x) axis for a time scale graph (timeseries plot)
* <BR><b>zoomFactor</b> optional; default 1.0; value to zoom in on the center point
* <BR><b>anchorValueD</b> optional; no default value; the domain axis (X axis) of xyplots is centered around this value
* <BR><b>anchorValueR</b> optional; no default value; the range axis (Y axis) of xyplots is centered around this value
* <P>
* Example usage:<P>
* &lt;chart:chartpostprocessor id="zoom"&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="lowerRangeVal" value="-3.2" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="upperRangeVal" value="3.2" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="lowerDomainValN" value="-6.0" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="upperDomainValN" value="12.5" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="lowerDomainValD" value="8:00" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="upperDomainValD" value="15:00" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="zoomFactor" value="1.5" /&gt;<BR>
* &lt;/chart:chartpostprocessor&gt;
* <P>
* @author Ann Kapusta
*/

public class ZoomBothAxis implements ChartPostProcessor, Serializable {

	static final long serialVersionUID = -3336573712263777733L;

    private double lowerRangeVal = 0.0, upperRangeVal = 0.0;
    private double lowerDomainValN = 0.0, upperDomainValN = 0.0;
    private GregorianCalendar lowerDomainValD = new GregorianCalendar(1901, 0, 1);
    private GregorianCalendar upperDomainValD = new GregorianCalendar(1901, 0, 1);
    private double zoomFactor = 1.0;
    private double anchorValueD = NaN, anchorValueR = NaN;

    public void processChart (JFreeChart chart, Map<String,String> params) {
        var plot = chart.getPlot();

        if (plot instanceof XYPlot) {
            var xyPlot = (XYPlot) plot;
            var rangeAxis = xyPlot.getRangeAxis();
            var domainAxis = xyPlot.getDomainAxis();
            if (rangeAxis instanceof NumberAxis){
				lowerRangeVal = ((NumberAxis) rangeAxis).getLowerBound();
				upperRangeVal = ((NumberAxis) rangeAxis).getUpperBound();
            }
            if (domainAxis instanceof DateAxis){
				lowerDomainValD.setTime((((DateAxis) domainAxis).getMinimumDate()));
				upperDomainValD.setTime((((DateAxis) domainAxis).getMaximumDate()));
            } else if (domainAxis instanceof NumberAxis){
				lowerDomainValN = ((NumberAxis) domainAxis).getLowerBound();
				upperDomainValN = ((NumberAxis) domainAxis).getUpperBound();
			 }
        } else if (plot instanceof CategoryPlot) {
            var catPlot = (CategoryPlot) plot;
            var axis = catPlot.getRangeAxis();
			lowerRangeVal = ((NumberAxis) axis).getLowerBound();
			upperRangeVal = ((NumberAxis) axis).getUpperBound();
		}

		var str = params.get("lowerRangeVal");
		if (str != null) {
			try {
				lowerRangeVal = parseDouble(str);
			} catch (NumberFormatException nfex) { }
		}

		str = params.get("upperRangeVal");
		if (str != null) {
			try {
				upperRangeVal = parseDouble(str);
			} catch (NumberFormatException nfex) { }
		}

		str = params.get("zoomFactor");
		if (str != null) {
			try {
				zoomFactor = parseDouble(str);
			} catch (NumberFormatException nfex) { }
		}

		str = params.get("anchorValueD");
		if (str != null) {
			try {
				anchorValueD = parseDouble(str);
			} catch (NumberFormatException nfex) { }
		}

		str = params.get("anchorValueR");
		if (str != null) {
			try {
				anchorValueR = parseDouble(str);
			} catch (NumberFormatException nfex) { }
		}

		str = params.get("lowerDomainValN");
		if (str != null) {
			try {
				var newDomainVal = parseDouble(str);
				if (newDomainVal >= lowerDomainValN) {
					lowerDomainValN = newDomainVal;
				}
			} catch (NumberFormatException nfex) { }
		}

		str = params.get("upperDomainValN");
		if (str != null) {
			try {
				var newDomainVal = parseDouble(str);
				if (newDomainVal <= upperDomainValN) {
					upperDomainValN = newDomainVal;
				}
			} catch (NumberFormatException nfex) { }
		}

		var strL = params.get("lowerDomainValD");
		var strU = params.get("upperDomainValD");
		if (strL != null && strU != null) {
			if (!strL.equals(strU)) {
				var component = strL.split(":");
				var hour = parseInt(component[0]);
				var minute = parseInt(component[1]);
				var tester = lowerDomainValD.get(HOUR_OF_DAY);
				if (hour > tester) {
					lowerDomainValD.set(HOUR_OF_DAY, hour);
					lowerDomainValD.set(MINUTE, minute);
				} else if (hour == HOUR_OF_DAY && minute > MINUTE){
					lowerDomainValD.set(HOUR_OF_DAY, hour);
					lowerDomainValD.set(MINUTE, minute);
				}

				component = strU.split(":");
				hour = parseInt(component[0]);
				minute = parseInt(component[1]);
				tester = upperDomainValD.get(HOUR_OF_DAY);
				if (hour < tester) {
				   upperDomainValD.set(HOUR_OF_DAY, hour);
				   upperDomainValD.set(MINUTE, minute);
				} else if (hour == HOUR_OF_DAY && minute < MINUTE){
				   upperDomainValD.set(HOUR_OF_DAY, hour);
				   upperDomainValD.set(MINUTE, minute);
				}
			}
		}        

		if (plot instanceof XYPlot) {
			var xyPlot = (XYPlot) plot;
			var rAxis = xyPlot.getRangeAxis();

			if (rAxis instanceof NumberAxis) {
				var nAxis = (NumberAxis) rAxis;
				if (lowerRangeVal != upperRangeVal) {
					nAxis.setLowerBound(lowerRangeVal);
					nAxis.setUpperBound(upperRangeVal);
				}
				nAxis.resizeRange(zoomFactor);
				if (! isNaN(anchorValueR))
					nAxis.centerRange(anchorValueR);
			}

			var dAxis = xyPlot.getDomainAxis();
			if (dAxis instanceof DateAxis) {
				((DateAxis) dAxis).setMinimumDate(lowerDomainValD.getTime());
				((DateAxis) dAxis).setMaximumDate(upperDomainValD.getTime());
			} else if (dAxis instanceof NumberAxis) {
				var nAxis = (NumberAxis) dAxis;
				if (lowerDomainValN != upperDomainValN) {
					nAxis.setRange(lowerDomainValN, upperDomainValN);
				}
				if (! isNaN(anchorValueD))
					nAxis.centerRange(anchorValueD);
			}
		} else if (plot instanceof CategoryPlot) {
			var catPlot = (CategoryPlot) plot;
			var axis = (NumberAxis) catPlot.getRangeAxis();
			axis.setLowerBound(lowerRangeVal);
			axis.setUpperBound(upperRangeVal);
			axis.resizeRange(zoomFactor);
		}
    }

    public double getAnchorValueD() { return anchorValueD; }

    public void setAnchorValueD (double anchorValueD) {
		this.anchorValueD = anchorValueD;
    }

    public double getAnchorValueR() { return anchorValueR; }

    public void setAnchorValueR (double anchorValueR) {
		this.anchorValueR = anchorValueR;
    }

    public double getZoomFactor() { return zoomFactor; }

    public void setZoomFactor (double zoomFactor) {
		this.zoomFactor = zoomFactor;
    }

    public double getLowerRangeVal() { return lowerRangeVal; }

    public void setLowerRangeVal (double lowerRangeVal) {
		this.lowerRangeVal = lowerRangeVal;
    }

    public double getUpperRangeVal() { return upperRangeVal; }

    public void setUpperRangeVal (double upperRangeVal) {
		this.upperRangeVal = upperRangeVal;
    }

    public double getUpperDomainValN() { return upperDomainValN; }

    public void setUpperDomainValN(double upperDomainVal) {
		this.upperDomainValN = upperDomainVal;
    }

    public double getLowerDomainValN() { return lowerDomainValN; }

    public void setLowerDomainValN (double lowerDomainVal) {
		this.lowerDomainValN = lowerDomainVal;
    }

    public String getUpperDomainValD() {
		if (upperDomainValD != null){
            var format = new SimpleDateFormat("HH:mm");
            return format.format(upperDomainValD.getTime());
        } else {
            return null;
        }
    }

    public void setUpperDomainValD (String upperDomainVal) {
        if (upperDomainVal != null) {
			// allow for missing minutes
			if (! upperDomainVal.contains(":"))
				upperDomainVal += ":00";

            var component = upperDomainVal.split(":");
            var hour = parseInt(component[0]);
            var minute = parseInt(component[1]);

            this.upperDomainValD.set(HOUR_OF_DAY, hour);
            this.upperDomainValD.set(MINUTE, minute);
        }
    }

    public String getLowerDomainValD() {
		if (lowerDomainValD != null) {
            var format = new SimpleDateFormat("HH:mm");
            return format.format(lowerDomainValD.getTime());
        } else {
            return null;
        }
    }

    public void setLowerDomainValD (String lowerDomainVal) {
		if (lowerDomainVal != null) {
			// allow for missing minutes
			if (! lowerDomainVal.contains(":"))
				lowerDomainVal += ":00";

			var component = lowerDomainVal.split(":");
            var hour = parseInt(component[0]);
            var minute = parseInt(component[1]);

            this.lowerDomainValD.set(HOUR_OF_DAY, hour);
            this.lowerDomainValD.set(MINUTE, minute);
        }
    }
}

