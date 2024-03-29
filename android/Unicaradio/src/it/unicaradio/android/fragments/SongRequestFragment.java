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

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import java.util.List;
import java.util.regex.Pattern;

import it.unicaradio.android.R;
import it.unicaradio.android.listeners.SongRequestTaskFailedListener;
import it.unicaradio.android.models.Response;
import it.unicaradio.android.models.SongRequest;
import it.unicaradio.android.tasks.BlockingAsyncTask.OnTaskCompletedListener;
import it.unicaradio.android.tasks.GetEmailAddressAsyncTask;
import it.unicaradio.android.tasks.SendSongRequestAsyncTask;
import it.unicaradio.android.utils.Constants;
import it.unicaradio.android.utils.StringUtils;

/**
 * @author Paolo Cortis
 */
public class SongRequestFragment extends UnicaradioFragment
{
	private SharedPreferences preferences;

	private String email;

	private AutoCompleteTextView emailView;

	private TextView authorView;

	private TextView titleView;

	// FIXME: Remove when remove support for android api level 7
	public static final Pattern EMAIL_ADDRESS = Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
																"\\@" +
																"[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
																"(" +
																"\\." +
																"[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
																")+");

	/**
	 * {@inheritDoc}
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
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

		preferences = getMainActivity().getPreferences(Context.MODE_PRIVATE);

		setupTab();

		if(savedInstanceState != null) {
			email = StringUtils.defaultString(savedInstanceState.getString("mail"));
			updateEmailOnView();

			authorView.setText(savedInstanceState.getString("author"));
			titleView.setText(savedInstanceState.getString("title"));
		}

		prepareEmailField();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onResume()
	{
		super.onResume();

		setupTab();
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
	public void onSaveInstanceState(Bundle outState)
	{
		if(emailView != null) {
			outState.putString("mail", emailView.getText().toString());
		}

		if(authorView != null) {
			outState.putString("author", authorView.getText().toString());
		}

		if(titleView != null) {
			outState.putString("title", titleView.getText().toString());
		}

		super.onSaveInstanceState(outState);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);

		inflater.inflate(R.menu.song_menu, menu);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()) {
			case R.id.songSend:
				elaborateRequest();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void prepareEmailField()
	{
		email = StringUtils.EMPTY;
		updateEmailOnView();

		GetEmailAddressAsyncTask getEmailAddressAsyncTask = new GetEmailAddressAsyncTask(getMainActivityContext());
		getEmailAddressAsyncTask.setOnTaskCompletedListener(new OnGetEmailAddressAsyncTaskCompletedListener());
		getEmailAddressAsyncTask.execute();
	}

	private void clearForm()
	{
		emailView.setHint("eMail");
		authorView.setText("");
		titleView.setText("");
	}

	private void setupTab()
	{
		emailView = (AutoCompleteTextView) getMainActivity().findViewById(R.id.songsEmail);
		authorView = (TextView) getMainActivity().findViewById(R.id.songsAuthor);
		titleView = (TextView) getMainActivity().findViewById(R.id.songsTitle);
	}

	private void updateEmailOnView()
	{
		emailView.setText(email);
		saveEmailInPreferences();
	}

	private boolean areThereInvalidFields()
	{
		String email = emailView.getText().toString().trim();
		String author = authorView.getText().toString().trim();
		String title = titleView.getText().toString().trim();

		return StringUtils.isEmpty(email) || StringUtils.isEmpty(author) || StringUtils.isEmpty(title);
	}

	private SongRequest elaborateSongRequest()
	{
		SongRequest songRequest = new SongRequest();

		String author = authorView.getText().toString().trim();
		songRequest.setAuthor(author);

		String email = emailView.getText().toString().trim();
		songRequest.setEmail(email);

		String title = titleView.getText().toString().trim();
		songRequest.setTitle(title);

		return songRequest;
	}

	private void warnUserForEmptyFields()
	{
		new AlertDialog.Builder(getMainActivityContext()).setTitle("Errore!")
				.setMessage("Attenzione! Hai dimenticato qualcosa :)").setCancelable(false)
				.setPositiveButton("OK", null).show();
	}

	private void saveEmailInPreferences()
	{
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(Constants.PREFERENCES_USER_EMAIL_KEY, email);
		editor.commit();
	}

	private void sendEmail()
	{
		SendSongRequestAsyncTask sendSongRequestAsyncTask =
				new SendSongRequestAsyncTask(getMainActivityContext(), elaborateSongRequest());
		sendSongRequestAsyncTask.setOnTaskCompletedListener(new OnSendSongRequestAsyncTaskCompletedListener());

		SongRequestTaskFailedListener<String> taskFailedListener =
				new SongRequestTaskFailedListener<String>(getMainActivityContext());
		sendSongRequestAsyncTask.setOnTaskFailedListener(taskFailedListener);
		sendSongRequestAsyncTask.execute();
	}

	/**
	 * @return
	 */
	private boolean isEmailValid()
	{
		String email = emailView.getText().toString().trim();
		return EMAIL_ADDRESS.matcher(email).matches();
	}

	private void warnUserForNotValidEmail()
	{
		new AlertDialog.Builder(getMainActivityContext()).setTitle("Errore!")
				.setMessage("Attenzione! L'email indicata non è corretta").setCancelable(false)
				.setPositiveButton("OK", null).show();
	}

	private void elaborateRequest()
	{
		if(areThereInvalidFields()) {
			warnUserForEmptyFields();
			return;
		}

		if(!isEmailValid()) {
			warnUserForNotValidEmail();
			return;
		}

		saveEmailInPreferences();
		sendEmail();
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
			new AlertDialog.Builder(getMainActivityContext()).setTitle("Richiesta inviata")
					.setMessage("La tua richiesta è stata presa in carico.").setCancelable(false)
					.setPositiveButton("OK", null).show();
			clearForm();
		}
	}

	private final class OnGetEmailAddressAsyncTaskCompletedListener
			implements OnTaskCompletedListener<Response<List<String>>>
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
			if(result == null || result.isEmpty()) {
				return;
			}

			if(result.size() == 1) {
				email = result.get(0);
				updateEmailOnView();
			}

			prepareAutocompleteSuggests(result);
		}

		private void prepareAutocompleteSuggests(List<String> result)
		{
			String[] emailsAsStringArray = result.toArray(new String[result.size()]);
			ArrayAdapter<String> arrayAdapter =
					new ArrayAdapter<String>(getMainActivityContext(), android.R.layout.simple_dropdown_item_1line,
											 emailsAsStringArray);
			emailView.setAdapter(arrayAdapter);
		}
	}
}
