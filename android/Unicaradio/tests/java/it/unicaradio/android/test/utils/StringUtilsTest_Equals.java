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
public class StringUtilsTest_Equals extends AndroidTestCase
{
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
}
