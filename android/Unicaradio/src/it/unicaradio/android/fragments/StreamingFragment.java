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

		private void checkIntentForErrors(Intent intent)
		{
			String error = intent.getStringExtra("error");
			if(error.length() != 0) {
				stop();
				Log.d(LOG, error);
				showAlertDialog("Unicaradio", "E' avvenuto un errore");
			}
		}

		private void updateTrackInfos(Intent intent)
		{
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

			downloadCover();
		}

		private void downloadCover()
		{
			if(canLoadCover()) {
				if(imageThread != null) {
					imageThread.stopThread();
				}

				imageThread = new StoppableThread(new LoadCoverRunnable());
				imageThread.start();
			}
		}

		private boolean canLoadCover()
		{
			CoverDownloadMode coverDownloadMode = UnicaradioPreferences
					.getCoverDownloadMode(getActivity());

			boolean connectedToWifi = NetworkUtils
					.isConnectedToWifi(getActivity());
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
			MainActivity mainActivity = (MainActivity) getActivity();

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
				Toast.makeText(getActivity(), message, Toast.LENGTH_LONG)
						.show();
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
			if(getActivity() == null) {
				return;
			}

			getActivity().registerReceiver(trackinforeceiver,
					new IntentFilter(StreamingService.ACTION_TRACK_INFO));
			getActivity().registerReceiver(stopReceiver,
					new IntentFilter(StreamingService.ACTION_STOP));
			getActivity().registerReceiver(toastMessageReceiver,
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
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

		ViewUtils.setRobotoFont(getActivity(), view.findViewById(R.id.author));
		ViewUtils.setRobotoFont(getActivity(),
				view.findViewById(R.id.songTitle));
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
				boolean isNotPlaying = (streamingService != null)
						&& !streamingService.isPlaying();
				if(isNotPlaying) {
					try {
						warnUserIfNotConnected();
					} catch(NotConnectedException e) {
						Log.d(TAG, e.getMessage(), e);
						showAlertDialog("Unicaradio",
								"Attenzione! Non sei connesso ad Internet!");
						return;
					} catch(WrongNetworkException e) {
						Log.d(TAG, e.getMessage(), e);
						showAlertDialog("Unicaradio",
								"Attenzione! Verifica a quale rete sei connesso");
						return;
					} catch(RoamingForbiddenException e) {
						Log.d(TAG, e.getMessage(), e);
						showAlertDialog("Unicaradio",
								"Attenzione! Sei in roaming!");
						return;
					}

					play();
				} else {
					stop();
				}
			}

			private void warnUserIfNotConnected() throws NotConnectedException,
					WrongNetworkException, RoamingForbiddenException
			{
				NetworkType networkType = UnicaradioPreferences
						.getNetworkType(getSherlockActivity());

				if(networkType == null) {
					throw new NotConnectedException("Unknown network");
				}

				boolean connectedToWifi = NetworkUtils
						.isConnectedToWifi(getActivity());
				boolean connectedToMobileData = NetworkUtils
						.isConnectedToMobileData(getActivity());
				switch(networkType) {
					case WIFI_ONLY:
						if(!connectedToWifi && !connectedToMobileData) {
							throw new NotConnectedException("Not connected");
						} else if(!connectedToWifi) {
							throw new WrongNetworkException(
									"Not connected to wifi");
						}
						break;

					case MOBILE_DATA:
						if(!connectedToMobileData && !connectedToWifi) {
							throw new NotConnectedException(
									"Not connected neither to wifi nor to mobile");
						} else if(connectedToMobileData) {
							warnUserIfInRoamingAndNotPermitted();
						}
						break;

					default:
						throw new WrongNetworkException("Unknown network");
				}
			}

			private void warnUserIfInRoamingAndNotPermitted()
					throws RoamingForbiddenException
			{
				TelephonyManager telephony = (TelephonyManager) getActivity()
						.getSystemService(Context.TELEPHONY_SERVICE);

				boolean isRoamingPermitted = UnicaradioPreferences
						.isRoamingPermitted(getActivity());
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
			oldInfos = new TrackInfos(getActivity().getApplicationContext());
			oldInfos.setTrackInfos(infos);
		}

		unregisterReceivers();

		if(streamingService != null) {
			getActivity().unbindService(serviceConnection);
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

	private void updateAuthorAndTitleInUi()
	{
		TextView trackAuthor = (TextView) getActivity().findViewById(
				R.id.author);
		TextView trackTitle = (TextView) getActivity().findViewById(
				R.id.songTitle);

		if(trackTitle == null) {
			return;
		}

		boolean isInfoNotBlank = !infos.isClean();
		if(trackAuthor != null) {
			boolean isDeviceATablet = getActivity().findViewById(
					R.id.tablet_layout) != null;
			trackAuthor.setText(StringUtils.EMPTY);
			if(isDeviceATablet && isInfoNotBlank) {
				trackAuthor.setText(infos.getAuthor());
				showAuthorAndTitleFields();
			} else if(isDeviceATablet) {
				hideAuthorAndTitleFields();
			} else {
				trackAuthor.setText("- " + infos.getAuthor() + " -");
			}

			trackTitle.setText(infos.getTitle());
		} else {
			String currentlyOnAir = infos.getAuthor();
			if(isInfoNotBlank && !StringUtils.isEmpty(infos.getTitle())) {
				currentlyOnAir += " - " + infos.getTitle();
			} else if(infos.isClean()) {
				currentlyOnAir = "- " + currentlyOnAir + " -";
			}

			trackTitle.setText(currentlyOnAir);
		}
	}

	private void showAuthorAndTitleFields()
	{
		getActivity().findViewById(R.id.author_label).setVisibility(
				View.VISIBLE);
		getActivity().findViewById(R.id.title_label)
				.setVisibility(View.VISIBLE);
	}

	private void hideAuthorAndTitleFields()
	{
		getActivity().findViewById(R.id.author_label).setVisibility(View.GONE);
		getActivity().findViewById(R.id.title_label).setVisibility(View.GONE);
	}

	private void updateCoverInUi()
	{
		ImageView cover = (ImageView) getActivity().findViewById(R.id.cover);
		if(cover != null) {
			// Bitmap bitmap = ImageUtils.resize(getActivity()
			// .getWindowManager().getDefaultDisplay(), infos
			// .getCover(), 60);
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
					btnAnimation.stop();
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

		try {
			getActivity().unregisterReceiver(toastMessageReceiver);
		} catch(IllegalArgumentException e) {
			// do nothing
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

				mHandler.post(mUpdateResults);
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
					mHandler.post(mUpdateResults);
				} catch(IOException e) {
					Log.d(LOG, MessageFormat.format("Cannot find file {0}",
							ONAIR_COVER_URL));
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
