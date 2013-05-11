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
package it.unicaradio.android.fragments;

import android.view.KeyEvent;

/**
 * @author Paolo Cortis
 */
public abstract class UnicaradioFragment extends UnicaradioBaseFragment implements KeyEvent.Callback
{
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onResume()
	{
		super.onResume();

		getSherlockActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event)
	{
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onKeyMultiple(int keyCode, int count, KeyEvent event)
	{
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		return false;
	}

	public boolean onBackPressed()
	{
		return false;
	}
}
