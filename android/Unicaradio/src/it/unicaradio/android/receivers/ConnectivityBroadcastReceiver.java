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

import it.unicaradio.android.R;
import it.unicaradio.android.services.StreamingService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

/**
 * @author Paolo Cortis
 */
public class ConnectivityBroadcastReceiver extends BroadcastReceiver
{
	private StreamingService streamingService;

	public ConnectivityBroadcastReceiver(StreamingService streamingService)
	{
		this.streamingService = streamingService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onReceive(Context context, Intent intent)
	{
		boolean noConnectivity = intent.getBooleanExtra(
				ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

		if(noConnectivity) {
			stopWithMessage(R.string.toast_error_connection_lost);
		}
	}

	private void stopWithMessage(int messageId)
	{
		if(streamingService.isPlaying()) {
			Intent i = new Intent(StreamingService.ACTION_TOAST_MESSAGE);
			i.putExtra("message", messageId);
			streamingService.sendBroadcast(i);
		}

		streamingService.stop();
	}
}
