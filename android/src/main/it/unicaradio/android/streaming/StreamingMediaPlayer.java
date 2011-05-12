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

package it.unicaradio.android.streaming;

import it.unicaradio.android.streaming.events.OnInfoListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

/**
 * @author Paolo Cortis
 */
public class StreamingMediaPlayer extends Thread
{
	private static final String SEPARATOR = " - ";

	private final String urlString; // "http://streaming.unicaradio.it:80/unica64.aac"

	private int metaint;

	private final AudioTrack track;

	private final List<OnInfoListener> listenerList;

	public StreamingMediaPlayer(String url)
	{
		this.urlString = url;
		track = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
				AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
				8000, AudioTrack.MODE_STREAM);
		listenerList = new ArrayList<OnInfoListener>();
	}

	@Override
	public void run()
	{
		URL url;
		try {
			url = new URL(urlString);
			URLConnection conn = url.openConnection();
			for(int i = 0;; i++) {
				String headerName = conn.getHeaderFieldKey(i);
				String headerValue = conn.getHeaderField(i);

				if(headerName == null && headerValue == null) {
					break;
				}

				if(headerName != null && headerValue != null) {
					String output = MessageFormat.format("{0}: {1}",
							headerName, headerValue);
					System.out.println(output);
				}

				if(headerName != null && headerName.equals("icy-metaint")) {
					metaint = Integer.parseInt(headerValue);
				}
			}

			InputStream inputStream = conn.getInputStream();
			track.play();

			int count = 0;
			while(true) {
				if(count == metaint) {
					int length = inputStream.read() * 16;
					count = 0;
					if(length != 0) {
						byte[] metadata = new byte[length + 1];
						inputStream.read(metadata, 0, length);
						String metadataString = new String(metadata);
						String artistAndTitle = StringUtils.substringBetween(
								metadataString, "'");
						String[] infos = StringUtils.split(artistAndTitle,
								SEPARATOR);
						// infos contiene:
						// [0] == artista
						// [1] == titolo canzone
						// oppure se infos è grande 1, in [0] contiene il titolo
						// di un programma
						System.out.println(metadataString);
						fireOnInfoEvent(infos);
					}
				}
				int readData = inputStream.read();
				byte[] buffer = new byte[2];
				Arrays.fill(buffer, (byte) readData);
				track.write(buffer, 0, 1);
				count++;
			}
		} catch(MalformedURLException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public void addOnInfoListener(OnInfoListener listener)
	{
		listenerList.add(listener);
	}

	public void removeOnInfoListener(OnInfoListener listener)
	{
		listenerList.remove(listener);
	}

	private void fireOnInfoEvent(String infos[])
	{
		for(OnInfoListener listener : listenerList) {
			listener.onInfo(infos);
		}
	}
}
