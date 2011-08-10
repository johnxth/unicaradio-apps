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
package it.unicaradio.android.activities;

import it.unicaradio.android.R;
import it.unicaradio.android.StreamingActivity;
import it.unicaradio.android.gui.Tabs;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * @author Paolo Cortis
 * 
 */
public abstract class TabbedActivity extends Activity
{
	protected void onCreate(Bundle savedInstanceState, int layoutResID)
	{
		super.onCreate(savedInstanceState);
		setContentView(layoutResID);

		setupTab();

		Map<Integer, View> tabs = updateReferences();
		tabs.get(getTab()).setSelected(true);

		_setupListeners(tabs);
		setupListeners();
	}

	protected abstract void setupTab();

	protected abstract void setupListeners();

	public abstract int getTab();

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if((keyCode == KeyEvent.KEYCODE_BACK)) {
			moveTaskToBack(true);
			return true;
		} else if(keyCode == KeyEvent.KEYCODE_HOME) {
			moveTaskToBack(true);
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	protected void showAlertDialog(String title, String message)
	{
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle(title);
		adb.setMessage(message);
		adb.setPositiveButton("Ok", null);
		adb.show();
	}

	protected Map<Integer, View> updateReferences()
	{
		Map<Integer, View> tabs = new HashMap<Integer, View>();

		tabs.put(Tabs.STREAMING, findViewById(R.id.streamingTab));
		tabs.put(Tabs.SCHEDULE, findViewById(R.id.scheduleTab));
		tabs.put(Tabs.SONG, findViewById(R.id.songTab));
		tabs.put(Tabs.FAVORITES, findViewById(R.id.favoritesTab));
		tabs.put(Tabs.INFO, findViewById(R.id.infosTab));

		return tabs;
	}

	public void _setupListeners()
	{
		Map<Integer, View> tabs = updateReferences();

		_setupListeners(tabs);
	}

	private void _setupListeners(Map<Integer, View> tabs)
	{
		for(final Map.Entry<Integer, View> tab : tabs.entrySet()) {
			View view = tab.getValue();
			view.setOnClickListener(new OnClickListener() {
				public void onClick(View view)
				{
					if(getTab() != tab.getKey()) {
						switch(tab.getKey()) {
							case Tabs.STREAMING:
								startActivity(new Intent(view.getContext(),
										StreamingActivity.class));
								break;
							case Tabs.SCHEDULE:
								startActivity(new Intent(view.getContext(),
										ScheduleActivity.class));
								break;
							case Tabs.SONG:
								startActivity(new Intent(view.getContext(),
										SongRequestActivity.class));
								break;
						// case Tabs.FAVORITES:
						// startActivity(new Intent(view
						// .getContext(), StreamingActivity.class));
						// break;
						// case Tabs.INFO:
						// startActivity(new Intent(view
						// .getContext(), StreamingActivity.class));
						// break;
						}
					}
				}
			});
		}
	}
}
