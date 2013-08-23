/*
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

package it.unicaradio.android.test.activities;

import android.support.v4.app.Fragment;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import junit.framework.Assert;

import it.unicaradio.android.R;
import it.unicaradio.android.activities.MainActivity;
import it.unicaradio.android.fragments.FavouritesFragment;
import it.unicaradio.android.fragments.InfoFragment;
import it.unicaradio.android.fragments.ScheduleFragment;
import it.unicaradio.android.fragments.SongRequestFragment;
import it.unicaradio.android.fragments.StreamingFragment;
import it.unicaradio.android.gui.Tabs;

/**
 * Created by paolo on 23/08/13.
 */
public class ChangeTabTest extends ActivityTest<MainActivity>
{
	public ChangeTabTest()
	{
		super(MainActivity.class);
	}

	public void testClickStreamingTab()
	{
		clickStreamingTab();

		doChecks(Tabs.STREAMING, StreamingFragment.class);
	}

	public void testClickScheduleTab()
	{
		clickScheduleTab();

		doChecks(Tabs.SCHEDULE, ScheduleFragment.class);
	}

	public void testClickSongTab()
	{
		clickSongTab();

		doChecks(Tabs.SONG, SongRequestFragment.class);
	}

	public void testClickFavouritesTab()
	{
		clickFavouritesTab();

		doChecks(Tabs.FAVORITES, FavouritesFragment.class);
	}

	public void testClickInfoTab()
	{
		clickInfoTab();

		doChecks(Tabs.INFO, InfoFragment.class);
	}

	private void doChecks(int tab, Class<? extends Fragment> expectedFragment)
	{
		solo.assertCurrentActivity("wrong activity", activity.getClass());

		SherlockFragmentActivity currentActivity = (SherlockFragmentActivity) solo.getCurrentActivity();
		String fragmentTag = "android:switcher:" + R.id.pager + ":" + tab;
		Fragment currentFragment = currentActivity.getSupportFragmentManager().findFragmentByTag(fragmentTag);
		Assert.assertNotNull(currentFragment);
		Assert.assertEquals(expectedFragment.getSimpleName(), currentFragment.getClass().getSimpleName());
	}
}
