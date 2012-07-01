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

import android.text.TextUtils;

/**
 * @author Paolo Cortis
 */
public enum Day {
	MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;

	private static final String MONDAY_KEY = "lunedi";

	private static final String TUESDAY_KEY = "martedi";

	private static final String WEDNESDAY_KEY = "mercoledi";

	private static final String THURSDAY_KEY = "giovedi";

	private static final String FRIDAY_KEY = "venerdi";

	private static final String SATURDAY_KEY = "sabato";

	private static final String SUNDAY_KEY = "domenica";

	public static Day fromString(String day)
	{
		if(TextUtils.equals(day, MONDAY_KEY)) {
			return MONDAY;
		}
		if(TextUtils.equals(day, TUESDAY_KEY)) {
			return TUESDAY;
		}
		if(TextUtils.equals(day, WEDNESDAY_KEY)) {
			return WEDNESDAY;
		}
		if(TextUtils.equals(day, THURSDAY_KEY)) {
			return THURSDAY;
		}
		if(TextUtils.equals(day, FRIDAY_KEY)) {
			return FRIDAY;
		}
		if(TextUtils.equals(day, SATURDAY_KEY)) {
			return SATURDAY;
		}
		if(TextUtils.equals(day, SUNDAY_KEY)) {
			return SUNDAY;
		}

		return MONDAY;
	}

	public String toString(Day day)
	{
		switch(day) {
			case MONDAY:
				return MONDAY_KEY;
			case TUESDAY:
				return TUESDAY_KEY;
			case WEDNESDAY:
				return WEDNESDAY_KEY;
			case THURSDAY:
				return THURSDAY_KEY;
			case FRIDAY:
				return FRIDAY_KEY;
			case SATURDAY:
				return SATURDAY_KEY;
			case SUNDAY:
				return SUNDAY_KEY;
		}

		return MONDAY_KEY;
	}
}
