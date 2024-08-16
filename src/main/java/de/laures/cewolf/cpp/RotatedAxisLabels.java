package de.laures.cewolf.cpp;

import java.io.Serializable;
import java.util.Map;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.data.category.CategoryDataset;

import de.laures.cewolf.ChartPostProcessor;
import static java.lang.Integer.parseInt;
import static java.lang.Integer.parseInt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import static org.apache.commons.logging.LogFactory.getLog;
import static org.jfree.chart.axis.CategoryLabelPositions.STANDARD;
import static org.jfree.chart.axis.CategoryLabelPositions.UP_90;

/**
* A postprocessor for rotating and/or removing the labels on the X-Axis. It supports the following parameters:
* <BR><b>rotate_at</b> make the labels vertical if this many categories are present; default 1
* <BR><b>remove_at</b> don't print any labels if this many categories are present; default 100
* <P>
* Usage:<P>
* &lt;chart:chartpostprocessor id="labelRotation"&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="rotate_at" value="10"/&gt;<BR>
* &nbsp;&nbsp;&lt;chart:param name="remove_at" value="100"/&gt;<BR>
* &lt;/chart:chartpostprocessor&gt;
*
* @author Rich Unger
*/

public class RotatedAxisLabels implements ChartPostProcessor, Serializable {

	static final long serialVersionUID = 5242029033037971789L;

    private static final Log log = getLog(RotatedAxisLabels.class);

	public void processChart (JFreeChart chart, Map<String,String> params) {
		int rotateThreshold=1, removeThreshold=100;

		var rotateParam = params.get("rotate_at");
		if (rotateParam != null && rotateParam.trim().length() > 0) {
			try {
				rotateThreshold = parseInt(rotateParam);
			} catch (NumberFormatException nfex) { }
		}

		var removeParam = params.get("remove_at");
		if (removeParam != null && removeParam.trim().length() > 0) {
			try {
				removeThreshold = parseInt(removeParam);
			} catch (NumberFormatException nfex) { }
		}

		var plot = chart.getPlot();
		Axis axis = null;
		var numValues = 0;

		try {
			if (plot instanceof CategoryPlot) {
				axis = ((CategoryPlot) plot).getDomainAxis();
				numValues = ((CategoryPlot) plot).getDataset().getRowCount();
			} else if (plot instanceof XYPlot) {
				axis = ((XYPlot) plot).getDomainAxis();
				numValues = ((XYPlot) plot).getDataset().getItemCount(0);
			} else if (plot instanceof FastScatterPlot) {
				axis = ((FastScatterPlot) plot).getDomainAxis();
				numValues = ((FastScatterPlot) plot).getData()[0].length;
			}

			if (axis instanceof CategoryAxis) {
				var catAxis = (CategoryAxis) axis;

				if (rotateThreshold > 0) {
					if (numValues >= rotateThreshold) {
						catAxis.setCategoryLabelPositions(UP_90);
					} else {
						catAxis.setCategoryLabelPositions(STANDARD);
					}
				}
			} else if (axis instanceof ValueAxis) {
				var valueAxis = (ValueAxis) axis;

				if (rotateThreshold > 0) {
					valueAxis.setVerticalTickLabels(numValues >= rotateThreshold);
				}
			}

			if ((axis != null) && (removeThreshold > 0)) {
				axis.setTickLabelsVisible(numValues < removeThreshold);
			}
		} catch (RuntimeException rtex) {
			log.error(rtex.getMessage());
		}
	}
}

