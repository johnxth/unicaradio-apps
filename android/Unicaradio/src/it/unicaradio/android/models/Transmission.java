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

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * @author Paolo Cortis
 */
public class Transmission implements Comparable<Transmission>, Serializable
{
	private static final long serialVersionUID = 4610873281831025836L;

	private static final String PROGRAM_KEY = "programma";

	private static final String START_TIME_KEY = "inizio";

	private static final String TAG = Transmission.class.getName();

	private String formatName;

	private String startTime;

	public Transmission()
	{
	}

	/**
	 * @param format
	 * @param startTime
	 */
	public Transmission(String formatName, String startTime)
	{
		this.formatName = formatName;
		this.startTime = startTime;
	}

	public static Transmission fromJSON(String json)
	{
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(json);
		} catch(JSONException e) {
			Log.e(TAG, "Error while parsing JSON", e);
		}

		return Transmission.fromJSON(jsonObject);
	}

	public static Transmission fromJSON(JSONObject json)
	{

		try {
			String name = json.getString(PROGRAM_KEY);
			String start = adjustTime(json.getString(START_TIME_KEY));

			return new Transmission(name, start);
		} catch(JSONException e) {
			Log.e(TAG, "Error while parsing Transmission JSON!!", e);
			return null;
		}
	}

	private static String adjustTime(String time)
	{
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
		String adjustedTime = time;
		try {
			Date date = formatter.parse(time);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.HOUR_OF_DAY, 1);
			adjustedTime = formatter.format(cal.getTime());
		} catch(ParseException e) {
		}

		return adjustedTime;
	}

	/**
	 * @return the formatName
	 */
	public String getFormatName()
	{
		return formatName;
	}

	/**
	 * @param formatName the formatName to set
	 */
	public void setFormatName(String formatName)
	{
		this.formatName = formatName;
	}

	/**
	 * @return the startTime
	 */
	public String getStartTime()
	{
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(String startTime)
	{
		this.startTime = startTime;
	}

	@Override
	public int compareTo(Transmission another)
	{
		return this.getStartTime().compareTo(another.getStartTime());
	}
}
