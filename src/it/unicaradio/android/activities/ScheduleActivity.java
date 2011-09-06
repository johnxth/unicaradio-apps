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
import it.unicaradio.android.adapters.ArrayAlternatedColoursAdapter;
import it.unicaradio.android.adapters.SimpleAlternatedColoursAdapter;
import it.unicaradio.android.gui.Tabs;
import it.unicaradio.android.utils.ActivityUtils;
import it.unicaradio.android.utils.Utils;

import java.io.IOException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.collections.map.LinkedMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * @author Paolo Cortis
 * 
 */
public class ScheduleActivity extends TabbedActivity
{
	private int state;

	private static String LOG = ScheduleActivity.class.getName();

	private static final String SCHEDULE_URL = "http://www.unicaradio.it/regia/test/palinsesto.php";

	private static ArrayList<ArrayList<HashMap<String, String>>> SCHEDULE;

	static final LinkedMap DAYS = new LinkedMap();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.d(ScheduleActivity.class.getName(), "Called ScheduleActivity");
		super.onCreate(savedInstanceState, R.layout.schedule);
	}

	@Override
	protected void setupTab()
	{
		DAYS.put("lunedi", "Lunedì");
		DAYS.put("martedi", "Martedì");
		DAYS.put("mercoledi", "Mercoledì");
		DAYS.put("giovedi", "Giovedì");
		DAYS.put("venerdi", "Venerdì");
		DAYS.put("sabato", "Sabato");
		DAYS.put("domenica", "Domenica");

		resetListView();

		if(SCHEDULE == null) {
			updateScheduleFromJSON();
		}
	}

	private void resetListView()
	{
		state = 0;
		ListView lv = (ListView) findViewById(R.id.scheduleList);
		Object[] days = DAYS.values().toArray();

		lv.setAdapter(new ArrayAlternatedColoursAdapter<Object>(
				ScheduleActivity.this, android.R.layout.simple_list_item_1,
				days));
	}

	@Override
	protected void setupListeners()
	{
		final ListView lv = (ListView) findViewById(R.id.scheduleList);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id)
			{
				if(state == 0) {
					if(SCHEDULE == null || SCHEDULE.size() == 0) {
						new AlertDialog.Builder(ScheduleActivity.this)
								.setTitle("Errore!")
								.setMessage(
										"Attenzione! Assicurati di essere connesso ad Internet e ricarica il palinsesto.")
								.setCancelable(false)
								.setPositiveButton("OK", null).show();
						return;
					}
					state = 1;
					lv.setAdapter(new SimpleAlternatedColoursAdapter(
							ScheduleActivity.this, SCHEDULE.get((int) id),
							R.layout.list_two_columns, new String[] {"line1",
									"line2"},
							new int[] {R.id.text1, R.id.text2}));
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.schedule_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle item selection
		switch(item.getItemId()) {
			case R.id.scheduleUpdate:
				updateScheduleFromJSON();
				resetListView();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if((keyCode == KeyEvent.KEYCODE_BACK) && state == 1) {
			setupTab();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public int getTab()
	{
		return Tabs.SCHEDULE;
	}

	private void updateScheduleFromJSON()
	{
		AsyncTask<String, Void, Integer> task = new AsyncTask<String, Void, Integer>() {
			private ProgressDialog dialog;

			@Override
			protected Integer doInBackground(String... arg0)
			{
				SCHEDULE = new ArrayList<ArrayList<HashMap<String, String>>>();

				String json = null;
				try {
					json = new String(Utils.downloadFromUrl(SCHEDULE_URL));
				} catch(IOException e) {
					Log.d(LOG, MessageFormat.format("Cannot find file {0}",
							SCHEDULE_URL));
					return 1;
				}

				JSONObject jObject = null;
				try {
					jObject = new JSONObject(json);

					String[] days = new String[] {"lunedi", "martedi",
							"mercoledi", "giovedi", "venerdi", "sabato",
							"domenica"};

					for(int j = 0; j < days.length; j++) {
						SCHEDULE.add(new ArrayList<HashMap<String, String>>());
						String day = days[j];

						JSONArray itemArray = jObject.getJSONArray(day);
						for(int i = 0; i < itemArray.length(); i++) {
							String programma = itemArray.getJSONObject(i)
									.get("programma").toString();
							String inizio = adjustTime(itemArray
									.getJSONObject(i).get("inizio").toString());

							SCHEDULE.get(j).add(
									ActivityUtils.addItem(inizio, programma));
						}
					}
				} catch(JSONException e) {
					Log.d(LOG, "Errore durante il parsing del file JSON", e);
					return 1;
				}

				return 0;
			}

			@Override
			protected void onPreExecute()
			{
				super.onPreExecute();
				dialog = ProgressDialog.show(ScheduleActivity.this,
						"Palinsesto...", "Caricamento in corso...", true);
			}

			@Override
			protected void onPostExecute(Integer result)
			{
				super.onPostExecute(result);
				dialog.dismiss();
				if(result == 1) {
					new AlertDialog.Builder(ScheduleActivity.this)
							.setTitle("Errore!")
							.setMessage(
									"È avvenuto un errore. Verifica di essere connesso ad Internet.")
							.setCancelable(false).setPositiveButton("OK", null)
							.show();
				}
			}
		};
		task.execute("");
	}

	private String adjustTime(String time)
	{
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
		String adjustedTime = time;
		try {
			Date date = formatter.parse(time);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.HOUR_OF_DAY, 1);
			adjustedTime = formatter.format(cal.getTime());
		} catch(ParseException e) {
		}

		return adjustedTime;
	}
}
