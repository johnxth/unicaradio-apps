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
package it.unicaradio.android.activities;

import it.unicaradio.android.R;
import it.unicaradio.android.exceptions.WrongCaptchaLengthException;
import it.unicaradio.android.exceptions.WrongCaptchaOperationException;
import it.unicaradio.android.gui.Tabs;
import it.unicaradio.android.listeners.GenericAsyncTaskFailedListener;
import it.unicaradio.android.models.Response;
import it.unicaradio.android.tasks.BlockingAsyncTask;
import it.unicaradio.android.tasks.BlockingAsyncTask.OnTaskCompletedListener;
import it.unicaradio.android.tasks.DownloadCaptchaAsyncTask;
import it.unicaradio.android.tasks.GetEmailAddressAsyncTask;
import it.unicaradio.android.tasks.SendSongRequestAsyncTask;
import it.unicaradio.android.utils.CaptchaParser;
import it.unicaradio.android.utils.Constants;
import it.unicaradio.android.utils.EncodingUtils;
import it.unicaradio.android.utils.StringUtils;

import java.text.MessageFormat;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @author Paolo Cortis
 */
public class SongRequestActivity extends TabbedActivity
{
	private static final String WEB_SERVICE = "http://www.unicaradio.it/regia/test/mail.php";

	private SharedPreferences preferences;

	private String captcha;

	private String email;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState, R.layout.songs);

		preferences = getPreferences(Context.MODE_PRIVATE);

		setCaptchaField();
		setEmailField();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.songs_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle item selection
		switch(item.getItemId()) {
			case R.id.songsClearForm:
				clearForm();
				return true;
			case R.id.songsChangeCaptcha:
				setCaptchaField();
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void setCaptchaField()
	{
		captcha = StringUtils.EMPTY;
		updateCaptchaOnView();

		DownloadCaptchaAsyncTask downloadCaptchaAsyncTask = new DownloadCaptchaAsyncTask(
				this);
		downloadCaptchaAsyncTask
				.setOnTaskCompletedListener(new OnDownloadCaptchaAsyncTaskCompletedListener());

		GenericAsyncTaskFailedListener<String> taskFailedListener = new GenericAsyncTaskFailedListener<String>(
				this);
		downloadCaptchaAsyncTask.setOnTaskFailedListener(taskFailedListener);

		downloadCaptchaAsyncTask.execute();
	}

	private void setEmailField()
	{
		email = StringUtils.EMPTY;
		updateEmailOnView();

		GetEmailAddressAsyncTask getEmailAddressAsyncTask = new GetEmailAddressAsyncTask(
				this);
		getEmailAddressAsyncTask
				.setOnTaskCompletedListener(new OnGetEmailAddressAsyncTaskCompletedListener());
		getEmailAddressAsyncTask.execute();
	}

	private void clearForm()
	{
		TextView emailView = (TextView) findViewById(R.id.songsEmail);
		TextView authorView = (TextView) findViewById(R.id.songsAuthor);
		TextView titleView = (TextView) findViewById(R.id.songsTitle);
		TextView resultView = (TextView) findViewById(R.id.songsCaptcha);

		emailView.setHint("eMail");
		setCaptchaField();
		resultView.setText("");
		authorView.setText("");
		titleView.setText("");
	}

	@Override
	protected void setupTab()
	{
	}

	@Override
	protected void setupListeners()
	{
		View songButton = findViewById(R.id.songButton);
		songButton.setOnClickListener(new SendSongRequestClickListener());
	}

	@Override
	public int getTab()
	{
		return Tabs.SONG;
	}

	private void updateCaptchaOnView()
	{
		final EditText captchaView = (EditText) findViewById(R.id.songsCaptcha);

		String humanReadableCaptcha;
		try {
			humanReadableCaptcha = CaptchaParser
					.generateHumanReadableCaptcha(captcha.toString());
			captchaView.setHint(humanReadableCaptcha);
		} catch(WrongCaptchaLengthException e) {
			new AlertDialog.Builder(SongRequestActivity.this)
					.setTitle("Errore")
					.setMessage(
							"Errore durante la generazione del CAPTCHA. Riprova più tardi.")
					.setCancelable(false).setPositiveButton("OK", null).show();
		} catch(WrongCaptchaOperationException e) {
			new AlertDialog.Builder(SongRequestActivity.this)
					.setTitle("Errore")
					.setMessage(
							"Errore durante la generazione del CAPTCHA. Riprova più tardi.")
					.setCancelable(false).setPositiveButton("OK", null).show();
		}

		captchaView.setText("");
	}

	private void updateEmailOnView()
	{
		final EditText emailView = (EditText) findViewById(R.id.songsEmail);
		emailView.setText(email.toString());
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
			String captchaString = result.getResult();

			captcha = captchaString;
			updateCaptchaOnView();
		}
	}

	private final class SendSongRequestClickListener implements OnClickListener
	{
		private final String TAG = SendSongRequestClickListener.class.getName();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onClick(View v)
		{
			String url = calculateUrl();
			if(url == null) {
				return;
			}

			SendSongRequestAsyncTask sendSongRequestAsyncTask = new SendSongRequestAsyncTask(
					SongRequestActivity.this, url);
			sendSongRequestAsyncTask
					.setOnTaskCompletedListener(new OnSendSongRequestAsyncTaskCompletedListener());

			GenericAsyncTaskFailedListener<String> taskFailedListener = new GenericAsyncTaskFailedListener<String>(
					SongRequestActivity.this);
			sendSongRequestAsyncTask
					.setOnTaskFailedListener(taskFailedListener);
			sendSongRequestAsyncTask.execute();
		}

		/**
		 * @return
		 */
		private String calculateUrl()
		{
			TextView emailView = (TextView) findViewById(R.id.songsEmail);
			TextView authorView = (TextView) findViewById(R.id.songsAuthor);
			TextView titleView = (TextView) findViewById(R.id.songsTitle);
			TextView resultView = (TextView) findViewById(R.id.songsCaptcha);

			final String email = emailView.getText().toString().trim();
			final String author = authorView.getText().toString().trim();
			final String title = titleView.getText().toString().trim();
			final String result = resultView.getText().toString().trim();

			if(StringUtils.isEmpty(email) || StringUtils.isEmpty(author)
					|| StringUtils.isEmpty(title)
					|| StringUtils.isEmpty(result)) {
				new AlertDialog.Builder(SongRequestActivity.this)
						.setTitle("Errore!")
						.setMessage("Attenzione! hai dimenticato qualcosa :)")
						.setCancelable(false).setPositiveButton("OK", null)
						.show();
				return null;
			}

			SharedPreferences.Editor editor = preferences.edit();
			editor.putString(Constants.PREFERENCES_USER_EMAIL_KEY, email);
			editor.commit();

			String url = MessageFormat.format(
					"{0}?r={1}&op={2}&art={3}&tit={4}&mail={5}", WEB_SERVICE,
					EncodingUtils.encodeURIComponent(result),
					EncodingUtils.encodeURIComponent(captcha.toString()),
					EncodingUtils.encodeURIComponent(author),
					EncodingUtils.encodeURIComponent(title),
					EncodingUtils.encodeURIComponent(email));

			Log.d(TAG, EncodingUtils.encodeURIComponent(url));
			return url;
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
			new AlertDialog.Builder(SongRequestActivity.this)
					.setTitle("E-mail inviata!").setCancelable(false)
					.setPositiveButton("OK", null).show();
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

				new AlertDialog.Builder(SongRequestActivity.this)
						.setTitle(
								"Quale indirizzo vuoi usare per inviare l'email?")
						.setCancelable(false)
						.setItems(tmpEmails,
								new DialogInterface.OnClickListener() {
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
}
