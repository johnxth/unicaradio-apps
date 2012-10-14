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
package it.unicaradio.android.gui;

import it.unicaradio.android.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * @author Paolo Cortis
 */
public class Tab extends LinearLayout
{
	private Context context;

	private OnTabSelectedListener listener;

	private int type;

	public Tab(Context context)
	{
		super(context);
		this.context = context;

		type = 0;

		init();
	}

	public Tab(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.context = context;

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Tab);
		type = a.getInteger(R.styleable.Tab_type, 0);
		a.recycle();

		init();
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public Tab(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		this.context = context;

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Tab,
				defStyle, 0);
		type = a.getInteger(R.styleable.Tab_type, 0);
		a.recycle();

		init();
	}

	public void setOnTabSelectedListener(OnTabSelectedListener listener)
	{
		this.listener = listener;
	}

	/**
	 * @return the type
	 */
	public int getType()
	{
		return type;
	}

	private void init()
	{
		int padding = convertDpToPixel(10);
		setPadding(padding, padding, padding, padding);

		setGravity(Gravity.CENTER);
		setClickable(true);
		setOnClickListener(new OnClickListener() {
			public void onClick(View view)
			{
				ViewGroup parent = (ViewGroup) view.getParent();
				for(int i = 0; i < parent.getChildCount(); i++) {
					parent.getChildAt(i).setSelected(false);
				}
				setSelected(true);

				if(listener != null) {
					listener.onTabSelected(Tab.this);
				}
			}
		});
	}

	public void select()
	{
		ViewGroup parent = (ViewGroup) getParent();
		for(int i = 0; i < parent.getChildCount(); i++) {
			parent.getChildAt(i).setSelected(false);
		}

		setSelected(true);
	}

	private int convertDpToPixel(int dp)
	{
		float scale = context.getResources().getDisplayMetrics().density;

		return (int) (dp * scale + 0.5f);
	}

	public interface OnTabSelectedListener
	{
		public void onTabSelected(Tab tab);
	}
}
