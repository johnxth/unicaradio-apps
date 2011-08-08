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
package it.unicaradio.android;

import it.unicaradio.android.events.OnInfoListener;
import it.unicaradio.android.gui.ImageUtils;
import it.unicaradio.android.gui.Tabs;
import it.unicaradio.android.gui.TrackInfos;
import it.unicaradio.android.gui.Utils;
import it.unicaradio.android.streamers.IcecastStreamer;
import it.unicaradio.android.streamers.Streamable;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.spoledge.aacplayer.ArrayAACPlayer;
import com.spoledge.aacplayer.ArrayDecoder;
import com.spoledge.aacplayer.Decoder;

/**
 * @author Paolo Cortis
 */
public class StreamingActivity extends Activity
{
	private static final String STREAM_URL = "http://streaming.unicaradio.it:80/unica64.aac";

	private static final String ONAIR_COVER_URL = "http://www.unicaradio.it/regia/OnAir.jpg";

	private static final String SCHEDULE_URL = "http://www.unicaradio.it/regia/test/palinsesto.php";

	private Streamable streamer;

	private TrackInfos infos;

	private ImageUtils imageUtils;

	private Thread imageThread;

	final Handler mHandler = new Handler();

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
				showMessage("Attenzione. Non sei connesso ad Internet.");
			}
		}
	};

	private class MyPhoneStateListener extends PhoneStateListener
	{
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
	}

	private final BroadcastReceiver telephonyBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent)
		{
			MyPhoneStateListener phoneListener = new MyPhoneStateListener();
			TelephonyManager telephony = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			telephony.listen(phoneListener,
					PhoneStateListener.LISTEN_CALL_STATE);
		}
	};

	private final View[] tabs = new View[5];

	static final String[] DAYS = new String[] {"Lunedì", "Martedì",
			"Mercoledì", "Giovedì", "Venerdì", "Sabato", "Domenica"};

	private int state = 0;

	private int curTab;

	private ArrayAACPlayer player;

	private URLConnection conn;

	private static String LOG = StreamingActivity.class.getName();

	private final static ArrayList<HashMap<String, String>> SITES = new ArrayList<HashMap<String, String>>();

	private static ArrayList<ArrayList<HashMap<String, String>>> SCHEDULE;

	static {
		SITES.add(addItem("Sito web", "http://www.unicaradio.it/",
				R.drawable.logo));

		SITES.add(addItem("Facebook", "http://www.facebook.com/unicaradio/",
				R.drawable.facebook));

		SITES.add(addItem("Youtube",
				"http://www.youtube.com/user/unicaradiotv", R.drawable.youtube));

		SITES.add(addItem("Twitter", "http://twitter.com/#!/UnicaRadio",
				R.drawable.twitter));
	};

	private static HashMap<String, String> addItem(String line1, String line2)
	{
		HashMap<String, String> item = new HashMap<String, String>();
		item.put("line1", line1);
		item.put("line2", line2);

		return item;
	}

	private static HashMap<String, String> addItem(String line1, String line2,
			int resourceImage)
	{
		HashMap<String, String> item = addItem(line1, line2);
		item.put("icon", String.valueOf(resourceImage));

		return item;
	}

	private void showMessage(String message)
	{
		Toast.makeText(StreamingActivity.this, message, Toast.LENGTH_LONG);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		infos = new TrackInfos(getApplicationContext());
		imageUtils = new ImageUtils(getWindowManager().getDefaultDisplay());

		setupStreamingListeners();
		curTab = 0;
		tabs[0].setSelected(true);

		updateResultsInUi();

		registerReceiver(connectivityBroadcastReceiver, new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION));
		registerReceiver(telephonyBroadcastReceiver, new IntentFilter(
				TelephonyManager.ACTION_PHONE_STATE_CHANGED));
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean isConnected = cm.getActiveNetworkInfo() != null
				&& cm.getActiveNetworkInfo().isConnected();
		if(!isConnected) {
			Toast.makeText(StreamingActivity.this,
					"Attenzione. Non sei connesso ad Internet.",
					Toast.LENGTH_LONG);
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
									infos.setCover(imageUtils
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

	private void stopWithException(Exception e)
	{
		stopWithException("E' avvenuto un problema. Riprova.", e);
	}

	private void stopWithException(String message, Exception e)
	{
		stop();
		showMessage(message);
		Log.d(LOG, message, e);
	}

	private void stop()
	{
		infos.clean();
		mHandler.post(mUpdateResults);
		if(player != null) {
			player.stop();
			player = null;
		}
		streamer = null;
		conn = null;
	}

	private void setupListeners()
	{
		updatedReferences();

		tabs[Tabs.STREAMING].setOnClickListener(new OnClickListener() {
			public void onClick(View arg0)
			{
				if(curTab != Tabs.STREAMING) {
					curTab = Tabs.STREAMING;
					setContentView(R.layout.main);
					updatedReferences();
					updateResultsInUi();
					setupStreamingListeners();
					tabs[0].setSelected(true);
				}
			}
		});
		tabs[Tabs.SCHEDULE].setOnClickListener(new OnClickListener() {
			public void onClick(View arg0)
			{
				if(curTab != Tabs.SCHEDULE) {
					curTab = Tabs.SCHEDULE;
					setContentView(R.layout.schedule);
					updatedReferences();
					setupScheduleTab();
					setupScheduleListeners();
					tabs[1].setSelected(true);
				}
			}
		});
		tabs[Tabs.SONG].setOnClickListener(new OnClickListener() {
			public void onClick(View arg0)
			{
				if(curTab != Tabs.SONG) {
					curTab = Tabs.SONG;
					setContentView(R.layout.songs);
					updatedReferences();
					setupSongsListeners();
					tabs[2].setSelected(true);
				}
			}
		});
		tabs[Tabs.FAVORITES].setOnClickListener(new OnClickListener() {
			public void onClick(View arg0)
			{
				if(curTab != Tabs.FAVORITES) {
					curTab = Tabs.FAVORITES;
					setContentView(R.layout.links);
					updatedReferences();
					setupFavoritesTab();
					setupFavoritesListeners();
					tabs[3].setSelected(true);
				}
			}
		});
		tabs[Tabs.INFO].setOnClickListener(new OnClickListener() {
			public void onClick(View arg0)
			{
				if(curTab != Tabs.INFO) {
					curTab = Tabs.INFO;
					setContentView(R.layout.infos);
					updatedReferences();
					setupInfosTab();
					setupListeners();
					tabs[4].setSelected(true);
				}
			}
		});
	}

	protected void setupStreamingListeners()
	{
		setupListeners();

		final ImageButton playPauseButton = (ImageButton) findViewById(R.id.playPauseButton);
		playPauseButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0)
			{
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
					playPauseButton
							.setImageResource(android.R.drawable.ic_media_play);
					stop();
				}
			}
		});
	}

	protected void setupInfosTab()
	{
		TextView infosText = (TextView) findViewById(R.id.infosText);
		infosText.setText(Html.fromHtml(getString(R.string.infos)));
		infosText.setMovementMethod(LinkMovementMethod.getInstance());
	}

	protected void setupFavoritesTab()
	{
		ListView lv = (ListView) findViewById(R.id.linksList);
		lv.setAdapter(new SimpleAdapter(this, SITES,
				R.layout.list_two_lines_and_image, new String[] {"line1",
						"line2", "icon"}, new int[] {R.id.text1, R.id.text2,
						R.id.icon}));
	}

	protected void setupFavoritesListeners()
	{
		setupListeners();

		final ListView lv = (ListView) findViewById(R.id.linksList);
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> a, View v, int position,
					long id)
			{
				HashMap<String, String> object = SITES.get((int) id);
				openLink(object.get("line2"));
			}
		});
	}

	protected void setupSongsListeners()
	{
		setupListeners();

		View songButton = findViewById(R.id.songButton);
		songButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v)
			{
				TextView author = (TextView) findViewById(R.id.songsAuthor);
				TextView title = (TextView) findViewById(R.id.songsTitle);
				final Intent emailIntent = new Intent(
						android.content.Intent.ACTION_SEND);

				emailIntent.setType("plain/text");
				emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
						new String[] {"diretta@unicaradio.it"});
				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
						author.getText().toString() + "*"
								+ title.getText().toString());
				emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
						"Sent from Android APP");

				AlertDialog.Builder adb = new AlertDialog.Builder(
						StreamingActivity.this);
				adb.setTitle("Invio richiesta canzone...");
				adb.setMessage("Attenzione!! Non modificare l'e-mail!");
				adb.setPositiveButton("Ok", null);
				adb.show();

				StreamingActivity.this.startActivity(Intent.createChooser(
						emailIntent, "Send mail..."));
			}
		});
	}

	private void setupScheduleListeners()
	{
		setupListeners();

		final ListView lv = (ListView) findViewById(R.id.scheduleList);
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id)
			{
				if(state == 0) {
					state = 1;
					if(SCHEDULE != null && SCHEDULE.get((int) id) != null) {
						lv.setAdapter(new SimpleAdapter(StreamingActivity.this,
								SCHEDULE.get((int) id),
								R.layout.list_two_columns, new String[] {
										"line1", "line2"}, new int[] {
										R.id.text1, R.id.text2}));

						// lv.setAdapter(new ArrayAdapter<String>(
						// StreamingActivity.this,
						// android.R.layout.simple_list_item_1, SCHEDULE
						// .get((int) id)));
					} else {
						lv.setAdapter(new ArrayAdapter<String>(
								StreamingActivity.this,
								android.R.layout.simple_list_item_1,
								new String[] {""}));
					}
				}
			}
		});
	}

	private void updatedReferences()
	{
		tabs[Tabs.STREAMING] = findViewById(R.id.streamingTab);
		tabs[Tabs.SCHEDULE] = findViewById(R.id.scheduleTab);
		tabs[Tabs.SONG] = findViewById(R.id.songTab);
		tabs[Tabs.FAVORITES] = findViewById(R.id.favoritesTab);
		tabs[Tabs.INFO] = findViewById(R.id.infosTab);
	}

	private void updateResultsInUi()
	{
		if(curTab == Tabs.STREAMING) {
			TextView trackAuthor = (TextView) findViewById(R.id.author);
			TextView trackTitle = (TextView) findViewById(R.id.songTitle);
			ImageView cover = (ImageView) findViewById(R.id.cover);

			if(trackAuthor != null) {
				trackAuthor.setText(infos.getAuthor());
			}
			if(trackTitle != null) {
				trackTitle.setText(infos.getTitle());
			}
			if(cover != null) {
				Bitmap bitmap = imageUtils.resize(infos.getCover(), 65);
				cover.setImageBitmap(bitmap);
			}
		}
	}

	private void setupScheduleTab()
	{
		state = 0;
		ListView lv = (ListView) findViewById(R.id.scheduleList);
		if(SCHEDULE == null) {
			updateScheduleFromJSON();
		}
		lv.setAdapter(new ArrayAdapter<String>(StreamingActivity.this,
				android.R.layout.simple_list_item_1, DAYS));
	}

	private void updateScheduleFromJSON()
	{
		SCHEDULE = new ArrayList<ArrayList<HashMap<String, String>>>();

		String json = null;
		try {
			json = new String(Utils.downloadFromUrl(SCHEDULE_URL));
		} catch(IOException e) {
			Log.d(LOG,
					MessageFormat.format("Cannot find file {0}", SCHEDULE_URL));
			return;
		}

		JSONObject jObject = null;
		try {
			jObject = new JSONObject(json);

			String[] days = new String[] {"lunedi", "martedi", "mercoledi",
					"giovedi", "venerdi", "sabato", "domenica"};

			for(int j = 0; j < days.length; j++) {
				SCHEDULE.add(new ArrayList<HashMap<String, String>>());
				String day = days[j];

				JSONArray itemArray = jObject.getJSONArray(day);
				for(int i = 0; i < itemArray.length(); i++) {
					String programma = itemArray.getJSONObject(i)
							.get("programma").toString();
					String inizio = itemArray.getJSONObject(i).get("inizio")
							.toString();

					SCHEDULE.get(j).add(addItem(inizio, programma));
				}
			}
		} catch(JSONException e) {
			Log.d(LOG, "Errore durante il parsing del file JSON", e);
		}
	}

	private void openLink(String url)
	{
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		startActivity(i);
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
				unregisterReceiver(connectivityBroadcastReceiver);
				stop();
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if((keyCode == KeyEvent.KEYCODE_BACK)) {
			if(curTab == Tabs.SCHEDULE && state == 1) {
				setupScheduleTab();
				return true;
			} else {
				moveTaskToBack(true);
				return true;
			}
		} else if(keyCode == KeyEvent.KEYCODE_HOME) {
			moveTaskToBack(true);
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}
}
