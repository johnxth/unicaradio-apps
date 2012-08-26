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
package it.unicaradio.android.fragments;

import it.unicaradio.android.R;
import it.unicaradio.android.exceptions.WrongCaptchaLengthException;
import it.unicaradio.android.exceptions.WrongCaptchaOperationException;
import it.unicaradio.android.listeners.GenericAsyncTaskFailedListener;
import it.unicaradio.android.models.Response;
import it.unicaradio.android.models.SongRequest;
import it.unicaradio.android.tasks.BlockingAsyncTask;
import it.unicaradio.android.tasks.BlockingAsyncTask.OnTaskCompletedListener;
import it.unicaradio.android.tasks.DownloadCaptchaAsyncTask;
import it.unicaradio.android.tasks.GetEmailAddressAsyncTask;
import it.unicaradio.android.tasks.SendSongRequestAsyncTask;
import it.unicaradio.android.utils.CaptchaParser;
import it.unicaradio.android.utils.Constants;
import it.unicaradio.android.utils.NetworkUtils;
import it.unicaradio.android.utils.StringUtils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

/**
 * @author Paolo Cortis
 */
public class SongRequestFragment extends UnicaradioFragment
{
	private SharedPreferences preferences;

	private String captcha;

	private String email;

	private TextView emailView;

	private TextView authorView;

	private TextView titleView;

	private TextView captchaView;

	private Button songButton;

	private boolean isEmailSet;

	private Timer updateCaptchaTimer;

	private UpdateCaptchaTimedTask updateCaptchaTimedTask;

	public SongRequestFragment()
	{
		isEmailSet = false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.songs, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		preferences = getActivity().getPreferences(Context.MODE_PRIVATE);

		setupTab();

		songButton.setOnClickListener(new SendSongRequestClickListener());

		if(savedInstanceState != null) {
			email = savedInstanceState.getString("mail");
			updateEmailOnView();

			authorView.setText(savedInstanceState.getString("author"));
			titleView.setText(savedInstanceState.getString("title"));
		} else {
			if(!isEmailSet) {
				setEmailField();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onResume()
	{
		super.onResume();

		initTimer();
		setCaptchaField();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPause()
	{
		super.onPause();

		disableUpdateCaptchaTimer();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);

		inflater.inflate(R.menu.songs_menu, menu);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()) {
			case R.id.songsChangeCaptcha:
				disableUpdateCaptchaTimer();
				setCaptchaField();
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		outState.putString("mail", emailView.getText().toString());
		outState.putString("author", authorView.getText().toString());
		outState.putString("title", titleView.getText().toString());

		super.onSaveInstanceState(outState);
	}

	private void setCaptchaField()
	{
		captcha = StringUtils.EMPTY;
		updateCaptchaOnView();

		if(NetworkUtils.isConnected(getActivity())) {
			DownloadCaptchaAsyncTask downloadCaptchaAsyncTask = new DownloadCaptchaAsyncTask(
					getActivity());
			downloadCaptchaAsyncTask
					.setOnTaskCompletedListener(new OnDownloadCaptchaAsyncTaskCompletedListener());

			GenericAsyncTaskFailedListener<String> taskFailedListener = new GenericAsyncTaskFailedListener<String>(
					getActivity());
			downloadCaptchaAsyncTask
					.setOnTaskFailedListener(taskFailedListener);

			downloadCaptchaAsyncTask.execute();
		}
	}

	private void setEmailField()
	{
		email = StringUtils.EMPTY;
		updateEmailOnView();

		GetEmailAddressAsyncTask getEmailAddressAsyncTask = new GetEmailAddressAsyncTask(
				getActivity());
		getEmailAddressAsyncTask
				.setOnTaskCompletedListener(new OnGetEmailAddressAsyncTaskCompletedListener());
		getEmailAddressAsyncTask.execute();
	}

	private void clearForm()
	{
		emailView.setHint("eMail");
		setCaptchaField();
		captchaView.setText("");
		authorView.setText("");
		titleView.setText("");
	}

	private void setupTab()
	{
		emailView = (TextView) getActivity().findViewById(R.id.songsEmail);
		authorView = (TextView) getActivity().findViewById(R.id.songsAuthor);
		titleView = (TextView) getActivity().findViewById(R.id.songsTitle);
		captchaView = (TextView) getActivity().findViewById(R.id.songsCaptcha);
		songButton = (Button) getActivity().findViewById(R.id.songButton);
	}

	private void updateCaptchaOnView()
	{
		captchaView.setText("");

		if(StringUtils.isEmpty(captcha)) {
			return;
		}

		String humanReadableCaptcha;
		try {
			humanReadableCaptcha = CaptchaParser
					.generateHumanReadableCaptcha(captcha.toString());
			captchaView.setHint(humanReadableCaptcha);
		} catch(WrongCaptchaLengthException e) {
			new AlertDialog.Builder(getActivity())
					.setTitle("Errore")
					.setMessage(
							"Errore durante la generazione del CAPTCHA. Riprova più tardi.")
					.setCancelable(false).setPositiveButton("OK", null).show();
		} catch(WrongCaptchaOperationException e) {
			new AlertDialog.Builder(getActivity())
					.setTitle("Errore")
					.setMessage(
							"Errore durante la generazione del CAPTCHA. Riprova più tardi.")
					.setCancelable(false).setPositiveButton("OK", null).show();
		}
	}

	private void updateEmailOnView()
	{
		isEmailSet = true;
		emailView.setText(email.toString());
	}

	private boolean areFieldsFilled()
	{
		final String email = emailView.getText().toString().trim();
		final String author = authorView.getText().toString().trim();
		final String title = titleView.getText().toString().trim();
		final String result = captchaView.getText().toString().trim();

		if(StringUtils.isEmpty(email) || StringUtils.isEmpty(author)
				|| StringUtils.isEmpty(title) || StringUtils.isEmpty(result)) {
			return false;
		}

		return true;
	}

	private SongRequest elaborateSongRequest()
	{
		SongRequest songRequest = new SongRequest();

		String author = authorView.getText().toString().trim();
		songRequest.setAuthor(author);

		songRequest.setCaptcha(captcha.toString());

		String email = emailView.getText().toString().trim();
		songRequest.setEmail(email);

		String result = captchaView.getText().toString().trim();
		songRequest.setResult(result);

		String title = titleView.getText().toString().trim();
		songRequest.setTitle(title);

		return songRequest;
	}

	private void warnUserForEmptyFields()
	{
		new AlertDialog.Builder(getActivity()).setTitle("Errore!")
				.setMessage("Attenzione! hai dimenticato qualcosa :)")
				.setCancelable(false).setPositiveButton("OK", null).show();
	}

	private void saveEmailInPreferences()
	{
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(Constants.PREFERENCES_USER_EMAIL_KEY, email);
		editor.commit();
	}

	private void sendEmail()
	{
		SendSongRequestAsyncTask sendSongRequestAsyncTask = new SendSongRequestAsyncTask(
				getActivity(), elaborateSongRequest());
		sendSongRequestAsyncTask
				.setOnTaskCompletedListener(new OnSendSongRequestAsyncTaskCompletedListener());

		GenericAsyncTaskFailedListener<String> taskFailedListener = new GenericAsyncTaskFailedListener<String>(
				getActivity());
		sendSongRequestAsyncTask.setOnTaskFailedListener(taskFailedListener);
		sendSongRequestAsyncTask.execute();
	}

	private void initTimer()
	{
		updateCaptchaTimer = new Timer();
		updateCaptchaTimedTask = new UpdateCaptchaTimedTask();
	}

	private void rescheduleTimer()
	{
		initTimer();

		long delay = (long) (1 * 60 * 1000);
		updateCaptchaTimer.schedule(updateCaptchaTimedTask, delay);
	}

	private void disableUpdateCaptchaTimer()
	{
		try {
			updateCaptchaTimer.cancel();
			updateCaptchaTimer = null;

			updateCaptchaTimedTask.cancel();
			updateCaptchaTimedTask = null;
		} catch(Exception e) {
			// do nothing
		}
	}

	private final class OnDownloadCaptchaAsyncTaskCompletedListener implements
			BlockingAsyncTask.OnTaskCompletedListener<Response<String>>
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onTaskCompleted(Response<String> result)
		{
			rescheduleTimer();

			String captchaString = result.getResult();

			captcha = captchaString;
			updateCaptchaOnView();
		}
	}

	private final class SendSongRequestClickListener implements OnClickListener
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onClick(View v)
		{
			if(!areFieldsFilled()) {
				warnUserForEmptyFields();
				return;
			}

			disableUpdateCaptchaTimer();
			saveEmailInPreferences();
			sendEmail();
		}
	}

	private final class OnSendSongRequestAsyncTaskCompletedListener implements
			OnTaskCompletedListener<Response<String>>
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onTaskCompleted(Response<String> result)
		{
			new AlertDialog.Builder(getActivity()).setTitle("E-mail inviata!")
					.setCancelable(false).setPositiveButton("OK", null).show();
			clearForm();
		}
	}

	private final class OnGetEmailAddressAsyncTaskCompletedListener implements
			OnTaskCompletedListener<Response<List<String>>>
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onTaskCompleted(Response<List<String>> response)
		{
			if(response == null) {
				return;
			}

			List<String> result = response.getResult();
			if((result == null) || (result.size() == 0)) {
				return;
			}

			if(result.size() == 1) {
				email = result.get(0);
				updateEmailOnView();
			} else {
				final String[] tmpEmails = result.toArray(new String[result
						.size()]);

				new AlertDialog.Builder(getActivity())
						.setTitle(
								"Quale indirizzo vuoi usare per inviare l'email?")
						.setCancelable(false)
						.setItems(tmpEmails,
								new DialogInterface.OnClickListener()
								{
									@Override
									public void onClick(DialogInterface dialog,
											int item)
									{
										email = tmpEmails[item];
										updateEmailOnView();
									}
								}).show();
			}
		}
	}

	private final class UpdateCaptchaTimedTask extends TimerTask
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run()
		{
			getActivity().runOnUiThread(new UpdateCaptchaOnView());
		}
	}

	private final class UpdateCaptchaOnView implements Runnable
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run()
		{
			setCaptchaField();
		}
	}
}
