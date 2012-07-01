package it.unicaradio.android.tasks;

import it.unicaradio.android.R;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public abstract class BlockingAsyncTask<Params, Progress, Result> extends
		AsyncTask<Params, Progress, Result>
{
	protected Context context;

	protected ProgressDialog progressDialog;

	protected String dialogTitle;

	protected String dialogMessage;

	protected List<OnTaskCompletedListener<Result>> listeners;

	public BlockingAsyncTask(Context context)
	{
		this.dialogTitle = context
				.getString(R.string.wait_dialog_default_title);
		this.dialogMessage = context
				.getString(R.string.wait_dialog_default_message);
		this.context = context;
		this.listeners = new ArrayList<OnTaskCompletedListener<Result>>();
	}

	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();

		progressDialog = ProgressDialog.show(context, dialogTitle,
				dialogMessage, true);
	}

	@Override
	protected void onPostExecute(Result result)
	{
		super.onPostExecute(result);

		try {
			progressDialog.dismiss();
		} catch(IllegalArgumentException e) {
			// do nothing
		}
	}

	@Override
	protected void onCancelled(Result result)
	{
		try {
			progressDialog.dismiss();
		} catch(IllegalArgumentException e) {
			// do nothing
		}
	}

	/**
	 * @param dialogTitle
	 *            the dialogTitle to set
	 */
	public void setDialogTitle(String dialogTitle)
	{
		this.dialogTitle = dialogTitle;
	}

	/**
	 * @param dialogMessage
	 *            the dialogMessage to set
	 */
	public void setDialogMessage(String dialogMessage)
	{
		this.dialogMessage = dialogMessage;
	}

	/**
	 * @param dialogTitle
	 *            the dialogTitle to set
	 */
	public void setDialogTitle(int dialogTitle)
	{
		this.dialogTitle = context.getString(dialogTitle);
	}

	/**
	 * @param dialogMessage
	 *            the dialogMessage to set
	 */
	public void setDialogMessage(int dialogMessage)
	{
		this.dialogMessage = context.getString(dialogMessage);
	}

	public void setOnTaskCompletedListener(
			OnTaskCompletedListener<Result> taskCompletedListener)
	{
		if(listeners == null) {
			listeners = new ArrayList<OnTaskCompletedListener<Result>>();
		}

		this.listeners.add(taskCompletedListener);
	}

	protected void emitTaskCompleted(Result result)
	{
		for(OnTaskCompletedListener<Result> listener : listeners) {
			listener.onTaskCompleted(result);
		}
	}

	public interface OnTaskCompletedListener<Result>
	{
		void onTaskCompleted(Result result);
	}
}
