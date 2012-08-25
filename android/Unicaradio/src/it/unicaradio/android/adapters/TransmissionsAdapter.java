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
import it.unicaradio.android.models.Transmission;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author Paolo Cortis
 */
public class TransmissionsAdapter extends AlternatedColoursAdapter
{
	private final List<Transmission> transmissions;

	/**
	 * Layout MUST have:
	 * 1) a TextView called text1 where to put the start time
	 * 2) a Textview called text2 where to put the format name
	 * 
	 * @param context
	 * @param websites
	 * @param layout
	 */
	public TransmissionsAdapter(Context context,
			List<Transmission> transmissions, int layout)
	{
		super(context, layout);

		this.transmissions = transmissions;
	}

	@Override
	public int getCount()
	{
		return transmissions.size();
	}

	@Override
	public Object getItem(int position)
	{
		return transmissions.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view = super.getView(position, convertView, parent);
		Transmission transmission = (Transmission) getItem(position);

		drawFormatName(view, transmission);
		drawStartTime(view, transmission);

		return view;
	}

	private void drawStartTime(View view, Transmission transmission)
	{
		TextView startTimeView = (TextView) view.findViewById(R.id.text1);
		startTimeView.setText(transmission.getStartTime());
	}

	private void drawFormatName(View view, Transmission transmission)
	{
		TextView formatNameView = (TextView) view.findViewById(R.id.text2);
		formatNameView.setText(transmission.getFormatName());
	}
}
