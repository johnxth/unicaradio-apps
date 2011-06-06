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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * @author Paolo Cortis
 * 
 */
public class Tab extends LinearLayout
{
	public Tab(Context context)
	{
		super(context);
		init();
	}

	public Tab(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	private void init()
	{
		setGravity(Gravity.CENTER);
		setClickable(true);
		setOnClickListener(new OnClickListener() {
			public void onClick(View view)
			{
				ViewGroup parent = (ViewGroup) view.getParent();
				for(int i = 0; i < parent.getChildCount(); i++) {
					parent.getChildAt(i).setSelected(false);
				}
				setSelected(true);
			}
		});
	}
}
