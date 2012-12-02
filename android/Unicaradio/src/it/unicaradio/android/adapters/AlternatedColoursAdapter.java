package it.unicaradio.android.adapters;

import it.unicaradio.android.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class AlternatedColoursAdapter extends BaseAdapter
{
	private Context context;

	private int layout;

	protected int[] backgrounds = new int[] {R.drawable.list_background_odd,
			R.drawable.list_background_even};

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
		int backgroundPos = position % backgrounds.length;
		view.setBackgroundDrawable(context.getResources().getDrawable(
				backgrounds[backgroundPos]));

		return view;
	}

	/**
	 * @return
	 */
	protected LayoutInflater getInflater()
	{
		return (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/**
	 * @param backgrounds the backgrounds to set
	 */
	public void setBackgrounds(int[] backgrounds)
	{
		this.backgrounds = backgrounds;
	}
}
