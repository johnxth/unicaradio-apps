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

import it.unicaradio.android.utils.StringUtils;
import junit.framework.Assert;
import android.test.AndroidTestCase;

/**
 * @author Paolo Cortis
 */
public class StringUtilsTest_Substring extends AndroidTestCase
{
	public void testSubstringWithNullString()
	{
		String substring = StringUtils.substring(null, 1);
		Assert.assertNull(substring);
	}

	public void testSubstringWithEmptyString()
	{
		String substring = StringUtils.substring("", 1);
		Assert.assertEquals("", substring);
	}

	public void testSubstringWithZeroStart()
	{
		String string = "abc";

		String substring = StringUtils.substring(string, 0);
		Assert.assertEquals(string, substring);
	}

	public void testSubstringWithStartBiggerThanLength()
	{
		String string = "abc";

		String substring = StringUtils.substring(string, string.length() + 1);
		Assert.assertEquals("", substring);
	}

	public void testSubstringWithNegativeStartBiggerThanLength()
	{
		String string = "abc";

		int start = string.length() + 1;
		int negative_start = -start;
		String substring = StringUtils.substring(string, negative_start);
		Assert.assertEquals(string, substring);
	}

	public void testSubstring()
	{
		String substring = StringUtils.substring("abc", 2);
		Assert.assertEquals("c", substring);
	}

	public void testSubstringWithNegativeStart()
	{
		String substring = StringUtils.substring("abc", -2);
		Assert.assertEquals("bc", substring);
	}

	public void testSubstringThreeParamsWithNullString()
	{
		String substring = StringUtils.substring(null, 1, 2);
		Assert.assertNull(substring);
	}

	public void testSubstringThreeParamsWithEmptyString()
	{
		String substring = StringUtils.substring("", 1, 2);
		Assert.assertEquals("", substring);
	}

	public void testSubstringThreeParamsWithZeroEnd()
	{
		String substring = StringUtils.substring("abc", 2, 0);
		Assert.assertEquals("", substring);
	}

	public void testSubstringThreeParamsWithStartBiggerThanLength()
	{
		String string = "abc";

		String substring = StringUtils.substring(string, string.length() + 2,
				string.length() * 2);
		Assert.assertEquals("", substring);

		substring = StringUtils.substring(string, string.length(), 2);
		Assert.assertEquals("", substring);
	}

	public void testSubstringThreeParams()
	{
		String substring = StringUtils.substring("abc", 0, 2);
		Assert.assertEquals("ab", substring);
	}

	public void testSubstringThreeParamsWithEndBiggerThanLength()
	{
		String string = "abc";
		String substring = StringUtils
				.substring(string, 2, string.length() + 1);
		Assert.assertEquals("c", substring);
	}

	public void testSubstringThreeParamsWithNegativeStartAndEnd()
	{
		String string = "abc";
		String substring = StringUtils.substring(string, -2, -1);
		Assert.assertEquals("b", substring);
	}

	public void testSubstringThreeParamsWithNegativeStartBiggerThanLength()
	{
		String string = "abc";

		int start = string.length() + 1;
		int negative_start = -start;
		String substring = StringUtils.substring(string, negative_start, 2);
		Assert.assertEquals("ab", substring);
	}
}
