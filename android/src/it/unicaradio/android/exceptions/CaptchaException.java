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

/**
 * @author Paolo Cortis
 */
public class CaptchaException extends UnicaradioException
{
	private static final long serialVersionUID = 2664533172459927776L;

	public CaptchaException()
	{
		super();
	}

	/**
	 * @param detailMessage
	 * @param throwable
	 */
	public CaptchaException(String detailMessage, Throwable throwable)
	{
		super(detailMessage, throwable);
	}

	/**
	 * @param detailMessage
	 */
	public CaptchaException(String detailMessage)
	{
		super(detailMessage);
	}

	/**
	 * @param throwable
	 */
	public CaptchaException(Throwable throwable)
	{
		super(throwable);
	}
}
