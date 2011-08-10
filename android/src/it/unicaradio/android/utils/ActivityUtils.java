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
package it.unicaradio.android.utils;

import java.util.HashMap;

/**
 * @author Paolo Cortis
 * 
 */
public class ActivityUtils
{

	public static HashMap<String, String> addItem(String line1, String line2)
	{
		HashMap<String, String> item = new HashMap<String, String>();
		item.put("line1", line1);
		item.put("line2", line2);

		return item;
	}

	public static HashMap<String, String> addItem(String line1, String line2,
			int resourceImage)
	{
		HashMap<String, String> item = addItem(line1, line2);
		item.put("icon", String.valueOf(resourceImage));

		return item;
	}
}
