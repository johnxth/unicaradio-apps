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
package it.unicaradio.android.services;

import it.unicaradio.android.gcm.GcmServerRegister;
import it.unicaradio.android.gcm.GcmServerRpcCall;
import it.unicaradio.android.gcm.GcmServerUnregister;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

/**
 * @author Paolo Cortis
 * 
 */
public class GCMIntentService extends GCMBaseIntentService
{

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onError(Context arg0, String arg1)
	{
		// ignore
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onMessage(Context arg0, Intent arg1)
	{
		Log.d("GCM", "RECEIVED A MESSAGE: " + arg1.getStringExtra("message"));

		Intent i = new Intent("it.unicaradio.android.intents.NEW_MESSAGE");
		i.putExtra("message", arg1.getStringExtra("message"));
		sendBroadcast(i);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onRegistered(Context context, String registrationId)
	{
		GcmServerRpcCall rpcCall = new GcmServerRegister(context);
		rpcCall.execute(registrationId, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onUnregistered(Context context, String registrationId)
	{
		if(GCMRegistrar.isRegisteredOnServer(context)) {
			GcmServerRpcCall rpcCall = new GcmServerUnregister(context);
			rpcCall.execute(registrationId, null);
		} else {
			// This callback results from the call to unregister made on
			// ServerUtilities when the registration to the server failed.
			Log.i(TAG, "Ignoring unregister callback");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onDeletedMessages(Context context, int total)
	{
		// TODO Auto-generated method stub
		super.onDeletedMessages(context, total);
	}
}
