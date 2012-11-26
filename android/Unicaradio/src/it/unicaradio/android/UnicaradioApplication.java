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
package it.unicaradio.android;

import static org.acra.ReportField.ANDROID_VERSION;
import static org.acra.ReportField.APP_VERSION_CODE;
import static org.acra.ReportField.APP_VERSION_NAME;
import static org.acra.ReportField.BRAND;
import static org.acra.ReportField.CRASH_CONFIGURATION;
import static org.acra.ReportField.DEVICE_FEATURES;
import static org.acra.ReportField.DISPLAY;
import static org.acra.ReportField.INITIAL_CONFIGURATION;
import static org.acra.ReportField.PHONE_MODEL;
import static org.acra.ReportField.PRODUCT;
import static org.acra.ReportField.REPORT_ID;
import static org.acra.ReportField.SHARED_PREFERENCES;
import static org.acra.ReportField.STACK_TRACE;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

/**
 * @author Paolo Cortis
 * 
 */
@ReportsCrashes(formKey = "dHpBTGtCV0RoZzdMRzV4aENjMkNMb0E6MQ", customReportContent = {
		REPORT_ID, APP_VERSION_CODE, APP_VERSION_NAME, PHONE_MODEL, BRAND,
		PRODUCT, ANDROID_VERSION, STACK_TRACE, INITIAL_CONFIGURATION,
		CRASH_CONFIGURATION, DISPLAY, DEVICE_FEATURES, SHARED_PREFERENCES})
public class UnicaradioApplication extends Application
{
	@Override
	public void onCreate()
	{
		super.onCreate();

		// The following line triggers the initialization of ACRA
		ACRA.init(this);
	}
}
