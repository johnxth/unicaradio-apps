package it.unicaradio.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class AlternatedColoursAdapter extends BaseAdapter
{
	private Context context;

	private int layout;

	private int[] colours = new int[] { 0xFF000000, 0xFF333333 };

	public AlternatedColoursAdapter(Context context, int layout)
	{
		this.context = context;
		this.layout = layout;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater layoutInflater = getInflater();

		View view = layoutInflater.inflate(layout, parent, false);
		int colourPos = position % colours.length;
		view.setBackgroundColor(colours[colourPos]);

		return view;
	}

	/**
	 * @return
	 */
	private LayoutInflater getInflater()
	{
		return (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/**
	 * @param colours
	 *            the colours to set
	 */
	public void setColours(int[] colours)
	{
		this.colours = colours;
	}
}
