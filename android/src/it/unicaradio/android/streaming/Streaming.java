/**
 * This program is free software; you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation; version 2 of the License.
 * 
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along 
 * with this program; if not, write to the Free Software Foundation, Inc., 
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Copyright UnicaRadio
 */

package it.unicaradio.android.streaming;

import it.unicaradio.android.streaming.events.OnInfoListener;

import org.apache.commons.lang.StringUtils;

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

/**
 * @author Paolo Cortis
 * 
 */
public class Streaming extends Activity
{
	private StreamingMediaPlayer mediaPlayer;

	private ImageView playPauseButton;

	final Handler mHandler = new Handler();

	String[] trackInfo;

	private TextView trackTitle;

	// Create runnable for posting
	final Runnable mUpdateResults = new Runnable() {
		public void run()
		{
			setTrackInfo();
		}
	};

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

		trackTitle = (TextView) findViewById(R.id.trackTitle);

		playPauseButton = (ImageView) findViewById(R.id.playPauseButton);
		playPauseButton.setOnClickListener(new OnClickListener() {

			public void onClick(View view)
			{

			}
		});

		mediaPlayer = new StreamingMediaPlayer(
				"http://streaming.unicaradio.it:80/unica64.aac");
		mediaPlayer.addOnInfoListener(new OnInfoListener() {

			public void onInfo(String[] infos)
			{
				trackInfo = infos;
				mHandler.post(mUpdateResults);
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

	protected void setTrackInfo()
	{
		trackTitle.setText(StringUtils.join(trackInfo, " - "));
	}

	private void play()
	{
		if(!mediaPlayer.isAlive()) {
			mediaPlayer.start();
		}
	}
}
