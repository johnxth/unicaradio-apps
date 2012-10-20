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
import it.unicaradio.android.fragments.ScheduleListFragment.RefreshType;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

/**
 * @author Paolo Cortis
 */
public class ScheduleFragment extends UnicaradioFragment
{
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
		View view = inflater.inflate(R.layout.schedule, null);

		boolean isTwoPane = getActivity().getResources().getBoolean(
				R.bool.isTablet);
		if(!isTwoPane) {
			getFragmentManager()
					.beginTransaction()
					.replace(R.id.schedule_container,
							new ScheduleListFragment()).commit();
		}

		return view;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPause()
	{
		super.onPause();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()) {
			case R.id.scheduleUpdate:
				// TODO: cancellare lato destro
				ScheduleListFragment.instance
						.updateScheduleFromJSON(RefreshType.FORCED);
				return true;

			case android.R.id.home:
				getFragmentManager().popBackStack();
				return true;

			default:
				return super.onOptionsItemSelected(item);
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
}
