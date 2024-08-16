package de.laures.cewolf.cpp;

import java.awt.Color;
import java.io.Serializable;
import java.util.Map;

import de.laures.cewolf.ChartPostProcessor;
import static java.awt.Color.decode;
import static java.lang.Double.parseDouble;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.*;
import org.jfree.ui.RectangleInsets;

/**
* A postprocessor for altering the border and padding of a chart. It supports the following parameters:
* <BR><b>border</b> true/false; optional, default true; whether or not a border is drawn around the chart
* <BR><b>borderpaint</b> optional; default #000000 (i.e., black); the color of the border
* <BR><b>top</b> optional; default 1; sets the top padding between the chart border and the chart drawing area
* <BR><b>left</b> optional; default 1; sets the left padding between the chart border and the chart drawing area
* <BR><b>right</b> optional; default 1; sets the right padding between the chart border and the chart drawing area
* <BR><b>bottom</b> optional; default 1; sets the bottom padding between the chart border and the chart drawing area
* <BR><b>plotTop</b> optional; default 4; sets the top padding of the plot
* <BR><b>plotLeft</b> optional; default 8; sets the left padding of the plot
* <BR><b>plotRight</b> optional; default 8; sets the right padding of the plot
* <BR><b>plotBottom</b> optional; default 4; sets the bottom padding of the plot
* <BR><b>rangeIncludesZero</b> true/false; optional; default true; whether the range (Y) axis always includes zero 
* <BR><b>showDomainAxes</b> true/false; optional; default true; whether or not to show any domain (X) axes
* <BR><b>showRangeAxes</b> true/false; optional; default true; whether or not to show any range (Y) axes
* <P>
* Usage:<P>
* &lt;chart:chartpostprocessor id="visualEnhance"&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="border" value="true" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="borderpaint" value="#4488BB" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="top" value="5" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="left" value="5" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="right" value="5" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="bottom" value="5" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="plotTop" value="0" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="plotLeft" value="0" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="plotRight" value="0" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="plotBottom" value="0" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="rangeIncludesZero" value="false" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="showDomainAxes" value="true" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="showRangeAxes" value="true" /&gt;<BR>
* &lt;/chart:chartpostprocessor&gt;
*/

public class VisualEnhancer implements ChartPostProcessor, Serializable
{
	static final long serialVersionUID = -4434675932582386052L;

    public void processChart (JFreeChart chart, Map<String,String> params) {
		var borderPaint = new Color(0, 0, 0);
		var hasBorder = true;
		var rangeIncludesZero = true;
		var showDomainAxes = true;
		var showRangeAxes = true;
		var top = 1.0;
		var left = 1.0;
		var right = 1.0;
		var bottom = 1.0;
		var plotTop = 4.0;
		var plotLeft = 8.0;
		var plotRight = 8.0;
		var plotBottom = 4.0;
		var str = params.get("top");
		if (str != null && str.trim().length() > 0) {
			try {
				top = parseDouble(str);
				if (top < 0)
					top = 0.0;
			} catch (NumberFormatException nfex) { }
		}

		str = params.get("left");
		if (str != null && str.trim().length() > 0) {
			try {
				left = parseDouble(str);
				if (left < 0)
					left = 0.0;
			} catch (NumberFormatException nfex) { }
		}

		str = params.get("right");
		if (str != null && str.trim().length() > 0) {
			try {
				right = parseDouble(str);
				if (right < 0)
					right = 0.0;
			} catch (NumberFormatException nfex) { }
		}

		str = params.get("bottom");
		if (str != null && str.trim().length() > 0) {
			try {
				bottom = parseDouble(str);
				if (bottom < 0)
					bottom = 0.0;
			} catch (NumberFormatException nfex) { }
		}

		str = params.get("plotTop");
		if (str != null && str.trim().length() > 0) {
			try {
				plotTop = parseDouble(str);
				if (plotTop < 0)
					plotTop = 0.0;
			} catch (NumberFormatException nfex) { }
		}

		str = params.get("plotLeft");
		if (str != null && str.trim().length() > 0) {
			try {
				plotLeft = parseDouble(str);
				if (plotLeft < 0)
					plotLeft = 0.0;
			} catch (NumberFormatException nfex) { }
		}

		str = params.get("plotRight");
		if (str != null && str.trim().length() > 0) {
			try {
				plotRight = parseDouble(str);
				if (plotRight < 0)
					plotRight = 0.0;
			} catch (NumberFormatException nfex) { }
		}

		str = params.get("plotBottom");
		if (str != null && str.trim().length() > 0) {
			try {
				plotBottom = parseDouble(str);
				if (plotBottom < 0)
					plotBottom = 0.0;
			} catch (NumberFormatException nfex) { }
		}

		str = params.get("borderpaint");
		if (str != null && str.trim().length() > 0) {
			try {
				borderPaint = decode(str);
			} catch (NumberFormatException nfex) { }
		}

		str = params.get("border");
		if (str != null)
			hasBorder = "true".equals(str.toLowerCase());

		str = params.get("rangeIncludesZero");
		if (str != null)
			rangeIncludesZero = "true".equals(str.toLowerCase());

		str = params.get("showDomainAxes");
		if (str != null)
			showDomainAxes = "true".equals(str.toLowerCase());

		str = params.get("showRangeAxes");
		if (str != null)
			showRangeAxes = "true".equals(str.toLowerCase());

		chart.setBorderVisible(hasBorder);
		chart.setBorderPaint(borderPaint);
		chart.setPadding(new RectangleInsets(top, left, bottom, right));

		var plot = chart.getPlot();
		plot.setInsets(new RectangleInsets(plotTop, plotLeft, plotBottom, plotRight));

		if (plot instanceof XYPlot) {
			var xyPlot = (XYPlot) plot;
			var axis = xyPlot.getRangeAxis();
			if (axis instanceof NumberAxis)
				((NumberAxis) axis).setAutoRangeIncludesZero(rangeIncludesZero);
			if (! showDomainAxes)
				xyPlot.clearDomainAxes();
			if (! showRangeAxes)
				xyPlot.clearRangeAxes();
		} else if (plot instanceof CategoryPlot) {
			var catPlot = (CategoryPlot) plot;
			var axis = catPlot.getRangeAxis();
			if (axis instanceof NumberAxis)
				((NumberAxis) axis).setAutoRangeIncludesZero(rangeIncludesZero);
			if (! showDomainAxes)
				catPlot.clearDomainAxes();
			if (! showRangeAxes)
				catPlot.clearRangeAxes();
		}
	}
}

