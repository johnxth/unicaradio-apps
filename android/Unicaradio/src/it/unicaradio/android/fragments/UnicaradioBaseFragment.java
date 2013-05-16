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

import it.unicaradio.android.activities.MainActivity;
import android.app.AlertDialog;
import android.content.Context;

import com.actionbarsherlock.app.SherlockFragment;

/**
 * @author Paolo Cortis
 * 
 */
public abstract class UnicaradioBaseFragment extends SherlockFragment
{
	protected void showAlertDialog(String title, String message)
	{
		AlertDialog.Builder adb = new AlertDialog.Builder(getMainActivityContext());
		adb.setTitle(title);
		adb.setMessage(message);
		adb.setPositiveButton("Ok", null);
		adb.show();
	}

	protected MainActivity getMainActivity()
	{
		if(getActivity() == null) {
			return MainActivity.activity;
		}

		return (MainActivity) getActivity();
	}

	protected Context getMainActivityContext()
	{
		if(getActivity() == null) {
			return MainActivity.activityContext;
		}

		return getActivity();
	}
}
