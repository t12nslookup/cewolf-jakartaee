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

package de.laures.cewolf.taglib.tags;

import java.io.IOException;

import jakarta.servlet.jsp.JspException;

import de.laures.cewolf.CewolfException;
import de.laures.cewolf.ChartHolder;
import de.laures.cewolf.ChartImage;
import static de.laures.cewolf.ChartImage.IMG_TYPE_LEGEND;
import de.laures.cewolf.Configuration;
import static de.laures.cewolf.Configuration.getInstance;
import de.laures.cewolf.Storage;
import de.laures.cewolf.WebConstants;
import static de.laures.cewolf.WebConstants.MIME_PNG;
import de.laures.cewolf.taglib.ChartImageDefinition;
import de.laures.cewolf.taglib.TaglibConstants;
import de.laures.cewolf.taglib.html.HTMLImgTag;
import static de.laures.cewolf.taglib.tags.ChartImgTag.buildImgURL;
import de.laures.cewolf.taglib.util.PageUtils;
import static de.laures.cewolf.taglib.util.PageUtils.getChartHolder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import static org.apache.commons.logging.LogFactory.getLog;

/**
 * Tag &lt;legend&gt; which can be used to render a chart's legend
 * separately somewhere in the page.
 * @author  Guido Laures
 */
public class LegendTag extends HTMLImgTag implements CewolfRootTag {

    private static final Log log = getLog(LegendTag.class);

	static final long serialVersionUID = 3011526353897699906L;

    private static final String DEFAULT_MIME_TYPE = MIME_PNG;
    private static final int DEFAULT_TIMEOUT = 300;
    private int timeout = DEFAULT_TIMEOUT;
    private transient String sessionKey;
    private transient String renderer;
    private String mimeType = DEFAULT_MIME_TYPE;

    public int doStartTag() throws JspException {
        var cd = getChartHolder(getChartId(), pageContext);
        ChartImage cid = new ChartImageDefinition(cd, width, height, IMG_TYPE_LEGEND, mimeType, timeout);
        var storage = getInstance(pageContext.getServletContext()).getStorage();
        try {
        	this.sessionKey = storage.storeChartImage(cid, pageContext);
        } catch(CewolfException cwex){
        	log.error("LegendTag.doStartTag: "+cwex.getMessage());
        	throw new JspException(cwex.getMessage());
        }
        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
		super.doStartTag();
		final var buffer = new StringBuffer(" src=\"");
		buffer.append(getImgURL());
		buffer.append("\"");
		try {
			pageContext.getOut().write(buffer.toString());
		} catch (IOException ioex) {
			reset();
        	log.error("LegendTag.doEndTag: "+ioex.getMessage());
			throw new JspException(ioex.getMessage());
		}
		return super.doEndTag();
    }

	/**
	* To enable further server side scriptings on JSP output the session ID is always
	* encoded into the image URL even if cookies are enabled on the client side.
	*/
	protected String getImgURL() {
		return buildImgURL(renderer, pageContext, sessionKey, width, height, mimeType, forceSessionId, removeAfterRender);
	}

    protected void reset() {
        // as of a weird JSP compiler in resin
        // a reused tag's attribute is only set if 
        // it changes. So width an height may not
        // be unset to ensure correct values.
        var lHeight = this.height;
        var lWidth = this.width;
        var lTimeout = this.timeout;
        super.reset();
        this.height = lHeight;
        this.width = lWidth;
        this.timeout = lTimeout;
    }

    public String getChartId() {
        return getId();
    }

    public void setRenderer(String rend) {
        this.renderer = rend;
    }

    /**
     * Sets the mimeType.
     * @param mimeType The mimeType to set
     */
    public void setMime(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * @return Returns the timeout.
     */
    public int getTimeout()
    {
      return timeout;
    }
    /**
     * @param timeout The timeout to set.
     */
    public void setTimeout( int timeout )
    {
      this.timeout = timeout;
    }
}
