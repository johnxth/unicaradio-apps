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
package it.unicaradio.android.enums;

import it.unicaradio.android.utils.StringUtils;
import android.support.v4.app.NotificationCompat;

/**
 * @author Paolo Cortis
 */
public enum GcmMessagePriority {
	HIGH, NORMAL, LOW;

	public static GcmMessagePriority fromString(String priority)
	{
		if(StringUtils.equals(priority, "NORMAL")) {
			return NORMAL;
		}
		if(StringUtils.equals(priority, "HIGH")) {
			return HIGH;
		}
		if(StringUtils.equals(priority, "LOW")) {
			return LOW;
		}

		return NORMAL;
	}

	public int toAndroidNotificationPriority()
	{
		switch(this) {
			case HIGH:
				return NotificationCompat.PRIORITY_DEFAULT;
			case NORMAL:
				return NotificationCompat.PRIORITY_LOW;
			case LOW:
				return NotificationCompat.PRIORITY_LOW;
			default:
				return NotificationCompat.PRIORITY_LOW;
		}
	}
}
