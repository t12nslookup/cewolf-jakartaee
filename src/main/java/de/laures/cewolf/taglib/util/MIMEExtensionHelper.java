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

package de.laures.cewolf.taglib.util;

import de.laures.cewolf.WebConstants;
import static de.laures.cewolf.WebConstants.MIME_JPEG;
import static de.laures.cewolf.WebConstants.MIME_PNG;
import static de.laures.cewolf.WebConstants.MIME_SVG;

/**
 * @author glaures
 */
public class MIMEExtensionHelper {

	public static String getExtensionForMimeType(String mimeType) {
		if (MIME_SVG.equalsIgnoreCase(mimeType)) {
			return ".svg";
		} else if (MIME_PNG.equalsIgnoreCase(mimeType)) {
			return ".png";
		}
		if (MIME_JPEG.equalsIgnoreCase(mimeType)) {
		    return ".jpg";
		}
		return null;
	}


}
