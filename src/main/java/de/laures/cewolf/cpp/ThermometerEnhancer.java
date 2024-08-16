package de.laures.cewolf.cpp;

import java.awt.Color;
import java.io.Serializable;
import java.util.Map;

import de.laures.cewolf.ChartPostProcessor;
import de.laures.cewolf.jfree.ThermometerPlot;
import static de.laures.cewolf.jfree.ThermometerPlot.CRITICAL;
import static de.laures.cewolf.jfree.ThermometerPlot.NORMAL;
import static de.laures.cewolf.jfree.ThermometerPlot.UNITS_CELCIUS;
import static de.laures.cewolf.jfree.ThermometerPlot.UNITS_FAHRENHEIT;
import static de.laures.cewolf.jfree.ThermometerPlot.UNITS_KELVIN;
import static de.laures.cewolf.jfree.ThermometerPlot.UNITS_NONE;
import static de.laures.cewolf.jfree.ThermometerPlot.WARNING;
import static java.awt.Color.black;
import static java.awt.Color.decode;
import static java.awt.Color.green;
import static java.awt.Color.orange;
import static java.awt.Color.red;
import static java.awt.Color.white;
import static java.lang.Double.parseDouble;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;

/**
* A postprocessor for changing details of a Compass plot.
* <BR><b>units</b> none/celsius/fahrenheit/kelvin; optional; default none
* <BR><b>mercuryColor</b> optional; default #FF0000 (i.e., red)
* <BR><b>thermometerColor</b> optional; default #000000 (i.e., black)
* <BR><b>valueColor</b> optional; default #FFFFFF (i.e., white)
* <BR><b>lowerBound</b> optional; default 0.0; starting value for the scale
* <BR><b>warningPoint</b> optional; default 50.0; boundary between normal range and warning range
* <BR><b>criticalPoint</b> optional; default 75.0; boundary between warning range and critical range
* <BR><b>upperBound</b> optional; default 100.0; end value for the scale
* <BR><b>subrangeIndicatorsVisible</b> true/false; optional; default true
* <BR><b>useSubrangePaint</b> true/false; optional; default true; if this is false, then mercuryColor is used
* <BR><b>subrangeColorNormal</b> optional; default #00FF00 (i.e., green)
* <BR><b>subrangeColorWarning</b> optional; default #FFC800 (i.e., orange)
* <BR><b>subrangeColorCritical</b> optional; default #FF0000 (i.e., red)
* <P>
* Usage:<P>
* &lt;chart:chartpostprocessor id="thermometerEnhancer"&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="units" value="celsius" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="mercuryColor" value="#336699" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="valueColor" value="#99AACC" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="thermometerColor" value="#CCCCCC" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="lowerBound" value="20" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="warningPoint" value="40" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="criticalPaint" value="60" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="upperBound" value="80" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="subrangeIndicatorsVisible" value="false" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="useSubrangePaint" value="true" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="subrangeColorNormal" value="#00FF00" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="subrangeColorWarning" value="#FFC800" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="subrangeColorCritical" value="#FF0000" /&gt;<BR>
* &lt;/chart:chartpostprocessor&gt;
*/

public class ThermometerEnhancer implements ChartPostProcessor, Serializable
{
	static final long serialVersionUID = -8459734218848320685L;

    public void processChart (JFreeChart chart, Map<String,String> params) {
		var lowerBound = 0.0;
		var warningPoint = 50.0;
		var criticalPoint = 75.0;
		var upperBound = 100.0;
		var mercuryColor = red;
		var valueColor = white;
		var thermometerColor = black;
		var normalColor = green;
		var warningColor = orange;
		var criticalColor = red;
		var units = UNITS_NONE;
		var useSubrangePaint = true;
		var subrangeIndicatorsVisible = true;
		var str = params.get("mercuryColor");
		if (str != null && str.trim().length() > 0) {
			try {
				mercuryColor = decode(str);
			} catch (NumberFormatException nfex) { }
		}

		str = params.get("thermometerColor");
		if (str != null && str.trim().length() > 0) {
			try {
				thermometerColor = decode(str);
			} catch (NumberFormatException nfex) { }
		}

		str = params.get("valueColor");
		if (str != null && str.trim().length() > 0) {
			try {
				valueColor = decode(str);
			} catch (NumberFormatException nfex) { }
		}

		str = params.get("subrangeColorNormal");
		if (str != null && str.trim().length() > 0) {
			try {
				normalColor = decode(str);
			} catch (NumberFormatException nfex) { }
		}

		str = params.get("subrangeColorWarning");
		if (str != null && str.trim().length() > 0) {
			try {
				warningColor = decode(str);
			} catch (NumberFormatException nfex) { }
		}

		str = params.get("subrangeColorCritical");
		if (str != null && str.trim().length() > 0) {
			try {
				criticalColor = decode(str);
			} catch (NumberFormatException nfex) { }
		}

		str = params.get("lowerBound");
		if (str != null) {
			try {
				lowerBound = parseDouble(str);
			} catch (NumberFormatException nfex) { }
		}

		str = params.get("warningPoint");
		if (str != null) {
			try {
				warningPoint = parseDouble(str);
				if (warningPoint < lowerBound || warningPoint > upperBound)
					warningPoint = 50.0;
			} catch (NumberFormatException nfex) { }
		}

		str = params.get("criticalPoint");
		if (str != null) {
			try {
				criticalPoint = parseDouble(str);
				if (criticalPoint < warningPoint || criticalPoint > upperBound)
					criticalPoint = 75.0;
			} catch (NumberFormatException nfex) { }
		}

		str = params.get("upperBound");
		if (str != null) {
			try {
				upperBound = parseDouble(str);
			} catch (NumberFormatException nfex) { }
		}

		str = params.get("units");
		if (str != null) {
            switch (str) {
                case "celsius":
                    units = UNITS_CELCIUS;
                    break;
                case "fahrenheit":
                    units = UNITS_FAHRENHEIT;
                    break;
                case "kelvin":
                    units = UNITS_KELVIN;
                    break;
                default:
                    break;
            }
		}

		str = params.get("subrangeIndicatorsVisible");
		if (str != null)
			subrangeIndicatorsVisible = "true".equals(str.toLowerCase());

		str = params.get("useSubrangePaint");
		if (str != null)
			useSubrangePaint = "true".equals(str.toLowerCase());

		var plot = chart.getPlot();
		if (plot instanceof ThermometerPlot) {
			var tplot = (ThermometerPlot) plot;
			tplot.setUnits(units);
			tplot.setUseSubrangePaint(false);
			tplot.setThermometerPaint(thermometerColor);
			tplot.setMercuryPaint(mercuryColor);
			tplot.setValuePaint(valueColor);
			tplot.setLowerBound(lowerBound);
			tplot.setSubrange(NORMAL, lowerBound, warningPoint);
			tplot.setSubrange(WARNING, warningPoint, criticalPoint);
			tplot.setSubrange(CRITICAL, criticalPoint, upperBound);
			tplot.setUpperBound(upperBound);
			tplot.setUseSubrangePaint(useSubrangePaint);
			tplot.setSubrangeIndicatorsVisible(subrangeIndicatorsVisible);
			tplot.setSubrangePaint(NORMAL, normalColor);
			tplot.setSubrangePaint(WARNING, warningColor);
			tplot.setSubrangePaint(CRITICAL, criticalColor);
		}
	}
}
