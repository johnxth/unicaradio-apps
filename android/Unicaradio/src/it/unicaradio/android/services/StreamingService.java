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
import it.unicaradio.android.events.OnInfoListener;
import it.unicaradio.android.gui.TrackInfos;
import it.unicaradio.android.receivers.ConnectivityBroadcastReceiver;
import it.unicaradio.android.receivers.NoisyAudioStreamBroadcastReceiver;
import it.unicaradio.android.receivers.TelephonyBroadcastReceiver;
import it.unicaradio.android.streamers.IcecastStreamer;
import it.unicaradio.android.streamers.Streamer;
import it.unicaradio.android.utils.StringUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;

import org.slackcnt.android.slacknotification.SlackNotification;
import org.slackcnt.android.slacknotification.SlackNotification.Builder;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.spoledge.aacdecoder.AACPlayer;

/**
 * @author Paolo Cortis
 */
public class StreamingService extends Service
{
	private static final String LOG = StreamingService.class.getName();

	public static final String ACTION_TRACK_INFO = "it.unicaradio.android.intent.action.TRACK_INFO";

	public static final String ACTION_STOP = "it.unicaradio.android.intent.action.STOP";

	private static final String STREAM_URL = "http://streaming.unicaradio.it:80/unica64.aac";

	private static final int NOTIFICATION_ID = 1;

	private final IBinder mBinder = new LocalBinder();

	private final BroadcastReceiver connectivityReceiver;

	private final BroadcastReceiver telephonyReceiver;

	private final BroadcastReceiver noisyAudioStreamReceiver;

	private NotificationManager notificationManager;

	private URLConnection conn;

	private Streamer streamer;

	private AACPlayer player;

	private TrackInfos infos;

	private String error;

	private boolean isPlaying;

	public StreamingService()
	{
		infos = new TrackInfos();
		error = StringUtils.EMPTY;

		connectivityReceiver = new ConnectivityBroadcastReceiver(this);
		telephonyReceiver = new TelephonyBroadcastReceiver(this);
		noisyAudioStreamReceiver = new NoisyAudioStreamBroadcastReceiver(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate()
	{
		super.onCreate();
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return mBinder;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy()
	{
		stop();
		super.onDestroy();
	}

	public boolean isPlaying()
	{
		return isPlaying;
	}

	public void play()
	{
		if(conn == null) {
			initReceivers();
			internalPlay();
		} else {
			stop();
		}
	}

	public void stop()
	{
		disableReceivers();
		clearNotification();

		if(player != null) {
			player.stop();
			player = null;
			streamer = null;
			conn = null;
			isPlaying = false;
			infos.clean();
			notifyStop();
			notifyChange();
		}
	}

	private void initReceivers()
	{
		registerReceiver(connectivityReceiver, new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION));
		registerReceiver(telephonyReceiver, new IntentFilter(
				TelephonyManager.ACTION_PHONE_STATE_CHANGED));
		registerReceiver(noisyAudioStreamReceiver, new IntentFilter(
				AudioManager.ACTION_AUDIO_BECOMING_NOISY));
	}

	private void disableReceivers()
	{
		try {
			unregisterReceiver(connectivityReceiver);
		} catch(Exception e) {
			// do nothing
		}

		try {
			unregisterReceiver(telephonyReceiver);
		} catch(Exception e) {
			// do nothing
		}

		try {
			unregisterReceiver(noisyAudioStreamReceiver);
		} catch(Exception e) {
			// do nothing
		}
	}

	private void internalPlay()
	{
		if(conn == null) {
			Thread playThread = new Thread(new PlayThread());
			playThread.start();
		}
	}

	private void stopWithException(String message, Throwable t)
	{
		error = MessageFormat.format("{0}: {1}", message, t.getMessage());
		stop();
		Log.d(LOG, message, t);
	}

	private void stopWithException(Exception e)
	{
		stopWithException("E' avvenuto un problema. Riprova.", e);
	}

	public void notifyChange()
	{
		Intent i = new Intent(ACTION_TRACK_INFO);
		i.putExtra("author", infos.getAuthor());
		i.putExtra("title", infos.getTitle());
		i.putExtra("isplaying", isPlaying);
		i.putExtra("error", error);

		sendBroadcast(i);
		if(!infos.isClean()) {
			if(infos.getTitle().equals("")) {
				sendNotification(infos.getCleanedAuthor(), "");
			} else {
				sendNotification(infos.getTitle(), infos.getCleanedAuthor());
			}
		}
		error = StringUtils.EMPTY;
	}

	public void notifyStop()
	{
		Intent i = new Intent(ACTION_STOP);
		sendBroadcast(i);
	}

	private void clearNotification()
	{
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(NOTIFICATION_ID);
	}

	private void sendNotification(String title, String message)
	{
		Intent intent = new Intent(this, MainActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);

		Builder b = new SlackNotification.Builder(this);
		b.setContentTitle(title);
		b.setContentText(message);
		b.setSmallIcon(R.drawable.ic_stat_notify);
		b.setTicker(MessageFormat.format("{0}\n{1}", message, title));
		b.setContentIntent(pIntent);
		b.setWhen(System.currentTimeMillis());
		b.setOngoing(true);

		notificationManager.notify(NOTIFICATION_ID, b.getNotification());
	}

	private final class PlayThread implements Runnable
	{
		@Override
		public void run()
		{
			if(!connectToStreamingServer()) {
				return;
			}

			streamer.addOnInfoListener(new OnInfoListener() {
				@Override
				public void onInfo(TrackInfos trackInfos)
				{
					infos.setTrackInfos(trackInfos);
					notifyChange();
				}
			});

			player = new AACPlayer();

			try {
				isPlaying = true;
				player.play(streamer);
			} catch(Exception e) {
				stopWithException(e);
			}
		}

		private boolean connectToStreamingServer()
		{
			try {
				URL url = new URL(STREAM_URL);
				conn = url.openConnection();
				conn.addRequestProperty("Icy-MetaData", "1");
				conn.connect();
				streamer = new IcecastStreamer(conn);
			} catch(MalformedURLException e) {
				stopWithException(
						"Errore: l'indirizzo di streaming non è corretto.", e);
				return false;
			} catch(IOException e) {
				stopWithException(e);
				return false;
			}

			return true;
		}
	}

	public class LocalBinder extends Binder
	{
		public StreamingService getService()
		{
			return StreamingService.this;
		}
	}
}
