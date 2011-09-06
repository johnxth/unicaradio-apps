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
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

/**
 * @author Paolo Cortis
 * 
 */
public class SimpleAlternatedColoursAdapter extends SimpleAdapter
{
	private final int[] colours = new int[] {0xFF000000, 0xFF333333};

	/**
	 * @param context
	 * @param data
	 * @param resource
	 * @param from
	 * @param to
	 */
	public SimpleAlternatedColoursAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to)
	{
		super(context, data, resource, from, to);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view = super.getView(position, convertView, parent);
		int colourPos = position % colours.length;
		view.setBackgroundColor(colours[colourPos]);

		return view;
	}
}
