package de.laures.cewolf.cpp;

import java.awt.Color;
import java.awt.Font;
import java.io.Serializable;
import java.util.Map;

import de.laures.cewolf.ChartPostProcessor;
import static java.awt.Color.decode;
import static java.awt.Font.BOLD;
import static java.awt.Font.ITALIC;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.title.LegendTitle;
import org.jfree.ui.RectangleInsets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import static org.apache.commons.logging.LogFactory.getLog;

/**
* A postprocessor for modifying the legend of a chart. It supports the following parameters:
* <BR><b>fontname</b> optional; default SansSerif
* <BR><b>fontsize</b> optional; default is 18
* <BR><b>paint</b> optional; default #000000 (i.e., black)
* <BR><b>backgroundpaint</b> optional; default #FFFFFF (i.e., white)
* <BR><b>bold</b> true/false; optional; default true
* <BR><b>italic</b> true/false; optional; default false
* <BR><b>top</b> optional; default 1; sets the top padding between the legend border and the legend 
* <BR><b>left</b> optional; default 1; sets the left padding between the legend border and the legend 
* <BR><b>right</b> optional; default 1; sets the right padding between the legend border and the legend 
* <BR><b>bottom</b> optional; default 1; sets the bottom padding between the legend border and the legend 
* <P>
* Usage:<P>
* &lt;chart:chartpostprocessor id="subTitle"&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="fontname" value="Serif" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="fontsize" value="24" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="paint" value="#FF8800" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="backgroundpaint" value="#0088FF" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="bold" value="false" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="italic" value="true" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="top" value="5" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="left" value="5" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="right" value="5" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="bottom" value="5" /&gt;<BR>
* &lt;/chart:chartpostprocessor&gt;
* <P>
*/

public class LegendEnhancer implements ChartPostProcessor, Serializable
{
	static final long serialVersionUID = -6071718115056160390L;

    private static final Log log = getLog(LegendEnhancer.class);

    public void processChart (JFreeChart chart, Map<String,String> params) {
		var fontName = "SansSerif";
		Color paint = null;
		Color backgroundPaint = null;
		var fontSize = 18;
		var isBold = true;
		var isItalic = false;
		var top = 5.0;
		var left = 5.0;
		var right = 5.0;
		var bottom = 5.0;
		var fontNameParam = params.get("fontname");
		if (fontNameParam != null && fontNameParam.trim().length() > 0)
			fontName = fontNameParam.trim();

		var fontSizeParam = params.get("fontsize");
		if (fontSizeParam != null && fontSizeParam.trim().length() > 0) {
			try {
				fontSize = parseInt(fontSizeParam);
				if (fontSize < 1)
					fontSize = 18;
			} catch (NumberFormatException nfex) { }
		}

		var paintParam = params.get("paint");
		if (paintParam != null && paintParam.trim().length() > 0) {
			try {
				paint = decode(paintParam);
			} catch (NumberFormatException nfex) { }
		}

		var backgroundpaintParam = params.get("backgroundpaint");
		if (backgroundpaintParam != null && backgroundpaintParam.trim().length() > 0) {
			try {
				backgroundPaint = decode(backgroundpaintParam);
			} catch (NumberFormatException nfex) { }
		}

		var boldParam = params.get("bold");
		if (boldParam != null)
			isBold = "true".equals(boldParam.toLowerCase());

		var italicParam = params.get("italic");
		if (italicParam != null)
			isItalic = "true".equals(italicParam.toLowerCase());

		var str = params.get("top");
		if (str != null && str.trim().length() > 0) {
			try {
				top = parseDouble(str);
				if (top < 0)
					top = 5.0;
			} catch (NumberFormatException nfex) { }
		}

		str = params.get("left");
		if (str != null && str.trim().length() > 0) {
			try {
				left = parseDouble(str);
				if (left < 0)
					left = 5.0;
			} catch (NumberFormatException nfex) { }
		}

		str = params.get("right");
		if (str != null && str.trim().length() > 0) {
			try {
				right = parseDouble(str);
				if (right < 0)
					right = 5.0;
			} catch (NumberFormatException nfex) { }
		}

		str = params.get("bottom");
		if (str != null && str.trim().length() > 0) {
			try {
				bottom = parseDouble(str);
				if (bottom < 0)
					bottom = 5.0;
			} catch (NumberFormatException nfex) { }
		}

		var legend = chart.getLegend();

		//legend.setLegendItemGraphicPadding(new RectangleInsets(top, left, bottom, right));
		legend.setItemLabelPadding(new RectangleInsets(top, left, bottom, right));

		var font = new Font(fontName,
							(isBold ? BOLD : 0) + (isItalic ? ITALIC : 0),
							fontSize);
		legend.setItemFont(font);

		if (paint != null)
			legend.setItemPaint(paint);
		if (backgroundPaint != null)
			legend.setBackgroundPaint(backgroundPaint);
	}
}
