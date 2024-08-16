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

package de.laures.cewolf.util;

import java.awt.Color;
import static java.awt.Color.black;
import static java.awt.Color.red;
import static java.awt.Color.white;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import static java.lang.String.valueOf;
import java.util.Enumeration;
import java.util.StringTokenizer;
import javax.imageio.*;
import static javax.imageio.ImageIO.createImageOutputStream;
import static javax.imageio.ImageIO.getImageWritersBySuffix;
import static javax.imageio.ImageWriteParam.MODE_EXPLICIT;

//import com.sun.image.codec.jpeg.JPEGCodec;
//import com.sun.image.codec.jpeg.JPEGEncodeParam;
//import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * Some methods to render messages or exceptions into an image.
 * @author glaures
 */
public class RenderingHelper {
	
	private final static int PADDING_X = 5;
	
    public static BufferedImage createImage (int width, int height) {
        // return GRAPHICS_CONV.createCompatibleImage(width, height);
        return new BufferedImage(width, height, TYPE_INT_RGB);
    }

	public static String renderMessage (String msg, int width, int height, OutputStream out) throws IOException {
		var image = createImage(width, height);
		var gr = image.getGraphics();
		gr.setColor(white);
		gr.fillRect(0, 0, width, height);
		gr.setColor(black);
		gr.drawString(msg, PADDING_X, height/2 - 7);
/*
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
		JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(image);
		param.setQuality(1.0f, true);
		encoder.encode(image, param);
*/
		var writer = getImageWritersBySuffix("jpeg").next();
		writer.setOutput(createImageOutputStream(out)); 
		var iwp = writer.getDefaultWriteParam();
		iwp.setCompressionMode(MODE_EXPLICIT); 
		iwp.setCompressionQuality (1.0f); 
		writer.write(null, new IIOImage(image, null, null), iwp); 
		writer.write(image); 
		writer.dispose(); 

		return "image/jpeg";
	}

	public static String renderException (Throwable ex, int width, int height, OutputStream out) throws IOException {
		var image = createImage(width, height);
		var gr = image.getGraphics();
		gr.setColor(white);
		gr.fillRect(0, 0, width, height);
		gr.setColor(red);
		gr.drawString(ex.getClass().getName() + " raised:", PADDING_X, 15);
		gr.drawString(valueOf(ex.getMessage()), PADDING_X, 30);
		gr.setColor(black);
		var stFont = gr.getFont().deriveFont(9f);
		gr.setFont(stFont);
		drawStackTrace(gr, PADDING_X, 50, ex);
/*
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
		JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(image);
		param.setQuality(1.0f, true);
		encoder.encode(image, param);
*/
		var writer = getImageWritersBySuffix("jpeg").next();
		writer.setOutput(createImageOutputStream(out)); 
		var iwp = writer.getDefaultWriteParam();
		iwp.setCompressionMode(MODE_EXPLICIT); 
		iwp.setCompressionQuality (1.0f); 
		writer.write(null, new IIOImage(image, null, null), iwp); 
		writer.write(image); 
		writer.dispose(); 

		return "image/jpeg";
	}

    private static void drawStackTrace (Graphics gr, int x, int y, Throwable ex) {
        final var linePadding = 4;
        var lineHeight = gr.getFont().getSize() + linePadding;
        var currY = y;
        Enumeration lines = new StringTokenizer(getStackTrace(ex), "\n", false);
        while (lines.hasMoreElements()) {
            gr.drawString(((String)lines.nextElement()).trim(), x, currY);
            currY += lineHeight;
        }
    }

    private static String getStackTrace (Throwable t) {
        var sw = new StringWriter();
        try (var pw = new PrintWriter(sw)) {
            t.printStackTrace(pw);
        }
        return sw.getBuffer().toString();
    }
}
