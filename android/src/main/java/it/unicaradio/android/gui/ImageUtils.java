/**
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; version 2 of the License.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Copyright UnicaRadio
 */
package it.unicaradio.android.gui;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;

import org.apache.http.util.ByteArrayBuffer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.view.Display;

/**
 * @author Paolo Cortis
 */
public class ImageUtils
{
	private final Display display;

	public ImageUtils(Display display)
	{
		this.display = display;
	}

	/**
	 * Resize a bitmap
	 * 
	 * @param origBitmap Image to resize
	 * @param resizeFactor Factor to be used to resize the image (based on
	 * screen size)
	 * @return the resized bitmap
	 */
	public Bitmap resize(Bitmap origBitmap, int resizeFactor)
	{
		int width = origBitmap.getWidth();
		int height = origBitmap.getHeight();

		int newWidth = display.getWidth() * resizeFactor / 100;
		int newHeight = newWidth;

		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		Log.d(ImageUtils.class.getName(),
				MessageFormat.format("Display width: {0}",
						String.valueOf(display.getWidth())));
		Log.d(ImageUtils.class.getName(),
				MessageFormat.format("Display height: {0}",
						String.valueOf(display.getHeight())));
		Log.d(ImageUtils.class.getName(), MessageFormat.format(
				"New width: {0}", String.valueOf(newWidth)));
		Log.d(ImageUtils.class.getName(),
				MessageFormat.format("New height: {0}",
						String.valueOf(newHeight)));

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);

		Bitmap outBitmap = Bitmap.createBitmap(origBitmap, 0, 0, width, height,
				matrix, true);

		return outBitmap;
	}

	public Bitmap downloadFromUrl(String fileUrl)
	{
		try {
			URL url = new URL(fileUrl); // you can write here any link

			// Open a connection to that URL.
			URLConnection ucon = url.openConnection();

			// Define InputStreams to read from the URLConnection.
			InputStream is = ucon.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);

			// Read bytes to the Buffer until there is nothing more to read(-1).
			ByteArrayBuffer baf = new ByteArrayBuffer(50);
			int current = 0;
			while((current = bis.read()) != -1) {
				baf.append((byte) current);
			}

			return BitmapFactory.decodeByteArray(baf.toByteArray(), 0,
					baf.length());
		} catch(IOException e) {
			Log.d(this.getClass().getName(), "Error: " + e);
			return null;
		}
	}
}
