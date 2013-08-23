/*
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

import android.test.AndroidTestCase;

import junit.framework.Assert;

import it.unicaradio.android.utils.StringUtils;

/**
 * @author Paolo Cortis
 */
public class StringUtilsTest_NotEquals extends AndroidTestCase
{
	public void testNotEqualsWithBothNull()
	{
		boolean result = StringUtils.notEquals(null, null);
		Assert.assertFalse(result);
	}

	public void testNotEqualsWithInstanceAndNull()
	{
		String string = "abc";
		boolean result = StringUtils.notEquals(null, string);
		Assert.assertTrue(result);

		result = StringUtils.notEquals(string, null);
		Assert.assertTrue(result);
	}

	public void testNotEqualsWithEqualStrings()
	{
		String string = "abc";
		boolean result = StringUtils.notEquals(string, string);
		Assert.assertFalse(result);
	}

	public void testEqualsWithUppercaseAndLowercase()
	{
		String string = "abc";
		boolean result = StringUtils.notEquals(string, string.toUpperCase());
		Assert.assertTrue(result);
	}
}
