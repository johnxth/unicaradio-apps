package it.unicaradio.android.tasks;

import it.unicaradio.android.enums.Error;
import it.unicaradio.android.models.Response;
import it.unicaradio.android.utils.NetworkUtils;

import java.io.IOException;

import android.app.AlertDialog;
import android.content.Context;

public class DownloadScheduleAsyncTask extends
		BlockingAsyncTask<Void, Void, Response<String>>
{
	private static final String SCHEDULE_URL = "http://www.unicaradio.it/regia/test/palinsesto.php";

	// private static final String TAG =
	// DownloadScheduleAsyncTask.class.getName();

	public DownloadScheduleAsyncTask(Context context)
	{
		super(context);

		setDialogMessage("Caricamento palinsesto in corso...");
	}

	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
	}

	@Override
	protected Response<String> doInBackground(Void... params)
	{
		try {
			Response<String> response = new Response<String>();
			response.setResult(new String(NetworkUtils.downloadFromUrl(SCHEDULE_URL)));
			response.setErrorCode(Error.OK);

			return response;
		} catch(IOException e) {
			return new Response<String>(Error.DOWNLOAD_ERROR);
		}
	}

	@Override
	protected void onPostExecute(Response<String> result)
	{
		super.onPostExecute(result);

		if(result.containsError()) {
			handleErrors(result);
			return;
		}

		emitTaskCompleted(result);
	}

	private void handleErrors(Response<String> result)
	{
		if(result.getErrorCode() == Error.DOWNLOAD_ERROR) {
			new AlertDialog.Builder(context)
					.setTitle("Errore!")
					.setMessage(
							"È avvenuto un errore. Verifica di essere connesso ad Internet.")
					.setCancelable(false).setPositiveButton("OK", null).show();
		}
	}
}
