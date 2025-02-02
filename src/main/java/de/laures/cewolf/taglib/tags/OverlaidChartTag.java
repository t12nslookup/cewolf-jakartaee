/*
 * Created on 13.04.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.laures.cewolf.taglib.tags;

import de.laures.cewolf.taglib.AbstractChartDefinition;
import de.laures.cewolf.taglib.AxisTypes;
import static de.laures.cewolf.taglib.AxisTypes.typeList;
import de.laures.cewolf.taglib.OverlaidChartDefinition;
import de.laures.cewolf.taglib.PlotContainer;
import de.laures.cewolf.taglib.PlotDefinition;


/**
 * @author guido
 */
public class OverlaidChartTag extends AbstractChartTag implements PlotContainer {

	static final long serialVersionUID = 3879037601548824461L;

    protected AbstractChartDefinition createChartDefinition() {
        return new OverlaidChartDefinition();
    }

	public void addPlot(PlotDefinition pd){
		((OverlaidChartDefinition)chartDefinition).addPlot(pd);
	}
    
	/**
	 * Sets the xAxisType.
	 * @param xAxisType The xAxisType to set
	 */
	public void setxaxistype(String xAxisType) {
        final var xAxisTypeConst = typeList.indexOf(xAxisType);
		((OverlaidChartDefinition)chartDefinition).setXAxisType(xAxisTypeConst);
	}

	/**
	 * Sets the yAxisType.
	 * @param yAxisType The yAxisType to set
	 */
	public void setyaxistype(String yAxisType) {
        final var yAxisTypeConst = typeList.indexOf(yAxisType);
        ((OverlaidChartDefinition)chartDefinition).setYAxisType(yAxisTypeConst);
	}

}
