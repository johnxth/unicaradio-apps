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
package it.unicaradio.android.streaming.decoders;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;

/**
 * @author paolo.cortis
 * 
 */
public class MP3Decoder extends AbstractDecoder
{
	private boolean ready;

	public MP3Decoder()
	{
		super();
		ready = false;
	}

	/**
	 * {@inheritDoc}
	 */
	public void decodeFrame()
	{
		Bitstream stream = new Bitstream(streamer.getInputStream());
		Decoder decoder = new Decoder();

		try {
			Header header = stream.readFrame();
			SampleBuffer output = (SampleBuffer) decoder.decodeFrame(header,
					stream);
			short[] pcm = output.getBuffer();
			for(short s : pcm) {
				buffer.add(s & 0xff);
				buffer.add((s >> 8) & 0xff);
				checkBuffer();
			}
			stream.closeFrame();
		} catch(BitstreamException e) {
			e.printStackTrace();
		} catch(DecoderException e) {
			e.printStackTrace();
		}

	}

	private void checkBuffer()
	{
		// 1300 is the size of about 200 mp3 frames at 192bpm, 44100Hz
		if(buffer.size() > 13000) {
			if(!ready) {
				fireOnBufferReadyEvent();
				ready = true;
			}
		} else {
			ready = false;
		}
		fireOnNewDataEvent();
	}
}
