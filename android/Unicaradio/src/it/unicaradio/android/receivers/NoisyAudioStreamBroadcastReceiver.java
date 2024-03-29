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
package it.unicaradio.android.receivers;

import it.unicaradio.android.services.StreamingService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

/**
 * @author Paolo Cortis
 */
public class NoisyAudioStreamBroadcastReceiver extends BroadcastReceiver
{
	private StreamingService streamingService;

	public NoisyAudioStreamBroadcastReceiver(StreamingService streamingService)
	{
		this.streamingService = streamingService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onReceive(Context context, Intent intent)
	{
		if(AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
			streamingService.stop();
		}
	}
}
