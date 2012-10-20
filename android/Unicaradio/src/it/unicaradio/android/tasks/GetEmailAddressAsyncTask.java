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

import it.unicaradio.android.models.Response;
import it.unicaradio.android.utils.Constants;
import it.unicaradio.android.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * @author Paolo Cortis
 */
public class GetEmailAddressAsyncTask extends
		BlockingAsyncTaskWithResponse<List<String>>
{
	/**
	 * @param context
	 */
	public GetEmailAddressAsyncTask(Context context)
	{
		super(context);

		hideDialog();
		setDialogTitle("Email");
		setDialogMessage("Recupero indirizzo e-mail in corso");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Response<List<String>> doInBackground(Void... params)
	{
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		String mailFromPreferences = preferences.getString(
				Constants.PREFERENCES_USER_EMAIL_KEY,
				Constants.PREFERENCES_NO_MAIL_VALUE);

		List<String> emails = new ArrayList<String>();
		if(StringUtils.notEquals(mailFromPreferences,
				Constants.PREFERENCES_NO_MAIL_VALUE)) {
			emails.add(mailFromPreferences);
			return new Response<List<String>>(emails);
		}

		// l'utente non ha indicato alcuna e-mail in precedenza
		Account[] accounts = AccountManager.get(context).getAccountsByType(
				Constants.GOOGLE_ACCOUNT_TYPE);
		for(Account account : accounts) {
			emails.add(account.name);
		}

		return new Response<List<String>>(emails);
	}
}
