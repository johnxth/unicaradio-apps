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
package it.unicaradio.android.listeners;

import it.unicaradio.android.enums.Error;
import it.unicaradio.android.models.Response;
import it.unicaradio.android.tasks.BlockingAsyncTask.OnTaskFailedListener;
import android.app.AlertDialog;
import android.content.Context;

/**
 * @author Paolo Cortis
 * 
 */
public class GenericAsyncTaskFailedListener<Result> implements
		OnTaskFailedListener<Response<Result>>
{
	private final Context context;

	public GenericAsyncTaskFailedListener(Context context)
	{
		this.context = context;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onTaskFailed(Response<Result> result)
	{
		if(result.getErrorCode() == Error.DOWNLOAD_ERROR) {
			new AlertDialog.Builder(context).setTitle("Errore")
					.setMessage("Verifica di essere connesso ad Internet.")
					.setCancelable(false).setPositiveButton("OK", null).show();
		} else {
			new AlertDialog.Builder(context)
					.setTitle("Errore!")
					.setMessage(
							"È avvenuto un errore imprevisto. Riprova più tardi.")
					.setCancelable(false).setPositiveButton("OK", null).show();
		}
	}
}
