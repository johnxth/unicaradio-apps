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
import it.unicaradio.android.gui.Tabs;
import it.unicaradio.android.gui.Utils;
import it.unicaradio.android.utils.ActivityUtils;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.collections.map.LinkedMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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
		state = 0;
		ListView lv = (ListView) findViewById(R.id.scheduleList);

		if(SCHEDULE == null) {
			updateScheduleFromJSON();
		}
		DAYS.put("lunedi", "Lunedì");
		DAYS.put("martedi", "Martedì");
		DAYS.put("mercoledi", "Mercoledì");
		DAYS.put("giovedi", "Giovedì");
		DAYS.put("venerdi", "Venerdì");
		DAYS.put("sabato", "Sabato");
		DAYS.put("domenica", "Domenica");

		Object[] days = DAYS.values().toArray();

		lv.setAdapter(new ArrayAdapter<Object>(ScheduleActivity.this,
				android.R.layout.simple_list_item_1, days));
	}

	@Override
	protected void setupListeners()
	{
		final ListView lv = (ListView) findViewById(R.id.scheduleList);
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id)
			{
				if(state == 0) {
					state = 1;
					if(SCHEDULE != null && SCHEDULE.get((int) id) != null) {
						lv.setAdapter(new SimpleAdapter(ScheduleActivity.this,
								SCHEDULE.get((int) id),
								R.layout.list_two_columns, new String[] {
										"line1", "line2"}, new int[] {
										R.id.text1, R.id.text2}));
					} else {
						lv.setAdapter(new ArrayAdapter<String>(
								ScheduleActivity.this,
								android.R.layout.simple_list_item_1,
								new String[] {""}));
					}
				}
			}
		});
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

					SCHEDULE.get(j).add(
							ActivityUtils.addItem(inizio, programma));
				}
			}
		} catch(JSONException e) {
			Log.d(LOG, "Errore durante il parsing del file JSON", e);
		}
	}
}
