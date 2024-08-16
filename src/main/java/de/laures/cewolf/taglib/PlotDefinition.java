/* ================================================================
 * Cewolf : Chart enabling Web Objects Framework
 * ================================================================
 *
 * Project Info:  http://cewolf.sourceforge.net
 * Project Lead:  Guido Laures (guido@laures.de);
 *
 * (C) Copyright 2002, by Guido Laures and contributers
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

import java.io.Serializable;
import java.util.Map;

import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.XYDataset;

import de.laures.cewolf.ChartValidationException;
import de.laures.cewolf.DatasetProduceException;
import de.laures.cewolf.DatasetProducer;
import static de.laures.cewolf.taglib.ChartConstants.COMBINED_XY;
import static de.laures.cewolf.taglib.ChartConstants.OVERLAY_CATEGORY;
import static de.laures.cewolf.taglib.ChartConstants.OVERLAY_XY;
import static de.laures.cewolf.taglib.PlotConstants.AREA;
import static de.laures.cewolf.taglib.PlotConstants.CANDLESTICK;
import static de.laures.cewolf.taglib.PlotConstants.HIGH_LOW;
import static de.laures.cewolf.taglib.PlotConstants.LINE;
import static de.laures.cewolf.taglib.PlotConstants.SCATTER;
import static de.laures.cewolf.taglib.PlotConstants.SHAPES_AND_LINES;
import static de.laures.cewolf.taglib.PlotConstants.SPLINE;
import static de.laures.cewolf.taglib.PlotConstants.STACKED_XY_AREA;
import static de.laures.cewolf.taglib.PlotConstants.STACKED_XY_AREA2;
import static de.laures.cewolf.taglib.PlotConstants.STEP;
import static de.laures.cewolf.taglib.PlotConstants.VERTICAL_BAR;
import static de.laures.cewolf.taglib.PlotConstants.XY_AREA;
import static de.laures.cewolf.taglib.PlotConstants.XY_LINE;
import static de.laures.cewolf.taglib.PlotConstants.XY_SHAPES_AND_LINES;
import static de.laures.cewolf.taglib.PlotConstants.XY_VERTICAL_BAR;
import static de.laures.cewolf.taglib.PlotTypes.getRenderer;
import static de.laures.cewolf.taglib.PlotTypes.getRendererIndex;
import static de.laures.cewolf.taglib.PlotTypes.typeList;

/**
 * (sub) Plot definition for combined/overlaid charts.
 * 
 * @author Chris McCann
 * @author Guido Laures
 */
public class PlotDefinition implements DataAware, Serializable {

	static final long serialVersionUID = 1789401987855998576L;

	private String type;
	private String xAxisLabel; // [tb]
	private String yAxisLabel; // [tb]
	private boolean xAxisInteger = false;
	private boolean yAxisInteger = false;

	private DataContainer dataAware = new DataContainer();
	private transient Plot plot;
	private transient DrawingSupplier drawingSupplier = null;

	public Plot getPlot(int chartType) throws DatasetProduceException, ChartValidationException {
		if (plot == null) {
			var rendererIndex = getRendererIndex(type);
			var data = getDataset();
			var rend = getRenderer(rendererIndex);
			if (chartType == OVERLAY_XY || chartType == COMBINED_XY) {
				switch (rendererIndex) {
					case XY_AREA :
					case XY_LINE :
					case XY_SHAPES_AND_LINES :
					case SCATTER :
					case STEP :
					case SPLINE :
					case STACKED_XY_AREA :
					case STACKED_XY_AREA2 :
						check(data, XYDataset.class, rendererIndex);
						plot = new XYPlot((XYDataset) data, null, null, (XYItemRenderer) rend);
						break;
					case XY_VERTICAL_BAR :
						check(data, IntervalXYDataset.class, rendererIndex);
						plot = new XYPlot((IntervalXYDataset) data, null, null, (XYItemRenderer) rend);
						break;
					case CANDLESTICK :
					case HIGH_LOW :
						check(data, OHLCDataset.class, rendererIndex);
						plot = new XYPlot((OHLCDataset) data, null, null, (XYItemRenderer) rend);
						break;
					default :
						throw new AttributeValidationException(chartType + ".type", type);
				}
			} else if (chartType == OVERLAY_CATEGORY) {
				switch (rendererIndex) {
					case AREA :
					case VERTICAL_BAR :
					case LINE :
					case SHAPES_AND_LINES :
						check(data, CategoryDataset.class, rendererIndex);
						plot =
							new CategoryPlot(
								(CategoryDataset) data,
								null,
								null,
								(CategoryItemRenderer) rend);
						break;
					default :
						throw new AttributeValidationException(chartType + ".type", type);
				}
			}
		}
		plot.setDrawingSupplier(drawingSupplier);
		return plot;
	}

	public Dataset getDataset() throws DatasetProduceException {
		return dataAware.getDataset();
	}

	/**
	 * Gets the y-axis label. [tb]
	 *
	 * @return the y-axis label.
	 */
	public String getXaxislabel() {
		return xAxisLabel;
	}

	/**
	 * Sets the x-axis label [tb]
	 *
	 * @return the x-axis label
	 */
	public String getYaxislabel() {
		return yAxisLabel;
	}

	/**
	 * Sets the x-axis label [tb]
	 *
	 * @param xAxisLabel New value of property xAxisLabel.
	 */
	public void setXaxislabel(String xAxisLabel) {
		this.xAxisLabel = xAxisLabel;
	}

	/**
	 * Sets the y-axis label [tb]
	 *
	 * @param yAxisLabel New value of property yAxisLabel.
	 */
	public void setYaxislabel(String yAxisLabel) {
		this.yAxisLabel = yAxisLabel;
	}

    /**
     * Setter for property xAxisinteger. 
     * @param xAxisInteger New value of property aAxisInteger.
     */
    public void setXaxisinteger (boolean xAxisInteger) {
        this.xAxisInteger = xAxisInteger;
    }

    /**
     * Getter for property xAxisinteger. 
	 *
	 * @return the xAxisinteger value
     */
    public boolean isXaxisinteger () {
        return xAxisInteger;
    }

    /**
     * Setter for property yAxisinteger. 
     * @param yAxisInteger New value of property yAxisInteger.
     */
    public void setYaxisinteger (boolean yAxisInteger) {
        this.yAxisInteger = yAxisInteger;
    }

    /**
     * Getter for property yAxisInteger. 
	 *
	 * @return the yAxisInteger value
     */
    public boolean isYaxisinteger () {
        return yAxisInteger;
    }

	/**
	 * Sets the type.
	 * @param type The type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Gets the type.
	 * @return type of plot as a String
	 */
	public String getType() {
		return this.type;
	}

	public void check(Dataset data, Class clazz, int plotType) throws IncompatibleDatasetException {
		if (!clazz.isInstance(data)) {
			throw new IncompatibleDatasetException(
				"Plots of type " + typeList.get(plotType) + " need a dataset of type " + clazz.getName());
		}
	}

	public void setDataProductionConfig(DatasetProducer dsp, Map<String,Object> params, boolean useCache) {
		dataAware.setDataProductionConfig(dsp, params, useCache);
	}

	/**
	 * Sets the drawingSupplier.
	 * @param drawingSupplier The drawingSupplier to set
	 */
	public void setDrawingSupplier(DrawingSupplier drawingSupplier) {
		this.drawingSupplier = drawingSupplier;
	}

}
