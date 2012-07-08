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

import android.content.Context;
import android.util.Log;

/**
 * @author Paolo Cortis
 */
public class SendSongRequestAsyncTask extends
		BlockingAsyncTaskWithResponse<String>
{
	private static final String TAG = SendSongRequestAsyncTask.class.getName();

	private final String url;

	/**
	 * @param context
	 */
	public SendSongRequestAsyncTask(Context context, String url)
	{
		super(context);

		this.url = url;

		setDialogTitle("Richiesta canzone");
		setDialogMessage("Invio richiesta canzone in corso...");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Response<String> doInBackground(Void... params)
	{
		try {
			String result = new String(NetworkUtils.downloadFromUrl(url));
			Log.d(TAG, result);

			// TODO: Handle errors received from server.
			if(result.equals("OK")) {
				return new Response<String>(result);
			} else {
				return new Response<String>(Error.GENERIC_ERROR);
			}
		} catch(IOException e) {
			return new Response<String>(Error.DOWNLOAD_ERROR);
		}
	}

}
