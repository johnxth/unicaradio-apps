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
import it.unicaradio.android.models.SongRequest;
import it.unicaradio.android.utils.NetworkUtils;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

/**
 * @author Paolo Cortis
 */
public class SendSongRequestAsyncTask extends
		BlockingAsyncTaskWithResponse<String>
{
	private static final String WEB_SERVICE = "http://www.unicaradio.it/regia/test/unicaradio-mail/endpoint.php";

	private SongRequest songRequest;

	/**
	 * @param context
	 */
	public SendSongRequestAsyncTask(Context context, SongRequest songRequest)
	{
		super(context);

		this.songRequest = songRequest;

		setDialogTitle("Richiesta canzone");
		setDialogMessage("Invio richiesta canzone in corso...");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Response<String> doInBackground(Void... params)
	{
		JSONObject request = new JSONObject();
		try {
			request.put("method", "sendEmail");
			request.put("params", songRequest.toJSON(context));
		} catch(JSONException e) {
			return new Response<String>(Error.INTERNAL_GENERIC_ERROR);
		}

		byte[] postData = request.toString().getBytes();
		byte[] httpResult;
		try {
			httpResult = NetworkUtils.httpPost(WEB_SERVICE, postData,
					"application/json");
		} catch(ClientProtocolException e1) {
			return new Response<String>(Error.INTERNAL_GENERIC_ERROR);
		} catch(IOException e1) {
			return new Response<String>(Error.INTERNAL_DOWNLOAD_ERROR);
		} catch(HttpException e1) {
			return new Response<String>(Error.INTERNAL_GENERIC_ERROR);
		}

		if(httpResult == null) {
			return new Response<String>(Error.INTERNAL_GENERIC_ERROR);
		}

		String httpResultString = new String(httpResult);
		try {
			JSONObject response = new JSONObject(httpResultString);
			int errorCodeInt = response.getInt("errorCode");
			Error errorCode = Error.fromInteger(errorCodeInt);
			if(errorCode == Error.NO_ERROR) {
				return new Response<String>(httpResultString);
			} else {
				return new Response<String>(errorCode);
			}
		} catch(JSONException e) {
			return new Response<String>(Error.INTERNAL_GENERIC_ERROR);
		}
	}
}
