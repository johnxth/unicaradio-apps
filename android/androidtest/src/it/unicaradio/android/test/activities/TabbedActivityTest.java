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

import it.unicaradio.android.R;
import it.unicaradio.android.activities.FavoritesActivity;
import it.unicaradio.android.activities.InfoActivity;
import it.unicaradio.android.activities.ScheduleActivity;
import it.unicaradio.android.activities.SongRequestActivity;
import it.unicaradio.android.activities.StreamingActivity;
import it.unicaradio.android.activities.TabbedActivity;
import junit.framework.Assert;
import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

import com.jayway.android.robotium.solo.Solo;

/**
 * @author Paolo Cortis
 * 
 */
public class TabbedActivityTest<T extends Activity> extends
		ActivityInstrumentationTestCase2<T>
{
	protected TabbedActivity activity;

	protected Solo solo;

	/**
	 * @param pkg
	 * @param activityClass
	 */
	public TabbedActivityTest(String pkg, Class<T> activityClass)
	{
		super(pkg, activityClass);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		Activity tmpActivity = getActivity();
		if(!(tmpActivity instanceof TabbedActivity)) {
			fail("Activity is not instance of TabbedActivity");
		}

		activity = (TabbedActivity) tmpActivity;
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

	public void testClickStreamingTab()
	{
		View view = activity.findViewById(R.id.streamingTab);
		clickToOpenActivity(view, StreamingActivity.class);
	}

	public void testClickScheduleTab()
	{
		View view = activity.findViewById(R.id.scheduleTab);
		clickToOpenActivity(view, ScheduleActivity.class);
	}

	public void testClickSongTab()
	{
		View view = activity.findViewById(R.id.songTab);
		clickToOpenActivity(view, SongRequestActivity.class);
	}

	public void testClickFavouritesTab()
	{
		View view = activity.findViewById(R.id.favoritesTab);
		clickToOpenActivity(view, FavoritesActivity.class);
	}

	public void testClickInfoTab()
	{
		View view = activity.findViewById(R.id.infosTab);
		clickToOpenActivity(view, InfoActivity.class);
	}

	private void clickToOpenActivity(View viewToClick,
			Class<? extends TabbedActivity> clazz)
	{
		Activity currentActivity = solo.getCurrentActivity();

		Assert.assertNotNull(viewToClick);
		solo.clickOnView(viewToClick);

		solo.assertCurrentActivity("wrong activity", clazz);
		solo.goBackToActivity(currentActivity.getClass().getSimpleName());
		solo.assertCurrentActivity("wrong activity", currentActivity.getClass());
	}

	// public void testClickStreamingTab()
	// {
	// Instrumentation instrumentation = getInstrumentation();
	// ActivityMonitor monitor = instrumentation.addMonitor(
	// StreamingActivity.class.getName(), null, false);
	// assertEquals(0, monitor.getHits());
	//
	// View view = activity.findViewById(R.id.streamingTab);
	// TouchUtils.clickView(this, view);
	//
	// monitor.waitForActivityWithTimeout(5000);
	// assertEquals(1, monitor.getHits());
	// instrumentation.removeMonitor(monitor);
	// }
	//
	// public void testClickInfoTab()
	// {
	// Instrumentation instrumentation = getInstrumentation();
	// ActivityMonitor monitor = instrumentation.addMonitor(
	// InfoActivity.class.getName(), null, false);
	// assertEquals(0, monitor.getHits());
	//
	// View view = activity.findViewById(R.id.infosTab);
	// TouchUtils.clickView(this, view);
	//
	// monitor.waitForActivityWithTimeout(5000);
	// assertEquals(1, monitor.getHits());
	// instrumentation.removeMonitor(monitor);
	// }
}
