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

package de.laures.cewolf.taglib;

import java.awt.Font;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.*;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.*;
import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.chart.plot.dial.DialPointer;
import org.jfree.chart.plot.dial.StandardDialScale;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.general.*;
import org.jfree.data.time.*;
import org.jfree.data.xy.*;

import static de.laures.cewolf.CewolfRenderer.chartUsed;
import de.laures.cewolf.ChartValidationException;
import de.laures.cewolf.DatasetProduceException;

// import these especially as some of the class names clash with the JFree/JCommon class names from which they're derived
import de.laures.cewolf.jfree.ThermometerPlot;
import de.laures.cewolf.jfree.WaferMapPlot;
import de.laures.cewolf.jfree.WaferMapRenderer;
import de.laures.cewolf.jfree.XYBlockRenderer;
import de.laures.cewolf.jfree.XYConditionRenderer;
import de.laures.cewolf.jfree.XYSplineRenderer;
import static de.laures.cewolf.taglib.AxisFactory.getInstance;
import static de.laures.cewolf.taglib.ChartTypes.typeList;
import static de.laures.cewolf.taglib.PlotTypes.getRenderer;
import static de.laures.cewolf.taglib.PlotTypes.getRendererIndex;
import static java.awt.Font.BOLD;
import static org.jfree.chart.ChartFactory.createAreaChart;
import static org.jfree.chart.ChartFactory.createBarChart;
import static org.jfree.chart.ChartFactory.createBarChart3D;
import static org.jfree.chart.ChartFactory.createBubbleChart;
import static org.jfree.chart.ChartFactory.createCandlestickChart;
import static org.jfree.chart.ChartFactory.createGanttChart;
import static org.jfree.chart.ChartFactory.createHighLowChart;
import static org.jfree.chart.ChartFactory.createHistogram;
import static org.jfree.chart.ChartFactory.createLineChart;
import static org.jfree.chart.ChartFactory.createPieChart;
import static org.jfree.chart.ChartFactory.createPieChart3D;
import static org.jfree.chart.ChartFactory.createStackedAreaChart;
import static org.jfree.chart.ChartFactory.createStackedBarChart;
import static org.jfree.chart.ChartFactory.createStackedBarChart3D;
import static org.jfree.chart.ChartFactory.createTimeSeriesChart;
import static org.jfree.chart.ChartFactory.createWindPlot;
import static org.jfree.chart.ChartFactory.createXYAreaChart;
import static org.jfree.chart.ChartFactory.createXYBarChart;
import static org.jfree.chart.ChartFactory.getChartTheme;
import static org.jfree.chart.JFreeChart.DEFAULT_TITLE_FONT;
import static org.jfree.chart.axis.NumberAxis.createIntegerTickUnits;
import static org.jfree.chart.plot.PlotOrientation.HORIZONTAL;
import static org.jfree.chart.plot.PlotOrientation.VERTICAL;

/**
 * Chart factory creates Jfreechart instances. To add a new factory use the
 * <code>CewolfChartFactory.registerFactory(new CewolfChartFactory() {...});</code> method.
 *
 * @author  Guido Laures
 */

public abstract class CewolfChartFactory implements ChartConstants, AxisConstants, LayoutConstants {

    // chart type string
  protected String chartType;

  // map contains registered factories, (String) chartType->CewolfChartFactory mappings
  private static Map<String,CewolfChartFactory> factories = new HashMap<>();

    /** Creates a new instance of ChartFactory */
  protected CewolfChartFactory(String chartType) {
      this.chartType = chartType;
  }

  /**
   * Callback when the chart instance to be created.
     * @param title The title of chart
     * @param xAxisLabel label on x axis
     * @param yAxisLabel label on y axis
     * @param data The dataset to create chart for
     * @return The newly created JFreeChart instance
     *
     * @throws IncompatibleDatasetException If the incoming data is not compatible with this factory
     */
    public abstract JFreeChart getChartInstance (String title, String xAxisLabel, String yAxisLabel, Dataset data)
			throws IncompatibleDatasetException;

    //////////////// static part ///////////////////////

  /**
   * Register a new chart factory instance.
   * @param factory The factory to register
   */
  public static void registerFactory (CewolfChartFactory factory) {
      factories.put(factory.chartType, factory);
  }

	private static int getChartTypeConstant (String type) {
		var res = typeList.indexOf(type.toLowerCase());
		if (res < 0) {
			throw new RuntimeException("unsupported chart type " + type);
		}
		return res;
	}

  private static int getLayoutConstant (String layout) {
    return typeList.indexOf(layout.toLowerCase());
  }
 
	public static JFreeChart getChartInstance (String chartType, String title,
					String xAxisLabel, String yAxisLabel, Dataset data, boolean showLegend)
  			throws ChartValidationException {
		// first check the dynamically registered chart types
		var factory = factories.get(chartType);
		if (factory != null) {
			// custom factory found, use it
			return factory.getChartInstance(title, xAxisLabel, yAxisLabel, data);
		}

		chartUsed(chartType);

		JFreeChart chart;

    switch (getChartTypeConstant(chartType)) {
      case XY :
        check(data, XYDataset.class, chartType);
        chart = createXYLineChart(title, xAxisLabel, yAxisLabel,
					(XYDataset) data, VERTICAL, showLegend, true, true);
        return chart;

      case PIE :
        check(data, PieDataset.class, chartType);
        chart = createPieChart(title, (PieDataset) data, showLegend, true, true);
		return chart;
      case AREA_XY :
        check(data, XYDataset.class, chartType);
        chart = createXYAreaChart(title, xAxisLabel, yAxisLabel,
					(XYDataset) data, VERTICAL, showLegend, false, false);
		return chart;

      case SCATTER :
        check(data, XYDataset.class, chartType);
        chart = createScatterPlot(title, xAxisLabel, yAxisLabel,
					(XYDataset) data, VERTICAL, showLegend, false, false);
		return chart;

      case AREA :
        check(data, CategoryDataset.class, chartType);
        chart = createAreaChart(title, xAxisLabel, yAxisLabel,
					(CategoryDataset) data, VERTICAL, showLegend, false, false);
		return chart;

      case HORIZONTAL_BAR :
        check(data, CategoryDataset.class, chartType);
        chart = createBarChart(title, xAxisLabel, yAxisLabel,
					(CategoryDataset) data, HORIZONTAL, showLegend, false, false);
		return chart;
      case HORIZONTAL_BAR_3D :
        check(data, CategoryDataset.class, chartType);
        chart = createBarChart3D(title, xAxisLabel, yAxisLabel,
					(CategoryDataset) data, HORIZONTAL, showLegend, false, false);
		return chart;

      case LINE :
        check(data, CategoryDataset.class, chartType);
        chart = createLineChart(title, xAxisLabel, yAxisLabel,
					(CategoryDataset) data, VERTICAL, showLegend, false, false);
		return chart;

      case STACKED_HORIZONTAL_BAR :
        check(data, CategoryDataset.class, chartType);
        chart = createStackedBarChart(title, xAxisLabel, yAxisLabel,
					(CategoryDataset) data, HORIZONTAL, showLegend, false, false);
		return chart;

      case STACKED_HORIZONTAL_BAR_3D :
        check(data, CategoryDataset.class, chartType);
        chart = createStackedBarChart3D(title, xAxisLabel, yAxisLabel,
					(CategoryDataset) data, HORIZONTAL, showLegend, false, false);
		return chart;

      case STACKED_VERTICAL_BAR :
        check(data, CategoryDataset.class, chartType);
        chart = createStackedBarChart(title, xAxisLabel, yAxisLabel,
					(CategoryDataset) data, VERTICAL, showLegend, false, false);
		return chart;

      case STACKED_VERTICAL_BAR_3D :
        check(data, CategoryDataset.class, chartType);
        chart = createStackedBarChart3D(title, xAxisLabel, yAxisLabel,
					(CategoryDataset) data, VERTICAL, showLegend, false, false);
		return chart;

      case VERTICAL_BAR :
        check(data, CategoryDataset.class, chartType);
        chart = createBarChart(title, xAxisLabel, yAxisLabel,
					(CategoryDataset) data, VERTICAL, showLegend, false, false);
		return chart;

      case VERTICAL_BAR_3D :
        check(data, CategoryDataset.class, chartType);
        chart = createBarChart3D(title, xAxisLabel, yAxisLabel,
					(CategoryDataset) data, VERTICAL, showLegend, false, false);
		return chart;

      case TIME_SERIES :
        check(data, XYDataset.class, chartType);
        chart = createTimeSeriesChart(title, xAxisLabel, yAxisLabel,
					(XYDataset) data, showLegend, false, false);
		return chart;

      case CANDLE_STICK :
        check(data, OHLCDataset.class, chartType);
        chart = createCandlestickChart(title, xAxisLabel, yAxisLabel, (OHLCDataset) data, true);
		return chart;

      case HIGH_LOW :
        check(data, OHLCDataset.class, chartType);
        chart = createHighLowChart(title, xAxisLabel, yAxisLabel, (OHLCDataset) data, true);
		return chart;

      case GANTT :
        check(data, IntervalCategoryDataset.class, chartType);
        chart = createGanttChart(title, xAxisLabel, yAxisLabel,
					(IntervalCategoryDataset) data, showLegend, false, false);
		return chart;

      case WIND :
        check(data, WindDataset.class, chartType);
        chart = createWindPlot(title, xAxisLabel, yAxisLabel,
					(WindDataset) data, showLegend, false, false);
		return chart;

      case VERTICAL_XY_BAR :
        check(data, IntervalXYDataset.class, chartType);
		var dateAxis = data instanceof DynamicTimeSeriesCollection || data instanceof TimePeriodValuesCollection
							|| data instanceof TimeSeriesCollection || data instanceof TimeTableXYDataset;
        chart = createXYBarChart(title, xAxisLabel, dateAxis, yAxisLabel,
					(IntervalXYDataset) data, VERTICAL, showLegend, false, false);
		return chart;

      case PIE_3D :
        check(data, PieDataset.class, chartType);
        chart = createPieChart3D(title, (PieDataset) data, showLegend, false, false);
		return chart;

      case METER :
        check(data, ValueDataset.class, chartType);
        var plot = new MeterPlot((ValueDataset) data);
        chart = new JFreeChart(title, new Font("SansSerif", BOLD, 18), plot, showLegend);
            getChartTheme().apply(chart);
        return chart;

		case DIAL :
			check(data, ValueDataset.class, chartType);
			var dplot = new DialPlot((ValueDataset) data);
			dplot.addPointer(new DialPointer.Pin());
			var scale = new StandardDialScale();
			scale.setTickLabelFont(new Font("Dialog", BOLD, 10));
			dplot.addScale(0, scale);
			chart = new JFreeChart(title, new Font("SansSerif", BOLD, 18), dplot, showLegend);
			getChartTheme().apply(chart);
			return chart;

		case THERMOMETER :
			check(data, ValueDataset.class, chartType);
			var tplot = new ThermometerPlot((ValueDataset) data);
			chart = new JFreeChart(title, new Font("SansSerif", BOLD, 18), tplot, showLegend);
			getChartTheme().apply(chart);
			return chart;

		case COMPASS :
			check(data, ValueDataset.class, chartType);
			var cplot = new CompassPlot((ValueDataset) data);
			chart = new JFreeChart(title, new Font("SansSerif", BOLD, 18), cplot, showLegend);
			getChartTheme().apply(chart);
			return chart;

      case STACKED_AREA :
        check(data, CategoryDataset.class, chartType);
        chart = createStackedAreaChart(title, xAxisLabel, yAxisLabel,
					(CategoryDataset) data, VERTICAL, showLegend, false, false);
		return chart;

      case BUBBLE :
        check(data, XYZDataset.class, chartType);
        chart = createBubbleChart(title, xAxisLabel, yAxisLabel,
					(XYZDataset) data, VERTICAL, showLegend, false, false);
		return chart;

      case SPLINE :
        check(data, XYDataset.class, chartType);
        chart = createSplineChart(title, xAxisLabel, yAxisLabel,
					(XYDataset) data, VERTICAL, showLegend, true, true);
		return chart;

	case HISTOGRAM :
		check(data, IntervalXYDataset.class, chartType);
        chart = createHistogram(title, xAxisLabel, yAxisLabel,
					(IntervalXYDataset) data, VERTICAL, true, false, false);
		return chart;

	case SPIDERWEB :
		check(data, CategoryDataset.class, chartType);
        var swplot = new SpiderWebPlot((CategoryDataset) data);
        chart = new JFreeChart(title, DEFAULT_TITLE_FONT, swplot, showLegend);
		getChartTheme().apply(chart);
		return chart;

	case WAFER :
		check(data, WaferMapDataset.class, chartType);
        var wmPlot = new WaferMapPlot((WaferMapDataset) data);
        var wmRenderer = new WaferMapRenderer();
        wmPlot.setRenderer(wmRenderer);
		// the next 3 lines should not be necessary - something's not right in the WaferMapRenderer
		for (var i=0; i<((WaferMapDataset) data).getUniqueValueCount(); i++) {
			wmRenderer.lookupSeriesPaint(i);
		}
        chart = new JFreeChart(title, DEFAULT_TITLE_FONT, wmPlot, showLegend);
		getChartTheme().apply(chart);
		return chart;

	case HEATMAP :
		check(data, XYZDataset.class, chartType);
		var xyPlot = new XYPlot((XYZDataset) data, new NumberAxis(), new NumberAxis(), new XYBlockRenderer());
        chart = new JFreeChart(title, DEFAULT_TITLE_FONT, xyPlot, showLegend);
		getChartTheme().apply(chart);
		return chart;

      default :
        throw new UnsupportedChartTypeException(chartType + " is not supported.");
    }
  }

    /**
     * Creates a spline chart (based on an {@link XYDataset}) with default settings.
     *
     * @param title  the chart title (<code>null</code> permitted).
     * @param xAxisLabel  a label for the X-axis (<code>null</code> permitted).
     * @param yAxisLabel  a label for the Y-axis (<code>null</code> permitted).
     * @param dataset  the dataset for the chart (<code>null</code> permitted).
     * @param orientation  the plot orientation (horizontal or vertical) (<code>null</code> NOT permitted).
     * @param showLegend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return The chart.
     */
    public static JFreeChart createSplineChart (String title, String xAxisLabel,
			String yAxisLabel, XYDataset dataset, PlotOrientation orientation,
			boolean showLegend, boolean tooltips, boolean urls) {

        if (orientation == null) {
            throw new IllegalArgumentException("Null 'orientation' argument.");
        }
        var xAxis = new NumberAxis(xAxisLabel);
        xAxis.setAutoRangeIncludesZero(false);
        var yAxis = new NumberAxis(yAxisLabel);
        XYItemRenderer renderer = new XYSplineRenderer(10);
        var plot = new XYPlot(dataset, xAxis, yAxis, renderer);
        plot.setOrientation(orientation);
        if (tooltips) {
            renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
        }
        if (urls) {
            renderer.setURLGenerator(new StandardXYURLGenerator());
        }

        var chart = new JFreeChart(title, DEFAULT_TITLE_FONT, plot, showLegend);
        getChartTheme().apply(chart);
        return chart;
    }

    /**
     * Creates a scatter plot with default settings.  The chart object
     * returned by this method uses an {@link XYPlot} instance as the plot,
     * with a {@link NumberAxis} for the domain axis, a  {@link NumberAxis}
     * as the range axis, and an {@link XYConditionRenderer} as the renderer.
     *
     * @param title  the chart title (<code>null</code> permitted).
     * @param xAxisLabel  a label for the X-axis (<code>null</code> permitted).
     * @param yAxisLabel  a label for the Y-axis (<code>null</code> permitted).
     * @param dataset  the dataset for the chart (<code>null</code> permitted).
     * @param orientation  the plot orientation (horizontal or vertical) (<code>null</code> NOT permitted).
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return A scatter plot.
     */
    public static JFreeChart createScatterPlot (String title, String xAxisLabel,
            String yAxisLabel, XYDataset dataset, PlotOrientation orientation,
            boolean legend, boolean tooltips, boolean urls) {

        if (orientation == null) {
            throw new IllegalArgumentException("Null 'orientation' argument.");
        }
        var xAxis = new NumberAxis(xAxisLabel);
        xAxis.setAutoRangeIncludesZero(false);
        var yAxis = new NumberAxis(yAxisLabel);
        yAxis.setAutoRangeIncludesZero(false);

        var plot = new XYPlot(dataset, xAxis, yAxis, null);

        XYToolTipGenerator toolTipGenerator = null;
        if (tooltips) {
            toolTipGenerator = new StandardXYToolTipGenerator();
        }

        XYURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardXYURLGenerator();
        }
        XYItemRenderer renderer = new XYConditionRenderer(false, true);
        renderer.setBaseToolTipGenerator(toolTipGenerator);
        renderer.setURLGenerator(urlGenerator);
        plot.setRenderer(renderer);
        plot.setOrientation(orientation);

        var chart = new JFreeChart(title, DEFAULT_TITLE_FONT, plot, legend);
        getChartTheme().apply(chart);
        return chart;
    }

    /**
     * Creates a line chart (based on an {@link XYDataset}) with default settings.
     *
     * @param title  the chart title (<code>null</code> permitted).
     * @param xAxisLabel  a label for the X-axis (<code>null</code> permitted).
     * @param yAxisLabel  a label for the Y-axis (<code>null</code> permitted).
     * @param dataset  the dataset for the chart (<code>null</code> permitted).
     * @param orientation  the plot orientation (horizontal or vertical) (<code>null</code> NOT permitted).
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return The chart.
     */
    public static JFreeChart createXYLineChart (String title, String xAxisLabel,
				String yAxisLabel, XYDataset dataset, PlotOrientation orientation,
				boolean legend, boolean tooltips, boolean urls) {

        if (orientation == null) {
            throw new IllegalArgumentException("Null 'orientation' argument.");
        }
        var xAxis = new NumberAxis(xAxisLabel);
        xAxis.setAutoRangeIncludesZero(false);
        var yAxis = new NumberAxis(yAxisLabel);
        XYItemRenderer renderer = new XYConditionRenderer(true, false);
        var plot = new XYPlot(dataset, xAxis, yAxis, renderer);
        plot.setOrientation(orientation);
        if (tooltips) {
            renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
        }
        if (urls) {
            renderer.setURLGenerator(new StandardXYURLGenerator());
        }

        var chart = new JFreeChart(title, DEFAULT_TITLE_FONT, plot, legend);
        getChartTheme().apply(chart);
        return chart;
    }

    public static JFreeChart getOverlaidChartInstance (String chartType, String title, String xAxisLabel,
			String yAxisLabel, int xAxisType, int yAxisType, List plotDefinitions, boolean showLegend)
    	throws ChartValidationException, DatasetProduceException {
    final var chartTypeConst = getChartTypeConstant(chartType);
    final var axisFactory = getInstance();
	chartUsed(chartType);
    switch (chartTypeConst) {
      case OVERLAY_XY :
        var domainAxis = (ValueAxis) axisFactory.createAxis(ORIENTATION_HORIZONTAL, xAxisType, xAxisLabel);
        // get main plot
        var mainPlotDef = (PlotDefinition) plotDefinitions.get(0);
		if (domainAxis instanceof NumberAxis && mainPlotDef.isXaxisinteger()) {
			((NumberAxis) domainAxis).setStandardTickUnits(createIntegerTickUnits());
		}
        check(mainPlotDef.getDataset(), XYDataset.class, chartType);
        var plot = (XYPlot) mainPlotDef.getPlot(chartTypeConst);
        plot.setDomainAxis(domainAxis);
        var rangeAxis = (ValueAxis) axisFactory.createAxis(ORIENTATION_VERTICAL, yAxisType, yAxisLabel);
		if (rangeAxis instanceof NumberAxis && mainPlotDef.isYaxisinteger()) {
			((NumberAxis) rangeAxis).setStandardTickUnits(createIntegerTickUnits());
		}
        plot.setRangeAxis(rangeAxis);
        //plot.setRenderer(new StandardXYItemRenderer());
        // add second and later datasets to main plot
        for (var plotidx = 1;plotidx<plotDefinitions.size();plotidx++) {
          var subPlotDef = (PlotDefinition) plotDefinitions.get(plotidx);
          check(subPlotDef.getDataset(), XYDataset.class, chartType);
          plot.setDataset(plotidx, (XYDataset)subPlotDef.getDataset());

                var rendererIndex = getRendererIndex(subPlotDef.getType());
          var rend = (XYItemRenderer) getRenderer(rendererIndex);
          plot.setRenderer(plotidx, rend);
        }
        return new JFreeChart(title, new Font("SansSerif", BOLD, 18), plot, showLegend);
      case OVERLAY_CATEGORY ://added by lrh 2005-07-11
        var domainAxis2 = (CategoryAxis)axisFactory.createAxis(ORIENTATION_HORIZONTAL, xAxisType, xAxisLabel);
        // get main plot
        mainPlotDef = (PlotDefinition) plotDefinitions.get(0);
        check(mainPlotDef.getDataset(), CategoryDataset.class, chartType);
        var plot2 = (CategoryPlot) mainPlotDef.getPlot(chartTypeConst);
        plot2.setDomainAxis(domainAxis2);
        var va = (ValueAxis) axisFactory.createAxis(ORIENTATION_VERTICAL, yAxisType, yAxisLabel);
		if (va instanceof NumberAxis && mainPlotDef.isYaxisinteger()) {
			((NumberAxis) va).setStandardTickUnits(createIntegerTickUnits());
		}
        plot2.setRangeAxis(va);
        //plot.setRenderer(new StandardXYItemRenderer());
        // add second and later datasets to main plot
        for (var plotidx = 1;plotidx<plotDefinitions.size();plotidx++) {
          var subPlotDef = (PlotDefinition) plotDefinitions.get(plotidx);
          check(subPlotDef.getDataset(), CategoryDataset.class, chartType);
          plot2.setDataset(plotidx, (CategoryDataset)subPlotDef.getDataset());

                var rendererIndex = getRendererIndex(subPlotDef.getType());
          var rend2 = (CategoryItemRenderer) getRenderer(rendererIndex);
          plot2.setRenderer(plotidx, rend2);
        }
        return new JFreeChart(title, new Font("SansSerif", BOLD, 18), plot2, showLegend);
      default :
        throw new UnsupportedChartTypeException(chartType + " is not supported.");
    }
  }

  public static JFreeChart getCombinedChartInstance (String chartType, String title, String xAxisLabel,
  			String yAxisLabel, List plotDefinitions, String layout, boolean showLegend)
    	throws ChartValidationException, DatasetProduceException {
    final var chartTypeConst = getChartTypeConstant(chartType);
	chartUsed(chartType);
    switch (chartTypeConst) {
      case COMBINED_XY :
        final var layoutConst = getLayoutConstant(layout);
        Plot plot = null;
        switch (layoutConst) {
          case DOMAIN :
            ValueAxis domainAxis = new DateAxis(xAxisLabel);
            plot = new CombinedDomainXYPlot(domainAxis);
            for (var i = 0; i < plotDefinitions.size(); i++) {
              var pd = (PlotDefinition) plotDefinitions.get(i);
              check(pd.getDataset(), XYDataset.class, chartType);
              var temp = (XYPlot) pd.getPlot(chartTypeConst);
              var na  = new NumberAxis(pd.getYaxislabel());
				if (pd.isYaxisinteger())
					na.setStandardTickUnits(createIntegerTickUnits());
              temp.setRangeAxis(na);
              ((CombinedDomainXYPlot) plot).add(temp);
            }
            return new JFreeChart(title, new Font("SansSerif", BOLD, 18), plot, showLegend);
          case RANGE :
            ValueAxis rangeAxis = new NumberAxis(yAxisLabel);
            plot = new CombinedRangeXYPlot(rangeAxis);
			var allInteger = true;
            for (var i = 0; i < plotDefinitions.size(); i++) {
              var pd = (PlotDefinition) plotDefinitions.get(i);
				if (! pd.isYaxisinteger())
					allInteger = false;
              check(pd.getDataset(), XYDataset.class, chartType);
              var temp = (XYPlot) pd.getPlot(chartTypeConst);
              temp.setDomainAxis(new DateAxis(pd.getXaxislabel()));
              ((CombinedRangeXYPlot) plot).add(temp);
            }
			if (allInteger)
				((NumberAxis) rangeAxis).setStandardTickUnits(createIntegerTickUnits());
            return new JFreeChart(title, new Font("SansSerif", BOLD, 18), plot, showLegend);
          default :
            throw new AttributeValidationException(layout, " any value");
        }
      default :
        throw new UnsupportedChartTypeException(chartType);
    }
  }

  /**
   * Helper to check if the given dataset is the expected type.
   * @param data The dataset
   * @param clazz Expected type (class)
   * @param chartType The chart type string
   * @throws IncompatibleDatasetException If not the expected class
   */
  public static void check(Dataset data, Class clazz, String chartType) throws IncompatibleDatasetException {
    if (!clazz.isInstance(data)) {
      throw new IncompatibleDatasetException("Charts of type " + chartType + " " + "need datasets of type " + clazz.getName());
    }
  }

}
