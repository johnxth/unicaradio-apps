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

import it.unicaradio.android.enums.CoverDownloadMode;
import it.unicaradio.android.enums.NetworkType;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * @author Paolo Cortis
 */
public class UnicaradioPreferences
{
	public static final String PREF_NETWORK_TYPE = "network_type";

	public static final String PREF_DOWNLOAD_COVER = "download_cover";

	public static final String PREF_PERMIT_ROAMING = "permit_roaming";

	public static final String PREF_APP_VERSION = "app_version";

	public static final String PREF_LASTRUNVERSIONCODE = "lastRunVersionCode";

	public static final String PREF_LICENCE_AACDECODER = "prefs_licences_aacdecoder";

	public static final String PREF_LICENCE_ABS = "prefs_licences_abs";

	public static final String PREF_LICENCE_ACRA = "prefs_licences_acra";

	public static final String PREF_LICENCE_ACRA_DETAILS = "prefs_licences_acra_details";

	public static final String DOWNLOAD_COVER_MOBILEDATA = "mobiledata";

	public static final String DOWNLOAD_COVER_WIFIONLY = "wifi_only";

	public static final String DOWNLOAD_COVER_NEVER = "never";

	private static final String TAG = UnicaradioPreferences.class.getName();

	private UnicaradioPreferences()
	{
	}

	public static NetworkType getNetworkType(Context context)
	{
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		String networkType = sharedPreferences.getString(
				UnicaradioPreferences.PREF_NETWORK_TYPE, StringUtils.EMPTY);

		Log.d(TAG, "Network type: " + networkType);

		return NetworkType.fromString(networkType);
	}

	public static boolean isRoamingPermitted(Context context)
	{
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		return sharedPreferences.getBoolean(
				UnicaradioPreferences.PREF_PERMIT_ROAMING, false);
	}

	public static CoverDownloadMode getCoverDownloadMode(Context context)
	{
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		String coverDownloadMode = sharedPreferences.getString(
				UnicaradioPreferences.PREF_DOWNLOAD_COVER, StringUtils.EMPTY);

		Log.d(TAG, "Cover download mode: " + coverDownloadMode);

		return CoverDownloadMode.fromString(coverDownloadMode);
	}
}
