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
package it.unicaradio.android.test.activities;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

import com.jayway.android.robotium.solo.Solo;

import junit.framework.Assert;

import java.lang.reflect.Field;

import it.unicaradio.android.R;
import it.unicaradio.android.listeners.TabSelectedListener;

/**
 * @author Paolo Cortis
 */
public class ActivityTest<T extends Activity> extends ActivityInstrumentationTestCase2<T>
{
	protected Activity activity;

	protected Solo solo;

	/**
	 * @param activityClass
	 */
	public ActivityTest(Class<T> activityClass)
	{
		super(activityClass);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		activity = getActivity();
		solo = new Solo(getInstrumentation(), activity);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void tearDown() throws Exception
	{
		activity = null;
		solo = null;

		super.tearDown();
	}

	public void clickStreamingTab()
	{
		View view = activity.findViewById(R.id.streamingTab);
		clickToChangeTab(view);
	}

	public void clickScheduleTab()
	{
		View view = activity.findViewById(R.id.scheduleTab);
		clickToChangeTab(view);
	}

	public void clickSongTab()
	{
		View view = activity.findViewById(R.id.songTab);
		clickToChangeTab(view);
	}

	public void clickFavouritesTab()
	{
		View view = activity.findViewById(R.id.favoritesTab);
		clickToChangeTab(view);
	}

	public void clickInfoTab()
	{
		View view = activity.findViewById(R.id.infosTab);
		clickToChangeTab(view);
	}

	private void clickToChangeTab(View viewToClick)
	{
		Assert.assertNotNull(viewToClick);
		solo.clickOnView(viewToClick);
	}

	protected int getCurrentTab() throws IllegalAccessException, NoSuchFieldException
	{
		Field tabSelectedListenerField = activity.getClass().getDeclaredField("tabSelectedListener");
		tabSelectedListenerField.setAccessible(true);

		TabSelectedListener tabSelectedListener = null;
		tabSelectedListener = (TabSelectedListener) tabSelectedListenerField.get(tabSelectedListener);

		return tabSelectedListener.getCurrentTab();
	}
}
