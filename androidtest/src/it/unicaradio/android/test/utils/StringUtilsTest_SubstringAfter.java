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
public class StringUtilsTest_SubstringAfter extends AndroidTestCase
{
	public void testSubstringAfterWithNullString()
	{
		String substring = StringUtils.substringAfter(null, "|");
		Assert.assertNull(substring);
	}

	public void testSubstringAfterWithEmptyString()
	{
		String substring = StringUtils.substringAfter("", "|");
		Assert.assertEquals("", substring);
	}

	public void testSubstringAfterWithSeparatorEqualsEndOfString()
	{
		String separator = "a";
		String string = "bc" + separator;

		String substring = StringUtils.substringAfter(string, separator);
		Assert.assertEquals("", substring);
	}

	public void testSubstringAfterWithMultipleMatches()
	{
		String separator = "b";

		String string = "abcabc";

		String substring = StringUtils.substringAfter(string, separator);
		Assert.assertEquals("cabc", substring);
	}

	public void testSubstringAfterWithUnknownSeparator()
	{
		String separator = "d";
		String string = "abc";

		String substring = StringUtils.substringAfter(string, separator);
		Assert.assertEquals("", substring);
	}

	public void testSubstringAfterWithEmptySeparator()
	{
		String string = "abc";

		String substring = StringUtils.substringAfter(string, "");
		Assert.assertEquals(string, substring);
	}

	public void testSubstringAfterWithNullSeparator()
	{
		String string = "abc";

		String substring = StringUtils.substringAfter(string, null);
		Assert.assertEquals("", substring);
	}

	public void testSubstringAfter()
	{
		String string = "abc";

		String substring = StringUtils.substringAfter(string, "a");
		Assert.assertEquals("bc", substring);
	}
}
