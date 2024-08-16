package de.laures.cewolf.taglib;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.JFreeChart;
import org.jfree.data.general.Dataset;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.DrawingSupplier;

import de.laures.cewolf.ChartValidationException;
import de.laures.cewolf.DatasetProduceException;
import static de.laures.cewolf.taglib.CewolfChartFactory.getCombinedChartInstance;

/**
 * Chart definition subclass to handle combined charts
 *
 * @author guido
 * @author tbardzil
 */
public class CombinedChartDefinition extends AbstractChartDefinition {

	private static final long serialVersionUID = -1069436182870570653L;

    private String layout;

    private List<PlotDefinition> plotDefinitions = new ArrayList<>();
    private transient DrawingSupplier drawingSupplier;

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public Dataset getDataset() throws DatasetProduceException {
        return plotDefinitions.get(0).getDataset();
    }

    public void addPlot(PlotDefinition pd) {
        pd.setDrawingSupplier(getDrawingSupplier());
        plotDefinitions.add(pd);
    }

    protected JFreeChart produceChart() throws DatasetProduceException, ChartValidationException {
        return getCombinedChartInstance(type, title, xAxisLabel, yAxisLabel, plotDefinitions, layout, showLegend);
    }

	private DrawingSupplier getDrawingSupplier() {
		if (drawingSupplier == null) {
			drawingSupplier = new DefaultDrawingSupplier();
		}
		return drawingSupplier;
	}
}
