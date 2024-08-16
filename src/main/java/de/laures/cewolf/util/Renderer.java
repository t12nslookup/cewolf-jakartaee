/* ================================================================
 * Cewolf : Chart enabling Web Objects Framework
 * ================================================================
 *
 * Project Info:  http://cewolf.sourceforge.net
 * Project Lead:  Guido Laures (guido@laures.de);
 *
 * (C) Copyright 2002, by Guido Laures
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package de.laures.cewolf.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.RectangleConstraint;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.title.LegendTitle;
import org.jfree.ui.RectangleEdge;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import javax.imageio.*;
//import com.sun.image.codec.jpeg.JPEGCodec;
//import com.sun.image.codec.jpeg.JPEGEncodeParam;
//import com.sun.image.codec.jpeg.JPEGImageEncoder;

import de.laures.cewolf.CewolfException;
import de.laures.cewolf.ChartImage;
import static de.laures.cewolf.ChartImage.IMG_TYPE_CHART;
import static de.laures.cewolf.ChartImage.IMG_TYPE_LEGEND;
import de.laures.cewolf.ChartRenderingException;
import de.laures.cewolf.ConfigurationException;
import de.laures.cewolf.WebConstants;
import static de.laures.cewolf.util.RenderingHelper.createImage;
import static java.awt.Color.white;
import static javax.imageio.ImageIO.createImageOutputStream;
import static javax.imageio.ImageIO.getImageWritersBySuffix;
import static javax.imageio.ImageWriteParam.MODE_EXPLICIT;
import static org.apache.batik.dom.GenericDOMImplementation.getDOMImplementation;
import static org.apache.batik.svggen.SVGGeneratorContext.createDefault;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import static org.apache.commons.logging.LogFactory.getLog;
import static org.jfree.chart.ChartUtilities.writeChartAsJPEG;
import static org.jfree.chart.ChartUtilities.writeChartAsPNG;
import static org.jfree.ui.RectangleEdge.BOTTOM;

/**
 * Renderer for ChartImageDefinitions.
 *
 * @author glaures
 * @author tbardzil
 * @see    de.laures.cewolf.ChartImage
 */
public class Renderer implements WebConstants {

    private static final Log log = getLog(Renderer.class);

	/** Creates a new instance of Renderer */
	private Renderer() { }

	/**
	 * Renders a chart image
	 *
	 * @param  cd                  the chart to render
	 * @return                     the rendered image
	 * @throws CewolfException
	 */
	public static RenderedImage render(ChartImage cd, Object chart) throws CewolfException {
		switch (cd.getType()) {
			case IMG_TYPE_CHART :
				return renderChart(cd, chart);
			case IMG_TYPE_LEGEND :
				return renderLegend(cd, chart);
			default :
				throw new ConfigurationException(cd.getType() + " is not a supported image type");
		}
	}

	/**
	 * Renders a chart
	 * @param cd the chart image to be rendered
	 * @return the rendered image
	 * @throws CewolfException
	 */
	private static RenderedImage renderChart(ChartImage cd, Object chart) throws CewolfException {
		try {
			final var baos = new ByteArrayOutputStream();
			final var info = new ChartRenderingInfo(new StandardEntityCollection());
			final var mimeType = cd.getMimeType();
			if (null == mimeType) {
                            throw new RenderingException("Mime type " + mimeType + " is unsupported.");
                        } else switch (mimeType) {
                case MIME_PNG:
                    handlePNG(baos, (JFreeChart)chart, cd.getWidth(), cd.getHeight(), info);
                    break;
                case MIME_JPEG:
                    handleJPEG(baos, (JFreeChart)chart, cd.getWidth(), cd.getHeight(), info);
                    break;
                case MIME_SVG:
                    handleSVG(baos, (JFreeChart)chart, cd.getWidth(), cd.getHeight());
                    break;
                default:
                    throw new RenderingException("Mime type " + mimeType + " is unsupported.");
            }
			baos.close();
			return new RenderedImage(baos.toByteArray(), mimeType, info);
		} catch (IOException ioex) {
			log.error("Renderer.renderChart: "+ioex.getMessage());
			throw new ChartRenderingException(ioex.getMessage(), ioex);
		}
	}

	/**
	 * Handles rendering a chart as a PNG.
	 *
	 * @param  baos
	 * @param  chart
	 * @param  width
	 * @param  height
	 * @param  info
	 * @throws IOException
	 */
	private static void handlePNG (ByteArrayOutputStream baos, JFreeChart chart,
			int width, int height, ChartRenderingInfo info)
		throws IOException {
			writeChartAsPNG(baos, chart, width, height, info);
	}

	/**
	 * Handles rendering a chart as a JPEG.
	 *
	 * @param  baos
	 * @param  chart
	 * @param  width
	 * @param  height
	 * @param  info
	 * @throws IOException
	 */
	private static void handleJPEG (ByteArrayOutputStream baos, JFreeChart chart,
			int width, int height, ChartRenderingInfo info)
		throws IOException {
			writeChartAsJPEG(baos, chart, width, height, info);
	}

	/**
	 * Handles rendering a chart as a SVG. 
	 *
	 * @param  baos
	 * @param  chart
	 * @param  width
	 * @param  height
	 * @throws IOException
	 */
	private static void handleSVG (ByteArrayOutputStream baos, JFreeChart chart, int width, int height)
		throws IOException {
        try (var writer = new OutputStreamWriter(baos, "UTF-8")) {
            var domImpl = getDOMImplementation();
            var document = domImpl.createDocument("cewolf-svg", "svg", null);
            var ctx = createDefault(document);
//		ctx.setComment("Generated by Cewolf using JFreeChart and Apache Batik SVG Generator");
            var svgGenerator = new SVGGraphics2D(ctx, false);
            svgGenerator.setSVGCanvasSize(new Dimension(width, height));
            chart.draw(svgGenerator, new Rectangle2D.Double(0, 0, width, height), null);
            svgGenerator.stream(writer, false);
        }
	}

  //gets first legend in the list
  public static LegendTitle getLegend(JFreeChart chart)
  {
    //i need to find the legend now.
    LegendTitle legend = null;
    var subTitles = chart.getSubtitles();
    var iter = subTitles.iterator();
    while (iter.hasNext())
    {
      var o = iter.next();
      if (o instanceof LegendTitle)
      {
        legend = (LegendTitle) o;
        break;
      }
    }
    return legend;
  }

  //removes first legend in the list
  public static void removeLegend(JFreeChart chart)
  {
    var subTitles = chart.getSubtitles();
    var iter = subTitles.iterator();
    while (iter.hasNext())
    {
      var o = iter.next();
      if (o instanceof LegendTitle)
      {
        iter.remove();
        break;
      }
    }
  }
  
	/**
	 * Renders a legend
	 * @param cd the chart iamge to be rendred
	 * @return the rendered image
	 * @throws CewolfException
	 */
	private static RenderedImage renderLegend(ChartImage cd, Object c) throws CewolfException {
		try {
		    var chart = (JFreeChart) c;
			final var width = cd.getWidth();
			final var height = cd.getHeight();
			var legend = getLegend(chart);
			var haslegend = true;

			// with JFreeChart v0.9.20, the only way to get a valid legend,
			// is either to retrieve it from the chart or to assign a new
			// one to the chart. In the case where the chart has no legend,
			// a new one must be assigned, but just for rendering. After, we
			// have to reset the legend to null in the chart.
			if (null == legend) {
				haslegend = false;
				legend = new LegendTitle(chart.getPlot());   
			}
			legend.setPosition(BOTTOM);
			var bi = createImage(width, height);
			var g = bi.createGraphics();
			g.setColor(white);
			g.fillRect(0, 0, width, height);
			legend.arrange(g,new RectangleConstraint(width,height));
 			legend.draw(g, new Rectangle(width, height));
			var out = new ByteArrayOutputStream();
/*
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bi);
			param.setQuality(1.0f, true);
			encoder.encode(bi, param);
*/
			var writer = getImageWritersBySuffix("jpeg").next();
			writer.setOutput(createImageOutputStream(out)); 
			var iwp = writer.getDefaultWriteParam();
			iwp.setCompressionMode(MODE_EXPLICIT); 
			iwp.setCompressionQuality (1.0f); 
			writer.write(null, new IIOImage(bi, null, null), iwp); 
			writer.write(bi); 
			writer.dispose(); 

			out.close();

			// if the chart had no legend, reset it to null in order to give back the
			// chart in the state we received it.
			if (!haslegend) {
				removeLegend(chart);
			}

			return new RenderedImage(out.toByteArray(), "image/jpeg", new ChartRenderingInfo(new StandardEntityCollection()));
		} catch (IOException ioex) {
			log.error("Renderer.renderLegend: "+ioex.getMessage());
			throw new ChartRenderingException(ioex.getMessage(), ioex);
		}
	}
}
