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
package it.unicaradio.android.gui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TableRow;

/**
 * @author Paolo Cortis
 */
public class Tabs extends TableRow
{
	public static final int STREAMING = 0;

	public static final int SCHEDULE = 1;

	public static final int SONG = 2;

	public static final int FAVORITES = 3;

	public static final int INFO = 4;

	public Tabs(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public Tabs(Context context)
	{
		super(context);
	}

	private void init()
	{
		setOrientation(HORIZONTAL);
	}
}
