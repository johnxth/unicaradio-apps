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
package it.unicaradio.android.gcm;

import java.io.IOException;
import java.util.Map;

import android.content.Context;

import com.google.android.gcm.GCMRegistrar;

/**
 * @author Paolo Cortis
 */
public class GcmServerRegister extends GcmServerRpcCall
{
	public GcmServerRegister(Context context)
	{
		super(context);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws GcmException
	 */
	@Override
	public void internalExecute(String request, Map<String, String> params)
			throws GcmException
	{
		try {
			post(request);
			// TODO: controllare risposta dal server.
			GCMRegistrar.setRegisteredOnServer(context, true);
		} catch(IOException e) {
			throw new GcmException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getMethod()
	{
		return "register";
	}
}
