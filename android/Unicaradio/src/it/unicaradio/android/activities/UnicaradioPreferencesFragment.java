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
import it.unicaradio.android.utils.UnicaradioPreferences;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

/**
 * @author Paolo Cortis
 */
@TargetApi(11)
public class UnicaradioPreferencesFragment extends PreferenceFragment implements
		OnSharedPreferenceChangeListener
{
	private UnicaradioPreferencesActivity preferencesActivity;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		addPreferencesFromResource(R.xml.prefs);

		if(getActivity() instanceof UnicaradioPreferencesActivity) {
			preferencesActivity = ((UnicaradioPreferencesActivity) getActivity());
			getPreferenceManager().getSharedPreferences()
					.registerOnSharedPreferenceChangeListener(this);

			setCurrentValuesInSummary();
		} else {
			throw new IllegalArgumentException(
					"You MUST call this fragment from a UnicaradioPreferencesActivity instance");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key)
	{
		Preference preference = findPreference(key);
		preferencesActivity.updatePreferenceSummary(preference);
	}

	private void setCurrentValuesInSummary()
	{
		for(int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
			preferencesActivity.initSummary(getPreferenceScreen()
					.getPreference(i));
		}

		Preference app_version = findPreference(UnicaradioPreferences.PREF_APP_VERSION);
		app_version.setSummary(preferencesActivity.getAppVersion());
	}
}
