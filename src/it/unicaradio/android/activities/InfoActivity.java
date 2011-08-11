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
package it.unicaradio.android.activities;

import it.unicaradio.android.R;
import it.unicaradio.android.gui.Tabs;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.TextView;

/**
 * @author Paolo Cortis
 * 
 */
public class InfoActivity extends TabbedActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.d(InfoActivity.class.getName(), "Called Infoactivity");
		super.onCreate(savedInstanceState, R.layout.infos);
	}

	@Override
	protected void setupTab()
	{
		TextView infosText = (TextView) findViewById(R.id.infosText);
		infosText.setText(Html.fromHtml(getString(R.string.infos)));
		infosText.setMovementMethod(LinkMovementMethod.getInstance());
	}

	@Override
	protected void setupListeners()
	{
	}

	@Override
	public int getTab()
	{
		return Tabs.INFO;
	}
}
