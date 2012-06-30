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
package it.unicaradio.android.models;

/**
 * @author Paolo Cortis
 */
public class Website
{
	private String description;

	private String url;

	private int logo;

	/**
	 * @param description
	 * @param url
	 * @param logo
	 */
	public Website(String description, String url, int logo)
	{
		this.description = description;
		this.url = url;
		this.logo = logo;
	}

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * @return the url
	 */
	public String getUrl()
	{
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url)
	{
		this.url = url;
	}

	/**
	 * @return the logo
	 */
	public int getLogo()
	{
		return logo;
	}

	/**
	 * @param logo the logo to set
	 */
	public void setLogo(int logo)
	{
		this.logo = logo;
	}
}
