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
package it.unicaradio.android.streaming.streamers;

import it.unicaradio.android.streaming.buffer.Bufferable;
import it.unicaradio.android.streaming.events.OnInfoListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Paolo Cortis
 * 
 */
public abstract class Streamable extends Bufferable
{
	private final List<OnInfoListener> onInfolistenerList;

	public Streamable()
	{
		onInfolistenerList = new ArrayList<OnInfoListener>();
	}

	public void addOnInfoListener(OnInfoListener listener)
	{
		onInfolistenerList.add(listener);
	}

	public void removeOnInfoListener(OnInfoListener listener)
	{
		onInfolistenerList.remove(listener);
	}

	protected void fireOnInfoEvent(String infos[])
	{
		for(OnInfoListener listener : onInfolistenerList) {
			listener.onInfo(infos);
		}
	}

	public abstract void startStreaming();

	public abstract void stopStreaming();
}
