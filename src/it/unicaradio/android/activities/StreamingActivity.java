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
package it.unicaradio.android.activities;

import it.unicaradio.android.R;
import it.unicaradio.android.events.OnInfoListener;
import it.unicaradio.android.gui.ImageUtils;
import it.unicaradio.android.gui.Tabs;
import it.unicaradio.android.gui.TrackInfos;
import it.unicaradio.android.streamers.IcecastStreamer;
import it.unicaradio.android.streamers.Streamer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.spoledge.aacplayer.AACPlayer;
import com.spoledge.aacplayer.ArrayAACPlayer;
import com.spoledge.aacplayer.ArrayDecoder;
import com.spoledge.aacplayer.Decoder;

/**
 * @author Paolo Cortis
 * 
 */
public class StreamingActivity extends TabbedActivity
{
	private static String LOG = StreamingActivity.class.getName();

	private static final String STREAM_URL = "http://streaming.unicaradio.it:80/unica64.aac";

	private static final String ONAIR_COVER_URL = "http://www.unicaradio.it/regia/OnAir.jpg";

	private URLConnection conn;

	private Streamer streamer;

	private AACPlayer player;

	private TrackInfos infos;

	private Thread imageThread;

	private final Handler mHandler = new Handler();

	final Runnable mUpdateResults = new Runnable() {
		public void run()
		{
			updateResultsInUi();
		}
	};

	private final BroadcastReceiver connectivityBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent)
		{
			boolean noConnectivity = intent.getBooleanExtra(
					ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

			if(noConnectivity) {
				stop();
				showAlertDialog("Unicaradio",
						"Attenzione. Non sei connesso ad Internet.");
			}
		}
	};

	private final BroadcastReceiver telephonyBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent)
		{
			PhoneStateListener phoneListener = new PhoneStateListener() {
				@Override
				public void onCallStateChanged(int state, String incomingNumber)
				{
					switch(state) {
						case TelephonyManager.CALL_STATE_OFFHOOK:
							stop();
							break;
						case TelephonyManager.CALL_STATE_RINGING:
							stop();
							break;
					}
				}
			};
			TelephonyManager telephony = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			telephony.listen(phoneListener,
					PhoneStateListener.LISTEN_CALL_STATE);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.d(StreamingActivity.class.getName(), "Called StreamingActivity");
		super.onCreate(savedInstanceState, R.layout.main);
	}

	@Override
	protected void setupTab()
	{
		infos = new TrackInfos(getApplicationContext());

		updateResultsInUi();

		registerReceiver(connectivityBroadcastReceiver, new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION));
		registerReceiver(telephonyBroadcastReceiver, new IntentFilter(
				TelephonyManager.ACTION_PHONE_STATE_CHANGED));
	}

	@Override
	protected void setupListeners()
	{
		final ImageButton playPauseButton = (ImageButton) findViewById(R.id.playPauseButton);
		playPauseButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0)
			{
				if(!isConnected()) {
					showAlertDialog("Unicaradio",
							"Attenzione! Non sei connesso ad internet!");
					return;
				}
				playPauseButton
						.setImageResource(android.R.drawable.ic_media_pause);
				if(conn == null) {
					try {
						URL url = new URL(STREAM_URL);
						play(url);
					} catch(MalformedURLException e) {
						stopWithException(
								"Errore: l'indirizzo di streaming non è corretto.",
								e);
					} catch(IOException e) {
						stopWithException(e);
					}
				} else {
					stop();
				}
			}
		});
	}

	@Override
	public int getTab()
	{
		return Tabs.STREAMING;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle item selection
		switch(item.getItemId()) {
			case R.id.exit:
				stop();
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void updateResultsInUi()
	{
		TextView trackAuthor = (TextView) findViewById(R.id.author);
		TextView trackTitle = (TextView) findViewById(R.id.songTitle);
		ImageView cover = (ImageView) findViewById(R.id.cover);
		ImageButton playPauseButton = (ImageButton) findViewById(R.id.playPauseButton);

		if(player == null) {
			infos.clean();
			playPauseButton.setImageResource(android.R.drawable.ic_media_play);
		}

		if(trackAuthor != null) {
			trackAuthor.setText(infos.getAuthor());
		}
		if(trackTitle != null) {
			trackTitle.setText(infos.getTitle());
		}
		if(cover != null) {
			Bitmap bitmap = ImageUtils.resize(getDisplay(), infos.getCover(),
					65);
			cover.setImageBitmap(bitmap);
		}
	}

	private void play(URL url) throws IOException
	{
		if(conn == null) {
			conn = url.openConnection();
			conn.addRequestProperty("Icy-MetaData", "1");
			conn.connect();
			streamer = new IcecastStreamer(conn);

			streamer.addOnInfoListener(new OnInfoListener() {
				public void onInfo(TrackInfos trackInfos)
				{
					infos.setTrackInfos(trackInfos);
					mHandler.post(mUpdateResults);
					imageThread = new Thread(new Runnable() {
						public void run()
						{
							synchronized(this) {
								infos.setCover(null);
								mHandler.post(mUpdateResults);
								try {
									wait(5000);
								} catch(InterruptedException e) {
									Log.d(LOG, "Thread interrotto", e);
								}
								try {
									infos.setCover(ImageUtils
											.downloadFromUrl(ONAIR_COVER_URL));
									mHandler.post(mUpdateResults);
								} catch(IOException e) {
									Log.d(LOG, MessageFormat.format(
											"Cannot find file {0}",
											ONAIR_COVER_URL));
								}
							}
						}
					});
					imageThread.start();
				}
			});

			player = new ArrayAACPlayer(
					ArrayDecoder.create(Decoder.DECODER_OPENCORE));

			Thread playThread = new Thread(new Runnable() {
				public void run()
				{
					try {
						player.play(streamer);
					} catch(Exception e) {
						stopWithException(e);
					}
				}
			});
			playThread.start();
		}
	}

	private void stop()
	{
		infos.clean();
		mHandler.post(mUpdateResults);
		if(player != null) {
			ImageButton playPauseButton = (ImageButton) findViewById(R.id.playPauseButton);
			playPauseButton.setImageResource(android.R.drawable.ic_media_play);
			player.stop();
			player = null;
		}
		streamer = null;
		conn = null;
	}

	private void stopWithException(String message, Exception e)
	{
		stop();
		showAlertDialog("Unicaradio", message);
		Log.d(LOG, message, e);
	}

	private void stopWithException(Exception e)
	{
		stopWithException("E' avvenuto un problema. Riprova.", e);
	}

	@Override
	protected void onDestroy()
	{
		unregisterReceiver(connectivityBroadcastReceiver);
		unregisterReceiver(telephonyBroadcastReceiver);
		super.onDestroy();
	}

}
