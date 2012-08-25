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
package it.unicaradio.android.fragments;

import it.unicaradio.android.R;
import it.unicaradio.android.gui.TrackInfos;
import it.unicaradio.android.services.StreamingService;
import it.unicaradio.android.services.StreamingService.LocalBinder;
import it.unicaradio.android.utils.ImageUtils;
import it.unicaradio.android.utils.NetworkUtils;
import it.unicaradio.android.utils.StringUtils;

import java.io.IOException;
import java.text.MessageFormat;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Paolo Cortis
 */
public class StreamingFragment extends UnicaradioFragment
{
	private static final String TAG = StreamingFragment.class.getName();

	private static final String ONAIR_COVER_URL = "http://www.unicaradio.it/regia/OnAir.jpg";

	private final Handler mHandler = new Handler();

	private static String LOG = StreamingFragment.class.getName();

	private StreamingService streamingService;

	private TrackInfos oldInfos;

	private TrackInfos infos;

	private Thread imageThread;

	private boolean isStopped;

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
			if(isPlaying == false && isStopped == false) {
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
			if(oldInfos != null
					&& StringUtils.equals(author, oldInfos.getAuthor())
					&& StringUtils.equals(title, oldInfos.getTitle())) {
				infos = oldInfos;
				oldInfos = null;
				mHandler.post(mUpdateResults);
				return;
			}

			infos.setAuthor(author);
			infos.setTitle(title);
			mHandler.post(mUpdateResults);

			imageThread = new Thread(new LoadCoverRunnable());
			imageThread.start();
		}
	};

	private final BroadcastReceiver stopReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent)
		{
			stop();
		}
	};

	private final ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName arg0)
		{
			streamingService = null;

			unregisterReceivers();
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			streamingService = ((LocalBinder) service).getService();
			getActivity().registerReceiver(trackinforeceiver,
					new IntentFilter(StreamingService.ACTION_TRACK_INFO));
			getActivity().registerReceiver(stopReceiver,
					new IntentFilter(StreamingService.ACTION_STOP));
			if(streamingService.isPlaying()) {
				streamingService.notifyChange();
				setPauseButton();
			} else {
				setPlayButton();
			}
		}
	};

	/**
	 * {@inheritDoc}
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.main, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onResume()
	{
		super.onResume();

		if(infos == null) {
			infos = new TrackInfos(getActivity().getApplicationContext());
		}

		updateResultsInUi();

		if(streamingService == null) {
			Intent intent = new Intent(getActivity(), StreamingService.class);
			getActivity().startService(intent);
			getActivity().bindService(intent, serviceConnection,
					Context.BIND_AUTO_CREATE);
		}

		ImageButton playPauseButton = (ImageButton) getActivity().findViewById(
				R.id.playPauseButton);
		playPauseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0)
			{
				if(!NetworkUtils.isConnected(getActivity())) {
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPause()
	{
		Log.d(TAG, "StreamingFragment - onPause");
		synchronized(this) {
			oldInfos = new TrackInfos(getActivity().getApplicationContext());
			oldInfos.setTrackInfos(infos);
		}

		if(streamingService != null) {
			getActivity().unbindService(serviceConnection);
			streamingService = null;
		}

		unregisterReceivers();

		super.onPause();
	}

	private void updateResultsInUi()
	{
		try {
			TextView trackAuthor = (TextView) getActivity().findViewById(
					R.id.author);
			TextView trackTitle = (TextView) getActivity().findViewById(
					R.id.songTitle);
			ImageView cover = (ImageView) getActivity()
					.findViewById(R.id.cover);

			if(((streamingService == null) || !streamingService.isPlaying())) {
				synchronized(this) {
					infos.clean();
				}
			}

			synchronized(this) {
				if(trackAuthor != null) {
					trackAuthor.setText(infos.getAuthor());
				}
				if(trackTitle != null) {
					trackTitle.setText(infos.getTitle());
				}
				if(cover != null) {
					Bitmap bitmap = ImageUtils.resize(getActivity()
							.getWindowManager().getDefaultDisplay(), infos
							.getCover(), 60);
					cover.setImageBitmap(bitmap);
				}
			}
		} catch(NullPointerException e) {
			Log.d(TAG, e.getMessage(), e);
		}
	}

	private void play()
	{
		isStopped = false;
		if((streamingService != null) && !streamingService.isPlaying()) {
			streamingService.play();
		}

		setPauseButton();
	}

	private void stop()
	{
		isStopped = true;
		if((streamingService != null) && streamingService.isPlaying()) {
			streamingService.stop();
		}

		setPlayButton();
		infos.clean();
		mHandler.post(mUpdateResults);
	}

	private void setPlayButton()
	{
		ImageButton playPauseButton = (ImageButton) getActivity().findViewById(
				R.id.playPauseButton);
		playPauseButton.setImageResource(R.drawable.play);
	}

	private void setPauseButton()
	{
		final ImageButton playPauseButton = (ImageButton) getActivity()
				.findViewById(R.id.playPauseButton);
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

	private void unregisterReceivers()
	{
		try {
			getActivity().unregisterReceiver(trackinforeceiver);
		} catch(IllegalArgumentException e) {
			// do nothing
		}

		try {
			getActivity().unregisterReceiver(stopReceiver);
		} catch(IllegalArgumentException e) {
			// do nothing
		}
	}

	private final class LoadCoverRunnable implements Runnable
	{
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
					infos.setCover(ImageUtils.downloadFromUrl(ONAIR_COVER_URL));
					mHandler.post(mUpdateResults);
				} catch(IOException e) {
					Log.d(LOG, MessageFormat.format("Cannot find file {0}",
							ONAIR_COVER_URL));
				}
			}
		}
	}
}
