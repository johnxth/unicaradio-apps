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

import it.unicaradio.android.R;
import it.unicaradio.android.activities.MainActivity;
import it.unicaradio.android.enums.GcmMessagePriority;
import it.unicaradio.android.gcm.GcmServerRegister;
import it.unicaradio.android.gcm.GcmServerRpcCall;
import it.unicaradio.android.gcm.GcmServerUnregister;
import it.unicaradio.android.utils.UnicaradioPreferences;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

/**
 * @author Paolo Cortis
 */
public class GCMIntentService extends GCMBaseIntentService
{
	public static final String ACTION_GCM_MESSAGE = "it.unicaradio.android.intent.action.GCM_MESSAGE";

	private static final int NOTIFICATION_ID = 2;

	public GCMIntentService()
	{
		super(MainActivity.SENDER_ID, null);
	}

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
		boolean gcmMessagesDisabled = UnicaradioPreferences.areGcmMessagesDisabled(this);
		if(gcmMessagesDisabled) {
			Log.v(TAG, "Messages are disabled. Ignoring.");
			return;
		}
		if(shouldIgnoreMessage(arg1)) {
			Log.v(TAG, "Message cannot be notified. Ignoring.");
			return;
		}

		Notification notification = createNotification(arg1);
		sendNotification(notification);
	}

	/**
	 * @param priorityEnum
	 * @return
	 */
	private boolean shouldIgnoreMessage(Intent intent)
	{
		String intentPriority = intent.getStringExtra("priority");
		GcmMessagePriority priorityEnum = GcmMessagePriority.fromString(intentPriority);

		return priorityEnum == GcmMessagePriority.LOW && !UnicaradioPreferences.areGcmLowMessagesEnabled(this);
	}

	private Notification createNotification(Intent i)
	{
		String text = i.getStringExtra("message");
		String intentPriority = i.getStringExtra("priority");
		GcmMessagePriority priorityEnum = GcmMessagePriority.fromString(intentPriority);
		int priority = priorityEnum.toAndroidNotificationPriority();

		Log.v("GCM", "NOTIFYING A MESSAGE: \"" + text + "\" with priority: " + intentPriority);

		Intent intent = new Intent(ACTION_GCM_MESSAGE);
		intent.putExtra("text", text);

		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

		Builder b = new NotificationCompat.Builder(this);
		b.setContentTitle("Nuovo messaggio!");
		b.setContentText(text);
		b.setSmallIcon(R.drawable.ic_stat_notify);
		b.setContentIntent(pIntent);
		b.setWhen(System.currentTimeMillis());
		b.setPriority(priority);
		b.setAutoCancel(true);
		b.setVibrate(new long[] { 100L, 600L });

		return b.build();
	}

	private void sendNotification(Notification notification)
	{
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(NOTIFICATION_ID, notification);
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
