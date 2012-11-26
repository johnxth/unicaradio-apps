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

import it.unicaradio.android.fragments.FavouritesFragment;
import it.unicaradio.android.fragments.InfoFragment;
import it.unicaradio.android.fragments.ScheduleFragment;
import it.unicaradio.android.fragments.SongRequestFragment;
import it.unicaradio.android.fragments.StreamingFragment;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
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

		init();
	}

	public static int getTabCount()
	{
		return 5;
	}

	public static SparseArray<Tab> getTabs(Tabs tabsContainer)
	{
		SparseArray<Tab> tabs = new SparseArray<Tab>();

		for(int i = 0; i < tabsContainer.getChildCount(); i++) {
			View child = tabsContainer.getChildAt(i);
			if(child instanceof Tab) {
				Tab tab = (Tab) child;
				tabs.put(tab.getType(), tab);
			}
		}

		return tabs;
	}

	private void init()
	{
		setOrientation(HORIZONTAL);
	}

	public static Fragment initFragment(Context context, int position)
	{
		Fragment fragment;

		switch(position) {
			case 0:
				// fragment = Fragment.instantiate(context,
				// StreamingFragment.class.getName());
				fragment = new StreamingFragment();
				break;
			case 1:
				// fragment = Fragment.instantiate(context,
				// ScheduleFragment.class.getName());
				fragment = new ScheduleFragment();
				break;
			case 2:
				// fragment = Fragment.instantiate(context,
				// SongRequestFragment.class.getName());
				fragment = new SongRequestFragment();
				break;
			case 3:
				// fragment = Fragment.instantiate(context,
				// FavouritesFragment.class.getName());
				fragment = new FavouritesFragment();
				break;
			case 4:
				// fragment = Fragment.instantiate(context,
				// InfoFragment.class.getName());
				fragment = new InfoFragment();
				break;

			default:
				return null;
		}

		return fragment;
	}
}
