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
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * @author Paolo Cortis
 * 
 */
public class SongRequestActivity extends TabbedActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.d(SongRequestActivity.class.getName(), "Called SongRequestActivity");
		super.onCreate(savedInstanceState, R.layout.songs);
	}

	@Override
	protected void setupTab()
	{
	}

	@Override
	protected void setupListeners()
	{
		View songButton = findViewById(R.id.songButton);
		songButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v)
			{
				TextView author = (TextView) findViewById(R.id.songsAuthor);
				TextView title = (TextView) findViewById(R.id.songsTitle);
				final Intent emailIntent = new Intent(
						android.content.Intent.ACTION_SEND);

				emailIntent.setType("plain/text");
				emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
						new String[] {"diretta@unicaradio.it"});
				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
						author.getText().toString() + "*"
								+ title.getText().toString());
				emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
						"Sent from Android APP");

				AlertDialog.Builder adb = new AlertDialog.Builder(
						SongRequestActivity.this);
				adb.setTitle("Invio richiesta canzone...");
				adb.setMessage("Attenzione!! Non modificare l'e-mail!");
				adb.setPositiveButton("Ok", null);
				adb.show();

				startActivity(Intent.createChooser(emailIntent, "Send mail..."));
			}
		});
	}

	@Override
	public int getTab()
	{
		return Tabs.SONG;
	}
}
