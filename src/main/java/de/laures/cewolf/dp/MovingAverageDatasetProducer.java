package de.laures.cewolf.dp;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.data.general.Dataset;
import org.jfree.data.time.MovingAverage;
import org.jfree.data.xy.XYDataset;

import de.laures.cewolf.DatasetProduceException;
import de.laures.cewolf.DatasetProducer;
import static java.lang.Integer.parseInt;
import static java.lang.Integer.parseInt;
import static org.apache.commons.logging.LogFactory.getLog;
import static org.jfree.data.time.MovingAverage.createMovingAverage;

/**
 * @author guido
 */
public class MovingAverageDatasetProducer implements DatasetProducer, Serializable {
	
	static final long serialVersionUID = -3599156193385103768L;

	private static final Log log = getLog(MovingAverageDatasetProducer.class);

	/**
	 * @see de.laures.cewolf.DatasetProducer#produceDataset(Map)
	 */
	public Object produceDataset (Map<String,Object> params) throws DatasetProduceException {
		//log.info(params);
		var datasetProducer = (DatasetProducer) params.get("producer");
		//log.info(datasetProducer);
		var dataset = (Dataset) datasetProducer.produceDataset(params);
		var suffix = (String) params.get("suffix");
		int period, skip;
		try {
			period = parseInt((String) params.get("period"));
			skip = parseInt((String) params.get("skip"));
		} catch (RuntimeException ex) {
			throw new DatasetProduceException("'period' and 'skip' parameters don't seem to have valid integer values");
		}
		if (dataset instanceof XYDataset) {
	        return createMovingAverage((XYDataset)dataset, suffix, period, skip);
		} else {
			throw new DatasetProduceException("moving average only supported for XYDatasets");
		}
	}

	/**
	 * @see de.laures.cewolf.DatasetProducer#hasExpired(Map, Date)
	 */
	public boolean hasExpired(Map params, Date since) {
		return true;
	}

	/**
	 * @see de.laures.cewolf.DatasetProducer#getProducerId()
	 */
	public String getProducerId() {
		return getClass().getName();
	}

}
