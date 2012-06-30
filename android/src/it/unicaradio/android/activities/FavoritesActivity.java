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
package it.unicaradio.android.activities;

import it.unicaradio.android.R;
import it.unicaradio.android.adapters.FavouriteSitesAdapter;
import it.unicaradio.android.gui.Tabs;
import it.unicaradio.android.listeners.FavouriteSitesOnItemClickListener;
import it.unicaradio.android.models.Website;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.widget.ListView;

/**
 * @author Paolo Cortis
 */
public class FavoritesActivity extends TabbedActivity
{
	private ListView sitesListView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState, R.layout.links);
	}

	@Override
	protected void setupTab()
	{
		List<Website> websites = prepareSitesList();

		sitesListView = (ListView) findViewById(R.id.linksList);

		FavouriteSitesAdapter favouriteSitesAdapter = new FavouriteSitesAdapter(
				this, websites, R.layout.list_two_lines_and_image);
		sitesListView.setAdapter(favouriteSitesAdapter);
	}

	private List<Website> prepareSitesList()
	{
		List<Website> websites = new ArrayList<Website>();

		Website mainSite = new Website("Sito web", "http://www.unicaradio.it/",
				R.drawable.logo);
		Website facebookPage = new Website("Facebook",
				"http://www.facebook.com/pages/Unica-Radio/306075247045",
				R.drawable.facebook);
		Website youtubeChannel = new Website("Youtube",
				"http://www.youtube.com/user/unicaradiotv", R.drawable.youtube);
		Website twitterProfile = new Website("Twitter",
				"http://twitter.com/#!/UnicaRadio", R.drawable.twitter);

		websites.add(mainSite);
		websites.add(facebookPage);
		websites.add(youtubeChannel);
		websites.add(twitterProfile);

		return websites;

	}

	@Override
	protected void setupListeners()
	{
		FavouriteSitesOnItemClickListener onItemClickListener;
		onItemClickListener = new FavouriteSitesOnItemClickListener(this);
		sitesListView.setOnItemClickListener(onItemClickListener);
	}

	@Override
	public int getTab()
	{
		return Tabs.FAVORITES;
	}
}
