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
import it.unicaradio.android.adapters.TransmissionsAdapter;
import it.unicaradio.android.enums.Day;
import it.unicaradio.android.gui.Tabs;
import it.unicaradio.android.models.Response;
import it.unicaradio.android.models.Schedule;
import it.unicaradio.android.models.Transmission;
import it.unicaradio.android.tasks.BlockingAsyncTask;
import it.unicaradio.android.tasks.DownloadScheduleAsyncTask;
import it.unicaradio.android.utils.NetworkUtils;

import java.util.List;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

/**
 * @author Paolo Cortis
 */
public class ScheduleActivity extends TabbedActivity
{
	private static Schedule schedule;

	private int state;

	private int clicked;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState, R.layout.schedule);
		Log.d(ScheduleActivity.class.getName(), "Called ScheduleActivity");
	}

	@Override
	protected void setupTab()
	{
		resetListView();

		if(!NetworkUtils.isConnected(this)) {
			showNotConnectedDialog();

			return;
		}

		if(schedule == null) {
			updateScheduleFromJSON();
		}
	}

	private void showNotConnectedDialog()
	{
		new AlertDialog.Builder(this)
				.setTitle("Errore!")
				.setMessage(
						"È avvenuto un errore. Verifica di essere connesso ad Internet.")
				.setCancelable(false).setPositiveButton("OK", null).show();
	}

	private void resetListView()
	{
		state = 0;
		clicked = -1;
		ListView scheduleListView = (ListView) findViewById(R.id.scheduleList);
		String[] days = getResources().getStringArray(R.array.days);

		BaseAdapter adapter = new ArrayAlternatedColoursAdapter<Object>(this,
				android.R.layout.simple_list_item_1, days);
		scheduleListView.setAdapter(adapter);
	}

	@Override
	protected void setupListeners()
	{
		final ListView scheduleListView = (ListView) findViewById(R.id.scheduleList);
		scheduleListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				if(state == 0) {
					if(schedule == null) {
						if(NetworkUtils.isConnected(ScheduleActivity.this)) {
							clicked = position;
							updateScheduleFromJSON();
						} else {
							showNotConnectedDialog();
						}

						return;
					}

					drawSecondLevel(position);
				}
			}
		});
	}

	/**
	 * @param scheduleListView
	 * @param position
	 */
	private void drawSecondLevel(int position)
	{
		state = 1;
		ListView scheduleListView = (ListView) findViewById(R.id.scheduleList);

		Day day = Day.fromInteger(position);
		List<Transmission> transmissions = schedule.getTransmissionsByDay(day);

		TransmissionsAdapter transmissionsAdapter = new TransmissionsAdapter(
				ScheduleActivity.this, transmissions, R.layout.list_two_columns);
		scheduleListView.setAdapter(transmissionsAdapter);
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
				resetListView();
				updateScheduleFromJSON();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if((keyCode == KeyEvent.KEYCODE_BACK) && (state == 1)) {
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
		DownloadScheduleAsyncTask task = new DownloadScheduleAsyncTask(this);
		task.setOnTaskCompletedListener(new OnDownloadScheduleAsyncTaskCompletedListener());
		task.execute();
	}

	private class OnDownloadScheduleAsyncTaskCompletedListener implements
			BlockingAsyncTask.OnTaskCompletedListener<Response<String>>
	{
		@Override
		public void onTaskCompleted(Response<String> result)
		{
			schedule = Schedule.fromJSON(result.getResult());

			if(clicked != -1) {
				drawSecondLevel(clicked);
				clicked = -1;
			}
		}
	}
}
