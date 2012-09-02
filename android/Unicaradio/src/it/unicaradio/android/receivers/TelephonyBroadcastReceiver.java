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
import it.unicaradio.android.enums.NetworkType;
import it.unicaradio.android.services.StreamingService;
import it.unicaradio.android.utils.UnicaradioPreferences;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/**
 * @author Paolo Cortis
 */
public class TelephonyBroadcastReceiver extends BroadcastReceiver
{
	private StreamingService streamingService;

	public TelephonyBroadcastReceiver(StreamingService streamingService)
	{
		this.streamingService = streamingService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onReceive(Context context, Intent intent)
	{
		PhoneStateListener phoneListener = new UnicaradioPhoneStateListener();

		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(phoneListener,
				PhoneStateListener.LISTEN_CALL_STATE);
		telephonyManager.listen(phoneListener,
				PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
	}

	private final class UnicaradioPhoneStateListener extends PhoneStateListener
	{
		@Override
		public void onCallStateChanged(int state, String incomingNumber)
		{
			switch(state) {
				case TelephonyManager.CALL_STATE_OFFHOOK:
					streamingService.stop();
					break;
				case TelephonyManager.CALL_STATE_RINGING:
					streamingService.stop();
					break;
			}
		}

		@Override
		public void onDataConnectionStateChanged(int state)
		{
			super.onDataConnectionStateChanged(state);

			TelephonyManager telephonyManager = (TelephonyManager) streamingService
					.getSystemService(Context.TELEPHONY_SERVICE);

			boolean isRoaming = telephonyManager.isNetworkRoaming();
			boolean isRoamingPermitted = UnicaradioPreferences
					.isRoamingPermitted(streamingService);
			boolean isConnectedToMobileData = TelephonyManager.DATA_CONNECTED == state;

			NetworkType networkType = UnicaradioPreferences
					.getNetworkType(streamingService);
			boolean isMobileDataPermitted = (networkType == NetworkType.MOBILE_DATA);

			if(isConnectedToMobileData && isRoaming && !isRoamingPermitted) {
				if(streamingService.isPlaying()) {
					Intent i = new Intent(StreamingService.ACTION_TOAST_MESSAGE);
					i.putExtra("message",
							R.string.toast_error_roaming_not_permitted);
					streamingService.sendBroadcast(i);
				}
				streamingService.stop();
			} else if(isConnectedToMobileData && !isMobileDataPermitted) {
				if(streamingService.isPlaying()) {
					Intent i = new Intent(StreamingService.ACTION_TOAST_MESSAGE);
					i.putExtra("message",
							R.string.toast_error_mobiledata_not_permitted);
					streamingService.sendBroadcast(i);
				}
				streamingService.stop();
			}
		}
	}
}
