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
package it.unicaradio.android.models;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * @author Paolo Cortis
 */
public class Schedule
{
	private static final String TAG = Schedule.class.getName();

	private final LinkedHashMap<Day, List<Transmission>> transmissions;

	private Schedule()
	{
		transmissions = new LinkedHashMap<Day, List<Transmission>>();
	}

	public static Schedule fromJSON(String json)
	{
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(json);
		} catch(JSONException e) {
			Log.e(TAG, "Error while parsing JSON", e);
		}

		return Schedule.fromJSON(jsonObject);
	}

	public static Schedule fromJSON(JSONObject json)
	{
		Schedule result = new Schedule();
		for(Day day : Day.values()) {
			result.transmissions.put(day, new ArrayList<Transmission>());
		}

		if(json == null) {
			Log.d(TAG, "JSON is null!!!");
			return result;
		}

		for(Day day : Day.values()) {
			List<Transmission> transmissionsByDay = result.transmissions
					.get(day);
			String dayKey = day.toString();

			try {
				JSONArray itemArray = json.getJSONArray(dayKey);
				for(int i = 0; i < itemArray.length(); i++) {
					JSONObject transmissionJSON = itemArray.getJSONObject(i);
					Transmission transmission = Transmission
							.fromJSON(transmissionJSON);
					if(transmission != null) {
						transmissionsByDay.add(transmission);
					}
				}
			} catch(JSONException e) {
				Log.e(TAG, "Error while parsing JSON", e);
				transmissionsByDay.clear();
			}

			result.transmissions.put(day, transmissionsByDay);
		}

		return result;
	}

	public List<Transmission> getTransmissionsByDay(Day day)
	{
		return transmissions.get(day);
	}

	public void setTransmissionsByDay(Day day, List<Transmission> transmissions)
	{
		this.transmissions.put(day, transmissions);
	}
}
