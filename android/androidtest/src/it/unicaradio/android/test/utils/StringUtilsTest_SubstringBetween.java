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
public class StringUtilsTest_SubstringBetween extends AndroidTestCase
{
	public void testSubstringBetweenWithNullOrigin()
	{
		String result = StringUtils.substringBetween(null, "a", "b");
		Assert.assertNull(result);
	}

	public void testSubstringBetweenWithNullStart()
	{
		String result = StringUtils.substringBetween("abc", null, "b");
		Assert.assertNull(result);
	}

	public void testSubstringBetweenWithNullEnd()
	{
		String result = StringUtils.substringBetween("abc", "a", null);
		Assert.assertNull(result);
	}

	public void testSubstringBetweenWithEmptyOrigin()
	{
		String result = StringUtils.substringBetween("", "a", "b");
		Assert.assertEquals("", result);
	}

	public void testSubstringBetweenWithEmptyStart()
	{
		String result = StringUtils.substringBetween("abc", "", "b");
		Assert.assertEquals("", result);
	}

	public void testSubstringBetweenWithEmptyEnd()
	{
		String result = StringUtils.substringBetween("abc", "a", "");
		Assert.assertEquals("", result);
	}

	public void testSubstringBetween()
	{
		String string = "abc";
		String open = "x";
		String close = "z";

		String origin = open + string + close;
		String result = StringUtils.substringBetween(origin, open, close);
		Assert.assertEquals(string, result);
	}

	public void testSubstringBetweenWithMultipleMatches()
	{
		String string = "abc";
		String open = "x";
		String close = "z";

		String origin = open + string + close;
		origin += origin;

		String result = StringUtils.substringBetween(origin, open, close);
		Assert.assertEquals(string, result);
	}

	public void testSubstringBetweenWithoutOpenString()
	{
		String string = "abc";
		String open = "x";
		String close = "z";

		String origin = string + close;
		String result = StringUtils.substringBetween(origin, open, close);
		Assert.assertEquals("", result);
	}

	public void testSubstringBetweenWithoutCloseString()
	{
		String string = "abc";
		String open = "x";
		String close = "z";

		String origin = open + string;
		String result = StringUtils.substringBetween(origin, open, close);
		Assert.assertEquals("", result);
	}

	public void testSubstringBetweenTwoParamsWithNullOrigin()
	{
		String result = StringUtils.substringBetween(null, "a");
		Assert.assertNull(result);
	}

	public void testSubstringBetweenTwoParamsWithNullStart()
	{
		String result = StringUtils.substringBetween("abc", null);
		Assert.assertNull(result);
	}

	public void testSubstringBetweenTwoParamsWithEmptyOrigin()
	{
		String result = StringUtils.substringBetween("", "a");
		Assert.assertEquals("", result);
	}

	public void testSubstringBetweenTwoParamsWithEmptyStart()
	{
		String result = StringUtils.substringBetween("abc", "");
		Assert.assertEquals("", result);
	}

	public void testSubstringTwoParamsBetween()
	{
		String string = "abc";
		String open = "x";

		String origin = open + string + open;
		String result = StringUtils.substringBetween(origin, open);
		Assert.assertEquals(string, result);
	}

	public void testSubstringBetweenTwoParamsWithMultipleMatches()
	{
		String string = "abc";
		String open = "|";

		String origin = open + string + open;
		origin += origin;

		String result = StringUtils.substringBetween(origin, open);
		Assert.assertEquals(string, result);
	}

	public void testSubstringBetweenTwoParamsWithoutOpenString()
	{
		String string = "abc";
		String open = "x";

		String origin = string;
		String result = StringUtils.substringBetween(origin, open);
		Assert.assertEquals("", result);
	}

	public void testSubstringBetweenTwoParamsWithoutCloseString()
	{
		String string = "abc";
		String open = "x";

		String origin = open + string;
		String result = StringUtils.substringBetween(origin, open);
		Assert.assertEquals("", result);
	}
}
