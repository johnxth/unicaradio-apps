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

import java.text.MessageFormat;

import org.apache.commons.lang.StringUtils;

/**
 * @author Paolo Cortis
 * 
 */
public class CaptchaParser
{
	private static final String PLUS = "0001";

	private static final String MINUS = "0010";

	private static final String MULT = "0100";

	public static String parse(String captcha)
	{
		String captcha_ = captcha.trim();

		if(captcha_.length() != 8) {
			throw new RuntimeException("Wrong length: " + captcha_.length());
		}

		String op1 = StringUtils.left(captcha_, 2);
		String op2 = StringUtils.right(captcha_, 2);
		String operation = StringUtils.mid(captcha_, 2, 4);
		if(operation.equals(PLUS)) {
			operation = "+";
		} else if(operation.equals(MINUS)) {
			operation = "-";
		} else if(operation.equals(MULT)) {
			operation = "*";
		} else {
			throw new RuntimeException("Unrecognized operation");
		}

		return MessageFormat.format("{0} {1} {2} = ...", op1, operation, op2);
	}
}
