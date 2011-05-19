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
package it.unicaradio.android.streaming.buffer;

import it.unicaradio.android.streaming.events.OnBufferReadyListener;
import it.unicaradio.android.streaming.events.OnNewDataListener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Paolo Cortis
 * 
 */
public abstract class Bufferable
{
	protected IBuffer buffer;

	public byte[] get(int size)
	{
		return buffer.get(size);
	}

	public InputStream getInputStream()
	{
		return buffer.getInputStream();
	}

	private final List<OnBufferReadyListener> onBufferReadylistenerList;

	private final List<OnNewDataListener> onNewDataListenerList;

	public Bufferable()
	{
		onBufferReadylistenerList = new ArrayList<OnBufferReadyListener>();
		onNewDataListenerList = new ArrayList<OnNewDataListener>();
	}

	public void addBufferReadyListener(OnBufferReadyListener listener)
	{
		onBufferReadylistenerList.add(listener);
	}

	public void removeOnInfoListener(OnBufferReadyListener listener)
	{
		onBufferReadylistenerList.remove(listener);
	}

	protected void fireOnBufferReadyEvent()
	{
		for(OnBufferReadyListener listener : onBufferReadylistenerList) {
			listener.onBufferReady();
		}
	}

	public void addOnNewDataListener(OnNewDataListener listener)
	{
		onNewDataListenerList.add(listener);
	}

	public void removeOnNewDataListener(OnNewDataListener listener)
	{
		onNewDataListenerList.remove(listener);
	}

	protected void fireOnNewDataEvent()
	{
		for(OnNewDataListener listener : onNewDataListenerList) {
			listener.onNewData();
		}
	}
}
