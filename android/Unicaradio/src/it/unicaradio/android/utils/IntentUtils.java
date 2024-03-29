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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

/**
 * @author Paolo Cortis
 */
public class IntentUtils
{
	public static final String ARG_SCHEDULE_DAY = "schedule_day";

	public static final String ARG_SCHEDULE = "schedule";

	private IntentUtils()
	{
	}

	public static void openLink(Context context, String url)
	{
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		context.startActivity(i);
	}

	public static Intent createIntentForSharing(Activity context, String subject, String content)
	{
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_TEXT, content);

		return intent;
	}

	public static String getAppVersion(Context context)
	{
		try {
			String packageName = context.getPackageName();
			PackageManager packageManager = context.getPackageManager();
			PackageInfo pInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA);

			return pInfo.versionName;
		} catch(PackageManager.NameNotFoundException e) {
			return StringUtils.EMPTY;
		}
	}
}
