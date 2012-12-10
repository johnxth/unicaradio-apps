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
package it.unicaradio.android.gcm;

import it.unicaradio.android.utils.StringUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;
import java.util.Random;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.util.Log;

/**
 * @author Paolo Cortis
 */
public abstract class GcmServerRpcCall
{
	private static final String TAG = GcmServerRpcCall.class.getSimpleName();

	private static final String SERVER_URL = "http://unicaradio.slack-counter.org/server/engine.php";

	private static final int MAX_ATTEMPTS = 5;

	private static final int BACKOFF_MILLI_SECONDS = 2000;

	private long backoff;

	private boolean stopTrying;

	protected Context context;

	public GcmServerRpcCall(Context context)
	{
		this.context = context;
		this.stopTrying = false;
	}

	protected abstract void internalExecute(String request,
			Map<String, String> params) throws GcmException;

	protected abstract String getMethod();

	public boolean execute(String registrationId, Map<String, String> params)
	{
		Log.i(TAG, "registering device (regId = " + registrationId + ")");
		String request = generateRpcJSON(getMethod(), registrationId);

		Random random = new Random();
		backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);

		for(int i = 1; i <= MAX_ATTEMPTS && stopTrying == false; i++) {
			Log.d(TAG, "Attempt #" + i + " to register");
			try {
				internalExecute(request, params);
				return true;
			} catch(GcmException e) {
				Log.e(TAG, "Failed to register on attempt " + i, e);

				if(i == MAX_ATTEMPTS) {
					break;
				}

				waitBeforeRetry();
				backoff *= 2;
			}
		}

		return false;
	}

	protected String generateRpcJSON(String method, String regId)
	{
		return "{ \"action\": \"" + method + "\", \"params\": { \"regId\": \""
				+ regId + "\" } }";
	}

	private void waitBeforeRetry()
	{
		try {
			Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
			Thread.sleep(backoff);
		} catch(InterruptedException e) {
			Log.d(TAG, "Thread interrupted: abort remaining retries!");
			Thread.currentThread().interrupt();

			stopTrying = true;
		}
	}

	/**
	 * Issue a POST request to the server.
	 * 
	 * @param params request parameters.
	 * 
	 * @throws IOException propagated from POST.
	 */
	protected String post(String request) throws IOException
	{
		URL url = convertAddressToURL();

		byte[] bytes = request.getBytes();

		HttpURLConnection conn = null;
		try {
			conn = setupConnection(url, bytes);
			postRequest(conn, bytes);
			checkHttpStatus(conn);

			return getResult(conn);
		} catch(Exception e) {
			return StringUtils.EMPTY;
		} finally {
			if(conn != null) {
				conn.disconnect();
			}
		}
	}

	/**
	 * @param url
	 * @return
	 */
	private static URL convertAddressToURL()
	{
		try {
			URL url = new URL(SERVER_URL);

			return url;
		} catch(MalformedURLException e) {
			throw new IllegalArgumentException("invalid url: " + SERVER_URL);
		}
	}

	/**
	 * @param url
	 * @param bytes
	 * @return
	 * @throws IOException
	 * @throws ProtocolException
	 */
	private static HttpURLConnection setupConnection(URL url, byte[] bytes)
			throws IOException, ProtocolException
	{
		HttpURLConnection conn;
		conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setFixedLengthStreamingMode(bytes.length);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type",
				"application/json;charset=UTF-8");
		return conn;
	}

	/**
	 * @param bytes
	 * @param conn
	 * @throws IOException
	 */
	private static void postRequest(HttpURLConnection conn, byte[] bytes)
			throws IOException
	{
		OutputStream out = conn.getOutputStream();
		out.write(bytes);
		out.close();
	}

	/**
	 * @param conn
	 * @throws IOException
	 */
	private void checkHttpStatus(HttpURLConnection conn) throws IOException
	{
		// handle the response
		int status = conn.getResponseCode();
		if(status != 200) {
			throw new IOException("Post failed with error code " + status);
		}
	}

	/**
	 * @param conn
	 * @throws IOException
	 */
	private String getResult(HttpURLConnection conn) throws IOException
	{
		InputStream is = conn.getInputStream();
		BufferedInputStream bis = new BufferedInputStream(is);

		// Read bytes to the Buffer until there is nothing more to read(-1).
		ByteArrayBuffer baf = new ByteArrayBuffer(50);
		int current = 0;
		while((current = bis.read()) != -1) {
			baf.append((byte) current);
		}

		String result = new String(baf.toByteArray());
		Log.d(TAG, result);

		return result;
	}
}
