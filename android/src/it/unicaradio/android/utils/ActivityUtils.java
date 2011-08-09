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
package it.unicaradio.android.utils;

import it.unicaradio.android.R;
import it.unicaradio.android.StreamingActivity;
import it.unicaradio.android.activities.ScheduleActivity;
import it.unicaradio.android.activities.TabbedActivity;
import it.unicaradio.android.gui.Tabs;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * @author Paolo Cortis
 * 
 */
public class ActivityUtils
{

	public static HashMap<String, String> addItem(String line1, String line2)
	{
		HashMap<String, String> item = new HashMap<String, String>();
		item.put("line1", line1);
		item.put("line2", line2);

		return item;
	}

	public static HashMap<String, String> addItem(String line1, String line2,
			int resourceImage)
	{
		HashMap<String, String> item = addItem(line1, line2);
		item.put("icon", String.valueOf(resourceImage));

		return item;
	}

	public static void setupListeners(final TabbedActivity activity)
	{
		Map<Integer, View> tabs = updateReferences(activity);

		setupListeners(activity, tabs);
	}

	public static void setupListeners(final TabbedActivity activity,
			Map<Integer, View> tabs)
	{
		for(final Map.Entry<Integer, View> tab : tabs.entrySet()) {
			View view = tab.getValue();
			view.setOnClickListener(new OnClickListener() {
				public void onClick(View view)
				{
					if(activity.getTab() != tab.getKey()) {
						switch(tab.getKey()) {
							case Tabs.STREAMING:
								activity.startActivity(new Intent(view
										.getContext(), StreamingActivity.class));
								break;
							case Tabs.SCHEDULE:
								activity.startActivity(new Intent(view
										.getContext(), ScheduleActivity.class));
								break;
						/*
						 * case Tabs.SONG: activity.startActivity(new
						 * Intent(view .getContext(), StreamingActivity.class));
						 * break; case Tabs.FAVORITES:
						 * activity.startActivity(new Intent(view .getContext(),
						 * StreamingActivity.class)); break; case Tabs.INFO:
						 * activity.startActivity(new Intent(view .getContext(),
						 * StreamingActivity.class)); break;
						 */
						}
					}
				}
			});
		}
	}

	public static Map<Integer, View> updateReferences(Activity activity)
	{
		Map<Integer, View> tabs = new HashMap<Integer, View>();

		tabs.put(Tabs.STREAMING, activity.findViewById(R.id.streamingTab));
		tabs.put(Tabs.SCHEDULE, activity.findViewById(R.id.scheduleTab));
		tabs.put(Tabs.SONG, activity.findViewById(R.id.songTab));
		tabs.put(Tabs.FAVORITES, activity.findViewById(R.id.favoritesTab));
		tabs.put(Tabs.INFO, activity.findViewById(R.id.infosTab));

		return tabs;
	}

	public static boolean onKeyDown(Activity activity, int keyCode,
			KeyEvent event)
	{
		if((keyCode == KeyEvent.KEYCODE_BACK)) {
			activity.moveTaskToBack(true);
			return true;
		} else if(keyCode == KeyEvent.KEYCODE_HOME) {
			activity.moveTaskToBack(true);
			return true;
		}

		return false;
	}
}
