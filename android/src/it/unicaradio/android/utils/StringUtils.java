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
	 * @param string
	 * @param tag
	 * @return
	 */
	public static String substringBetween(String string, String tag)
	{
		return substringBetween(string, tag, tag);
	}

	/**
	 * @param string
	 * @param open
	 * @param close
	 * @return
	 */
	public static String substringBetween(String string, String open,
			String close)
	{
		if((string == null) || (open == null) || (close == null)) {
			return null;
		}

		if(string.equals(EMPTY) || open.equals(EMPTY) || close.equals(EMPTY)) {
			return EMPTY;
		}

		int start = string.indexOf(open);
		if(start == -1) {
			return EMPTY;
		}

		int end = string.indexOf(close, start + open.length());
		if(end == -1) {
			return EMPTY;
		}

		return string.substring(start + open.length(), end);
	}

	/**
	 * @param string
	 * @param start
	 * @param end
	 * @return
	 */
	public static String substring(String string, int start)
	{
		if(string == null) {
			return null;
		}

		if(string.equals(EMPTY)) {
			return EMPTY;
		}

		if(start < 0) {
			start = string.length() + start;
		}

		start = Math.max(0, start);
		if(start > string.length()) {
			return EMPTY;
		}

		return string.substring(start);
	}

	/**
	 * @param string
	 * @param start
	 * @param end
	 * @return
	 */
	public static String substring(String string, int start, int end)
	{
		if(string == null) {
			return null;
		}

		if(start < 0) {
			start = string.length() + start;
		}
		if(end < 0) {
			end = string.length() + end;
		}

		end = Math.min(string.length(), end);
		if(start > end) {
			return EMPTY;
		}

		start = Math.max(0, start);
		end = Math.max(0, end);

		return string.substring(start, end);
	}

	/**
	 * @param string
	 * @param separator
	 * @return
	 */
	public static String substringBefore(String string, String separator)
	{
		if(isEmpty(string) || (separator == null)) {
			return string;
		}

		if(separator.length() == 0) {
			return EMPTY;
		}

		int pos = string.indexOf(separator);
		if(pos == -1) {
			return string;
		}

		return string.substring(0, pos);
	}

	/**
	 * @param string
	 * @param separator
	 * @return
	 */
	public static String substringAfter(String string, String separator)
	{
		if(isEmpty(string)) {
			return string;
		}

		if(separator == null) {
			return EMPTY;
		}

		int pos = string.indexOf(separator);
		if(pos == -1) {
			return EMPTY;
		}

		return string.substring(pos + separator.length());
	}

	public static boolean isEmpty(String string)
	{
		return (string == null) || string.equals(EMPTY);
	}
}
