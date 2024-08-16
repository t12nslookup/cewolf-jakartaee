package de.laures.cewolf.cpp;

import java.awt.Color;
import java.io.Serializable;
import java.util.*;

import de.laures.cewolf.ChartPostProcessor;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;

import de.laures.cewolf.jfree.WaferMapPlot;
import de.laures.cewolf.jfree.WaferMapRenderer;
import static java.awt.Color.decode;
import static java.lang.String.valueOf;
import static java.lang.String.valueOf;
import static java.lang.String.valueOf;
import static java.lang.String.valueOf;
import static java.lang.String.valueOf;

/**
* A postprocessor for setting alternative colors for pie charts, category plots, XY plots and spider web plots.
* It takes numbered parameters containing the hex color values.
* <P>
* Usage:<P>
* &lt;chart:chartpostprocessor id="seriesPaint"&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="0" value="#FFFFAA" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="1" value="#AAFFAA" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="2" value="#FFAAFF" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="3" value="#FFAAAA" /&gt;<BR>
* &lt;/chart:chartpostprocessor&gt;
*/

public class SeriesPaintProcessor implements ChartPostProcessor, Serializable
{
	static final long serialVersionUID = -2290498142826058256L;

    public void processChart (JFreeChart chart, Map<String,String> params) {
        var plot = chart.getPlot();

		// pie charts
		if (plot instanceof PiePlot) {
			var piePlot = (PiePlot) plot;
			var keys = piePlot.getDataset().getKeys();
			for (var i=0; i<params.size(); i++) {
				var colorStr = params.get(valueOf(i));
				piePlot.setSectionPaint((Comparable) keys.get(i), decode(colorStr));
			}

		// category plots
		} else if (plot instanceof CategoryPlot) {
			var render = ((CategoryPlot) plot).getRenderer();

			for (var i=0; i<params.size(); i++) {
				var colorStr = params.get(valueOf(i));
				render.setSeriesPaint(i, decode(colorStr));
			}

		// spider web plots
		} else if (plot instanceof SpiderWebPlot) {
			var swPlot = (SpiderWebPlot) plot;

			for (var i=0; i<params.size(); i++) {
				var colorStr = params.get(valueOf(i));
				swPlot.setSeriesPaint(i, decode(colorStr));
			}

		// XY plots
		} else if (plot instanceof XYPlot) {
			var render = ((XYPlot) plot).getRenderer();

			for (var i=0; i<params.size(); i++) {
				var colorStr = params.get(valueOf(i));
				render.setSeriesPaint(i, decode(colorStr));
			}

		// Wafer Map plots
		} else if (plot instanceof WaferMapPlot) {
			var render = ((WaferMapPlot) plot).getRenderer();

			for (var i=0; i<params.size(); i++) {
				var colorStr = params.get(valueOf(i));
				render.setSeriesPaint(i, decode(colorStr));
			}
		}
	}
}

