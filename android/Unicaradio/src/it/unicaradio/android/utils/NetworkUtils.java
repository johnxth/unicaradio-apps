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
package it.unicaradio.android.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author paolo.cortis
 */
public class NetworkUtils
{
	public static boolean isConnected(Context context)
	{
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		for(NetworkInfo info : connectivityManager.getAllNetworkInfo()) {
			if(info.isConnected()) {
				return true;
			}
		}

		return false;
	}

	public static boolean isConnectedToWifi(Context context)
	{
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if(info == null) {
			return false;
		}

		return info.isConnected();
	}

	public static boolean isConnectedToMobileData(Context context)
	{
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if(info == null) {
			return false;
		}

		return info.isConnected();
	}

	public static byte[] httpGet(String urlString) throws IOException
	{
		URL url = new URL(urlString);

		URLConnection ucon = url.openConnection();

		InputStream is = ucon.getInputStream();
		BufferedInputStream bis = new BufferedInputStream(is);

		// Read bytes to the Buffer until there is nothing more to read(-1).
		ByteArrayBuffer baf = new ByteArrayBuffer(50);
		int current = 0;
		while((current = bis.read()) != -1) {
			baf.append((byte) current);
		}

		return baf.toByteArray();
	}

	public static byte[] httpPost(String url, byte[] postData,
			String contentType) throws ClientProtocolException, IOException,
			HttpException
	{
		HttpParams httpParams = new BasicHttpParams();

		HttpPost post = new HttpPost(url);
		post.setEntity(new ByteArrayEntity(postData));
		post.addHeader("Content-Type", contentType);

		HttpClient client = new DefaultHttpClient(httpParams);
		HttpResponse response = client.execute(post);

		HttpEntity httpEntity = response.getEntity();
		if(httpEntity == null) {
			throw new HttpException("No answer from server.");
		}

		return EntityUtils.toByteArray(httpEntity);
	}
}
