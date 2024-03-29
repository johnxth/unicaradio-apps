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
import it.unicaradio.android.activities.MainActivity;
import it.unicaradio.android.adapters.ScheduleListAdapter;
import it.unicaradio.android.enums.Error;
import it.unicaradio.android.models.Response;
import it.unicaradio.android.models.Schedule;
import it.unicaradio.android.tasks.BlockingAsyncTask;
import it.unicaradio.android.tasks.BlockingAsyncTask.OnTaskFailedListener;
import it.unicaradio.android.tasks.DownloadScheduleAsyncTask;
import it.unicaradio.android.utils.IntentUtils;
import it.unicaradio.android.utils.NetworkUtils;
import android.app.AlertDialog;
import android.content.Context;
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

import com.actionbarsherlock.app.SherlockListFragment;

/**
 * @author Paolo Cortis
 */
public class ScheduleListFragment extends SherlockListFragment
{
	private static final String TAG = ScheduleListFragment.class.getSimpleName();

	public static ScheduleListFragment instance;

	private int clicked = -1;

	private boolean isTwoPane;

	private boolean refreshForcedFromDetail = false;

	private static Schedule schedule;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onResume()
	{
		super.onResume();

		instance = this;

		initListView();

		if(!NetworkUtils.isConnected(getMainActivityContext())) {
			// alertNoConnectionAvailable();
			return;
		}

		if(schedule == null) {
			updateScheduleFromJSON(RefreshType.NORMAL);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPause()
	{
		Log.v(TAG, "onPause");
		super.onPause();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onDestroy()
	{
		Log.v(TAG, "onDestroy");
		super.onDestroy();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onDestroyView()
	{
		Log.v(TAG, "onDestroyView");
		super.onDestroyView();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Log.v(TAG, "onCreateView");
		isTwoPane = getMainActivityContext().getResources().getBoolean(R.bool.isTablet);
		return inflater.inflate(R.layout.schedule_fragment, null);
	}

	private void initListView()
	{
		drawList();

		getListView().setOnItemClickListener(new ScheduleListItemClickListener());

		if(isTwoPane) {
			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		}
	}

	private ListView drawList()
	{
		clicked = -1;
		ListView scheduleListView = getListView();

		BaseAdapter adapter = new ScheduleListAdapter(getMainActivityContext(), R.layout.list_simple);
		scheduleListView.setAdapter(adapter);

		return scheduleListView;
	}

	private void drawSecondLevel(int position)
	{
		Bundle arguments = new Bundle();
		arguments.putInt(IntentUtils.ARG_SCHEDULE_DAY, position);
		arguments.putSerializable(IntentUtils.ARG_SCHEDULE, schedule);

		ScheduleDetailFragment fragment = new ScheduleDetailFragment();
		fragment.setArguments(arguments);
		if(isTwoPane) {
			getFragmentManager().beginTransaction()
					.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
					.replace(R.id.item_detail_container, fragment).commit();
		} else {
			getFragmentManager().beginTransaction().replace(R.id.schedule_container, fragment).addToBackStack(null)
					.commit();
		}
	}

	private void alertNoConnectionAvailable()
	{
		// FIXME: gestire le traduzioni
		Toast.makeText(getMainActivityContext(), "Verifica la connessione Internet.", Toast.LENGTH_LONG).show();
	}

	void deselectItemOnList()
	{
		try {
			if(getListView() == null) {
				return;
			}

			getListView().setItemChecked(getListView().getCheckedItemPosition(), false);
		} catch(Exception e) {
			Log.d(TAG, "deselectItemOnList() threw exception: ", e);
		}
	}

	void updateScheduleFromJSON(RefreshType refreshType)
	{
		if(refreshType == RefreshType.FORCED_FROM_DETAIL) {
			schedule = null;
			clicked = -1;
			refreshForcedFromDetail = true;
		} else {
			boolean shouldShowDialog = (refreshType == RefreshType.FORCED) || refreshForcedFromDetail;
			refreshForcedFromDetail = false;

			DownloadScheduleAsyncTask task = new DownloadScheduleAsyncTask(getMainActivityContext(), shouldShowDialog);
			task.setOnTaskCompletedListener(new OnDownloadScheduleAsyncTaskCompletedListener());
			task.setOnTaskFailedListener(new OnDownloadScheduleAsyncTaskFailedListener());
			task.execute();
		}
	}

	private MainActivity getMainActivity()
	{
		if(getActivity() == null) {
			return MainActivity.activity;
		}

		return (MainActivity) getActivity();
	}

	private Context getMainActivityContext()
	{
		if(getActivity() == null) {
			return MainActivity.activityContext;
		}

		return getActivity();
	}

	private final class ScheduleListItemClickListener implements OnItemClickListener
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			if(schedule == null) {
				if(NetworkUtils.isConnected(getMainActivityContext())) {
					clicked = position;
					updateScheduleFromJSON(RefreshType.FORCED);
				} else {
					alertNoConnectionAvailable();
					deselectItemOnList();
				}

				return;
			}

			drawSecondLevel(position);
		}
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

	private final class OnDownloadScheduleAsyncTaskFailedListener implements OnTaskFailedListener<Response<String>>
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onTaskFailed(Response<String> result)
		{
			Log.e(TAG, "Got error: " + result.getErrorCode());

			Context context = getMainActivityContext();
			if(context == null) {
				return;
			}

			try {
				if(result.getErrorCode() == Error.INTERNAL_DOWNLOAD_ERROR) {
					new AlertDialog.Builder(context).setTitle("Errore!")
							.setMessage("È avvenuto un errore. Verifica di essere connesso ad Internet.")
							.setCancelable(false).setPositiveButton("OK", null).show();
				} else {
					new AlertDialog.Builder(context).setTitle("Errore!")
							.setMessage("È avvenuto un errore imprevisto. Riprova più tardi.").setCancelable(false)
							.setPositiveButton("OK", null).show();
				}
			} catch(Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
	}

	enum RefreshType {
		NORMAL, FORCED, FORCED_FROM_DETAIL
	}
}
