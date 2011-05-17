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


/**
 * @author Paolo Cortis
 * 
 */
public interface IBuffer
{
	/**
	 * Add a single element of type {@code byte}
	 * 
	 * @param element
	 *            the new element
	 */
	public void add(byte element);

	/**
	 * Adds more than one element of type {@code byte[]} to the buffer
	 * 
	 * @param elements
	 *            new elements
	 */
	public void add(byte[] elements);

	/**
	 * Add a single element of type {@code short}
	 * 
	 * @param element
	 *            the new element
	 */

	public void add(short element);

	/**
	 * Add a single element of type {@code int}
	 * 
	 * @param element
	 *            the new element
	 */

	public void add(int element);

	/**
	 * Gets a byte from the top of the buffer
	 * 
	 * @return a byte from the buffer
	 */
	public byte get();

	/**
	 * Gets specified number of bytes from the top of the buffer
	 * 
	 * @param size
	 *            number of bytes to return
	 * @return if {@code size} is less than buffer size will return specified
	 *         bytes, otherwise the entire buffer
	 */
	public byte[] get(int size);

	/**
	 * Gets the size of the buffer
	 * 
	 * @return the size of the buffer
	 */
	public int size();
}
