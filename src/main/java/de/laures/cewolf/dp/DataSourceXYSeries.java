package de.laures.cewolf.dp;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.data.xy.XYSeries;

import de.laures.cewolf.DatasetProduceException;
import static org.apache.commons.logging.LogFactory.getLog;

/**
 * Data source for xy series
 * @author glaures
 */
public class DataSourceXYSeries implements Serializable {

	static final long serialVersionUID = 6094882122063279978L;

	private static final Log logger = getLog(DataSourceXYSeries.class);

	private String dataSourceName;
	private String query;
	private String xCol = "x";
	private String yCol = "y";
	private String seriesName = "name";
	
	/**
	 * Constructor for DataSourceXYSeries.
	 */
	public DataSourceXYSeries(String dataSourceName, String query) {
		this.dataSourceName = dataSourceName;
		this.query = query;
	}

	protected DataSource getDataSource() throws NamingException {
		Context initCtx = new InitialContext();
		var envCtx = (Context) initCtx.lookup("java:comp/env");
		return (DataSource) envCtx.lookup(dataSourceName);
	}

	/**
	 * @see de.laures.cewolf.DatasetProducer#produceDataset(Map)
	 */
	public XYSeries produceXYSeries() throws DatasetProduceException {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		var series = new XYSeries(seriesName);
		try {
			var ds = getDataSource();
			con = ds.getConnection();
			stmt = con.prepareStatement(query);
			rs = stmt.executeQuery();
			var xColIndex = rs.findColumn(xCol);
			var yColIndex = rs.findColumn(yCol);
			while (rs.next()) {
				series.add((Number) rs.getObject(xColIndex), (Number) rs.getObject(yColIndex));
			}
		} catch (Exception namingEx) {
			logger.error(namingEx.getMessage());
			throw new DatasetProduceException(namingEx.getMessage(), namingEx);
		} finally {
			try { if (rs != null) rs.close(); } catch (Exception ex) {
				logger.error(ex.getMessage());
			}
			try { if (stmt != null) stmt.close(); } catch (Exception ex) {
				logger.error(ex.getMessage());
			}
			try { if (con != null) con.close(); } catch (Exception ex) {
				logger.error(ex.getMessage());
			}
		}
		return series;
	}
}
