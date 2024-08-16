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

import static de.laures.cewolf.taglib.tags.ChartImgURLTag.VAR_NAME;
import jakarta.servlet.jsp.tagext.TagData;
import jakarta.servlet.jsp.tagext.TagExtraInfo;
import jakarta.servlet.jsp.tagext.VariableInfo;
import static jakarta.servlet.jsp.tagext.VariableInfo.AT_END;

/**
 * TagExxtraInfo for the ChartImgURLTag
 * @see ChartImgURLTag
 * @author glaures
 */
public class ChartImgURLTagEI extends TagExtraInfo {
	
	/**
	 * @see javax.servlet.jsp.tagext.TagExtraInfo#getVariableInfo(TagData)
	 */
	public VariableInfo[] getVariableInfo(TagData tagData) {
		var varName = (String)(tagData.getAttribute(VAR_NAME));
		if(varName == null){
			return new VariableInfo[] {};
		}
		var info = new VariableInfo(varName, "java.lang.String", true, AT_END);
		return new VariableInfo[] {info};
	}

}
