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
import it.unicaradio.android.gui.Tabs;
import it.unicaradio.android.utils.CaptchaParser;
import it.unicaradio.android.utils.EncodingUtils;
import it.unicaradio.android.utils.NetworkUtils;

import java.io.IOException;
import java.text.MessageFormat;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
 * 
 */
public class SongRequestActivity extends TabbedActivity
{
	private static final String NO_MAIL = "no_mail";

	private static final String USER_EMAIL = "user_email";

	private static final String WEB_SERVICE = "http://www.unicaradio.it/regia/test/mail.php";

	private final Handler mHandler = new Handler();

	private final StringBuilder captcha = new StringBuilder();

	private final Runnable mUpdateCaptcha = new Runnable() {
		@Override
		public void run()
		{
			final EditText captchaView = (EditText) findViewById(R.id.songsCaptcha);
			captchaView.setHint(CaptchaParser.parse(captcha.toString()));
			captchaView.setText("");
		}
	};

	private final StringBuilder email = new StringBuilder();

	private final Runnable mUpdateEmail = new Runnable() {
		@Override
		public void run()
		{
			final EditText emailView = (EditText) findViewById(R.id.songsEmail);
			emailView.setText(email.toString());
		}
	};

	private SharedPreferences preferences;

	private CharSequence[] emails;

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
		AsyncTask<String, Void, Integer> task = new AsyncTask<String, Void, Integer>() {
			private ProgressDialog dialog;

			@Override
			protected Integer doInBackground(String... arg0)
			{
				captcha.delete(0, captcha.length());
				try {
					captcha.append(new String(NetworkUtils
							.downloadFromUrl(WEB_SERVICE)));
				} catch(Exception e) {
					return 1;
				}
				mHandler.post(mUpdateCaptcha);

				return 0;
			}

			@Override
			protected void onPreExecute()
			{
				super.onPreExecute();
				dialog = ProgressDialog.show(SongRequestActivity.this,
						"CAPTCHA...", "Generazione CAPTCHA in corso...", true);
			}

			@Override
			protected void onPostExecute(Integer result)
			{
				super.onPostExecute(result);
				dialog.dismiss();
				if(result == 1) {
					new AlertDialog.Builder(SongRequestActivity.this)
							.setTitle("Errore")
							.setMessage(
									"Verifica di essere connesso ad Internet.")
							.setCancelable(false).setPositiveButton("OK", null)
							.show();
				}
			}
		};
		task.execute("");
	}

	private void setEmailField()
	{
		AsyncTask<String, Void, Account[]> task = new AsyncTask<String, Void, Account[]>() {
			private ProgressDialog dialog;

			@Override
			protected Account[] doInBackground(String... arg0)
			{
				String mailFromPreferences = preferences.getString(USER_EMAIL,
						NO_MAIL);
				if(mailFromPreferences.equals(NO_MAIL)) {
					// l'utente non ha indicato alcuna e-mail in precedenza
					Account[] accounts = AccountManager.get(
							SongRequestActivity.this).getAccountsByType(
							"com.google");

					email.delete(0, email.length());
					if(accounts.length == 1) {
						// c'è un account google
						email.append(accounts[0].name);
					} else if(accounts.length > 1) {
						// ce n'è più di uno
						return accounts;
					}
				} else {
					email.append(mailFromPreferences);
				}

				return null;
			}

			@Override
			protected void onPreExecute()
			{
				dialog = ProgressDialog.show(SongRequestActivity.this,
						"Email...", "Recupero indirizzo e-mail in corso...",
						true);
			}

			@Override
			protected void onPostExecute(Account[] accounts)
			{
				if(accounts != null) {
					// There are more than one google account
					emails = new CharSequence[accounts.length];
					for(int i = 0; i < accounts.length; i++) {
						emails[i] = accounts[i].name;
					}
					new AlertDialog.Builder(SongRequestActivity.this)
							.setTitle(
									"Quale indirizzo vuoi usare per inviare l'email?")
							.setCancelable(false)
							.setItems(emails,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog, int item)
										{
											email.append(emails[item]);
											mHandler.post(mUpdateEmail);
										}
									}).show();
				} else {
					mHandler.post(mUpdateEmail);
				}

				dialog.dismiss();
			}
		};
		task.execute("");
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
		songButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				TextView emailView = (TextView) findViewById(R.id.songsEmail);
				TextView authorView = (TextView) findViewById(R.id.songsAuthor);
				TextView titleView = (TextView) findViewById(R.id.songsTitle);
				TextView resultView = (TextView) findViewById(R.id.songsCaptcha);

				final String email = emailView.getText().toString().trim();
				final String author = authorView.getText().toString().trim();
				final String title = titleView.getText().toString().trim();
				final String result = resultView.getText().toString().trim();

				if(email.equals("") || author.equals("") || title.equals("")
						|| result.equals("")) {
					new AlertDialog.Builder(SongRequestActivity.this)
							.setTitle("Errore!")
							.setMessage(
									"Attenzione! hai dimenticato qualcosa :)")
							.setCancelable(false).setPositiveButton("OK", null)
							.show();
					return;
				}

				SharedPreferences.Editor editor = preferences.edit();
				editor.putString(USER_EMAIL, email);
				editor.commit();

				AsyncTask<String, Void, Integer> task = new AsyncTask<String, Void, Integer>() {
					private ProgressDialog dialog;

					@Override
					protected Integer doInBackground(String... arg0)
					{
						try {
							String url = MessageFormat.format(
									"?r={0}&op={1}&art={2}&tit={3}&mail={4}",
									EncodingUtils.encodeURIComponent(result),
									EncodingUtils.encodeURIComponent(captcha
											.toString()), EncodingUtils
											.encodeURIComponent(author),
									EncodingUtils.encodeURIComponent(title),
									EncodingUtils.encodeURIComponent(email));
							String sendResult = new String(NetworkUtils
									.downloadFromUrl(WEB_SERVICE + url));
							Log.d(SongRequestActivity.class.getCanonicalName(),
									sendResult);
							Log.d(SongRequestActivity.class.getCanonicalName(),
									EncodingUtils.encodeURIComponent(url));
							if(sendResult.equals("OK")) {
								return 0;
							} else {
								return 1;
							}
						} catch(IOException e) {
							return 1;
						}
					}

					@Override
					protected void onPreExecute()
					{
						super.onPreExecute();
						dialog = ProgressDialog.show(SongRequestActivity.this,
								"Email...", "Invio e-mail in corso...", true);
					}

					@Override
					protected void onPostExecute(Integer result)
					{
						super.onPostExecute(result);
						dialog.dismiss();
						if(result == 1) {
							new AlertDialog.Builder(SongRequestActivity.this)
									.setTitle("Errore!")
									.setMessage(
											"È avvenuto un errore durante l'invio del messaggio")
									.setCancelable(false)
									.setPositiveButton("OK", null).show();
						} else if(result == 0) {
							new AlertDialog.Builder(SongRequestActivity.this)
									.setTitle("E-mail inviata!")
									.setCancelable(false)
									.setPositiveButton("OK", null).show();
							clearForm();
						}
					}
				};
				task.execute("");
			}
		});
	}

	@Override
	public int getTab()
	{
		return Tabs.SONG;
	}
}
