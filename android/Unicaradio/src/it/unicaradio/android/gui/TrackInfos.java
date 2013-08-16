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

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * @author Paolo Cortis
 */
public class TrackInfos
{
	private String author;

	private String title;

	private Bitmap cover;

	private Context context;

	private boolean cleaned;

	public TrackInfos()
	{
		clean();
	}

	public TrackInfos(Context context)
	{
		this();
		this.context = context;
		clean();
	}

	public void clean()
	{
		setAuthor(null);
		setTitle(null);
		setCover(null);
		cleaned = true;
	}

	public void setTrackInfos(TrackInfos infos)
	{
		synchronized(this) {
			setAuthor(infos.getAuthor());
			setTitle(infos.getTitle());
			setCover(infos.getCover());
			cleaned = false;
		}
	}

	public String getAuthor()
	{
		synchronized(this) {
			return author;
		}
	}

	public void setAuthor(String author)
	{
		synchronized(this) {
			if(author != null) {
				this.author = author;
				cleaned = false;
			} else {
				this.author = "UnicaRadio";
			}
		}
	}

	public String getTitle()
	{
		synchronized(this) {
			return title;
		}
	}

	public void setTitle(String title)
	{
		synchronized(this) {
			if(title != null) {
				this.title = title;
				cleaned = false;
			} else {
				this.title = "";
			}
		}
	}

	public Bitmap getCover()
	{
		synchronized(this) {
			return cover;
		}
	}

	public void setCover(Bitmap cover)
	{
		synchronized(this) {
			if(cover != null) {
				this.cover = cover;
				cleaned = false;
			} else if(context != null) {
				this.cover = getDefaultCover();
			}
		}
	}

	private Bitmap getDefaultCover()
	{
		if(isXmasTime()) {
			return BitmapFactory.decodeResource(context.getResources(),
					R.drawable.cover_xmas);
		}

		return BitmapFactory.decodeResource(context.getResources(),
				R.drawable.cover);
	}

	private boolean isXmasTime()
	{
		Calendar today = GregorianCalendar.getInstance();
		int thisMonth = today.get(Calendar.MONTH);
		int day = today.get(Calendar.DAY_OF_MONTH);
		if(thisMonth == Calendar.DECEMBER && day >= 8) {
			return true;
		} else if(thisMonth == Calendar.JANUARY && day <= 6) {
			return true;
		}

		return false;
	}

	public boolean isClean()
	{
		return cleaned;
	}
}
