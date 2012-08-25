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
import it.unicaradio.android.activities.InfoActivity;
import it.unicaradio.android.gui.Tabs;
import android.test.MoreAsserts;
import android.widget.TextView;

/**
 * @author Paolo Cortis
 */
public class InfoActivityTest extends TabbedActivityTest<InfoActivity>
{
	private static final String TAG = InfoActivityTest.class.getName();

	public InfoActivityTest()
	{
		super(InfoActivity.class.getPackage().getName(), InfoActivity.class);
	}

	public void testInfoText()
	{
		TextView infosText = (TextView) activity.findViewById(R.id.infosText);

		CharSequence text = infosText.getText();
		assertNotNull(text);
		MoreAsserts.assertNotEqual("", text);
	}

	public void testTab()
	{
		assertEquals(activity.getTab(), Tabs.INFO);
	}

	// public void testClickDeveloperLink()
	// {
	// View view = activity.findViewById(R.id.infosText);
	// final Instrumentation instrumentation = getInstrumentation();
	// IntentFilter intentFilter = new IntentFilter(Intent.ACTION_VIEW);
	// intentFilter.addDataScheme("http");
	// intentFilter.addCategory(Intent.CATEGORY_BROWSABLE);
	//
	// ActivityMonitor monitor = instrumentation.addMonitor(intentFilter,
	// null, false);
	//
	// assertEquals(0, monitor.getHits());
	// TouchUtils.clickView(this, view);
	// monitor.waitForActivityWithTimeout(15000);
	// assertEquals(1, monitor.getHits());
	// instrumentation.removeMonitor(monitor);
	// }

	public void testClickDeveloperLink() throws InterruptedException
	{
		solo.clickOnText("Paolo Cortis");
		solo.waitForText("unicaradio-apps", 1, 5000);
		solo.goBack();
		solo.assertCurrentActivity("wrong activity", InfoActivity.class);
	}

	public void testClickSourceCodeLink() throws InterruptedException
	{
		solo.clickOnText("Google Code");
		solo.waitForText("unicaradio-apps", 1, 5000);
		solo.goBack();
		solo.assertCurrentActivity("wrong activity", InfoActivity.class);
	}
}
