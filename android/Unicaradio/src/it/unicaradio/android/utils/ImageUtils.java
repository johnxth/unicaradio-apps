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
package it.unicaradio.android.utils;

import java.io.IOException;
import java.text.MessageFormat;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.view.Display;

/**
 * @author Paolo Cortis
 */
public class ImageUtils
{
	private ImageUtils()
	{
	}

	/**
	 * Resize a bitmap
	 *
	 * @param origBitmap   Image to resize
	 * @param resizeFactor Factor to be used to resize the image (based on
	 *                     screen size)
	 * @return the resized bitmap
	 */
	public static Bitmap resize(Display display, Bitmap origBitmap, int resizeFactor)
	{
		int width = origBitmap.getWidth();
		int height = origBitmap.getHeight();

		int minSize = getMinSize(display);
		int newWidth = minSize * resizeFactor / 100;
		int newHeight = newWidth;

		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		Log.d(ImageUtils.class.getName(), MessageFormat.format("New width: {0}", String.valueOf(newWidth)));
		Log.d(ImageUtils.class.getName(), MessageFormat.format("New height: {0}", String.valueOf(newHeight)));

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);

		Bitmap outBitmap = Bitmap.createBitmap(origBitmap, 0, 0, width, height, matrix, true);

		return outBitmap;
	}

	private static int getMinSize(Display display)
	{
		int minSize;
		if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB_MR1) {
			minSize = getMinSizeForOlderDevices(display);
		} else {
			minSize = getMinSizeForNewerDevices(display);
		}

		return minSize;
	}

	@TargetApi(13)
	private static int getMinSizeForNewerDevices(Display display)
	{
		Point size = new Point();
		display.getSize(size);

		Log.d(ImageUtils.class.getName(), MessageFormat.format("Display width: {0}", String.valueOf(size.x)));
		Log.d(ImageUtils.class.getName(), MessageFormat.format("Display height: {0}", String.valueOf(size.y)));

		return Math.min(size.x, size.y);
	}

	@SuppressWarnings("deprecation")
	private static int getMinSizeForOlderDevices(Display display)
	{
		Log.d(ImageUtils.class.getName(),
			  MessageFormat.format("Display width: {0}", String.valueOf(display.getWidth())));
		Log.d(ImageUtils.class.getName(),
			  MessageFormat.format("Display height: {0}", String.valueOf(display.getHeight())));

		return Math.min(display.getWidth(), display.getHeight());
	}

	public static Bitmap downloadFromUrl(String fileUrl) throws IOException
	{
		byte[] buf = NetworkUtils.httpGet(fileUrl);

		return BitmapFactory.decodeByteArray(buf, 0, buf.length);
	}
}
