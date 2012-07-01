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

import android.text.TextUtils;

/**
 * @author Paolo Cortis
 */
public class StringUtils
{
	public static final String EMPTY = "";

	private StringUtils()
	{
	}

	public static String left(String string, int length)
	{
		if(string == null) {
			return null;
		}

		if(length <= 0) {
			return StringUtils.EMPTY;
		}

		length = Math.min(string.length(), length);
		return TextUtils.substring(string, 0, length);
	}

	public static String right(String string, int length)
	{
		if(string == null) {
			return null;
		}

		if(length <= 0) {
			return StringUtils.EMPTY;
		}

		int end = string.length();
		int start = Math.max(end - length, 0);
		return TextUtils.substring(string, start, end);
	}

	public static String mid(String string, int start, int length)
	{
		if(string == null) {
			return null;
		}

		if(length <= 0) {
			return StringUtils.EMPTY;
		}

		if(StringUtils.equals(string, EMPTY)) {
			return EMPTY;
		}

		start = Math.max(Math.min(start, string.length()), 0);
		int end = Math.min(start + length, string.length());
		return TextUtils.substring(string, start, end);
	}

	public static boolean equals(String str1, String str2)
	{
		if((str1 == null) && (str2 == null)) {
			return true;
		}

		if((str1 == null) && (str2 != null)) {
			return false;
		}

		if((str1 != null) && (str2 == null)) {
			return false;
		}

		return TextUtils.equals(str1, str2);
	}

	/**
	 * @param metadataString
	 * @param string
	 * @param string2
	 * @return
	 */
	public static String substringBetween(String metadataString, String string,
			String string2)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param artistAndTitle
	 * @param i
	 * @param j
	 * @return
	 */
	public static String substring(String artistAndTitle, int i, int j)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param artistAndTitle
	 * @param separator
	 * @return
	 */
	public static String substringBefore(String artistAndTitle, String separator)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param artistAndTitle
	 * @param separator
	 * @return
	 */
	public static String substringAfter(String artistAndTitle, String separator)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
