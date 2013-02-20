package echelonfour.quantumdice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class RandomGrabber {
	private static String TAG = "echelonfour.quantumdice.random";
	private HttpClient httpClient;
	private Random rand = new Random();

	public RandomGrabber() {
		httpClient = new DefaultHttpClient();
	}
	
	public void deinitilise() {
		httpClient.getConnectionManager().shutdown();
		httpClient = null;
	}
	public int getRandomBound(int n) {
		
		return rand.nextInt(n);
	}

	public void reseed() throws QuantumException {
		long seed = getQuantumRandomLong();
		if (seed < 0) {
			throw new QuantumException();
		}
		rand.setSeed(seed);
	}
	public long getQuantumRandomLong() {
		String rawJson;
		try {
			HttpGet get = new HttpGet(
					"http://qrng.anu.edu.au/API/jsonI.php?length=1&type=uint16");
			HttpResponse response = httpClient.execute(get);
			rawJson = flushStream(response.getEntity().getContent());
			JSONObject json = new JSONObject(rawJson);
			return json.getJSONArray("data").getLong(0);
		} catch (ClientProtocolException e) {
			Log.e(TAG, "This should never happen", e);
			return -1;
		} catch (IOException e) {
			Log.e(TAG, "HTTP Request error", e);
			return -1;
		} catch (JSONException e) {
			Log.e(TAG, "JSON Parse exception", e);
			return -1;
		}

	}

	private String flushStream(InputStream is) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			return "";
		} catch (IOException e) {
			return "";
		} finally {
			try {
				is.close();
			} catch (IOException e) {}
		}
	}
}
