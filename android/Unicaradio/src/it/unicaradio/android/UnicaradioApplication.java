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
import static org.acra.ReportField.PHONE_MODEL;
import static org.acra.ReportField.PRODUCT;
import static org.acra.ReportField.REPORT_ID;
import static org.acra.ReportField.STACK_TRACE;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

/**
 * @author Paolo Cortis
 */
@ReportsCrashes(formKey = "", formUri = "http://unicaradio.slack-counter.org/submit.php", customReportContent = {
		REPORT_ID, APP_VERSION_CODE, APP_VERSION_NAME, PHONE_MODEL, BRAND,
		PRODUCT, ANDROID_VERSION, STACK_TRACE})
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
