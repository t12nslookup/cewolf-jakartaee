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

package de.laures.cewolf.taglib.tags;

import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import javax.imageio.ImageIO;
import static javax.imageio.ImageIO.read;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import static org.apache.commons.logging.LogFactory.getLog;

/** 
 * Special texture paint which can be serialized.
 * @see TexturePaint
 * @author  Guido Laures 
 */
public class SerializableTexturePaint implements Paint, Serializable {

    private static final Log log = getLog(SerializableTexturePaint.class);

	static final long serialVersionUID = 3377875007493302352L;

    private String image;
    private int width;
    private int height;
    private transient Paint paint = null;

    public SerializableTexturePaint() {
    }

    /** Creates a new instance of SerializableGradientPaint */
    public SerializableTexturePaint(String image, int width, int height) {
        this.image = image;
        this.width = width;
        this.height = height;
    }

    private Paint getPaint() {
        if (paint == null) {
            createPaint();
        }
        return paint;
    }

    private void createPaint() {
		try {
			var bim = read(new File(image));
			var rect = new Rectangle(width, height);
			paint = new TexturePaint(bim, rect);
		} catch (IOException ioex) {
			log.error("SerializableTexturePaint.createPaint: "+ioex.getMessage());
		}
    }

    public java.awt.PaintContext createContext (ColorModel colorModel, Rectangle rectangle,
			Rectangle2D rectangle2D, AffineTransform affineTransform, RenderingHints renderingHints) {
		if (renderingHints == null)
			renderingHints = new RenderingHints(null);
        return getPaint().createContext(colorModel, rectangle, rectangle2D, affineTransform, renderingHints);
    }

    public int getTransparency() {
        return getPaint().getTransparency();
    }

}
