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
import it.unicaradio.android.fragments.UnicaradioFragment;
import it.unicaradio.android.gui.Tab;
import it.unicaradio.android.gui.Tabs;
import it.unicaradio.android.listeners.TabSelectedListener;

import java.util.HashMap;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

/**
 * @author Paolo Cortis
 */
public class MainActivity extends SherlockFragmentActivity
{
	private static TabSelectedListener tabSelectedListener;

	private SharedPreferences preferences;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		if(tabSelectedListener == null) {
			tabSelectedListener = new TabSelectedListener(
					getSupportFragmentManager());
		}

		getSupportActionBar().setDisplayOptions(
				ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE
						| ActionBar.DISPLAY_USE_LOGO);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setDisplayUseLogoEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);

		setContentView(R.layout.main_layout);

		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			BitmapDrawable bg = (BitmapDrawable) getResources().getDrawable(
					R.drawable.title_bg);
			bg.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
			getSupportActionBar().setBackgroundDrawable(bg);

			BitmapDrawable bgSplit = (BitmapDrawable) getResources()
					.getDrawable(R.drawable.black_stripe);
			bgSplit.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
			getSupportActionBar().setSplitBackgroundDrawable(bgSplit);
		}

		Map<Integer, Tab> tabs = updateReferences();
		tabs.get(Tabs.STREAMING).setSelected(true);
		setupListeners(tabs);

		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		preferences = getPreferences(Context.MODE_PRIVATE);
		if(hasBeenUpdated()) {
			showUpdatesDialog();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		UnicaradioFragment currentFragment = tabSelectedListener
				.getCurrentFragment();
		boolean result = currentFragment.onKeyDown(keyCode, event);
		return result || super.onKeyDown(keyCode, event);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		UnicaradioFragment currentFragment = tabSelectedListener
				.getCurrentFragment();
		boolean result = currentFragment.onKeyUp(keyCode, event);
		return result || super.onKeyUp(keyCode, event);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event)
	{
		UnicaradioFragment currentFragment = tabSelectedListener
				.getCurrentFragment();
		boolean result = currentFragment.onKeyMultiple(keyCode, repeatCount,
				event);
		return result || super.onKeyMultiple(keyCode, repeatCount, event);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event)
	{
		UnicaradioFragment currentFragment = tabSelectedListener
				.getCurrentFragment();
		boolean result = currentFragment.onKeyLongPress(keyCode, event);
		return result || super.onKeyLongPress(keyCode, event);
	}

	private Map<Integer, Tab> updateReferences()
	{
		Map<Integer, Tab> tabs = new HashMap<Integer, Tab>();

		Tabs tabsContainer = (Tabs) findViewById(R.id.tabs);
		for(int i = 0; i < tabsContainer.getChildCount(); i++) {
			View child = tabsContainer.getChildAt(i);
			if(child instanceof Tab) {
				Tab tab = (Tab) child;
				tabs.put(tab.getType(), tab);
			}
		}

		return tabs;
	}

	private void setupListeners(Map<Integer, Tab> tabs)
	{
		for(final Map.Entry<Integer, Tab> tab : tabs.entrySet()) {
			Tab viewTab = tab.getValue();

			viewTab.setOnTabSelectedListener(tabSelectedListener);
		}
	}

	private boolean hasBeenUpdated()
	{
		try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(
					getPackageName(), PackageManager.GET_META_DATA);
			long lastRunVersionCode = preferences.getLong("lastRunVersionCode",
					0);
			if(lastRunVersionCode < pInfo.versionCode) {
				Editor editor = preferences.edit();
				editor.putLong("lastRunVersionCode", pInfo.versionCode);
				editor.commit();

				return(lastRunVersionCode > 0);
			}
		} catch(NameNotFoundException e) {
			e.printStackTrace();
		}

		return false;
	}

	private void showUpdatesDialog()
	{
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.popup);
		dialog.setTitle("L'applicazione è stata aggiornata!");
		dialog.setCancelable(true);

		TextView textView = (TextView) dialog.findViewById(R.id.updatesText);
		textView.setText(R.string.updates);

		Button button = (Button) dialog.findViewById(R.id.updatesButton);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				dialog.hide();
			}
		});
		dialog.show();
	}
}