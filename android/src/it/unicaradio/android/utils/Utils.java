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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

/**
 * @author paolo.cortis
 */
public class Utils
{
	private static String LOG = Utils.class.getName();

	public static byte[] downloadFromUrl(String fileUrl) throws IOException
	{
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

		return baf.toByteArray();
	}
}
