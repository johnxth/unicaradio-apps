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
public class StringUtilsTest_Left extends AndroidTestCase
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
}
