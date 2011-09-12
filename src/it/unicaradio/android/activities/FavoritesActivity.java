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
import it.unicaradio.android.gui.Tabs;
import it.unicaradio.android.utils.ActivityUtils;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * @author Paolo Cortis
 * 
 */
public class FavoritesActivity extends TabbedActivity
{
	private final ArrayList<HashMap<String, String>> SITES = new ArrayList<HashMap<String, String>>();

	private ListView lv;

	{
		SITES.add(ActivityUtils.addItem("Sito web",
				"http://www.unicaradio.it/", R.drawable.logo));

		SITES.add(ActivityUtils.addItem("Facebook",
				"http://www.facebook.com/unicaradio/", R.drawable.facebook));

		SITES.add(ActivityUtils.addItem("Youtube",
				"http://www.youtube.com/user/unicaradiotv", R.drawable.youtube));

		SITES.add(ActivityUtils.addItem("Twitter",
				"http://twitter.com/#!/UnicaRadio", R.drawable.twitter));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState, R.layout.links);
	}

	@Override
	protected void setupTab()
	{
		lv = (ListView) findViewById(R.id.linksList);
		lv.setAdapter(new SimpleAdapter(this, SITES,
				R.layout.list_two_lines_and_image, new String[] {"line1",
						"line2", "icon"}, new int[] {R.id.text1, R.id.text2,
						R.id.icon}));
	}

	@Override
	protected void setupListeners()
	{
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id)
			{
				HashMap<String, String> object = SITES.get((int) id);
				openLink(object.get("line2"));
			}
		});
	}

	@Override
	public int getTab()
	{
		return Tabs.FAVORITES;
	}
}
