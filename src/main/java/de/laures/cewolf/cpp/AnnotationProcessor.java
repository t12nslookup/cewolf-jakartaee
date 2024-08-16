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
import org.jfree.chart.annotations.*;
import static org.jfree.chart.annotations.TextAnnotation.DEFAULT_TEXT_ANCHOR;
import org.jfree.chart.axis.CategoryAnchor;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.ui.TextAnchor;
import static org.jfree.ui.TextAnchor.BASELINE_CENTER;
import static org.jfree.ui.TextAnchor.BASELINE_LEFT;
import static org.jfree.ui.TextAnchor.BASELINE_RIGHT;
import static org.jfree.ui.TextAnchor.BOTTOM_CENTER;
import static org.jfree.ui.TextAnchor.BOTTOM_LEFT;
import static org.jfree.ui.TextAnchor.BOTTOM_RIGHT;
import static org.jfree.ui.TextAnchor.CENTER;
import static org.jfree.ui.TextAnchor.CENTER_LEFT;
import static org.jfree.ui.TextAnchor.CENTER_RIGHT;
import static org.jfree.ui.TextAnchor.HALF_ASCENT_CENTER;
import static org.jfree.ui.TextAnchor.HALF_ASCENT_LEFT;
import static org.jfree.ui.TextAnchor.HALF_ASCENT_RIGHT;
import static org.jfree.ui.TextAnchor.TOP_CENTER;
import static org.jfree.ui.TextAnchor.TOP_LEFT;
import static org.jfree.ui.TextAnchor.TOP_RIGHT;

/**
* A postprocessor for adding annotation to an X/Y or Category plot.
* If either arrowPaint or arrowAngle is set then a pointer will be drawn; otherwise, just the text.
* <BR><b>text</b> the text to display; mandatory
* <BR><b>x</b> (for X/Y plots only) the X value at which to show the text; mandatory
* <BR><b>y</b> (for X/Y plots only) the Y value at which to show the text; mandatory
* <BR><b>category</b> (for category plots only) the category for which to show the text; mandatory
* <BR><b>value</b> (for category plots only) the value at which to show the text; mandatory
* <BR><b>fontname</b> optional; default SansSerif
* <BR><b>fontsize</b> optional; default 14
* <BR><b>bold</b> true/false; optional; default false
* <BR><b>italic</b> true/false; optional; default false
* <BR><b>textPaint</b> the color to use for the text; optional; default #000000 (i.e., black)
* <BR><b>arrowPaint</b> the color to use for the text; optional; default #000000 (i.e., black)
* <BR><b>arrowAngle</b> the angle at which to display the arrow; optional; default 0
* <BR><b>textAnchor</b> the position of text relative to its origin point; optional; possible values are:
BASELINE_CENTER, BASELINE_LEFT, BASELINE_RIGHT, BOTTOM_CENTER, BOTTOM_LEFT, BOTTOM_RIGHT, CENTER, CENTER_LEFT,
CENTER_RIGHT, HALF_ASCENT_CENTER, HALF_ASCENT_LEFT, HALF_ASCENT_RIGHT, TOP_CENTER, TOP_LEFT, TOP_RIGHT 
* <P>
* See the annotations.jsp page of the sample web app for usage examples.
*/

public class AnnotationProcessor implements ChartPostProcessor, Serializable
{
	static final long serialVersionUID = 6321794363389448612L;

    public void processChart (JFreeChart chart, Map<String,String> params) {
		var text = "text goes here";
		var fontName = "SansSerif";
		var fontSize = 14;
		var isBold = false;
		var isItalic = false;
		var x = 0.0;
		var y = 0.0;
		String category = null;
		var value = 0.0;
		var textAnchor = DEFAULT_TEXT_ANCHOR;
		var textPaint = new Color(0, 0, 0);
		var arrowPaint = new Color(0, 0, 0);
		double arrowAngle = 0;
		var drawArrow = false;
		var str = params.get("text");
		if (str != null && str.trim().length() > 0)
			text = str.trim();

		var fontNameParam = params.get("fontname");
		if (fontNameParam != null && fontNameParam.trim().length() > 0)
			fontName = fontNameParam.trim();

		var fontSizeParam = params.get("fontsize");
		if (fontSizeParam != null && fontSizeParam.trim().length() > 0) {
			try {
				fontSize = parseInt(fontSizeParam);
				if (fontSize < 4)
					fontSize = 14;
			} catch (NumberFormatException nfex) { }
		}

		var boldParam = params.get("bold");
		if (boldParam != null)
			isBold = "true".equals(boldParam.toLowerCase());

		var italicParam = params.get("italic");
		if (italicParam != null)
			isItalic = "true".equals(italicParam.toLowerCase());

		var font = new Font(fontName,
							(isBold ? BOLD : 0) + (isItalic ? ITALIC : 0),
							fontSize);

		str = params.get("x");
		if (str != null) {
			try {
				x = parseDouble(str);
			} catch (NumberFormatException nfex) { }
		}

		str = params.get("y");
		if (str != null) {
			try {
				y = parseDouble(str);
			} catch (NumberFormatException nfex) { }
		}

		str = params.get("category");
		if (str != null && str.trim().length() > 0)
			category = str.trim();

		str = params.get("value");
		if (str != null) {
			try {
				value = parseDouble(str);
			} catch (NumberFormatException nfex) { }
		}

		str = params.get("textPaint");
		if (str != null && str.trim().length() > 0) {
			try {
				textPaint = decode(str);
			} catch (NumberFormatException nfex) { }
		}

		str = params.get("arrowPaint");
		if (str != null && str.trim().length() > 0) {
			try {
				arrowPaint = decode(str);
				drawArrow = true;
			} catch (NumberFormatException nfex) { }
		}

		str = params.get("arrowAngle");
		if (str != null) {
			try {
				arrowAngle = parseDouble(str);
				drawArrow = true;
			} catch (NumberFormatException nfex) { }
		}

		str = params.get("textAnchor");
		if (str != null) {
            switch (str) {
                case "BASELINE_CENTER":
                    textAnchor = BASELINE_CENTER;
                    break;
                case "BASELINE_LEFT":
                    textAnchor = BASELINE_LEFT;
                    break;
                case "BASELINE_RIGHT":
                    textAnchor = BASELINE_RIGHT;
                    break;
                case "BOTTOM_CENTER":
                    textAnchor = BOTTOM_CENTER;
                    break;
                case "BOTTOM_LEFT":
                    textAnchor = BOTTOM_LEFT;
                    break;
                case "BOTTOM_RIGHT":
                    textAnchor = BOTTOM_RIGHT;
                    break;
                case "CENTER":
                    textAnchor = CENTER;
                    break;
                case "CENTER_LEFT":
                    textAnchor = CENTER_LEFT;
                    break;
                case "CENTER_RIGHT":
                    textAnchor = CENTER_RIGHT;
                    break;
                case "HALF_ASCENT_CENTER":
                    textAnchor = HALF_ASCENT_CENTER; 
                    break;
                case "HALF_ASCENT_LEFT":
                    textAnchor = HALF_ASCENT_LEFT;
                    break;
                case "HALF_ASCENT_RIGHT":
                    textAnchor = HALF_ASCENT_RIGHT;
                    break;
                case "TOP_CENTER":
                    textAnchor = TOP_CENTER;
                    break;
                case "TOP_LEFT":
                    textAnchor = TOP_LEFT;
                    break;
                case "TOP_RIGHT":
                    textAnchor = TOP_RIGHT;
                    break;
                default:
                    break;
            }
		}

		var plot = chart.getPlot();
        if (plot instanceof XYPlot) {
			var anno = drawArrow
									? new XYPointerAnnotation(text, x, y, arrowAngle)
									: new XYTextAnnotation(text, x, y);
			anno.setPaint(textPaint);
			anno.setFont(font);
			anno.setTextAnchor(textAnchor);
			if (drawArrow) {
				((XYPointerAnnotation) anno).setArrowPaint(arrowPaint);
			}
			((XYPlot) plot).addAnnotation(anno);
        } else if (plot instanceof CategoryPlot) {
			var anno = drawArrow
									? new CategoryPointerAnnotation(text, category, value, arrowAngle)
									: new CategoryTextAnnotation(text, category, value);
			anno.setPaint(textPaint);
			anno.setFont(font);
			anno.setTextAnchor(textAnchor);
			if (drawArrow) {
				((CategoryPointerAnnotation) anno).setArrowPaint(arrowPaint);
			}
			((CategoryPlot) plot).addAnnotation(anno);
		}
	}
}
