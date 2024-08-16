package de.laures.cewolf.cpp;

import java.awt.Color;
import java.io.Serializable;
import java.util.Map;

import de.laures.cewolf.ChartPostProcessor;
import static java.awt.Color.decode;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.CompassPlot;

/**
* A postprocessor for changing details of a Compass plot.
* <BR><b>drawBorder</b> boolean; default false
* <BR><b>needleType</b> arrow,line,long,pin,plum,pointer,ship,wind,middlepin; default arrow
* <BR><b>needleFill</b> optional; default #000000 (i.e., black)
* <BR><b>needleOutline</b> optional; default #000000 (i.e., black)
* <P>
* Usage:<P>
* &lt;chart:chartpostprocessor id="compassEnhancer"&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="needleType" value="ship" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="drawBorder" value="false" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="needleFill" value="#336699" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="needleOutline" value="#99AACC" /&gt;<BR>
* &lt;/chart:chartpostprocessor&gt;
*/

// possible further settings: rose paint, rose center paint, rose highlight paint

public class CompassEnhancer implements ChartPostProcessor, Serializable
{
	static final long serialVersionUID = -1392284687232625608L;

    public void processChart (JFreeChart chart, Map<String,String> params) {
		var needleType = "arrow";
		var drawBorder = false;
		var needleFill = new Color(0, 0, 0);
		var needleOutline = new Color(0, 0, 0);
		var str = params.get("needleType");
		if (str != null && str.trim().length() > 0)
			needleType = str.trim();

		str = params.get("needleFill");
		if (str != null && str.trim().length() > 0) {
			try {
				needleFill = decode(str);
			} catch (NumberFormatException nfex) { }
		}

		str = params.get("needleOutline");
		if (str != null && str.trim().length() > 0) {
			try {
				needleOutline = decode(str);
			} catch (NumberFormatException nfex) { }
		}

		str = params.get("drawBorder");
		if (str != null)
			drawBorder = "true".equals(str);

		var plot = chart.getPlot();
		if (plot instanceof CompassPlot) {
			var cplot = (CompassPlot) plot;

			cplot.setDrawBorder(drawBorder);

			if (null != needleType) switch (needleType) {
                case "line":
                    cplot.setSeriesNeedle(1);
                    break;
                case "long":
                    cplot.setSeriesNeedle(2);
                    break;
                case "pin":
                    cplot.setSeriesNeedle(3);
                    break;
                case "plum":
                    cplot.setSeriesNeedle(4);
                    break;
                case "pointer":
                    cplot.setSeriesNeedle(5);
                    break;
                case "ship":
                    cplot.setSeriesNeedle(6);
                    break;
                case "wind":
                    cplot.setSeriesNeedle(7);
                    break;
                case "arrow":
                    cplot.setSeriesNeedle(8);
                    break;
                case "middlepin":
                    cplot.setSeriesNeedle(9);
                    break;
                default:
                    break;
            }

			cplot.setSeriesPaint(0, needleFill);
			cplot.setSeriesOutlinePaint(0, needleOutline);
		}
	}
}
