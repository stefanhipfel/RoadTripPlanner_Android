package de.tiidim.roadtrips;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * activity that features the upload of an existing trip to the web server back
 * end, running in a background task.
 * 
 * @author stefanhipfel
 * 
 */
public class TripUpload extends AsyncTask<Context, Void, HttpResponse> {

	// for config use
	private static String user;
	private static String pw;
	private static String ip;
	private static String url;
	
	private Context context;
	private Long trip_id;
	
	/**
	 * const
	 * @param context
	 * @param trip_id
	 */
	public TripUpload(Context context, Long trip_id){
		this.context = context;
		this.trip_id = trip_id;
	}
	
	/**
	 * background thread computation, that uploads the data to the server
	 */
	@Override
	protected HttpResponse doInBackground(Context... params) {
		HttpResponse response = null;
		try {
			response = doPost(params[0]);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		return response;
	}

	/**
	 * triggers the POST request
	 * 
	 * @param kvPairs
	 * @param context
	 *            the given context (typically of the calling view)
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws JSONException
	 */
	public HttpResponse doPost(Context context)
			throws ClientProtocolException, IOException, JSONException {
		
		readPreferences(context);
		//Log.i("trip", tripID + "");
		JSONObject data = new JSONObject();
		JSONObject trip = new JSONObject();
		JSONObject userData = new JSONObject();
		
		TripList.mDbHelper.open();
		Cursor c = TripRouting.fetchTripLocations(trip_id);

		c.moveToFirst();
		Log.i("size", c.getCount() + "");

		int i = 1;

		if (c.getCount() > 1) {
			while (i <= c.getCount()) {
				
				trip.put("name", c.getString(6));
				trip.put("remark", c.getInt(7));
				trip.put("latitude", c.getDouble(8));
				trip.put("longitude", c.getDouble(9));
				
				data.put("locations"+ i, trip);
				
				c.move(1);
				i++;
			}
		}
		
		userData.put("password", pw);
		userData.put("user", user);

		
		Map<String, String> datasets = new HashMap<String, String>();
		datasets.put("trip", data.toString());
		datasets.put("userData", userData.toString());
		

		HttpClient httpclient = new DefaultHttpClient();
		
		HttpPost httppost = createHttpPost(datasets);
		
		HttpResponse response;
		response = httpclient.execute(httppost);
		Log.i("http", response.toString());
		
		
		return response;
		

	}
	/**
	 * Assemble request for pushing a JSON object to the server
	 * @param pairs
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private HttpPost createHttpPost(Map<String, String> pairs)
			throws UnsupportedEncodingException {
		HttpPost httppost = new HttpPost(url);
		
		if (pairs != null && pairs.isEmpty() == false) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
					pairs.size());
			String k, v;
			Iterator<String> itKeys = pairs.keySet().iterator();
			while (itKeys.hasNext()) {
				k = itKeys.next();
				v = pairs.get(k);
				nameValuePairs.add(new BasicNameValuePair(k, v));
			}
			Log.i("pairs", nameValuePairs.toString());
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		}
		
		return httppost;
	}

	/**
	 * gets the preferences by using the previously defined user login
	 */
	public void readPreferences(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				"de.tiidim.roadtrips_preferences", 0);
		ip = sp.getString("serveradress", "");
		user = sp.getString("username", "test");
		pw = sp.getString("password", "test");
		
		url = "http://" + ip;

	}

	/**
	 * Invoked on the UI thread! after the background computation finishes, to be able to access UI elements.
	 * HttpResponse is passed to this step from the background thread.
	 * 
	 * @param response
	 */
	 protected void onPostExecute(HttpResponse response) {
		 String temp;
		if (response != null) {
		try {
			temp = EntityUtils.toString(response.getEntity());
			Log.i("Apache", temp);
			if (temp.matches("SUCCESS")) {
				Toast.makeText(context, R.string.transfer_completed, Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(context, R.string.transfer_error, Toast.LENGTH_LONG).show();
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}else{
			Toast.makeText(context, R.string.transfer_server_er, Toast.LENGTH_LONG).show();
		}
	      
		}


}