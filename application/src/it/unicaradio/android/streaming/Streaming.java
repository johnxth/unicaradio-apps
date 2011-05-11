package it.unicaradio.android.streaming;

import java.io.IOException;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class Streaming extends Activity
{
	private MediaPlayer mediaPlayer;

	private ImageView playPauseButton;

	private static final String STREAM_URL = "http://streaming.unicaradio.it:8000";

	// private static final String STREAM_URL =
	// "http://www.pocketjourney.com/downloads/pj/tutorials/audio.mp3";

	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent)
		{
			boolean noConnectivity = intent.getBooleanExtra(
					ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
			if(noConnectivity) {
				Toast.makeText(Streaming.this,
						"Attenzione. Non sei connesso ad Internet.",
						Toast.LENGTH_LONG);
			} else {
				play();
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		playPauseButton = (ImageView) findViewById(R.id.playPauseButton);
		playPauseButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view)
			{
				if(mediaPlayer.isPlaying()) {
					mediaPlayer.stop();
				} else {
					mediaPlayer.start();
				}
			}
		});

		registerReceiver(broadcastReceiver, new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION));

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		boolean isConnected = cm.getActiveNetworkInfo() != null && cm
				.getActiveNetworkInfo().isConnected() || false;

		if(isConnected) {
			play();
		} else {
			Toast.makeText(Streaming.this,
					"Attenzione. Non sei connesso ad Internet.",
					Toast.LENGTH_LONG);
		}

	}

	private void play()
	{
		if(mediaPlayer == null) {
			try {
				mediaPlayer = new MediaPlayer();
				mediaPlayer.setDataSource(STREAM_URL);
				mediaPlayer.prepareAsync();
				mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

					@Override
					public void onPrepared(MediaPlayer player)
					{
						player.start();
						ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar1);
						progressBar.setVisibility(View.GONE);
						playPauseButton.setImageResource(R.drawable.stop);
					}
				});
				mediaPlayer.setOnInfoListener(new OnInfoListener() {

					@Override
					public boolean onInfo(MediaPlayer player, int what,
							int extra)
					{
						if(what == MediaPlayer.MEDIA_INFO_METADATA_UPDATE) {

							return true;
						}
						return false;
					}
				});
			} catch(IllegalArgumentException e) {
				e.printStackTrace();
			} catch(IllegalStateException e) {
				e.printStackTrace();
			} catch(IOException e) {
				e.printStackTrace();
			}
		} else {
			// TODO
		}
	}
}
