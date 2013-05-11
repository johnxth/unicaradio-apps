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
import it.unicaradio.android.activities.MainActivity;
import it.unicaradio.android.enums.CoverDownloadMode;
import it.unicaradio.android.enums.NetworkType;
import it.unicaradio.android.exceptions.NotConnectedException;
import it.unicaradio.android.exceptions.RoamingForbiddenException;
import it.unicaradio.android.exceptions.WrongNetworkException;
import it.unicaradio.android.gui.TrackInfos;
import it.unicaradio.android.services.StreamingService;
import it.unicaradio.android.services.StreamingService.LocalBinder;
import it.unicaradio.android.utils.ImageUtils;
import it.unicaradio.android.utils.NetworkUtils;
import it.unicaradio.android.utils.StringUtils;
import it.unicaradio.android.utils.UnicaradioPreferences;
import it.unicaradio.android.utils.ViewUtils;

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
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Paolo Cortis
 */
public class StreamingFragment extends UnicaradioFragment
{
	private static final String TAG = StreamingFragment.class.getSimpleName();

	private static final String ONAIR_COVER_URL = "http://www.unicaradio.it/regia/OnAir.jpg";

	private final Handler mHandler = new Handler();

	private static String LOG = StreamingFragment.class.getName();

	private StreamingService streamingService;

	private static TrackInfos oldInfos;

	private static TrackInfos infos;

	private StoppableThread imageThread;

	private boolean isStopped;

	private final Runnable mUpdateResults = new Runnable() {
		@Override
		public void run()
		{
			updateResultsInUi();
		}
	};

	private final Runnable mUpdateCover = new Runnable() {
		@Override
		public void run()
		{
			updateOnlyCoverInUi();
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

			checkIntentForErrors(intent);

			updateTrackInfos(intent);

			updateShareIntent(isPlaying);
		}

		private void updateTrackInfos(Intent intent)
		{
			Log.v(TAG, "Updating track info");

			String author = intent.getStringExtra("author");
			String title = intent.getStringExtra("title");

			Log.d(TAG, "Author: " + author);
			Log.d(TAG, "Title: " + title);

			if(oldInfos != null && StringUtils.equals(author, oldInfos.getAuthor())
					&& StringUtils.equals(title, oldInfos.getTitle())) {
				Log.v(TAG, "new info equals old");
				infos = oldInfos;
				oldInfos = null;
				mHandler.post(mUpdateResults);

				return;
			}

			infos.setAuthor(author);
			infos.setTitle(title);
			mHandler.post(mUpdateResults);

			downloadCover();
		}

		private void downloadCover()
		{
			if(canLoadCover()) {
				Log.v(TAG, "downloading cover");
				if(imageThread != null) {
					imageThread.stopThread();
				}

				imageThread = new StoppableThread(new LoadCoverRunnable());
				imageThread.start();
			}
		}

		private boolean canLoadCover()
		{
			CoverDownloadMode coverDownloadMode = UnicaradioPreferences.getCoverDownloadMode(getMainActivityContext());

			boolean connectedToWifi = NetworkUtils.isConnectedToWifi(getMainActivityContext());
			switch(coverDownloadMode) {
				case MOBILE_DATA:
					return true;

				case WIFI_ONLY:
					return connectedToWifi;

				case NEVER:
					return false;

				default:
					return false;
			}
		}

		private void updateShareIntent(boolean isPlaying)
		{
			MainActivity mainActivity = getMainActivity();

			if(isPlaying) {
				mainActivity.setSharingIntentWithInfos(infos);
			} else {
				mainActivity.setDefaultSharingIntent();
			}
		}
	};

	private final BroadcastReceiver stopReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent)
		{
			stop();

			checkIntentForErrors(intent);
		}
	};

	private final BroadcastReceiver toastMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent)
		{
			int messageId = intent.getIntExtra("message", 0);
			Log.d(TAG, "Received toast to show: " + messageId);
			String message = getString(messageId);
			Log.d(TAG, "toast string: " + message);

			if(message != null) {
				Toast.makeText(getMainActivityContext(), message, Toast.LENGTH_LONG).show();
			}
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

			getMainActivity().registerReceiver(trackinforeceiver, new IntentFilter(StreamingService.ACTION_TRACK_INFO));
			getMainActivity().registerReceiver(stopReceiver, new IntentFilter(StreamingService.ACTION_STOP));
			getMainActivity().registerReceiver(toastMessageReceiver,
					new IntentFilter(StreamingService.ACTION_TOAST_MESSAGE));
			if(streamingService.isPlaying()) {
				streamingService.notifyView();
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Log.v(TAG, "onCreateView");
		return inflater.inflate(R.layout.streaming, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onDestroyView()
	{
		Log.v(TAG, "onDestroyView");
		super.onDestroyView();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onDestroy()
	{
		Log.v(TAG, "onDestroy");
		super.onDestroy();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		ViewUtils.setRobotoFont(getMainActivityContext(), view.findViewById(R.id.author));
		ViewUtils.setRobotoFont(getMainActivityContext(), view.findViewById(R.id.songTitle));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onResume()
	{
		super.onResume();

		if(infos == null) {
			infos = new TrackInfos(getMainActivity().getApplicationContext());
		}

		updateResultsInUi();

		if(streamingService == null) {
			Intent intent = new Intent(getMainActivityContext(), StreamingService.class);
			getMainActivity().startService(intent);
			getMainActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
		}

		ImageButton playPauseButton = (ImageButton) getMainActivity().findViewById(R.id.playPauseButton);
		playPauseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0)
			{
				boolean isNotPlaying = (streamingService != null) && !streamingService.isPlaying();
				if(isNotPlaying) {
					try {
						warnUserIfNotConnected();
					} catch(NotConnectedException e) {
						Log.d(TAG, e.getMessage(), e);
						showAlertDialog("Unicaradio", "Attenzione! Non sei connesso ad Internet!");
						return;
					} catch(WrongNetworkException e) {
						Log.d(TAG, e.getMessage(), e);
						showAlertDialog("Unicaradio", "Attenzione! Verifica a quale rete sei connesso");
						return;
					} catch(RoamingForbiddenException e) {
						Log.d(TAG, e.getMessage(), e);
						showAlertDialog("Unicaradio", "Attenzione! Sei in roaming!");
						return;
					}

					play();
				} else {
					stop();
				}
			}

			private void warnUserIfNotConnected() throws NotConnectedException, WrongNetworkException,
					RoamingForbiddenException
			{
				NetworkType networkType = UnicaradioPreferences.getNetworkType(getSherlockActivity());

				if(networkType == null) {
					throw new NotConnectedException("Unknown network");
				}

				boolean connectedToWifi = NetworkUtils.isConnectedToWifi(getMainActivityContext());
				boolean connectedToMobileData = NetworkUtils.isConnectedToMobileData(getMainActivityContext());
				switch(networkType) {
					case WIFI_ONLY:
						if(!connectedToWifi && !connectedToMobileData) {
							throw new NotConnectedException("Not connected");
						} else if(!connectedToWifi) {
							throw new WrongNetworkException("Not connected to wifi");
						}
						break;

					case MOBILE_DATA:
						if(!connectedToMobileData && !connectedToWifi) {
							throw new NotConnectedException("Not connected neither to wifi nor to mobile");
						} else if(connectedToMobileData) {
							warnUserIfInRoamingAndNotPermitted();
						}
						break;

					default:
						throw new WrongNetworkException("Unknown network");
				}
			}

			private void warnUserIfInRoamingAndNotPermitted() throws RoamingForbiddenException
			{
				TelephonyManager telephony = (TelephonyManager) getMainActivityContext().getSystemService(
						Context.TELEPHONY_SERVICE);

				boolean isRoamingPermitted = UnicaradioPreferences.isRoamingPermitted(getMainActivityContext());
				if(!isRoamingPermitted && telephony.isNetworkRoaming()) {
					throw new RoamingForbiddenException("Unknown network");
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
			oldInfos = new TrackInfos(getMainActivityContext().getApplicationContext());
			oldInfos.setTrackInfos(infos);
		}

		unregisterReceivers();

		if(streamingService != null) {
			getMainActivity().unbindService(serviceConnection);
			streamingService = null;
		}

		super.onPause();
	}

	private void updateResultsInUi()
	{
		try {
			if(((streamingService == null) || !streamingService.isPlaying())) {
				synchronized(this) {
					infos.clean();
				}
			}

			synchronized(this) {
				updateAuthorAndTitleInUi();
				updateCoverInUi();
			}
		} catch(NullPointerException e) {
			Log.d(TAG, e.getMessage(), e);
		}
	}

	private void updateOnlyCoverInUi()
	{
		try {
			if(((streamingService == null) || !streamingService.isPlaying())) {
				synchronized(this) {
					infos.clean();
				}
			}

			synchronized(this) {
				updateCoverInUi();
			}
		} catch(NullPointerException e) {
			Log.d(TAG, e.getMessage(), e);
		}
	}

	private void updateAuthorAndTitleInUi()
	{
		TextView trackAuthor = (TextView) getMainActivity().findViewById(R.id.author);
		TextView trackTitle = (TextView) getMainActivity().findViewById(R.id.songTitle);

		if(trackTitle == null) {
			return;
		}

		if(trackAuthor != null) {
			updateAuthorAndTitleSeparateFields(trackAuthor, trackTitle);
		} else {
			updateAuthorAndTitleOneField(trackTitle);
		}
	}

	private void updateAuthorAndTitleSeparateFields(TextView trackAuthor, TextView trackTitle)
	{
		boolean isDeviceATablet = isDeviceATablet();

		trackAuthor.setText(StringUtils.EMPTY);
		if(isDeviceATablet && infos.isClean()) {
			hideAuthorAndTitleFields();
		} else if(isDeviceATablet) {
			hideAuthorAndTitleFields();
			if(StringUtils.isEmpty(infos.getTitle())) {
				showOnAirField();
			} else {
				showAuthorAndTitleFields();
			}

			trackAuthor.setText(infos.getAuthor());
		} else {
			trackAuthor.setText("- " + infos.getAuthor() + " -");
		}

		trackTitle.setText(infos.getTitle());
	}

	private void updateAuthorAndTitleOneField(TextView trackTitle)
	{
		boolean isInfoNotBlank = !infos.isClean();
		String currentlyOnAir = infos.getAuthor();

		if(isInfoNotBlank && !StringUtils.isEmpty(infos.getTitle())) {
			currentlyOnAir += " - " + infos.getTitle();
		} else if(infos.isClean()) {
			currentlyOnAir = "- " + currentlyOnAir + " -";
		}

		trackTitle.setText(currentlyOnAir);
	}

	private boolean isDeviceATablet()
	{
		return getMainActivity().findViewById(R.id.tablet_layout) != null;
	}

	private void showAuthorAndTitleFields()
	{
		TextView author_label = (TextView) getMainActivity().findViewById(R.id.author_label);
		author_label.setVisibility(View.VISIBLE);
		author_label.setText(R.string.streaming_author);
		getMainActivity().findViewById(R.id.title_label).setVisibility(View.VISIBLE);
	}

	private void showOnAirField()
	{
		TextView author_label = (TextView) getMainActivity().findViewById(R.id.author_label);
		author_label.setVisibility(View.VISIBLE);
		author_label.setText(R.string.streaming_onair);
	}

	private void hideAuthorAndTitleFields()
	{
		getMainActivity().findViewById(R.id.author_label).setVisibility(View.GONE);
		getMainActivity().findViewById(R.id.title_label).setVisibility(View.GONE);
	}

	private void updateCoverInUi()
	{
		ImageView cover = (ImageView) getMainActivity().findViewById(R.id.cover);
		if(cover != null) {
			Bitmap bitmap = infos.getCover();
			cover.setImageBitmap(bitmap);
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

		if(imageThread != null) {
			imageThread.stopThread();
		}
	}

	private void setPlayButton()
	{
		ImageButton playPauseButton = (ImageButton) getMainActivity().findViewById(R.id.playPauseButton);
		playPauseButton.setImageResource(R.drawable.play);
	}

	private void setPauseButton()
	{
		final ImageButton playPauseButton = (ImageButton) getMainActivity().findViewById(R.id.playPauseButton);
		playPauseButton.setImageResource(R.drawable.pause);
		playPauseButton.post(new Runnable() {
			@Override
			public void run()
			{
				StateListDrawable background = (StateListDrawable) playPauseButton.getDrawable();
				Drawable current = background.getCurrent();
				if(current instanceof AnimationDrawable) {
					AnimationDrawable btnAnimation = (AnimationDrawable) current;
					btnAnimation.stop();
					btnAnimation.start();
				}
			}
		});
	}

	private void unregisterReceivers()
	{
		try {
			getMainActivity().unregisterReceiver(trackinforeceiver);
		} catch(IllegalArgumentException e) {
			// do nothing
		}

		try {
			getMainActivity().unregisterReceiver(stopReceiver);
		} catch(IllegalArgumentException e) {
			// do nothing
		}

		try {
			getMainActivity().unregisterReceiver(toastMessageReceiver);
		} catch(IllegalArgumentException e) {
			// do nothing
		}
	}

	private void checkIntentForErrors(Intent intent)
	{
		String error = intent.getStringExtra("error");
		if(!StringUtils.isEmpty(error)) {
			stop();
			Log.d(LOG, error);
			showAlertDialog("Unicaradio", "E' avvenuto un errore. Per favore, riprova.");
		}
	}

	private interface StoppableRunnable extends Runnable
	{
		void stop();
	}

	public class StoppableThread extends Thread
	{
		private StoppableRunnable runnable;

		/**
		 * @param runnable
		 * @param threadName
		 */
		public StoppableThread(StoppableRunnable runnable, String threadName)
		{
			super(runnable, threadName);

			this.runnable = (StoppableRunnable) runnable;
		}

		/**
		 * @param runnable
		 */
		public StoppableThread(StoppableRunnable runnable)
		{
			super(runnable);
			this.runnable = (StoppableRunnable) runnable;
		}

		public void stopThread()
		{
			this.runnable.stop();
		}
	}

	private final class LoadCoverRunnable implements StoppableRunnable
	{
		private boolean mustStop = false;

		@Override
		public void run()
		{
			synchronized(this) {
				infos.setCover(null);

				Log.v(TAG, "resetting cover in view");
				mHandler.post(mUpdateCover);
				if(mustStop) {
					return;
				}
				try {
					wait(5000);
				} catch(InterruptedException e) {
					Log.d(LOG, "Thread interrotto", e);
				}
				if(mustStop) {
					return;
				}

				try {
					infos.setCover(ImageUtils.downloadFromUrl(ONAIR_COVER_URL));
					if(mustStop) {
						return;
					}
					mHandler.post(mUpdateCover);
					Log.v(TAG, "updated cover in view");
				} catch(IOException e) {
					Log.d(LOG, MessageFormat.format("Cannot find file {0}", ONAIR_COVER_URL));
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void stop()
		{
			mustStop = true;
		}
	}
}
