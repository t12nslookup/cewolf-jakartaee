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
 * either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package de.laures.cewolf.taglib;

import java.awt.Image;
import java.awt.Paint;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.*;
import org.jfree.chart.title.LegendTitle;

import de.laures.cewolf.*;
import static de.laures.cewolf.CewolfRenderer.cppUsed;
import de.laures.cewolf.event.ChartImageRenderListener;
import static de.laures.cewolf.taglib.TaglibConstants.ANCHOR_EAST;
import static de.laures.cewolf.taglib.TaglibConstants.ANCHOR_NORTH;
import static de.laures.cewolf.taglib.TaglibConstants.ANCHOR_SOUTH;
import static de.laures.cewolf.taglib.TaglibConstants.ANCHOR_WEST;
import de.laures.cewolf.util.RenderedImage;
import static javax.imageio.ImageIO.read;

import org.apache.commons.logging.Log;
import static org.apache.commons.logging.LogFactory.getLog;
import static org.jfree.chart.axis.NumberAxis.createIntegerTickUnits;
import static org.jfree.ui.RectangleEdge.BOTTOM;
import static org.jfree.ui.RectangleEdge.LEFT;
import static org.jfree.ui.RectangleEdge.RIGHT;
import static org.jfree.ui.RectangleEdge.TOP;

/**
 * Serializable implementaton of a ChartDefinition.
 * @author glaures
 * @see de.laures.cewolf.ChartHolder
 */
public abstract class AbstractChartDefinition
		implements ChartHolder, Serializable, ChartImageRenderListener {

    private static final Log log = getLog(AbstractChartDefinition.class);

    protected String title;
	protected String xAxisLabel, yAxisLabel;
	protected String type;
    protected boolean xAxisInteger = false, yAxisInteger = false;
    protected boolean xTickMarksVisible = true, yTickMarksVisible = true;
    protected boolean xTickLabelsVisible = true, yTickLabelsVisible = true;
    protected boolean borderVisible = false;
    protected boolean plotBorderVisible = true;

    private boolean antialias = true;
    private String background;
    private float backgroundImageAlpha = 1.0f;
    private Paint backgroundPaint, plotBackgroundPaint, borderPaint, plotBorderPaint;

    private int legendAnchor = ANCHOR_SOUTH;
    protected boolean showLegend = true;

    private List<ChartPostProcessor> postProcessors = new ArrayList<>();
    private List<Map<String,String>> postProcessorsParams = new ArrayList<>();

    private JFreeChart chart;
	private String webRootDir;

	protected abstract JFreeChart produceChart() throws DatasetProduceException, ChartValidationException;

	public void setWebRootDir (String webRootDir) {
		this.webRootDir = webRootDir;
	}

    //gets first legend in the list
    public LegendTitle getLegend()
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
    public void removeLegend()
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
     * This method triggers the dataset and chart production. It is only
     * from outside if there is no cached image available in the the image cache.
     */
    public JFreeChart getChart() throws DatasetProduceException, ChartValidationException, PostProcessingException {
        if (chart == null) {
            chart = produceChart();
            chart.setAntiAlias(antialias);

            if (background != null) {
				try {
					// UCD: somewhere 'background' is prepended with the webRootDir before it gets here
					// not sure if that used to be the case; I don't recall ever touching this
					//Image image = ImageIO.read(new File(webRootDir + background));
					Image image = read(new File(background));
					chart.setBackgroundImage(image);
				} catch (IOException ioex) {
					log.error("AbstractChartDefinition.getChart: "+ioex.getMessage());
				}
				chart.setBackgroundImageAlpha(backgroundImageAlpha);
            }

            if (backgroundPaint != null) {
                chart.setBackgroundPaint(backgroundPaint);
            }

            if (plotBackgroundPaint != null) {
                chart.getPlot().setBackgroundPaint(plotBackgroundPaint);
            }

            if (borderPaint != null) {
                chart.setBorderPaint(borderPaint);
            }

            if (plotBorderPaint != null) {
                chart.getPlot().setOutlinePaint(plotBorderPaint);
            }

			chart.setBorderVisible(borderVisible);;
			chart.getPlot().setOutlineVisible(plotBorderVisible);;

            if (showLegend) {
                var legend = this.getLegend();
                switch (legendAnchor) 
                {
                    case ANCHOR_NORTH :
                        legend.setPosition(TOP);
                        break;
                    case ANCHOR_WEST :
						legend.setPosition(LEFT);
                        break;
                    case ANCHOR_EAST :
						legend.setPosition(RIGHT);
                        break;
                    default :
						legend.setPosition(BOTTOM);
                }
            } else {
              this.removeLegend();
            }

			var plot = chart.getPlot();
			if (plot instanceof CategoryPlot) {
				var cplot = (CategoryPlot) plot;

				for (var i=0; i<cplot.getRangeAxisCount(); i++) {
					var axis = cplot.getRangeAxis(i);
					if (axis instanceof NumberAxis) {
						if (yAxisInteger)
							((NumberAxis) axis).setStandardTickUnits(createIntegerTickUnits());
					}
					if (axis != null) {
						axis.setTickMarksVisible(yTickMarksVisible);
						axis.setTickLabelsVisible(yTickLabelsVisible);
					}
				}

				for (var i=0; i<cplot.getDomainAxisCount(); i++) {
					cplot.getDomainAxis(i).setTickMarksVisible(xTickMarksVisible);
					cplot.getDomainAxis(i).setTickLabelsVisible(xTickLabelsVisible);
				}
			} else if (plot instanceof FastScatterPlot) {
				var fsplot = (FastScatterPlot) plot;
				var axis = fsplot.getDomainAxis();
				if (axis instanceof NumberAxis) {
					if (xAxisInteger)
						((NumberAxis) axis).setStandardTickUnits(createIntegerTickUnits());
				}
				if (axis != null) {
					axis.setTickMarksVisible(xTickMarksVisible);
					axis.setTickLabelsVisible(xTickLabelsVisible);
				}

				axis = fsplot.getRangeAxis();
				if (axis instanceof NumberAxis) {
					if (yAxisInteger)
						((NumberAxis) axis).setStandardTickUnits(createIntegerTickUnits());
				}
				if (axis != null) {
					axis.setTickMarksVisible(yTickMarksVisible);
				}
			} else if (plot instanceof ThermometerPlot) {
				var axis = ((ThermometerPlot) plot).getRangeAxis();
				if (axis instanceof NumberAxis) {
					if (yAxisInteger)
						((NumberAxis) axis).setStandardTickUnits(createIntegerTickUnits());
				}
				if (axis != null) {
					axis.setTickMarksVisible(yTickMarksVisible);
					axis.setTickLabelsVisible(yTickLabelsVisible);
				}
			} else if (plot instanceof XYPlot) {
				var xyplot = (XYPlot) plot;

				for (var i=0; i<xyplot.getRangeAxisCount(); i++) {
					var axis = xyplot.getRangeAxis(i);
					if (axis instanceof NumberAxis) {
						if (yAxisInteger)
							((NumberAxis) axis).setStandardTickUnits(createIntegerTickUnits());
					}
					if (axis != null) {
						axis.setTickMarksVisible(yTickMarksVisible);
						axis.setTickLabelsVisible(yTickLabelsVisible);
					}
				}

				for (var i=0; i<xyplot.getDomainAxisCount(); i++) {
					var axis = xyplot.getDomainAxis(i);
					if (axis instanceof NumberAxis) {
						if (xAxisInteger)
							((NumberAxis) axis).setStandardTickUnits(createIntegerTickUnits());
					}
					if (axis != null) {
						axis.setTickMarksVisible(xTickMarksVisible);
						axis.setTickLabelsVisible(xTickLabelsVisible);
					}
				}
			}

            // postProcessing
			var i = 0;
			for (var cpp : postProcessors) {
				try {
					cppUsed(cpp);
					cpp.processChart(chart, postProcessorsParams.get(i));
					i++;
				} catch (Throwable t) {
					t.printStackTrace();
					throw new PostProcessingException(t.getClass().getName() + " raised by post processor '" +
							cpp + "'.\nPost processing of this post processor " + "has been ignored.");
				}
			}
        }
        return chart;
    }

    /**
     * Sets the antialias.
     * @param antialias The antialias to set
     */
    public void setAntialias (boolean antialias) {
        this.antialias = antialias;
    }

    /**
     * Sets the background.
     * @param background The background to set
     */
    public void setBackground (String background) {
        this.background = background;
    }

    /**
     * Sets the backgroundImageAlpha.
     * @param backgroundImageAlpha The backgroundImageAlpha to set
     */
    public void setBackgroundImageAlpha (float backgroundImageAlpha) {
        this.backgroundImageAlpha = backgroundImageAlpha;
    }

    /**
     * Sets the legendAnchor.
     * @param legendAnchor The legendAnchor to set
     */
    public void setLegendAnchor (int legendAnchor) {
        this.legendAnchor = legendAnchor;
    }

    /**
     * Sets the background paint.
     * @param paint The background paint to set
     */
    public void setBackgroundPaint (Paint paint) {
        this.backgroundPaint = paint;
    }

    /**
     * Sets the plot's background paint.
     * @param paint The plot's background paint to set
     */
    public void setPlotBackgroundPaint (Paint paint) {
        this.plotBackgroundPaint = paint;
    }

    /**
     * Sets the showLegend.
     * @param showLegend The showLegend to set
     */
    public void setShowLegend (boolean showLegend) {
        this.showLegend = showLegend;
    }

    /**
     * Sets the title.
     * @param title The title to set
     */
    public void setTitle (String title) {
        this.title = title;
    }

    /**
     * Sets the type.
     * @param type The type to set
     */
    public void setType (String type) {
        this.type = type;
    }

    /**
     * Sets the xAxisLabel.
     * @param xAxisLabel The xAxisLabel to set
     */
    public void setXAxisLabel (String xAxisLabel) {
        this.xAxisLabel = xAxisLabel;
    }

    /**
     * Sets the yAxisLabel.
     * @param yAxisLabel The yAxisLabel to set
     */
    public void setYAxisLabel (String yAxisLabel) {
        this.yAxisLabel = yAxisLabel;
    }

    /**
     * Whether the domain (X) axis should show integer values only.
     */
    public void setXaxisinteger (boolean xAxisInteger) {
        this.xAxisInteger = xAxisInteger;
    }

    /**
     * Whether the range (Y) axis should show integer values only.
     */
    public void setYaxisinteger (boolean yAxisInteger) {
        this.yAxisInteger = yAxisInteger;
    }

    /**
     * Whether the domain (X) axis should show any tick marks.
     */
    public void setXtickmarksvisible (boolean xTickMarksVisible) {
        this.xTickMarksVisible = xTickMarksVisible;
    }

    /**
     * Whether the range (Y) axis should show any tick marks.
     */
    public void setYtickmarksvisible (boolean yTickMarksVisible) {
        this.yTickMarksVisible = yTickMarksVisible;
    }

    /**
     * Whether the domain (X) axis should show any tick labels.
     */
    public void setXticklabelsvisible (boolean xTickLabelsVisible) {
        this.xTickLabelsVisible = xTickLabelsVisible;
    }

    /**
     * Whether the range (Y) axis should show any tick labels.
     */
    public void setYticklabelsvisible (boolean yTickLabelsVisible) {
        this.yTickLabelsVisible = yTickLabelsVisible;
    }

    /**
     * Whether the chart is drawn with a border.
     */
    public void setBorderVisible (boolean borderVisible) {
        this.borderVisible = borderVisible;
    }

    /**
     * Whether the plot is drawn with a border.
     */
    public void setPlotBorderVisible (boolean plotBorderVisible) {
        this.plotBorderVisible = plotBorderVisible;
    }

    /**
     * Sets the chart border paint.
     */
    public void setBorderPaint (Paint paint) {
        this.borderPaint = paint;
    }

    /**
     * Sets the plot border paint.
     */
    public void setPlotBorderPaint (Paint paint) {
        this.plotBorderPaint = paint;
    }

    public void addPostProcessor (ChartPostProcessor cpp, Map<String,String> params) {
        postProcessors.add(cpp);
        postProcessorsParams.add(params);
    }

	/**
	 * Callback right after a new image gets rendered.
	 * Implemented so if postprocessors implement the ImageRenderListener interface then they will be called back also
	 * 
	 * @param renderedImage The fresh image just got rendered
	 */
	public void onImageRendered (RenderedImage renderedImage) {
		// if the postprocessor implements ImageRenderListener interface call it!
		for (var cpp : postProcessors) {
            if (cpp instanceof ChartImageRenderListener) {
            	((ChartImageRenderListener) cpp).onImageRendered(renderedImage);
            }
        }
	}
}
