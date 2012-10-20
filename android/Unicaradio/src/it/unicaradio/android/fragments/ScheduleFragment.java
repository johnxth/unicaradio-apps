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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

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
		return inflater.inflate(R.layout.schedule, null);
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
