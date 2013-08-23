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
public class StringUtilsTest_DefaultString extends AndroidTestCase
{
	public void testDefaultStringWithNullString()
	{
		String s = StringUtils.defaultString(null);
		Assert.assertEquals("", s);
	}

	public void testDefaultStringWithGoodString()
	{
		String testString = "test string";
		String s = StringUtils.defaultString(testString);
		Assert.assertEquals(testString, s);
	}

	public void testDefaultStringWithNullStringAndGoodDefault()
	{
		String testDefault = "test Default";
		String s = StringUtils.defaultString(null, testDefault);
		Assert.assertEquals(testDefault, s);
	}

	public void testDefaultStringWithGoodStringAndNullDefault()
	{
		String testString = "test string";
		String s = StringUtils.defaultString(testString, null);
		Assert.assertEquals(testString, s);
	}

	public void testDefaultStringWithNullStringAndNullDefault()
	{
		String s = StringUtils.defaultString(null, null);
		Assert.assertEquals(null, s);
	}

	public void testDefaultStringWithGoodStringAndGoodDefault()
	{
		String testString = "test string";
		String s = StringUtils.defaultString(testString, "test Default");
		Assert.assertEquals(testString, s);
	}
}
