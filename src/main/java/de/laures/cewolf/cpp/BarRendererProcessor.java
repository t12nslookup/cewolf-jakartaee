package de.laures.cewolf.cpp;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.io.Serializable;
import java.util.*;

import de.laures.cewolf.ChartPostProcessor;
import static java.awt.Color.decode;
import static java.awt.Font.BOLD;
import static java.awt.Font.ITALIC;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.*;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.renderer.category.*;
import static org.jfree.chart.renderer.category.BarRenderer.DEFAULT_ITEM_MARGIN;

/**
* A postprocessor for setting/removing the bar outline (default: false),
* the item margin for 2D and 3D bar charts (default: 0.2%), whether or not
* item labels are visible (default: no), the color to use for item labels (default: black)
* and their font size (default: 12).
* It also has an option to set custom category colors (as opposed to custom series colors,
* which is what the SeriesPaintProcessor provides); if you use this you'll want to set showlegend=false.
* <P>
* Usage:<P>
* &lt;chart:chartpostprocessor id="barRenderer"&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="outline" value="true"/&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="itemMargin" value="0.1"/&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="showItemLabels" value="true"/&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="itemLabelColor" value="#336699" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="itemLabelSize" value="14" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="categoryColors" value="true"/&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="0" value="#FFFFAA" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="1" value="#AAFFAA" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="2" value="#FFAAFF" /&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="3" value="#FFAAAA" /&gt;<BR>
* &lt;/chart:chartpostprocessor&gt;
*/

public class BarRendererProcessor implements ChartPostProcessor, Serializable
{
	static final long serialVersionUID = 6687503315061004361L;

    public void processChart (JFreeChart chart, Map<String,String> params) {
        var plot = chart.getPlot();

		if (plot instanceof CategoryPlot) {
			var catPlot = (CategoryPlot) plot;
			var ciRenderer = catPlot.getRenderer();

			if (ciRenderer instanceof BarRenderer) {
				var outline = false;
				var showItemLabels = false;
				var categoryColors = false;
				var itemMargin = DEFAULT_ITEM_MARGIN;
				var itemLabelColor = new Color(0, 0, 0);
				var fontName = "SansSerif";
				var fontSize = 12;
				var isBold = false;
				var isItalic = false;
				var str = params.get("outline");
				if (str != null)
					outline = "true".equals(str);

				str = params.get("showItemLabels");
				if (str != null)
					showItemLabels = "true".equals(str);

				str = params.get("categoryColors");
				if (str != null)
					categoryColors = "true".equals(str);

				str = params.get("itemLabelColor");
				if (str != null && str.trim().length() > 0) {
					try {
						itemLabelColor = decode(str);
					} catch (NumberFormatException nfex) { }
				}

				str = params.get("itemMargin");
				if (str != null && str.trim().length() > 0) {
					try {
						itemMargin = parseDouble(str);
					} catch (NumberFormatException nfex) { }
				}

				str = params.get("fontname");
				if (str != null && str.trim().length() > 0)
					fontName = str.trim();

				str = params.get("bold");
				if (str != null)
					isBold = "true".equals(str.toLowerCase());

				str = params.get("italic");
				if (str != null)
					isItalic = "true".equals(str.toLowerCase());

				str = params.get("itemLabelSize");
				if (str != null && str.trim().length() > 0) {
					try {
						fontSize = parseInt(str);
						if (fontSize < 4)
							fontSize = 12;
					} catch (NumberFormatException nfex) { }
				}

				if (categoryColors) {
					List<Paint> paints = new ArrayList<>();
					for (var i = 0; ; i++) {
						var colorStr = params.get(valueOf(i));
						if (colorStr == null)
							break;
						paints.add(decode(colorStr));
					}

					/* need to do most specific first! */
					if (ciRenderer instanceof BarRenderer3D) {
						catPlot.setRenderer(new CustomBarRenderer3D(paints));
					} else {
						catPlot.setRenderer(new CustomBarRenderer(paints));
					}

					ciRenderer = catPlot.getRenderer();
				}

				var renderer = (BarRenderer) ciRenderer;
				renderer.setDrawBarOutline(outline);
				renderer.setItemMargin(itemMargin);

				if (showItemLabels) {
					renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
					renderer.setBaseItemLabelsVisible(true);
					renderer.setBaseItemLabelPaint(itemLabelColor);
					var font = new Font(fontName,
										(isBold ? BOLD : 0) + (isItalic ? ITALIC : 0),
										fontSize);
					renderer.setBaseItemLabelFont(font);
				}
				//ItemLabelPosition itemlabelposition
				//	= new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER, TextAnchor.CENTER, 0.0);
				//renderer.setBasePositiveItemLabelPosition(itemlabelposition);
			}
		}
	}

    /**
     * A custom renderer that returns a different color for each item in a single series.
     */
    private static class CustomBarRenderer extends BarRenderer {

		static final long serialVersionUID = 2451764538621611708L;

        /** The colors. */
        private List colors;

        /**
         * Creates a new renderer.
         *
         * @param colors  the colors.
         */
        public CustomBarRenderer (List colors) {
            this.colors = colors;
        }

        /**
         * Returns the paint for an item. Overrides the default behaviour inherited from AbstractSeriesRenderer.
         *
         * @param row  the series.
         * @param column  the category.
         *
         * @return The item color.
         */
        public Paint getItemPaint (int row, int column) {
            return (Paint) colors.get(column % colors.size());
        }

		public boolean equals (Object obj) {
			if (obj == this) {
				return true;
			}
			if (!(obj instanceof CustomBarRenderer)) {
				return false;
			}
			var that = (CustomBarRenderer) obj;
			if (!super.equals(obj)) {
				return false;
			}
			if (!this.colors.equals(that.colors)) {
				return false;
			}
			return true;
		}

		public int hashCode() {
			assert false : "hashCode not designed";
			return 42; // any arbitrary constant will do 
		}
    }

    /**
     * A custom renderer that returns a different color for each item in a single series.
     */
    private static class CustomBarRenderer3D extends BarRenderer3D {

		static final long serialVersionUID = 2674255384600916413L;

        /** The colors. */
        private List colors;

        /**
         * Creates a new renderer.
         *
         * @param colors  the colors.
         */
        public CustomBarRenderer3D (List colors) {
            this.colors = colors;
        }

        /**
         * Returns the paint for an item. Overrides the default behaviour inherited from AbstractSeriesRenderer.
         *
         * @param row  the series.
         * @param column  the category.
         *
         * @return The item color.
         */
        public Paint getItemPaint (int row, int column) {
            return (Paint) colors.get(column % colors.size());
        }

		public boolean equals (Object obj) {
			if (obj == this) {
				return true;
			}
			if (!(obj instanceof CustomBarRenderer3D)) {
				return false;
			}
			var that = (CustomBarRenderer3D) obj;
			if (!super.equals(obj)) {
				return false;
			}
			if (!this.colors.equals(that.colors)) {
				return false;
			}
			return true;
		}

		public int hashCode() {
			assert false : "hashCode not designed";
			return 42; // any arbitrary constant will do 
		}
    }
}

