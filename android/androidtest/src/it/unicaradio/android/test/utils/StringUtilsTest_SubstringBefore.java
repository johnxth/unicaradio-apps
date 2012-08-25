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
public class StringUtilsTest_SubstringBefore extends AndroidTestCase
{
	public void testSubstringBeforeWithNullString()
	{
		String substring = StringUtils.substringBefore(null, "|");
		Assert.assertNull(substring);
	}

	public void testSubstringBeforeWithEmptyString()
	{
		String substring = StringUtils.substringBefore("", "|");
		Assert.assertEquals("", substring);
	}

	public void testSubstringBeforeWithSeparatorEqualsStartOfString()
	{
		String separator = "a";
		String string = separator + "bc";

		String substring = StringUtils.substringBefore(string, separator);
		Assert.assertEquals("", substring);
	}

	public void testSubstringBeforeWithMultipleMatches()
	{
		String separator = "b";

		String string = "a" + separator + "c";
		string = string + string;

		String substring = StringUtils.substringBefore(string, separator);
		Assert.assertEquals("a", substring);
	}

	public void testSubstringBeforeWithUnknownSeparator()
	{
		String separator = "d";
		String string = "abc";

		String substring = StringUtils.substringBefore(string, separator);
		Assert.assertEquals(string, substring);
	}

	public void testSubstringBeforeWithEmptySeparator()
	{
		String string = "abc";

		String substring = StringUtils.substringBefore(string, "");
		Assert.assertEquals("", substring);
	}

	public void testSubstringBeforeWithNullSeparator()
	{
		String string = "abc";

		String substring = StringUtils.substringBefore(string, null);
		Assert.assertEquals(string, substring);
	}

	public void testSubstringBefore()
	{
		String string = "abc";

		String substring = StringUtils.substringBefore(string, "c");
		Assert.assertEquals("ab", substring);
	}
}
