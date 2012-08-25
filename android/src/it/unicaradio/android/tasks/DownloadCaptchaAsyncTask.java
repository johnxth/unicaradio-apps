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
package it.unicaradio.android.tasks;

import it.unicaradio.android.enums.Error;
import it.unicaradio.android.models.Response;
import it.unicaradio.android.utils.NetworkUtils;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

/**
 * @author Paolo Cortis
 */
public class DownloadCaptchaAsyncTask extends
		BlockingAsyncTaskWithResponse<String>
{
	private static final String WEB_SERVICE = "http://www.unicaradio.it/regia/test/unicaradio-mail/endpoint.php";

	private static final String TAG = DownloadCaptchaAsyncTask.class.getName();

	/**
	 * @param context
	 */
	public DownloadCaptchaAsyncTask(Context context)
	{
		super(context);

		setDialogTitle("CAPTCHA");
		setDialogMessage("Generazione CAPTCHA in corso...");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Response<String> doInBackground(Void... params)
	{
		try {
			JSONObject request = new JSONObject();
			request.put("method", "getCaptcha");
			byte[] postData = request.toString().getBytes();

			Response<String> response = new Response<String>();
			byte[] resultBytes = NetworkUtils.httpPost(WEB_SERVICE, postData,
					"application/json");

			String resultString = new String(resultBytes);
			Log.d(TAG, "Got CAPTCHA answer: " + resultString);
			JSONObject resultJSON = new JSONObject(resultString);
			response.setResult(resultJSON.getString("result"));

			return response;
		} catch(ClientProtocolException e) {
			Log.e(TAG, e.getMessage(), e);
			return new Response<String>(Error.INTERNAL_GENERIC_ERROR);
		} catch(IOException e) {
			Log.e(TAG, e.getMessage(), e);
			return new Response<String>(Error.INTERNAL_DOWNLOAD_ERROR);
		} catch(HttpException e) {
			Log.e(TAG, e.getMessage(), e);
			return new Response<String>(Error.INTERNAL_GENERIC_ERROR);
		} catch(JSONException e) {
			Log.e(TAG, e.getMessage(), e);
			return new Response<String>(Error.INTERNAL_GENERIC_ERROR);
		}
	}
}
