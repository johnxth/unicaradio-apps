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
public class StringUtilsTest_Mid extends AndroidTestCase
{
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
