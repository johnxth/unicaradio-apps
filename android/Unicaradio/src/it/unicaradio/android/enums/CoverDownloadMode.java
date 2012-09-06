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
package it.unicaradio.android.enums;

import it.unicaradio.android.utils.StringUtils;

/**
 * @author Paolo Cortis
 */
public enum CoverDownloadMode {
	MOBILE_DATA, WIFI_ONLY, NEVER;

	public static final String COVER_DOWNLOAD_ON_MOBILEDATA = "mobiledata";

	public static final String COVER_DOWNLOAD_ON_WIFIONLY = "wifi_only";

	public static final String COVER_DOWNLOAD_NEVER = "never";

	public static CoverDownloadMode fromString(String coverDownloadEnum)
	{
		if(StringUtils.equals(coverDownloadEnum, COVER_DOWNLOAD_ON_MOBILEDATA)) {
			return MOBILE_DATA;
		}

		if(StringUtils.equals(coverDownloadEnum, COVER_DOWNLOAD_ON_WIFIONLY)) {
			return WIFI_ONLY;
		}

		if(StringUtils.equals(coverDownloadEnum, COVER_DOWNLOAD_NEVER)) {
			return NEVER;
		}

		return null;
	}
}
