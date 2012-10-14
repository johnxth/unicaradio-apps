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

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * @author Paolo Cortis
 */
public class UnicaradioPagerAdapter extends FragmentPagerAdapter
{
	private List<Fragment> fragments;

	public UnicaradioPagerAdapter(FragmentManager fragmentManager,
			List<Fragment> fragments)
	{
		super(fragmentManager);
		this.fragments = fragments;
	}

	@Override
	public Fragment getItem(int position)
	{
		return this.fragments.get(position);
	}

	@Override
	public int getCount()
	{
		return this.fragments.size();
	}
}
