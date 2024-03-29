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
package it.unicaradio.android.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;

/**
 * @author Paolo Cortis
 */
public class ViewUtils
{
	private static Typeface robotoTypeFace;

	private ViewUtils()
	{
	}

	public static void setRobotoFont(Context context, View view)
	{
		if(robotoTypeFace == null) {
			robotoTypeFace = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto/Roboto-Regular.ttf");
		}
		setFont(view, robotoTypeFace);
	}

	public static void setupActionBar(ActionBar actionBar, Resources resources)
	{
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE
				| ActionBar.DISPLAY_USE_LOGO);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);

		// if(Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
		// BitmapDrawable bg = (BitmapDrawable) resources
		// .getDrawable(R.drawable.title_bg);
		// bg.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
		// actionBar.setBackgroundDrawable(bg);
		//
		// BitmapDrawable bgSplit = (BitmapDrawable) resources
		// .getDrawable(R.drawable.black_stripe);
		// bgSplit.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
		// actionBar.setSplitBackgroundDrawable(bgSplit);
		// }
	}

	private static void setFont(View view, Typeface robotoTypeFace)
	{
		if(view instanceof ViewGroup) {
			for(int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				setFont(((ViewGroup) view).getChildAt(i), robotoTypeFace);
			}
		} else if(view instanceof TextView) {
			((TextView) view).setTypeface(robotoTypeFace);
		}
	}
}
