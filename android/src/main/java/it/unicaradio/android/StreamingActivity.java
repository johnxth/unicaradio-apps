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
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
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

	private ImageView playPauseButton;

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

	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent)
		{
			boolean noConnectivity = intent.getBooleanExtra(
					ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

			if(noConnectivity) {
				stop();
				Toast.makeText(StreamingActivity.this,
						"Attenzione. Non sei connesso ad Internet.",
						Toast.LENGTH_LONG);
			}
		}
	};

	private final View[] tabs = new View[5];

	static final String[] DAYS = new String[] {"Lunedì", "Martedì",
			"Mercoledì", "Giovedì", "Venerdì", "Sabato", "Domenica"};

	// static final String[][] SCHEDULE = new String[7][];

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

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		infos = new TrackInfos(getApplicationContext());
		imageUtils = new ImageUtils(getWindowManager().getDefaultDisplay());

		setupListeners();
		curTab = 0;
		tabs[0].setSelected(true);

		updateResultsInUi();

		// try {
		// URL url = new URL(STREAM_URL);
		// play(url);
		// } catch (MalformedURLException e) {
		// // TODO: handle exception
		// e.printStackTrace();
		// } catch (IOException e) {
		// // TODO: handle exception
		// e.printStackTrace();
		// }

		registerReceiver(broadcastReceiver, new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION));
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
									e.printStackTrace();
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
						e.printStackTrace();
					}
				}
			});
			playThread.start();
		}
	}

	private void stop()
	{
		conn = null;
		streamer = null;
		if(player != null) {
			player.stop();
			player = null;
		}
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
					setupListeners();
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

		// TODO: Completare
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
			e.printStackTrace();
		}
	}

	private void openLink(String url)
	{
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		startActivity(i);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if((keyCode == KeyEvent.KEYCODE_BACK)) {
			if(curTab == Tabs.SCHEDULE && state == 1) {
				setupScheduleTab();
				return true;
			}
		}

		return super.onKeyDown(keyCode, event);
	}
}
