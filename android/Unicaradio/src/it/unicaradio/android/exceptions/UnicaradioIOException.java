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
package it.unicaradio.android.exceptions;

import java.io.IOException;

/**
 * @author Paolo Cortis
 */
public class UnicaradioIOException extends IOException
{

	private static final long serialVersionUID = -537128066300460211L;

	public UnicaradioIOException()
	{
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public UnicaradioIOException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * @param detailMessage
	 */
	public UnicaradioIOException(String detailMessage)
	{
		super(detailMessage);
	}

	/**
	 * @param cause
	 */
	public UnicaradioIOException(Throwable cause)
	{
		super(cause);
	}
}
