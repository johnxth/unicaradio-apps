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
public class StringUtilsTest extends AndroidTestCase
{
	public void testLeftWithNullString()
	{
		String result = StringUtils.left(null, 0);
		Assert.assertNull(result);

		result = StringUtils.left(null, -1);
		Assert.assertNull(result);
	}

	public void testLeftWithNegativeLength()
	{
		String result = StringUtils.left("abc", -1);
		Assert.assertEquals("", result);
	}

	public void testLeftWithBiggerLength()
	{
		String string = "abc";

		String result = StringUtils.left(string, string.length() + 1);
		Assert.assertEquals(string, result);
	}

	public void testLeftWithPositiveLength()
	{
		String string = "abc";

		String result = StringUtils.left(string, 0);
		Assert.assertEquals("", result);

		result = StringUtils.left(string, 2);
		Assert.assertEquals("ab", result);
	}

	public void testRightWithNullString()
	{
		String result = StringUtils.right(null, 0);
		Assert.assertNull(result);

		result = StringUtils.right(null, -1);
		Assert.assertNull(result);
	}

	public void testRightWithNegativeLength()
	{
		String result = StringUtils.right("abc", -1);
		Assert.assertEquals("", result);
	}

	public void testRightWithBiggerLength()
	{
		String string = "abc";

		String result = StringUtils.right(string, string.length() + 1);
		Assert.assertEquals(string, result);
	}

	public void testRightWithPositiveLength()
	{
		String string = "abc";

		String result = StringUtils.right(string, 0);
		Assert.assertEquals("", result);

		result = StringUtils.right(string, 2);
		Assert.assertEquals("bc", result);
	}

	public void testEqualsWithBothNull()
	{
		boolean result = StringUtils.equals(null, null);
		Assert.assertTrue(result);
	}

	public void testEqualsWithInstanceAndNull()
	{
		String string = "abc";
		boolean result = StringUtils.equals(null, string);
		Assert.assertFalse(result);

		result = StringUtils.equals(string, null);
		Assert.assertFalse(result);
	}

	public void testEqualsWithEqualStrings()
	{
		String string = "abc";
		boolean result = StringUtils.equals(string, string);
		Assert.assertTrue(result);
	}

	public void testEqualsWithUppercaseAndLowercase()
	{
		String string = "abc";
		boolean result = StringUtils.equals(string, string.toUpperCase());
		Assert.assertFalse(result);
	}

	public void testMidWithNullString()
	{
		String result = StringUtils.mid(null, 0, 4);
		Assert.assertNull(result);
	}

	public void testMidWithNegativeLength()
	{
		String result = StringUtils.mid("abc", 0, -2);
		Assert.assertEquals("", result);
	}

	public void testMidWithEmptyString()
	{
		String result = StringUtils.mid("", 0, 2);
		Assert.assertEquals("", result);
	}

	public void testMidWithBiggerLength()
	{
		String string = "abc";
		String result = StringUtils.mid(string, 0, string.length() + 1);
		Assert.assertEquals(string, result);

		result = StringUtils.mid(string, 2, string.length() + 1);
		Assert.assertEquals("c", result);
	}

	public void testMidWithNegativeStart()
	{
		String string = "abc";
		String result = StringUtils.mid(string, -2, 2);
		Assert.assertEquals("ab", result);
	}

	public void testMidWithStartBiggerThanLength()
	{
		String result = StringUtils.mid("abc", 4, 2);
		Assert.assertEquals("", result);
	}

	public void testMid()
	{
		String string = "abc";
		String result = StringUtils.mid(string, 0, 2);
		Assert.assertEquals("ab", result);
	}
}
