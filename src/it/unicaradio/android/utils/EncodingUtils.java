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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.commons.lang.StringUtils;

/**
 * Utility class for JavaScript compatible UTF-8 encoding and decoding.
 * 
 * @author Paolo Cortis
 */
public class EncodingUtils
{
	/**
	 * Decodes the passed UTF-8 String using an algorithm that's compatible with
	 * JavaScript's <code>decodeURIComponent</code> function. Returns
	 * <code>null</code> if the String is <code>null</code>.
	 * 
	 * @param s The UTF-8 encoded String to be decoded
	 * @return the decoded String
	 */
	public static String decodeURIComponent(String s)
	{
		if(s == null) {
			return StringUtils.EMPTY;
		}

		String result;
		try {
			result = URLDecoder.decode(s, "UTF-8");
		} catch(UnsupportedEncodingException e) {
			result = s;
		}

		return result;
	}

	/**
	 * Encodes the passed String as UTF-8 using an algorithm that's compatible
	 * with JavaScript's <code>encodeURIComponent</code> function. Returns
	 * <code>null</code> if the String is <code>null</code>.
	 * 
	 * @param s The String to be encoded
	 * @return the encoded String
	 */
	public static String encodeURIComponent(String s)
	{
		String result;

		try {
			result = URLEncoder.encode(s, "UTF-8").replaceAll("\\+", "%20")
					.replaceAll("\\%21", "!").replaceAll("\\%27", "'")
					.replaceAll("\\%28", "(").replaceAll("\\%29", ")")
					.replaceAll("\\%7E", "~");
		} catch(UnsupportedEncodingException e) {
			result = s;
		}

		return result;
	}
}
