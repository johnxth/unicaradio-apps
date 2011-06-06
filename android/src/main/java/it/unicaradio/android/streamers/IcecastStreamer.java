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
package it.unicaradio.android.streamers;

import it.unicaradio.android.gui.TrackInfos;

import java.io.IOException;
import java.net.URLConnection;
import java.text.MessageFormat;

import org.apache.commons.lang.StringUtils;

import android.util.Log;

/**
 * @author Paolo Cortis
 */
public class IcecastStreamer extends Streamable
{
	private static final String CLASSNAME = IcecastStreamer.class.getName();

	private static final String SEPARATOR = " - ";

	private int metaint;

	private int bytesUntilNextInfos;

	public IcecastStreamer(URLConnection conn) throws IOException
	{
		super(conn.getInputStream());

		readMetaInt(conn);

		bytesUntilNextInfos = metaint;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized int read() throws IOException
	{
		if(bytesUntilNextInfos == 0) {
			getIcyInfos();
		}

		bytesUntilNextInfos--;
		return super.read();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int read(byte[] b) throws IOException
	{
		return read(b, 0, b.length);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized int read(byte[] b, int off, int len) throws IOException
	{
		int ret;

		if(bytesUntilNextInfos == 0) {
			getIcyInfos();
		}

		int min = Math.min(bytesUntilNextInfos, len);
		ret = super.read(b, off, min);
		bytesUntilNextInfos -= ret;

		return ret;
	}

	private void readMetaInt(URLConnection conn)
	{
		for(int i = 0;; i++) {
			String headerName = conn.getHeaderFieldKey(i);
			String headerValue = conn.getHeaderField(i);

			if(headerName == null && headerValue == null) {
				break;
			}

			if(headerName != null && headerValue != null) {
				String output = MessageFormat.format("{0}: {1}", headerName,
						headerValue);
				Log.i(CLASSNAME, output);
			}

			if(headerName != null && headerName.equals("icy-metaint")) {
				metaint = Integer.parseInt(headerValue);
			}
		}
	}

	private void getIcyInfos() throws IOException
	{
		bytesUntilNextInfos = metaint;
		int length = super.read() * 16;
		Log.d(CLASSNAME, "Length:" + String.valueOf(length));
		if(length > 0) {
			byte[] metadata = new byte[length];
			for(int i = 0; i < length; i++) {
				metadata[i] = (byte) super.read();
			}
			String metadataString = new String(metadata);
			String artistAndTitle = StringUtils.substringBetween(
					metadataString, "=", ";");
			artistAndTitle = StringUtils.substring(artistAndTitle, 1, -1);
			// String[] infos =
			// StringUtils.splitByWholeSeparator(artistAndTitle, SEPARATOR);
			TrackInfos infos = new TrackInfos();
			infos.setAuthor(StringUtils.substringBefore(artistAndTitle,
					SEPARATOR));
			infos.setTitle(StringUtils
					.substringAfter(artistAndTitle, SEPARATOR));
			// infos contiene:
			// [0] == artista
			// [1] == titolo canzone
			// oppure se infos è grande 1, in [0] contiene il titolo
			// di un programma
			Log.i(CLASSNAME, metadataString);
			fireOnInfoEvent(infos);
		}
	}
}
