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

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * @author Paolo Cortis
 * @param <T>
 * 
 */
public class ArrayAlternatedColoursAdapter<T> extends ArrayAdapter<T>
{
	private int[] colours = new int[] {0xFF000000, 0xFF333333};

	/**
	 * @param context
	 * @param resource
	 * @param textViewResourceId
	 * @param objects
	 */
	public ArrayAlternatedColoursAdapter(Context context, int resource,
			int textViewResourceId, List<T> objects)
	{
		super(context, resource, textViewResourceId, objects);
	}

	/**
	 * @param context
	 * @param resource
	 * @param textViewResourceId
	 * @param objects
	 */
	public ArrayAlternatedColoursAdapter(Context context, int resource,
			int textViewResourceId, T[] objects)
	{
		super(context, resource, textViewResourceId, objects);
	}

	/**
	 * @param context
	 * @param resource
	 * @param textViewResourceId
	 */
	public ArrayAlternatedColoursAdapter(Context context, int resource,
			int textViewResourceId)
	{
		super(context, resource, textViewResourceId);
	}

	/**
	 * @param context
	 * @param textViewResourceId
	 * @param objects
	 */
	public ArrayAlternatedColoursAdapter(Context context,
			int textViewResourceId, List<T> objects)
	{
		super(context, textViewResourceId, objects);
	}

	/**
	 * @param context
	 * @param textViewResourceId
	 * @param objects
	 */
	public ArrayAlternatedColoursAdapter(Context context,
			int textViewResourceId, T[] objects)
	{
		super(context, textViewResourceId, objects);
	}

	/**
	 * @param context
	 * @param textViewResourceId
	 */
	public ArrayAlternatedColoursAdapter(Context context, int textViewResourceId)
	{
		super(context, textViewResourceId);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view = super.getView(position, convertView, parent);
		int colourPos = position % colours.length;
		view.setBackgroundColor(colours[colourPos]);

		return view;
	}

	/**
	 * @param colours the colours to set
	 */
	public void setColours(int[] colours)
	{
		this.colours = colours;
	}
}
