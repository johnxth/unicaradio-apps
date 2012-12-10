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
package it.unicaradio.android.receivers;

import static com.google.android.gcm.GCMConstants.DEFAULT_INTENT_SERVICE_CLASS_NAME;
import android.content.Context;

import com.google.android.gcm.GCMBroadcastReceiver;

/**
 * @author Paolo Cortis
 */
public class UGCMBroadcastReceiver extends GCMBroadcastReceiver
{
	private static final String SERVICES_PACKAGE = ".services";

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getGCMIntentServiceClassName(Context context)
	{
		return context.getPackageName() + SERVICES_PACKAGE
				+ DEFAULT_INTENT_SERVICE_CLASS_NAME;
	}
}
