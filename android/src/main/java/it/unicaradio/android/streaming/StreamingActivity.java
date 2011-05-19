package it.unicaradio.android.streaming;

import it.unicaradio.android.streaming.decoders.AbstractDecoder;
import it.unicaradio.android.streaming.decoders.MP3Decoder;
import it.unicaradio.android.streaming.events.OnBufferReadyListener;
import it.unicaradio.android.streaming.events.OnInfoListener;
import it.unicaradio.android.streaming.events.OnNewDataListener;
import it.unicaradio.android.streaming.streamers.IcecastStreamer;
import it.unicaradio.android.streaming.streamers.Streamable;

import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class StreamingActivity extends Activity
{
	Streamable streamer;

	AbstractDecoder decoder;

	AudioTrack player;

	private ImageView playPauseButton;

	private TextView trackAuthor;

	private TextView trackTitle;

	private String[] trackInfos;

	final Handler mHandler = new Handler();

	final Runnable mUpdateResults = new Runnable() {
		public void run()
		{
			updateResultsInUi();
		}
	};

	private static final String STREAM_URL = "http://streaming.unicaradio.it:80/unica192.mp3";

	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent)
		{
			boolean noConnectivity = intent.getBooleanExtra(
					ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
			if(noConnectivity) {
				Toast.makeText(StreamingActivity.this,
						"Attenzione. Non sei connesso ad Internet.",
						Toast.LENGTH_LONG);
				// } else {
				// play();
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		try {
			streamer = new IcecastStreamer(new URL(STREAM_URL));
			decoder = new MP3Decoder();

			streamer.addBufferReadyListener(new OnBufferReadyListener() {
				public void onBufferReady()
				{
					decoder.setStreamer(streamer);
				}
			});
			streamer.addOnNewDataListener(new OnNewDataListener() {
				public void onNewData()
				{
					decoder.decodeFrame();
				}
			});
			streamer.addOnInfoListener(new OnInfoListener() {
				public void onInfo(String[] infos)
				{
					trackInfos = infos.clone();
					updateResultsInUi();
				}
			});

			streamer.startStreaming();

			decoder.addBufferReadyListener(new OnBufferReadyListener() {
				public void onBufferReady()
				{
					player.play();
				}
			});
			decoder.addOnNewDataListener(new OnNewDataListener() {
				public void onNewData()
				{
					player.write(decoder.get(1000), 0, 1000);
				}
			});

			player = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
					AudioFormat.CHANNEL_CONFIGURATION_STEREO,
					AudioFormat.ENCODING_PCM_16BIT, 16000,
					AudioTrack.MODE_STREAM);
			Log.d(StreamingActivity.class.getName(), String.valueOf(player.getState()));
			player.play();
		} catch(MalformedURLException e) {
			e.printStackTrace();
		}

		trackTitle = (TextView) findViewById(R.id.trackTitle);
		trackAuthor = (TextView) findViewById(R.id.trackAuthor);

		playPauseButton = (ImageView) findViewById(R.id.playPauseButton);
		playPauseButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view)
			{

			}
		});

		registerReceiver(broadcastReceiver, new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION));

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		boolean isConnected = cm.getActiveNetworkInfo() != null
				&& cm.getActiveNetworkInfo().isConnected() || false;

		// if(isConnected) {
		// play();
		// } else {
		// Toast.makeText(StreamingActivity.this,
		// "Attenzione. Non sei connesso ad Internet.",
		// Toast.LENGTH_LONG);
		// }

	}

	protected void updateResultsInUi()
	{
		trackAuthor.setText(trackInfos[0]);
		if(trackInfos.length > 1) {
			trackTitle.setText(trackInfos[1]);
		} else {
			trackTitle.setText("");
		}
	}
}
