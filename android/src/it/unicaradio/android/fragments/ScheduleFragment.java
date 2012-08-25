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
import it.unicaradio.android.adapters.ArrayAlternatedColoursAdapter;
import it.unicaradio.android.adapters.TransmissionsAdapter;
import it.unicaradio.android.enums.Day;
import it.unicaradio.android.enums.Error;
import it.unicaradio.android.models.Response;
import it.unicaradio.android.models.Schedule;
import it.unicaradio.android.models.Transmission;
import it.unicaradio.android.tasks.BlockingAsyncTask;
import it.unicaradio.android.tasks.BlockingAsyncTask.OnTaskFailedListener;
import it.unicaradio.android.tasks.DownloadScheduleAsyncTask;
import it.unicaradio.android.utils.NetworkUtils;

import java.util.List;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * @author Paolo Cortis
 */
public class ScheduleFragment extends UnicaradioFragment
{
	public static final String TAG = ScheduleFragment.class.getName();

	private static Schedule schedule;

	private int state;

	private int clicked;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.schedule, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		resetListView();

		if(!NetworkUtils.isConnected(getActivity())) {
			alertNoConnectionAvailable();
			return;
		}

		if(schedule == null) {
			updateScheduleFromJSON();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);

		inflater.inflate(R.menu.schedule_menu, menu);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()) {
			case R.id.scheduleUpdate:
				resetListView();
				updateScheduleFromJSON();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void resetListView()
	{
		getSherlockActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(
				false);

		state = 0;
		clicked = -1;
		ListView scheduleListView = (ListView) getActivity().findViewById(
				R.id.scheduleList);
		String[] days = getResources().getStringArray(R.array.days);

		BaseAdapter adapter = new ArrayAlternatedColoursAdapter<Object>(
				getActivity(), android.R.layout.simple_list_item_1, days);
		scheduleListView.setAdapter(adapter);
		scheduleListView
				.setOnItemClickListener(new OnScheduleListItemClickListener());
	}

	private void alertNoConnectionAvailable()
	{
		// FIXME: gestire le traduzioni
		Toast.makeText(getActivity(), "Verifica la connessione Internet.",
				Toast.LENGTH_LONG).show();
	}

	private void updateScheduleFromJSON()
	{
		DownloadScheduleAsyncTask task = new DownloadScheduleAsyncTask(
				getActivity());
		task.setOnTaskCompletedListener(new OnDownloadScheduleAsyncTaskCompletedListener());
		task.setOnTaskFailedListener(new OnDownloadScheduleAsyncTaskFailedListener());
		task.execute();
	}

	private void drawSecondLevel(int position)
	{
		state = 1;
		ListView scheduleListView = (ListView) getActivity().findViewById(
				R.id.scheduleList);

		Day day = Day.fromInteger(position);
		List<Transmission> transmissions = schedule.getTransmissionsByDay(day);

		TransmissionsAdapter transmissionsAdapter = new TransmissionsAdapter(
				getActivity(), transmissions, R.layout.list_two_columns);
		scheduleListView.setAdapter(transmissionsAdapter);

		getSherlockActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(
				true);
	}

	private final class OnDownloadScheduleAsyncTaskCompletedListener implements
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

	private final class OnDownloadScheduleAsyncTaskFailedListener implements
			OnTaskFailedListener<Response<String>>
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onTaskFailed(Response<String> result)
		{
			Log.e(TAG, "Got error: " + result.getErrorCode());

			if(result.getErrorCode() == Error.INTERNAL_DOWNLOAD_ERROR) {
				new AlertDialog.Builder(getActivity())
						.setTitle("Errore!")
						.setMessage(
								"È avvenuto un errore. Verifica di essere connesso ad Internet.")
						.setCancelable(false).setPositiveButton("OK", null)
						.show();
			} else {
				new AlertDialog.Builder(getActivity())
						.setTitle("Errore!")
						.setMessage(
								"È avvenuto un errore imprevisto. Riprova più tardi.")
						.setCancelable(false).setPositiveButton("OK", null)
						.show();
			}
		}
	}

	private final class OnScheduleListItemClickListener implements
			OnItemClickListener
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id)
		{
			if(state == 1) {
				return;
			}

			if(schedule == null) {
				if(NetworkUtils.isConnected(getActivity())) {
					clicked = position;
					updateScheduleFromJSON();
				} else {
					alertNoConnectionAvailable();
				}

				return;
			}

			drawSecondLevel(position);
		}
	}
}
