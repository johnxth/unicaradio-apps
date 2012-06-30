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

import it.unicaradio.android.models.Website;
import it.unicaradio.android.utils.IntentUtils;
import android.content.Context;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author Paolo Cortis
 */
public class FavouriteSitesOnItemClickListener implements OnItemClickListener
{
	private final Context context;

	public FavouriteSitesOnItemClickListener(Context context)
	{
		this.context = context;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id)
	{
		Adapter adapter = parent.getAdapter();
		Website website = (Website) adapter.getItem(position);
		IntentUtils.openLink(context, website.getUrl());
	}
}
