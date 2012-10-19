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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
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

	private static List<Fragment> fragments;

	private Context context;

	public Tabs(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		this.context = context;
		init();
	}

	public Tabs(Context context)
	{
		super(context);

		this.context = context;
		init();
	}

	public static List<Fragment> getFragmentsAsList(Context context)
	{
		if(fragments == null) {
			fragments = initFragments(context);
		}

		return fragments;
	}

	public static Map<Integer, Tab> getTabs(Tabs tabsContainer)
	{
		Map<Integer, Tab> tabs = new HashMap<Integer, Tab>();

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

		if(fragments == null) {
			fragments = initFragments(context);
		}
	}

	private static ArrayList<Fragment> initFragments(Context context)
	{
		ArrayList<Fragment> fragments = new ArrayList<Fragment>();

		fragments.add(Fragment.instantiate(context,
				StreamingFragment.class.getName()));
		fragments.add(Fragment.instantiate(context,
				ScheduleFragment.class.getName()));
		fragments.add(Fragment.instantiate(context,
				SongRequestFragment.class.getName()));
		fragments.add(Fragment.instantiate(context,
				FavouritesFragment.class.getName()));
		fragments.add(Fragment.instantiate(context,
				InfoFragment.class.getName()));

		return fragments;
	}
}
