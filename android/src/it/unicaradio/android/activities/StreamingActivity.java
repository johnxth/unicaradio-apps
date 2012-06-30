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
import it.unicaradio.android.gui.Tabs;
import it.unicaradio.android.gui.TrackInfos;
import it.unicaradio.android.services.StreamingService;
import it.unicaradio.android.services.StreamingService.LocalBinder;
import it.unicaradio.android.utils.ImageUtils;

import java.io.IOException;
import java.text.MessageFormat;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Paolo Cortis
 * 
 */
public class StreamingActivity extends TabbedActivity
{
	private static final String ONAIR_COVER_URL = "http://www.unicaradio.it/regia/OnAir.jpg";

	private static String LOG = StreamingActivity.class.getName();

	private static StreamingService streamingService;

	private final Handler mHandler = new Handler();

	private TrackInfos infos;

	private Thread imageThread;

	private SharedPreferences preferences;

	private final Runnable mUpdateResults = new Runnable() {
		@Override
		public void run()
		{
			updateResultsInUi();
		}
	};

	protected BroadcastReceiver trackinforeceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent)
		{
			boolean isPlaying = intent.getBooleanExtra("isplaying", false);
			if(isPlaying == false) {
				stop();
			}
			String error = intent.getStringExtra("error");
			if(error.length() != 0) {
				stop();
				Log.d(LOG, error);
				showAlertDialog("Unicaradio", "E' avvenuto un errore");
			}

			String author = intent.getStringExtra("author");
			String title = intent.getStringExtra("title");
			infos.setAuthor(author);
			infos.setTitle(title);
			mHandler.post(mUpdateResults);
			imageThread = new Thread(new Runnable() {
				@Override
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
									"Cannot find file {0}", ONAIR_COVER_URL));
						}
					}
				}
			});
			imageThread.start();
		}
	};

	private final ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName arg0)
		{
			streamingService = null;
			unregisterReceiver(trackinforeceiver);
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			streamingService = ((LocalBinder) service).getService();
			registerReceiver(trackinforeceiver, new IntentFilter(
					StreamingService.ACTION_TRACK_INFO));
			if(streamingService.isPlaying()) {
				streamingService.notifyChange();
				setPauseButton();
			} else {
				setPlayButton();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState, R.layout.main);

		preferences = getPreferences(Context.MODE_PRIVATE);
		if(hasBeenUpdated()) {
			showUpdatesDialog();
		}
	}

	private void showUpdatesDialog()
	{
		final Dialog dialog = new Dialog(StreamingActivity.this);
		dialog.setContentView(R.layout.popup);
		dialog.setTitle("L'applicazione è stata aggiornata!");
		dialog.setCancelable(true);

		TextView textView = (TextView) dialog.findViewById(R.id.updatesText);
		textView.setText(R.string.updates);

		Button button = (Button) dialog.findViewById(R.id.updatesButton);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				dialog.hide();
			}
		});
		dialog.show();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		infos.clean();
		mHandler.post(mUpdateResults);

		if(streamingService == null) {
			Intent intent = new Intent(this, StreamingService.class);
			startService(intent);
			bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
		}
	}

	@Override
	protected void onPause()
	{
		if(streamingService != null) {
			unbindService(serviceConnection);
			streamingService = null;
		}
		try {
			unregisterReceiver(trackinforeceiver);
		} catch(IllegalArgumentException e) {
		}
		super.onPause();
	}

	@Override
	protected void setupTab()
	{
		infos = new TrackInfos(getApplicationContext());

		updateResultsInUi();
	}

	@Override
	protected void setupListeners()
	{
		final ImageButton playPauseButton = (ImageButton) findViewById(R.id.playPauseButton);
		playPauseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0)
			{
				if(!isConnected()) {
					showAlertDialog("Unicaradio",
							"Attenzione! Non sei connesso ad internet!");
					return;
				}
				if((streamingService != null) && !streamingService.isPlaying()) {
					play();
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
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.streaming_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);
		if(hasFocus) {
			if((streamingService != null) && streamingService.isPlaying()) {
				streamingService.notifyChange();
				setPauseButton();
			} else if(streamingService != null) {
				setPlayButton();
			}
		}
	}

	private void updateResultsInUi()
	{
		TextView trackAuthor = (TextView) findViewById(R.id.author);
		TextView trackTitle = (TextView) findViewById(R.id.songTitle);
		ImageView cover = (ImageView) findViewById(R.id.cover);

		if((streamingService == null) || !streamingService.isPlaying()) {
			infos.clean();
		}

		if(trackAuthor != null) {
			trackAuthor.setText(infos.getAuthor());
		}
		if(trackTitle != null) {
			trackTitle.setText(infos.getTitle());
		}
		if(cover != null) {
			Bitmap bitmap = ImageUtils.resize(getDisplay(), infos.getCover(),
					60);
			cover.setImageBitmap(bitmap);
		}
	}

	private void play()
	{
		if((streamingService != null) && !streamingService.isPlaying()) {
			streamingService.play();
			setPauseButton();
		}
	}

	private void stop()
	{
		if((streamingService != null) && streamingService.isPlaying()) {
			streamingService.stop();
			infos.clean();
			mHandler.post(mUpdateResults);
			setPlayButton();
		}
	}

	private void setPlayButton()
	{
		ImageButton playPauseButton = (ImageButton) findViewById(R.id.playPauseButton);
		playPauseButton.setImageResource(R.drawable.play);
	}

	private void setPauseButton()
	{
		final ImageButton playPauseButton = (ImageButton) findViewById(R.id.playPauseButton);
		playPauseButton.setImageResource(R.drawable.pause);
		playPauseButton.post(new Runnable() {
			@Override
			public void run()
			{
				StateListDrawable background = (StateListDrawable) playPauseButton
						.getDrawable();
				Drawable current = background.getCurrent();
				if(current instanceof AnimationDrawable) {
					AnimationDrawable btnAnimation = (AnimationDrawable) current;
					btnAnimation.start();
				}
			}
		});
	}

	private boolean hasBeenUpdated()
	{
		try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(
					getPackageName(), PackageManager.GET_META_DATA);
			long lastRunVersionCode = preferences.getLong("lastRunVersionCode",
					0);
			if(lastRunVersionCode < pInfo.versionCode) {
				Editor editor = preferences.edit();
				editor.putLong("lastRunVersionCode", pInfo.versionCode);
				editor.commit();

				return(lastRunVersionCode > 0);
			}
		} catch(NameNotFoundException e) {
			e.printStackTrace();
		}

		return false;
	}
}
