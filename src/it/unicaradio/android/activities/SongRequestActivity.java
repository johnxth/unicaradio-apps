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
import it.unicaradio.android.utils.Utils;

import java.io.IOException;
import java.text.MessageFormat;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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

	private static final String WEB_SERVICE = "http://paolo86.slack-counter.org/mail.php";

	private final Handler mHandler = new Handler();

	private final StringBuilder captcha = new StringBuilder();

	private final Runnable mUpdateCaptcha = new Runnable() {
		@Override
		public void run()
		{
			final EditText captchaView = (EditText) findViewById(R.id.songsCaptcha);
			captchaView.setHint(CaptchaParser.parse(captcha.toString()));
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

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.d(SongRequestActivity.class.getName(), "Called SongRequestActivity");
		super.onCreate(savedInstanceState, R.layout.songs);

		preferences = getPreferences(Context.MODE_PRIVATE);

		setCaptchaField();

		setEmailField();
	}

	private void setCaptchaField()
	{
		Thread t = new Thread(new Runnable() {
			@Override
			public void run()
			{
				try {
					captcha.delete(0, captcha.length());
					captcha.append(new String(Utils
							.downloadFromUrl(WEB_SERVICE)));
					mHandler.post(mUpdateCaptcha);
				} catch(IOException e) {
					e.printStackTrace();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		t.start();
	}

	private void setEmailField()
	{
		Thread t = new Thread(new Runnable() {
			@Override
			public void run()
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
						final CharSequence[] emails = new CharSequence[accounts.length];
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
													DialogInterface dialog,
													int item)
											{
												email.append(emails[item]);
											}
										}).show();
					}
				} else {
					email.append(mailFromPreferences);
				}
				mHandler.post(mUpdateEmail);
			}
		});
		t.start();
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

				String email = emailView.getText().toString().trim();
				String author = authorView.getText().toString().trim();
				String title = titleView.getText().toString().trim();
				String result = resultView.getText().toString().trim();

				if(email.equals("") || author.equals("") || title.equals("")
						|| result.equals("")) {
					// TODO: Error!
					throw new RuntimeException("You forgot some fields");
				}

				SharedPreferences.Editor editor = preferences.edit();
				editor.putString(USER_EMAIL, email);
				editor.commit();

				try {
					Utils.downloadFromUrl(MessageFormat.format(
							"{0}?r={1}&op={2}&art={3}&tit={4}&mail={5}",
							WEB_SERVICE, result, captcha.toString(), author,
							title, email));
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public int getTab()
	{
		return Tabs.SONG;
	}
}
