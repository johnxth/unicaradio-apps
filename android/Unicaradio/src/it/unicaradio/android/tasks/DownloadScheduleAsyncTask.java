package it.unicaradio.android.tasks;

import it.unicaradio.android.enums.Error;
import it.unicaradio.android.models.Response;
import it.unicaradio.android.utils.NetworkUtils;

import java.io.IOException;

import android.content.Context;

public class DownloadScheduleAsyncTask extends
		BlockingAsyncTaskWithResponse<String>
{
	private static final String SCHEDULE_URL = "http://www.unicaradio.it/regia/test/palinsesto.php";

	public DownloadScheduleAsyncTask(Context context, boolean shouldShowDialog)
	{
		super(context);

		setDialogMessage("Caricamento palinsesto in corso...");
		setDialogVisible(shouldShowDialog);
	}

	@Override
	protected Response<String> doInBackground(Void... params)
	{
		try {
			Response<String> response = new Response<String>();
			response.setResult(new String(NetworkUtils.httpGet(SCHEDULE_URL)));

			return response;
		} catch(IOException e) {
			return new Response<String>(Error.INTERNAL_DOWNLOAD_ERROR);
		}
	}
}
