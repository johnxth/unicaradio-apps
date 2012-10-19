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
package it.unicaradio.android.listeners;

import it.unicaradio.android.R;
import it.unicaradio.android.adapters.UnicaradioPagerAdapter;
import it.unicaradio.android.fragments.UnicaradioFragment;
import it.unicaradio.android.gui.Tab;
import it.unicaradio.android.gui.Tab.OnTabSelectedListener;
import it.unicaradio.android.gui.Tabs;

import java.util.Map;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

/**
 * @author Paolo Cortis
 */
public class TabSelectedListener implements OnTabSelectedListener
{
	private static final String TAG = TabSelectedListener.class.getName();

	private static int currentTab = Tabs.STREAMING;

	private FragmentManager fragmentManager;

	private ViewPager viewPager;

	private UnicaradioPagerAdapter pagerAdapter;

	private Context context;

	public TabSelectedListener(Context context,
			FragmentManager fragmentManager, ViewPager viewPager)
	{
		this.viewPager = viewPager;
		this.context = context;
		this.fragmentManager = fragmentManager;

		init();
	}

	public void init()
	{
		this.pagerAdapter = new UnicaradioPagerAdapter(fragmentManager,
				Tabs.getFragmentsAsList(context));
		this.viewPager.setAdapter(pagerAdapter);
		this.viewPager
				.setOnPageChangeListener(new UnicaradioOnPageChangeListener());

		showCurrentFragment();
	}

	/**
	 * @return
	 */
	public UnicaradioFragment getCurrentFragment()
	{
		return (UnicaradioFragment) pagerAdapter.getItem(currentTab);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onTabSelected(Tab tab)
	{
		int selectedTab = tab.getType();
		Log.d(TAG, "Tab selected: " + Integer.toString(selectedTab));

		if(selectedTab == currentTab) {
			return;
		}

		currentTab = selectedTab;
		showCurrentFragment();
	}

	/**
	 * @return the currentTab
	 */
	public static int getCurrentTab()
	{
		return currentTab;
	}

	/**
	 * @param fragmentManager the fragmentManager to set
	 */
	public void setFragmentManager(FragmentManager fragmentManager)
	{
		this.fragmentManager = fragmentManager;
	}

	private void showCurrentFragment()
	{
		viewPager.setCurrentItem(getCurrentTab());
	}

	/**
	 * @param viewPager the viewPager to set
	 */
	public void setViewPager(ViewPager viewPager)
	{
		this.viewPager = viewPager;
	}

	/**
	 * @param context the context to set
	 */
	public void setContext(Context context)
	{
		this.context = context;
	}

	/**
	 * @author Paolo Cortis
	 */
	private final class UnicaradioOnPageChangeListener extends
			ViewPager.SimpleOnPageChangeListener
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onPageSelected(int position)
		{
			currentTab = position;

			View rootView = viewPager.getRootView();
			Tabs tabsContainer = (Tabs) rootView.findViewById(R.id.tabs);

			Map<Integer, Tab> tabs = Tabs.getTabs(tabsContainer);
			tabs.get(position).select();
		}
	}
}
