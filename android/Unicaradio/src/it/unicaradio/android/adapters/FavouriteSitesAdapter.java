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

import it.unicaradio.android.R;
import it.unicaradio.android.models.Website;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Paolo Cortis
 */
public class FavouriteSitesAdapter extends AlternatedColoursAdapter
{
	private final Context context;

	private final List<Website> websites;

	private final int layout;

	/**
	 * Layout MUST have: 1) a TextView called text1 where to put the description
	 * 2) a Textview called text2 where to put the address 3) a ImageView called
	 * icon where to put the logo
	 * 
	 * @param context
	 * @param websites
	 * @param layout
	 */
	public FavouriteSitesAdapter(Context context, List<Website> websites,
			int layout)
	{
		super(context, layout);
		this.context = context;
		this.websites = websites;
		this.layout = layout;
	}

	@Override
	public int getCount()
	{
		return websites.size();
	}

	@Override
	public Object getItem(int position)
	{
		return websites.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = getInflater();

		View view = inflater.inflate(layout, parent, false);
		Website website = (Website) getItem(position);

		drawDescription(view, website);
		drawUrl(view, website);
		drawLogo(view, website);

		int backgroundPos = position % backgrounds.length;
		view.setBackgroundDrawable(context.getResources().getDrawable(
				backgrounds[backgroundPos]));

		return view;
	}

	/**
	 * @param view
	 * @param website
	 */
	private void drawDescription(View view, Website website)
	{
		TextView descriptionView = (TextView) view.findViewById(R.id.text1);
		descriptionView.setText(website.getDescription());
	}

	/**
	 * @param view
	 * @param website
	 */
	private void drawUrl(View view, Website website)
	{
		TextView urlView = (TextView) view.findViewById(R.id.text2);
		urlView.setText(website.getUrl());
	}

	/**
	 * @param view
	 * @param website
	 */
	private void drawLogo(View view, Website website)
	{
		ImageView logoView = (ImageView) view.findViewById(R.id.icon);
		logoView.setImageResource(website.getLogo());
	}
}
