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

import android.app.AlertDialog;
import android.content.Context;
import it.unicaradio.android.enums.Error;
import it.unicaradio.android.models.Response;

/**
 * @author Paolo Cortis
 */
public class SongRequestTaskFailedListener<Result> extends
		GenericAsyncTaskFailedListener<Result>
{
	/**
	 * @param context
	 */
	public SongRequestTaskFailedListener(Context context)
	{
		super(context);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onTaskFailed(Response<Result> result)
	{
		if(result.getErrorCode() == Error.OPERATION_FORBIDDEN) {
			new AlertDialog.Builder(getContext()).setTitle("Limite massimo raggiunto")
					.setMessage("Per favore attendi prima di effettuare altre richieste.")
					.setCancelable(false).setPositiveButton("OK", null).show();
		} else {
			super.onTaskFailed(result);
		}
	}
}
