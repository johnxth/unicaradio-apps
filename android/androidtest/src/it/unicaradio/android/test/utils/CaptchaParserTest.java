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
package it.unicaradio.android.test.utils;

import it.unicaradio.android.exceptions.WrongCaptchaLengthException;
import it.unicaradio.android.exceptions.WrongCaptchaOperationException;
import it.unicaradio.android.utils.CaptchaParser;
import android.test.AndroidTestCase;
import android.util.Log;

/**
 * @author Paolo Cortis
 */
public class CaptchaParserTest extends AndroidTestCase
{
	private static final String TAG = CaptchaParserTest.class.getName();

	private static final String FIXED_CAPTCHA_TAIL = " = ...";

	public void testFailForWrongLength()
	{
		try {
			CaptchaParser.generateHumanReadableCaptcha("1");
			fail("Function should have thrown a WrongCaptchaLengthException.");
		} catch(WrongCaptchaLengthException e) {
			// ok
		} catch(WrongCaptchaOperationException e) {
			fail("Function should have thrown a WrongCaptchaLengthException.");
		}
	}

	public void testGenerateCaptchaWithSum()
	{
		String firstNumber = "02";
		String secondNumber = "03";
		String goodCaptchaWithSum = firstNumber + CaptchaParser.PLUS
				+ secondNumber;

		try {
			String result = CaptchaParser
					.generateHumanReadableCaptcha(goodCaptchaWithSum);
			assertEquals(firstNumber + " + " + secondNumber
					+ FIXED_CAPTCHA_TAIL, result);
		} catch(WrongCaptchaLengthException e) {
			Log.e(TAG, e.getMessage(), e);
			fail("Function should have not thrown a WrongCaptchaLengthException.");
		} catch(WrongCaptchaOperationException e) {
			Log.e(TAG, e.getMessage(), e);
			fail("Function should have not thrown a WrongCaptchaOperationException.");
		}
	}

	public void testGenerateCaptchaWithDifference()
	{
		String firstNumber = "01";
		String secondNumber = "04";
		String goodCaptchaWithSum = firstNumber + CaptchaParser.MINUS
				+ secondNumber;

		try {
			String result = CaptchaParser
					.generateHumanReadableCaptcha(goodCaptchaWithSum);
			assertEquals(firstNumber + " - " + secondNumber
					+ FIXED_CAPTCHA_TAIL, result);
		} catch(WrongCaptchaLengthException e) {
			Log.e(TAG, e.getMessage(), e);
			fail("Function should have not thrown a WrongCaptchaLengthException.");
		} catch(WrongCaptchaOperationException e) {
			Log.e(TAG, e.getMessage(), e);
			fail("Function should have not thrown a WrongCaptchaOperationException.");
		}
	}

	public void testGenerateCaptchaWithMultiply()
	{
		String firstNumber = "01";
		String secondNumber = "02";
		String goodCaptchaWithSum = firstNumber + CaptchaParser.MULT
				+ secondNumber;

		try {
			String result = CaptchaParser
					.generateHumanReadableCaptcha(goodCaptchaWithSum);
			assertEquals(firstNumber + " * " + secondNumber
					+ FIXED_CAPTCHA_TAIL, result);
		} catch(WrongCaptchaLengthException e) {
			Log.e(TAG, e.getMessage(), e);
			fail("Function should have not thrown a WrongCaptchaLengthException.");
		} catch(WrongCaptchaOperationException e) {
			Log.e(TAG, e.getMessage(), e);
			fail("Function should have not thrown a WrongCaptchaOperationException.");
		}
	}

	public void testFailWithUnknownOperation()
	{
		try {
			CaptchaParser.generateHumanReadableCaptcha("02100003");
			fail("Function should have thrown a WrongCaptchaOperationException.");
		} catch(WrongCaptchaLengthException e) {
			fail("Function should have thrown a WrongCaptchaOperationException.");
		} catch(WrongCaptchaOperationException e) {
			// ok
		}
	}
}
