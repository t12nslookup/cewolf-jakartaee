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

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.Dataset;

import de.laures.cewolf.CewolfException;
import de.laures.cewolf.ChartHolder;
import de.laures.cewolf.ChartImage;
import de.laures.cewolf.ChartValidationException;
import de.laures.cewolf.DatasetProduceException;
import de.laures.cewolf.PostProcessingException;
import de.laures.cewolf.event.ChartImageRenderListener;
import de.laures.cewolf.util.RenderedImage;
import de.laures.cewolf.util.Renderer;
import static de.laures.cewolf.util.Renderer.render;
import static java.util.Calendar.SECOND;

/**
 * Serializable implementaton of a ChartImage.
 * @author glaures
 * @see de.laures.cewolf.ChartImage
 */
public class ChartImageDefinition implements ChartImage, ChartHolder, Serializable {

	static final long serialVersionUID = 8919126983568810996L;

	private final ChartHolder chartHolder;
	private final int height;
	private final int width;
	private final int type;
	private final String mimeType;
	private final Date timeoutTime;

	private RenderedImage renderedImage;

	/**
	 * Constructor for ChartImage
	 */
	public ChartImageDefinition (ChartHolder ch, int width, int height, int type, String mimeType, int timeout) {
		if(width <= 0 || height <= 0){
			throw new IllegalArgumentException("ChartImage with height or width <= 0 is illegal");
		}
		this.chartHolder = ch;
		this.width = width;
		this.height = height;
		this.type = type;
		this.mimeType = mimeType;
		Calendar cal = new GregorianCalendar();
		cal.add(SECOND,timeout);
		this.timeoutTime = cal.getTime();
	}

	/**
	 * Returns the height.
	 * @return int
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Returns the width.
	 * @return int
	 */
	public int getWidth() {
		return width;
	}

	public int getType() {
		return type;
	}

	public JFreeChart getChart() throws DatasetProduceException, ChartValidationException, PostProcessingException {
		return chartHolder.getChart();
	}

	public Dataset getDataset() throws DatasetProduceException {
		return chartHolder.getDataset();
	}

	/**
	 * Returns the mimeType.
	 * @return String
	 */
	public String getMimeType() {
		return mimeType;
	}

	public ChartRenderingInfo getRenderingInfo() throws CewolfException {
		ensureRendered();
		return renderedImage.renderingInfo;
	}

	public byte[] getBytes() throws CewolfException{
		ensureRendered();
		return renderedImage.data;
	}

	private void ensureRendered() throws CewolfException{
		if(renderedImage == null){
			renderedImage = render(this, chartHolder.getChart());
			onImageRendered(renderedImage);
		}
	}

	/**
	 * @see de.laures.cewolf.ChartImage#getSize()
	 */
	public int getSize() throws CewolfException {
		ensureRendered();
		return renderedImage.data.length;
	}

	/* (non-Javadoc)
	 * @see de.laures.cewolf.ChartImage#getTimeoutTime()
	 */
	public Date getTimeoutTime() {
		return timeoutTime;
	}

	/**
	 * Implemented onImageRendered method.
	 * @see de.laures.cewolf.ChartHolder#onImageRendered(de.laures.cewolf.util.RenderedImage).
	 * @param renderedImage The image
	 */
	private void onImageRendered(RenderedImage renderedImage) {
		if (chartHolder instanceof ChartImageRenderListener) {
			// delegate to chartHolder if it's interested...
			((ChartImageRenderListener) chartHolder).onImageRendered(renderedImage);
		}
	}

}
