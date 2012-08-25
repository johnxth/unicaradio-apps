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
package it.unicaradio.android.listeners;

import it.unicaradio.android.R;
import it.unicaradio.android.fragments.FavouritesFragment;
import it.unicaradio.android.fragments.InfoFragment;
import it.unicaradio.android.fragments.ScheduleFragment;
import it.unicaradio.android.fragments.SongRequestFragment;
import it.unicaradio.android.fragments.StreamingFragment;
import it.unicaradio.android.fragments.UnicaradioFragment;
import it.unicaradio.android.gui.Tab;
import it.unicaradio.android.gui.Tab.OnTabSelectedListener;
import it.unicaradio.android.gui.Tabs;
import android.support.v4.app.FragmentManager;
import android.util.Log;

/**
 * @author Paolo Cortis
 */
public class TabSelectedListener implements OnTabSelectedListener
{
	private static final String TAG = TabSelectedListener.class.getName();

	private static int currentTab = Tabs.STREAMING;

	private static FragmentManager fragmentManager;

	private static StreamingFragment streamingFragment;

	private static ScheduleFragment scheduleFragment;

	private static SongRequestFragment songRequestFragment;

	private static FavouritesFragment favouritesFragment;

	private static InfoFragment infoFragment;

	private UnicaradioFragment currentFragment;

	public TabSelectedListener(FragmentManager fragmentManager)
	{
		TabSelectedListener.fragmentManager = fragmentManager;

		showFragment(new StreamingFragment());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onTabSelected(Tab tab)
	{
		int selectedTab = tab.getType();
		Log.d(TAG, "Tab selected: " + Integer.toString(selectedTab));

		if(selectedTab == currentTab) {
			return;
		}

		currentTab = selectedTab;
		switch(selectedTab) {
			case Tabs.STREAMING:
				Log.i(TAG, "Showing Streaming");
				showFragment(getStreamingFragment());
				break;
			case Tabs.SCHEDULE:
				Log.i(TAG, "Showing Schedule");
				showFragment(getScheduleFragment());
				break;
			case Tabs.SONG:
				Log.i(TAG, "Showing SongRequest");
				showFragment(getSongRequestFragment());
				break;
			case Tabs.FAVORITES:
				Log.i(TAG, "Showing Favourites");
				showFragment(getFavouritesFragment());
				break;
			case Tabs.INFO:
				Log.i(TAG, "Showing Info");
				showFragment(getInfoFragment());
				break;

			default:
				break;
		}
	}

	/**
	 * @return the streamingFragment
	 */
	private static StreamingFragment getStreamingFragment()
	{
		if(streamingFragment == null) {
			streamingFragment = new StreamingFragment();
		}

		return streamingFragment;
	}

	private static ScheduleFragment getScheduleFragment()
	{
		if(scheduleFragment == null) {
			scheduleFragment = new ScheduleFragment();
		}

		return scheduleFragment;
	}

	private static SongRequestFragment getSongRequestFragment()
	{
		if(songRequestFragment == null) {
			songRequestFragment = new SongRequestFragment();
		}

		return songRequestFragment;
	}

	private static FavouritesFragment getFavouritesFragment()
	{
		if(favouritesFragment == null) {
			favouritesFragment = new FavouritesFragment();
		}

		return favouritesFragment;
	}

	private static InfoFragment getInfoFragment()
	{
		if(infoFragment == null) {
			infoFragment = new InfoFragment();
		}

		return infoFragment;
	}

	/**
	 * @return the currentFragment
	 */
	public UnicaradioFragment getCurrentFragment()
	{
		return currentFragment;
	}

	private void showFragment(UnicaradioFragment fragment)
	{
		currentFragment = fragment;
		fragmentManager.beginTransaction()
				.replace(R.id.fragment_content, fragment).commit();
	}
}
