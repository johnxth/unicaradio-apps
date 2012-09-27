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
package it.unicaradio.android.models;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

/**
 * @author Paolo Cortis
 */
public class SongRequest
{
	private static final int ANDROID_APP_CODE = 0x01B3;

	private String email;

	private String author;

	private String title;

	/**
	 * @return the email
	 */
	public String getEmail()
	{
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email)
	{
		this.email = email;
	}

	/**
	 * @return the author
	 */
	public String getAuthor()
	{
		return author;
	}

	/**
	 * @param author the author to set
	 */
	public void setAuthor(String author)
	{
		this.author = author;
	}

	/**
	 * @return the title
	 */
	public String getTitle()
	{
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}

	public JSONObject toJSON(Context context)
	{
		JSONObject params = new JSONObject();
		try {
			params.put("art", getAuthor());
			params.put("mail", getEmail());
			params.put("tit", getTitle());

			JSONObject app = new JSONObject();
			app.put("code", ANDROID_APP_CODE);
			app.put("versionCode", getAppVersionCode(context));
			app.put("systemVersion", Build.VERSION.SDK_INT);

			params.put("app", app);
		} catch(JSONException e) {
			// do nothing
		}

		return params;
	}

	private int getAppVersionCode(Context context)
	{

		try {
			PackageInfo pInfo = context.getPackageManager().getPackageInfo(
					context.getPackageName(), PackageManager.GET_META_DATA);

			return pInfo.versionCode;
		} catch(NameNotFoundException e) {
			// do nothing
		}

		return -1;
	}
}
