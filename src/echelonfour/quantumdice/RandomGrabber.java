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
import org.json.*;

import android.net.http.AndroidHttpClient;
import android.util.Log;

public class RandomGrabber {
	private static String TAG = "echelonfour.quantumdice.random";
	static HttpClient httpClient = AndroidHttpClient.newInstance(null);
	static Random rand = new Random();

	public static int getRandomBound(int n) throws QuantumException {
		long seed = getQuantumRandomLong();
		if (seed < 0) {
			throw new QuantumException();
		}
		rand.setSeed(seed);
		return rand.nextInt(n);
	}

	public static long getQuantumRandomLong() {
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

	public static String flushStream(InputStream is) {
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
		}
	}
}
