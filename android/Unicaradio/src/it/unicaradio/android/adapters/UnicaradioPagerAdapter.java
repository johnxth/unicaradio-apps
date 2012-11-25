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
package it.unicaradio.android.adapters;

import it.unicaradio.android.gui.Tabs;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * @author Paolo Cortis
 */
public class UnicaradioPagerAdapter extends FragmentPagerAdapter
{
	private Context context;

	public UnicaradioPagerAdapter(Context context,
			FragmentManager fragmentManager)
	{
		super(fragmentManager);

		this.context = context;
	}

	@Override
	public Fragment getItem(int position)
	{
		return Tabs.initFragment(context, position);
	}

	@Override
	public int getCount()
	{
		return Tabs.getTabCount();
	}
}
