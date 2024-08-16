package de.laures.cewolf.cpp;

import java.awt.Color;
import java.awt.Font;
import java.io.Serializable;
import java.util.Map;

import de.laures.cewolf.ChartPostProcessor;
import static java.awt.Color.decode;
import static java.awt.Font.BOLD;
import static java.awt.Font.ITALIC;
import static java.lang.Integer.parseInt;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.title.TextTitle;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;

import org.apache.commons.logging.Log;
import static org.apache.commons.logging.LogFactory.getLog;
import static org.jfree.ui.HorizontalAlignment.CENTER;
import static org.jfree.ui.RectangleEdge.BOTTOM;
import static org.jfree.ui.RectangleEdge.TOP;

/**
* A postprocessor for setting a (sub)title on a chart. It supports the following parameters:
* <BR><b>type</b> title/subtitle; default is title
* <BR><b>title</b> no default, title won't be set if empty
* <BR><b>fontname</b> optional; default SansSerif
* <BR><b>fontsize</b> optional; default is 18
* <BR><b>paint</b> optional; default #000000 (i.e., black)
* <BR><b>backgroundpaint</b> optional; default #FFFFFF (i.e., white)
* <BR><b>bold</b> true/false; optional; default true
* <BR><b>italic</b> true/false; optional; default false
* <BR><b>position</b> top/left/bottom/right; optional; default top - where on the plot to show the title
* <BR><b>halign</b> left/center/right; optional; default center - where to put the title
* <BR><b>talign</b> left/center/right; optional; default center - how to align multiline text (separate lines by commas)
* <P>
* Usage:<P>
* &lt;chart:chartpostprocessor id="subTitle"&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="type" value="title" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="title" value="My Important Title" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="fontname" value="Serif" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="fontsize" value="24" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="paint" value="#FF8800" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="backgroundpaint" value="#0088FF" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="bold" value="false" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="italic" value="true" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="talign" value="center" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="halign" value="left" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="position" value="top" /&gt;<BR>
* &lt;/chart:chartpostprocessor&gt;
* <P>
* Based on the ExtraTitleEnhancer class from the cewolfexample web app.
*/

/**
 * Renderer for ChartImageDefinitions.
 *
 * @author glaures
 * @author tbardzil
 * @see    de.laures.cewolf.ChartImage
 */

public class TitleEnhancer implements ChartPostProcessor, Serializable
{
	static final long serialVersionUID = 591686288142936677L;

    private static final Log log = getLog(TitleEnhancer.class);

    public void processChart (JFreeChart chart, Map<String,String> params) {
		var title = new StringBuilder();
		var type = "title";
		var fontName = "SansSerif";
		Color paint = null;
		Color backgroundPaint = null;
		var fontSize = 18;
		var isBold = true;
		var isItalic = false;
		HorizontalAlignment tAlign = null, hAlign = null;
		RectangleEdge position = null; 

		var typeParam = params.get("type");
		if (typeParam != null && typeParam.trim().length() > 0)
			type = typeParam.trim();

		// change on the title entry will provide a way to have a
		// subtitle with multiple lines on the rendered image.
		var titleParam = params.get("title");
		if (titleParam != null && titleParam.trim().length() > 0) {
			for (var str : titleParam.split(",")) {
				title.append(str.trim()).append("\n");
			}
		} 

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

		// just uncommented the previous code to enable text align
		var tAlignParam = params.get("talign");
		if (tAlignParam != null) {
			if ("left".equalsIgnoreCase(tAlignParam)) {
				tAlign = HorizontalAlignment.LEFT;
			} else if ("right".equalsIgnoreCase(tAlignParam)) {
				tAlign = HorizontalAlignment.RIGHT;
			} else if ("center".equalsIgnoreCase(tAlignParam)) {
				tAlign = CENTER;
			}
		}

		// allow users to configure the horizontal alignment of the object
		var hAlignParam = params.get("halign");
		if (hAlignParam != null) {
			if ("left".equalsIgnoreCase(hAlignParam)) {
				hAlign = HorizontalAlignment.LEFT;
			} else if ("right".equalsIgnoreCase(hAlignParam)) {
				hAlign = HorizontalAlignment.RIGHT;
			} else if ("center".equalsIgnoreCase(hAlignParam)) {
				hAlign = CENTER;
			}
		} 

		// allow users to configure the position of the object
		var positionParam = params.get("position");
		if (positionParam != null) {
			if ("right".equalsIgnoreCase(positionParam)) {
				position = RectangleEdge.RIGHT;
			} else if ("bottom".equalsIgnoreCase(positionParam)) {
				position = BOTTOM;
			} else if ("left".equalsIgnoreCase(positionParam)) {
				position = RectangleEdge.LEFT;
			} else if ("top".equalsIgnoreCase(positionParam)) {
				position = TOP;
			}
		} 

		TextTitle tt = null;
		if (null == type) {
                    log.error("type='"+type+"' - now what?");
                    return;
                } else switch (type) {
            case "title":
                tt = chart.getTitle();
                if (tt == null) {
                    tt = new TextTitle(title.toString());
                    chart.setTitle(tt);
                }
                break;
            case "subtitle":
                // add subtitle below all existing ones
                tt = new TextTitle(title.toString());
                var subTitles = chart.getSubtitles();
                chart.addSubtitle(subTitles.size(), tt);
                break;
            default:
                log.error("type='"+type+"' - now what?");
                return;
        }
		var font = new Font(fontName,
							(isBold ? BOLD : 0) + (isItalic ? ITALIC : 0),
							fontSize);
		tt.setFont(font);
		if (paint != null)
			tt.setPaint(paint);
		if (backgroundPaint != null)
			tt.setBackgroundPaint(backgroundPaint);
		if (tAlign != null)
			tt.setTextAlignment(tAlign);
		if (hAlign != null)
			tt.setHorizontalAlignment(hAlign); 
		if (position != null)
			tt.setPosition(position); 
	}
}
