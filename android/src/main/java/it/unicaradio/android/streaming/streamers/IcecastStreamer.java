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
package it.unicaradio.android.streaming.streamers;

import it.unicaradio.android.streaming.buffer.ByteArrayBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;

import org.apache.commons.lang.StringUtils;

import android.util.Log;

/**
 * @author Paolo Cortis
 * 
 */
public class IcecastStreamer extends Streamable
{
	private static final String SEPARATOR = " - ";

	URL url;

	private boolean done;

	private boolean ready;

	public IcecastStreamer(URL url)
	{
		super();

		this.buffer = new ByteArrayBuffer();
		done = false;
		ready = false;

		startStreaming();
	}

	@Override
	public void startStreaming()
	{
		Thread t = new Thread(new Runnable() {

			public void run()
			{
				URLConnection conn;
				try {
					conn = url.openConnection();
					conn.addRequestProperty("Icy-MetaData", "1");
					InputStream inputStream = conn.getInputStream();

					int metaint = getMetaInt(conn);
					int count = 0;
					while(!done) {
						if(count == metaint) {
							count = 0;
							getIcyInfos(inputStream);
						}
						int readData = inputStream.read();
						buffer.add(readData);
						checkBuffer();
						count++;
					}
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		});
		t.start();
	}

	private void checkBuffer()
	{
		// 1300 is the size of about 20 mp3 frames at 192bpm, 44100Hz
		if(buffer.size() > 1300) {
			if(!ready) {
				fireOnBufferReadyEvent();
				ready = true;
			}
		} else {
			ready = false;
		}
		fireOnNewDataEvent();
	}

	private int getMetaInt(URLConnection conn)
	{
		int metaint = 0;

		for(int i = 0;; i++) {
			String headerName = conn.getHeaderFieldKey(i);
			String headerValue = conn.getHeaderField(i);

			if(headerName == null && headerValue == null) {
				break;
			}

			if(headerName != null && headerValue != null) {
				String output = MessageFormat.format("{0}: {1}", headerName,
						headerValue);
				Log.i(IcecastStreamer.class.getName(), output);
			}

			if(headerName != null && headerName.equals("icy-metaint")) {
				metaint = Integer.parseInt(headerValue);
			}
		}

		return metaint;
	}

	private void getIcyInfos(InputStream inputStream) throws IOException
	{
		int length = inputStream.read() * 16;
		if(length != 0) {
			byte[] metadata = new byte[length];
			inputStream.read(metadata, 0, length);
			String metadataString = new String(metadata);
			String artistAndTitle = StringUtils.substringBetween(
					metadataString, "'");
			String[] infos = StringUtils.splitByWholeSeparator(artistAndTitle,
					SEPARATOR);
			// infos contiene:
			// [0] == artista
			// [1] == titolo canzone
			// oppure se infos è grande 1, in [0] contiene il titolo
			// di un programma
			Log.i(IcecastStreamer.class.getName(), metadataString);
			fireOnInfoEvent(infos);
		}
	}

	@Override
	public void stopStreaming()
	{
		done = true;
	}
}
