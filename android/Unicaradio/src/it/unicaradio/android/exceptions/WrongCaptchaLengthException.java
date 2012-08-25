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
public class WrongCaptchaLengthException extends CaptchaException
{
	private static final long serialVersionUID = -5596105391404903728L;

	public WrongCaptchaLengthException()
	{
		super();
	}

	/**
	 * @param detailMessage
	 * @param throwable
	 */
	public WrongCaptchaLengthException(String detailMessage, Throwable throwable)
	{
		super(detailMessage, throwable);
	}

	/**
	 * @param detailMessage
	 */
	public WrongCaptchaLengthException(String detailMessage)
	{
		super(detailMessage);
	}

	/**
	 * @param throwable
	 */
	public WrongCaptchaLengthException(Throwable throwable)
	{
		super(throwable);
	}
}
