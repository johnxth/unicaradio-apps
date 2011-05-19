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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author Paolo Cortis
 * 
 */
public class ByteArrayBuffer implements IBuffer
{
	private static final int BUF_SIZE = 512000;

	private byte[] buffer;

	private int count = 0;

	private int size = 0;

	/**
	 * Creates a buffer of 500 KB
	 */
	public ByteArrayBuffer()
	{
		this(BUF_SIZE);
	}

	/**
	 * Creates a buffer of the specified size
	 * 
	 * @param size
	 *            the size of the buffer in bytes
	 */
	public ByteArrayBuffer(int size)
	{
		this.size = size;
		buffer = new byte[size];
	}

	/**
	 * {@inheritDoc}
	 */
	public void add(byte element)
	{
		synchronized(this) {
			if(count < BUF_SIZE) {
				buffer[count++] = element;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void add(byte[] elements)
	{
		synchronized(this) {
			for(byte element : elements) {
				if(count < BUF_SIZE) {
					buffer[count++] = element;
				} else {
					return;
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void add(short element)
	{
		add((byte) element);
	}

	/**
	 * {@inheritDoc}
	 */
	public void add(int element)
	{
		add((byte) element);
	}

	/**
	 * {@inheritDoc}
	 */
	public byte get()
	{
		synchronized(this) {
			byte value = buffer[0];
			for(int i = 1; i < buffer.length; i++) {
				buffer[i - 1] = buffer[i];
			}

			count--;
			return value;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public byte[] get(int size)
	{
		synchronized(this) {
			if(buffer.length > size) {
				byte[] tempBuffer = new byte[size];
				for(int i = 0; i < size; i++) {
					tempBuffer[i] = buffer[i];
				}

				for(int i = size; i < buffer.length; i++) {
					buffer[i - size] = buffer[i];
					count--;
				}
				return tempBuffer;
			} else {
				byte[] oldBuffer = buffer.clone();
				count = 0;
				buffer = new byte[this.size];
				return oldBuffer;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public InputStream getInputStream()
	{
		InputStream stream = new ByteArrayInputStream(buffer);
		buffer = new byte[this.size];
		count = 0;

		return stream;
	}

	/**
	 * {@inheritDoc}
	 */
	public int size()
	{
		return buffer.length;
	}
}
