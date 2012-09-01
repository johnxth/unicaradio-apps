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
import it.unicaradio.android.consts.UnicaradioPreferences;
import it.unicaradio.android.utils.StringUtils;
import it.unicaradio.android.utils.ViewUtils;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

/**
 * @author Paolo Cortis
 */
public class UnicaradioPreferencesActivity extends SherlockPreferenceActivity
		implements OnSharedPreferenceChangeListener
{
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		ViewUtils.setupActionBar(getSupportActionBar(), getResources());
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			addPreferencesFromResource(R.xml.prefs);

			setCurrentValuesInSummary();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onResume()
	{
		super.onResume();

		SharedPreferences defaultSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		defaultSharedPreferences.registerOnSharedPreferenceChangeListener(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onPause()
	{
		super.onPause();

		SharedPreferences defaultSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		defaultSharedPreferences
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key)
	{
		@SuppressWarnings("deprecation")
		Preference preference = findPreference(key);

		updatePreferenceSummary(preference);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@SuppressWarnings("deprecation")
	private void setCurrentValuesInSummary()
	{
		for(int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
			initSummary(getPreferenceScreen().getPreference(i));
		}

		Preference app_version = findPreference(UnicaradioPreferences.PREF_APP_VERSION);
		app_version.setSummary(getAppVersion());
	}

	void initSummary(Preference preference)
	{
		if(preference instanceof PreferenceCategory) {
			PreferenceCategory preferenceCategory = (PreferenceCategory) preference;
			for(int i = 0; i < preferenceCategory.getPreferenceCount(); i++) {
				initSummary(preferenceCategory.getPreference(i));
			}
		} else {
			updatePreferenceSummary(preference);
		}
	}

	void updatePreferenceSummary(Preference preference)
	{
		if(preference instanceof ListPreference) {
			ListPreference listPreference = (ListPreference) preference;
			preference.setSummary(getSummaryForListPreference(listPreference));
			return;
		}
		if(preference instanceof EditTextPreference) {
			EditTextPreference editTextPreference = (EditTextPreference) preference;
			preference.setSummary(editTextPreference.getText());
			return;
		}
	}

	/**
	 * @param preference
	 * @return
	 */
	String getSummaryForListPreference(ListPreference preference)
	{
		if(StringUtils.equals(preference.getKey(),
				UnicaradioPreferences.PREF_NETWORK_TYPE)) {
			if(StringUtils.equals(preference.getValue(),
					UnicaradioPreferences.NETWORK_TYPE_MOBILEDATA)) {
				return getString(R.string.prefs_network_type_mobiledata_label);
			} else {
				return getString(R.string.prefs_network_type_wifi_label);
			}
		} else if(StringUtils.equals(preference.getKey(),
				UnicaradioPreferences.PREF_DOWNLOAD_COVER)) {
			if(StringUtils.equals(preference.getValue(),
					UnicaradioPreferences.DOWNLOAD_COVER_MOBILEDATA)) {
				return getString(R.string.prefs_download_cover_mobiledata_label);
			} else if(StringUtils.equals(preference.getValue(),
					UnicaradioPreferences.DOWNLOAD_COVER_WIFIONLY)) {
				return getString(R.string.prefs_download_cover_wifi_label);
			} else {
				return getString(R.string.prefs_download_cover_never_label);
			}
		}

		return StringUtils.EMPTY;
	}

	String getAppVersion()
	{
		try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(
					getPackageName(), PackageManager.GET_META_DATA);
			return pInfo.versionName;
		} catch(NameNotFoundException e) {
			return StringUtils.EMPTY;
		}
	}
}
