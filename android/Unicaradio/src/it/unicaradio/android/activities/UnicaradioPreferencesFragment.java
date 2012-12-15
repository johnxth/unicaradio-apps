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
import it.unicaradio.android.activities.UnicaradioPreferencesActivity.OpenPreferenceLink;
import it.unicaradio.android.utils.UnicaradioPreferences;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

/**
 * @author Paolo Cortis
 */
@TargetApi(11)
public class UnicaradioPreferencesFragment extends PreferenceFragment implements
		OnSharedPreferenceChangeListener
{
	private UnicaradioPreferencesActivity preferencesActivity;

	private long[] mHits = new long[3];

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
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key)
	{
		Preference preference = findPreference(key);
		preferencesActivity.updatePreferenceSummary(preference);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference)
	{
		if(preference.getKey().equals(UnicaradioPreferences.PREF_APP_VERSION)) {
			System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
			mHits[mHits.length - 1] = SystemClock.uptimeMillis();
			if(mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
				AlertDialog.Builder dialogBuilder = new Builder(getActivity());
				dialogBuilder.setTitle("Messaggi");

				String message = preferencesActivity
						.getGcmMessagesDialogMessage();
				dialogBuilder.setMessage(message);
				dialogBuilder.setPositiveButton(android.R.string.yes,
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which)
							{
								preferencesActivity.toggleGcmMessagesValue();
							}
						});
				dialogBuilder.setNegativeButton(android.R.string.no, null);
				dialogBuilder.show();
			}
		}

		return super.onPreferenceTreeClick(preferenceScreen, preference);
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

	private void initListeners()
	{
		Preference prefs_licences_aacdecoder = findPreference(UnicaradioPreferences.PREF_LICENCE_AACDECODER);
		prefs_licences_aacdecoder
				.setOnPreferenceClickListener(new OpenPreferenceLink(
						getActivity(), R.string.prefs_aacdecoder_link));

		Preference prefs_licences_abs = findPreference(UnicaradioPreferences.PREF_LICENCE_ABS);
		prefs_licences_abs.setOnPreferenceClickListener(new OpenPreferenceLink(
				getActivity(), R.string.prefs_abs_link));

		Preference prefs_licences_acra = findPreference(UnicaradioPreferences.PREF_LICENCE_ACRA);
		prefs_licences_acra
				.setOnPreferenceClickListener(new OpenPreferenceLink(
						getActivity(), R.string.prefs_acra_link));

		Preference prefs_licences_acra_details = findPreference(UnicaradioPreferences.PREF_LICENCE_ACRA_DETAILS);
		prefs_licences_acra_details
				.setOnPreferenceClickListener(new OpenPreferenceLink(
						getActivity(), R.string.prefs_acra_details_link));
	}
}
