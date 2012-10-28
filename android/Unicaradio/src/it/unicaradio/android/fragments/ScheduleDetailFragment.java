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
import it.unicaradio.android.adapters.TransmissionsAdapter;
import it.unicaradio.android.enums.Day;
import it.unicaradio.android.models.Schedule;
import it.unicaradio.android.models.Transmission;
import it.unicaradio.android.utils.IntentUtils;

import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;

/**
 * @author Paolo Cortis
 */
public class ScheduleDetailFragment extends SherlockListFragment
{
	private static final String TAG = ScheduleDetailFragment.class
			.getSimpleName();

	private int dayInt;

	private static Schedule schedule;

	public static ScheduleDetailFragment instance;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.v(TAG, "onCreate");

		dayInt = getArguments().getInt(IntentUtils.ARG_SCHEDULE_DAY);

		schedule = (Schedule) getArguments().getSerializable(
				IntentUtils.ARG_SCHEDULE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onStart()
	{
		super.onStart();
		Log.v(TAG, "onStart");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onResume()
	{
		super.onResume();
		Log.v(TAG, "onResume");

		instance = this;

		initList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPause()
	{
		super.onPause();

		getSherlockActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(
				false);
	}

	private void initList()
	{
		ListView scheduleListView = getListView();

		Day day = Day.fromInteger(dayInt);
		List<Transmission> transmissions = schedule.getTransmissionsByDay(day);

		TransmissionsAdapter transmissionsAdapter = new TransmissionsAdapter(
				getActivity(), transmissions, R.layout.list_two_columns);
		scheduleListView.setAdapter(transmissionsAdapter);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.schedule_fragment, null);
	}
}
