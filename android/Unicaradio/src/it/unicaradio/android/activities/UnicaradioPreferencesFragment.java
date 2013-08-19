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

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import it.unicaradio.android.R;
import it.unicaradio.android.activities.UnicaradioPreferencesActivity.OpenPreferenceLink;
import it.unicaradio.android.utils.IntentUtils;
import it.unicaradio.android.utils.StringUtils;
import it.unicaradio.android.utils.UnicaradioPreferences;

/**
 * @author Paolo Cortis
 */
@TargetApi(11)
public class UnicaradioPreferencesFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener
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
			getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

			setCurrentValuesInSummary();
			initListeners();
		} else {
			throw new IllegalArgumentException(
					"You MUST call this fragment from a UnicaradioPreferencesActivity instance");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
	{
		Preference preference = findPreference(key);
		preferencesActivity.updatePreferenceSummary(preference);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference)
	{
		if(StringUtils.equals(preference.getKey(), UnicaradioPreferences.PREF_APP_VERSION)) {
			// TODO
		} else if(StringUtils.equals(preference.getKey(), UnicaradioPreferences.PREF_LICENSE_GCM)) {
			preferencesActivity.onGcmVersionClick();
		}

		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	private void setCurrentValuesInSummary()
	{
		for(int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
			preferencesActivity.initSummary(getPreferenceScreen().getPreference(i));
		}

		Preference app_version = findPreference(UnicaradioPreferences.PREF_APP_VERSION);
		app_version.setSummary(IntentUtils.getAppVersion(preferencesActivity));
	}

	private void initListeners()
	{
		Preference prefs_licenses_aacdecoder = findPreference(UnicaradioPreferences.PREF_LICENSE_AACDECODER);
		prefs_licenses_aacdecoder.setOnPreferenceClickListener(
				new OpenPreferenceLink(preferencesActivity, R.string.prefs_aacdecoder_link));

		Preference prefs_licenses_abs = findPreference(UnicaradioPreferences.PREF_LICENSE_ABS);
		prefs_licenses_abs
				.setOnPreferenceClickListener(new OpenPreferenceLink(preferencesActivity, R.string.prefs_abs_link));

		Preference prefs_licenses_acra = findPreference(UnicaradioPreferences.PREF_LICENSE_ACRA);
		prefs_licenses_acra
				.setOnPreferenceClickListener(new OpenPreferenceLink(preferencesActivity, R.string.prefs_acra_link));

		Preference prefs_licenses_acra_details = findPreference(UnicaradioPreferences.PREF_LICENSE_ACRA_DETAILS);
		prefs_licenses_acra_details.setOnPreferenceClickListener(
				new OpenPreferenceLink(preferencesActivity, R.string.prefs_acra_details_link));

		Preference prefs_gcm_messages_info = findPreference(UnicaradioPreferences.PREF_GCM_MESSAGES_INFO);
		prefs_gcm_messages_info
				.setOnPreferenceClickListener(new OpenPreferenceLink(preferencesActivity, R.string.prefs_gcm_link));

		Preference prefs_wversionmanager = findPreference(UnicaradioPreferences.PREF_LICENSE_WVERSIONMANAGER);
		prefs_wversionmanager.setOnPreferenceClickListener(
				new OpenPreferenceLink(preferencesActivity, R.string.prefs_wversionmanager_link));
	}
}
