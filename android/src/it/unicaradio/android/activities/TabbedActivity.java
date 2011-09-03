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
import it.unicaradio.android.gui.Tabs;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle item selection
		switch(item.getItemId()) {
			case R.id.exit:
				System.runFinalizersOnExit(true);
				System.exit(0);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public void openLink(String url)
	{
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		startActivity(i);
	}

	public Display getDisplay()
	{
		return getWindowManager().getDefaultDisplay();
	}

	public boolean isConnected()
	{
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		for(NetworkInfo info : connectivityManager.getAllNetworkInfo()) {
			if(info.isConnected()) {
				return true;
			}
		}
		return false;
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

	private void _setupListeners(Map<Integer, View> tabs)
	{
		for(final Map.Entry<Integer, View> tab : tabs.entrySet()) {
			View view = tab.getValue();
			view.setOnClickListener(new OnClickListener() {
				public void onClick(View view)
				{
					if(getTab() != tab.getKey()) {
						Log.d(TabbedActivity.class.getName(), tab.getKey()
								.toString());
						switch(tab.getKey()) {
							case Tabs.STREAMING:
								Log.d(TabbedActivity.class.getName(),
										"Calling StreamingActivity");
								startActivity(new Intent(view.getContext(),
										StreamingActivity.class));
								break;
							case Tabs.SCHEDULE:
								Log.d(TabbedActivity.class.getName(),
										"Calling ScheduleActivity");
								startActivity(new Intent(view.getContext(),
										ScheduleActivity.class));
								break;
							case Tabs.SONG:
								Log.d(TabbedActivity.class.getName(),
										"Calling SongRequestActivity");
								startActivity(new Intent(view.getContext(),
										SongRequestActivity.class));
								break;
							case Tabs.FAVORITES:
								Log.d(TabbedActivity.class.getName(),
										"Calling FavoritesActivity");
								startActivity(new Intent(view.getContext(),
										FavoritesActivity.class));
								break;
							case Tabs.INFO:
								Log.d(TabbedActivity.class.getName(),
										"Calling InfoActivity");
								startActivity(new Intent(view.getContext(),
										InfoActivity.class));
								break;
						}
					}
				}
			});
		}
	}
}
