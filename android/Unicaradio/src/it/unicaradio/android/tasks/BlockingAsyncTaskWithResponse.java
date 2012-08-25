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
import android.content.Context;

/**
 * @author Paolo Cortis
 */
public abstract class BlockingAsyncTaskWithResponse<T> extends
		BlockingAsyncTask<Void, Void, Response<T>>
{
	/**
	 * @param context
	 */
	public BlockingAsyncTaskWithResponse(Context context)
	{
		super(context);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onPostExecute(Response<T> result)
	{
		super.onPostExecute(result);

		if(result.containsError()) {
			emitTaskFailed(result);
			return;
		}

		emitTaskCompleted(result);
	}
}
