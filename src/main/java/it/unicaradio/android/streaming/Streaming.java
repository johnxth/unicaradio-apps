package it.unicaradio.android.streaming;

import it.unicaradio.android.streaming.events.OnInfoListener;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Streaming extends Activity
{
	private StreamingMediaPlayer player;

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

	private static final String STREAM_URL = "http://streaming.unicaradio.it:80/unica64.aac";

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

		trackTitle = (TextView) findViewById(R.id.trackTitle);
		trackAuthor = (TextView) findViewById(R.id.trackAuthor);

		playPauseButton = (ImageView) findViewById(R.id.playPauseButton);
		playPauseButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view)
			{
				if(player != null) {
					player.done();
					player = null;
					// } else {
					// play();
				}
			}
		});

		registerReceiver(broadcastReceiver, new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION));

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		boolean isConnected = cm.getActiveNetworkInfo() != null
				&& cm.getActiveNetworkInfo().isConnected() || false;

		if(isConnected) {
			play();
		} else {
			Toast.makeText(Streaming.this,
					"Attenzione. Non sei connesso ad Internet.",
					Toast.LENGTH_LONG);
		}

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

	private void play()
	{
		if(player == null) {
			player = new StreamingMediaPlayer(STREAM_URL);
			player.start();
			player.addOnInfoListener(new OnInfoListener() {
				public void onInfo(String[] infos)
				{
					trackInfos = infos;
					mHandler.post(mUpdateResults);
				}
			});
		}
	}
}
