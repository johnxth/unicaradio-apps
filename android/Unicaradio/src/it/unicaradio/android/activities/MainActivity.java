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
import it.unicaradio.android.fragments.UnicaradioFragment;
import it.unicaradio.android.gcm.GcmServerRegister;
import it.unicaradio.android.gcm.GcmServerRpcCall;
import it.unicaradio.android.gui.Tab;
import it.unicaradio.android.gui.Tabs;
import it.unicaradio.android.gui.TrackInfos;
import it.unicaradio.android.listeners.TabSelectedListener;
import it.unicaradio.android.services.GCMIntentService;
import it.unicaradio.android.utils.IntentUtils;
import it.unicaradio.android.utils.StringUtils;
import it.unicaradio.android.utils.UnicaradioPreferences;
import it.unicaradio.android.utils.ViewUtils;

import java.text.MessageFormat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.google.android.gcm.GCMRegistrar;
import com.winsontan520.wversionmanager.library.WVersionManager;

/**
 * @author Paolo Cortis
 */
public class MainActivity extends SherlockFragmentActivity
{
	private static final String TAG = MainActivity.class.getSimpleName();

	public static final String SENDER_ID = "698508099376";

	private static TabSelectedListener tabSelectedListener;

	private SharedPreferences preferences;

	private AsyncTask<Void, Void, Void> mRegisterTask;

	private ShareActionProvider shareActionProvider;

	public static Context activityContext;

	public static MainActivity activity;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);

		initGcm();
		initView();

		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		preferences = getPreferences(Context.MODE_PRIVATE);
		if(isFirstRun()) {
			PreferenceManager.setDefaultValues(this, R.xml.prefs, true);
		}
		if(hasBeenUpdated()) {
			showUpdatesDialog();
		}

		setupUpdatesChecker();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onResume()
	{
		super.onResume();

		activityContext = this;
		activity = this;

		Intent intent = getIntent();
		if(StringUtils.equals(intent.getAction(), GCMIntentService.ACTION_GCM_MESSAGE) && intent.hasExtra("text")) {
			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
			dialogBuilder.setTitle("Hai ricevuto un nuovo messaggio!");
			dialogBuilder.setMessage(intent.getStringExtra("text"));
			dialogBuilder.setPositiveButton(android.R.string.ok, null);
			dialogBuilder.show();
			getIntent().removeExtra("text");
		}
	}

	private void initGcm()
	{
		try {
			GCMRegistrar.checkDevice(this);
		} catch(UnsupportedOperationException e) {
			Log.v(TAG, "GCM is not supported on this device :(");
			return;
		}

		GCMRegistrar.checkManifest(this);

		String registrationId = GCMRegistrar.getRegistrationId(this);
		if(StringUtils.isEmpty(registrationId)) {
			GCMRegistrar.register(this, SENDER_ID);
		} else if(!GCMRegistrar.isRegisteredOnServer(this)) {
			registerOnServer(registrationId);
		}
	}

	private void registerOnServer(final String regId)
	{
		final Context context = this;
		mRegisterTask = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params)
			{
				GcmServerRpcCall rpcCall = new GcmServerRegister(context);
				boolean registered = rpcCall.execute(regId, null);

				if(!registered) {
					GCMRegistrar.unregister(context);
				}

				return null;
			}

			@Override
			protected void onPostExecute(Void result)
			{
				mRegisterTask = null;
			}

		};
		mRegisterTask.execute(null, null, null);
	}

	private void initView()
	{
		decideAppOrientation();

		initViewPager();

		ViewUtils.setupActionBar(getSupportActionBar(), getResources());
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);

		initTabs();
	}

	private void decideAppOrientation()
	{
		if(getResources().getBoolean(R.bool.isTablet)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		}
	}

	private void initViewPager()
	{
		ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
		if(tabSelectedListener == null) {
			tabSelectedListener = new TabSelectedListener(this, getSupportFragmentManager(), viewPager);
		} else {
			tabSelectedListener.setFragmentManager(getSupportFragmentManager());
			tabSelectedListener.setViewPager(viewPager);
			tabSelectedListener.setContext(this);
			tabSelectedListener.init();
		}
	}

	private void initTabs()
	{
		Tabs tabsContainer = (Tabs) findViewById(R.id.tabs);
		SparseArray<Tab> tabs = Tabs.getTabs(tabsContainer);
		tabs.get(TabSelectedListener.getCurrentTab()).setSelected(true);
		setupListeners(tabs);
	}

	private void setupListeners(SparseArray<Tab> tabs)
	{
		for(int i = 0; i < tabs.size(); i++) {
			Tab viewTab = tabs.valueAt(i);

			viewTab.setOnTabSelectedListener(tabSelectedListener);
		}
	}

	private boolean isFirstRun()
	{
		long lastRunVersionCode = preferences.getLong(UnicaradioPreferences.PREF_LASTRUNVERSIONCODE, 0);

		return(lastRunVersionCode == 0);
	}

	private boolean hasBeenUpdated()
	{
		try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
			long lastRunVersionCode = preferences.getLong(UnicaradioPreferences.PREF_LASTRUNVERSIONCODE, 0);
			if(lastRunVersionCode < pInfo.versionCode) {
				Editor editor = preferences.edit();
				editor.putLong("lastRunVersionCode", pInfo.versionCode);
				editor.commit();

				return(lastRunVersionCode > 0);
			}
		} catch(NameNotFoundException e) {
			e.printStackTrace();
		}

		return false;
	}

	private void showUpdatesDialog()
	{
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.popup);
		dialog.setTitle(R.string.application_updated);
		dialog.setCancelable(true);

		TextView textView = (TextView) dialog.findViewById(R.id.updatesText);
		textView.setText(R.string.updates);

		Button button = (Button) dialog.findViewById(R.id.updatesButton);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				dialog.hide();
			}
		});
		dialog.show();
	}

	private void setupUpdatesChecker()
	{
		WVersionManager versionManager = new WVersionManager(this);
		versionManager.setVersionContentUrl("http://www.unicaradio.it/regia/test/updates.php");
		versionManager.setIgnoreThisVersionLabel("Ignora");
		versionManager.setUpdateNowLabel("Aggiorna!");
		versionManager.setTitle("Aggiornamento disponibile");
		versionManager.setRemindMeLaterLabel("Più tardi");
		versionManager.checkVersion();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		UnicaradioFragment currentFragment = tabSelectedListener.getCurrentFragment();
		if(currentFragment == null) {
			Log.w(TAG, "MainActivity.onKeyDown(): currentFragment is NULL!");
			return super.onKeyDown(keyCode, event);
		}

		boolean result = currentFragment.onKeyDown(keyCode, event);
		return result || super.onKeyDown(keyCode, event);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		UnicaradioFragment currentFragment = tabSelectedListener.getCurrentFragment();
		if(currentFragment == null) {
			return super.onKeyUp(keyCode, event);
		}

		boolean result = currentFragment.onKeyUp(keyCode, event);
		return result || super.onKeyUp(keyCode, event);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event)
	{
		UnicaradioFragment currentFragment = tabSelectedListener.getCurrentFragment();
		if(currentFragment == null) {
			return super.onKeyMultiple(keyCode, repeatCount, event);
		}

		boolean result = currentFragment.onKeyMultiple(keyCode, repeatCount, event);
		return result || super.onKeyMultiple(keyCode, repeatCount, event);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event)
	{
		UnicaradioFragment currentFragment = tabSelectedListener.getCurrentFragment();
		if(currentFragment == null) {
			return super.onKeyLongPress(keyCode, event);
		}

		boolean result = currentFragment.onKeyLongPress(keyCode, event);
		return result || super.onKeyLongPress(keyCode, event);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBackPressed()
	{
		UnicaradioFragment currentFragment = tabSelectedListener.getCurrentFragment();
		if(currentFragment == null) {
			super.onBackPressed();
			return;
		}

		boolean backAlreadyHandled = currentFragment.onBackPressed();

		if(backAlreadyHandled) {
			return;
		}

		super.onBackPressed();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);

		getSupportMenuInflater().inflate(R.menu.shared_menu, menu);

		MenuItem item = menu.findItem(R.id.menu_item_share);
		shareActionProvider = (ShareActionProvider) item.getActionProvider();
		setDefaultSharingIntent();

		return true;
	}

	public void setDefaultSharingIntent()
	{
		Intent intent = IntentUtils.createIntentForSharing(this, getString(R.string.suggest_subject),
				getString(R.string.suggest_content));

		shareActionProvider.setShareIntent(intent);
	}

	public void setSharingIntentWithInfos(TrackInfos infos)
	{
		String content;
		if(StringUtils.isEmpty(infos.getTitle())) {
			content = MessageFormat.format(getText(R.string.suggest_content_only_author).toString(), infos.getAuthor());
		} else {
			content = MessageFormat.format(getText(R.string.suggest_content_song).toString(), infos.getTitle(),
					infos.getAuthor());
		}

		Intent intent = IntentUtils.createIntentForSharing(this, getString(R.string.suggest_subject), content);

		shareActionProvider.setShareIntent(intent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(item.getItemId() == R.id.unicaradio_settings) {
			Intent intent = new Intent(this, UnicaradioPreferencesActivity.class);
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				intent.putExtra(UnicaradioPreferencesActivity.EXTRA_NO_HEADERS, true);
				intent.putExtra(UnicaradioPreferencesActivity.EXTRA_SHOW_FRAGMENT,
						UnicaradioPreferencesFragment.class.getName());
			}

			startActivity(intent);
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy()
	{
		if(mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}

		try {
			GCMRegistrar.onDestroy(this);
		} catch(Exception e) {
			// do nothing
		}

		super.onDestroy();
	}
}
