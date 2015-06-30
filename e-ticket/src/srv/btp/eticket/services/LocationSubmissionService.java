package srv.btp.eticket.services;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import srv.btp.eticket.FormObjectTransfer;
import srv.btp.eticket.R;
import srv.btp.eticket.crud.CRUD_Route_Table;
import srv.btp.eticket.crud.CRUD_Transaction_Queue;
import srv.btp.eticket.crud.Datafield_Route;
import android.annotation.SuppressLint;
import android.preference.PreferenceManager;
import android.util.Log;
import android.os.AsyncTask;

public class LocationSubmissionService extends AsyncTask<String, Integer, Boolean> {


	@Override
	protected Boolean doInBackground(String... values) {
		if (values.length != 3) {
			Log.v("LoactionSubmission : status", "invalid data length");
			return Boolean.FALSE;
		} else {
			Log.v("LoactionSubmission : status", "Valid data! Working...");
			String id_trayek, dateTime;
			id_trayek = dateTime = null;
			int id_bus = 0;
			//0 = ID bus
			//1 = long
			//2 = Lat
			
			//0
			if (values[0] != null) { //id_bus
				id_bus = Integer.parseInt(values[0]);
			}
			
			/*//1
			if (values[1] != null){ //ID Trayek
				id_trayek = values[1];
			}else{
				String arah = PreferenceManager.getDefaultSharedPreferences(
						FormObjectTransfer.main_activity.getBaseContext())
						.getString("trajectory_direction", "maju");
				int rt = Integer.parseInt(
						PreferenceManager.getDefaultSharedPreferences(
								FormObjectTransfer.main_activity.getBaseContext())
								.getString("route_list","-1"));
				if(!arah.equals("maju"))rt+=1;
				id_trayek = String.valueOf(rt);
			}*/
			
			//1 long
			float latitude, longitude; 
			if (values[1] != null) { //longitude
				longitude = Float.parseFloat(values[1]);
			} else {
				longitude = PreferenceManager.getDefaultSharedPreferences(
						FormObjectTransfer.main_activity.getBaseContext())
						.getFloat("long", 0);
			}
			//2 lat
			if (values[2] != null) { //latitude
				latitude = Float.parseFloat(values[2]);
			} else {
				latitude = PreferenceManager.getDefaultSharedPreferences(
						FormObjectTransfer.main_activity.getBaseContext())
						.getFloat("lat", 0);
			}

			/*//Date
			if (values[4] != null) { //date
				dateTime = values[4];
				Log.e("DATE","INSIDE QUEUESERVICE "+dateTime);
			}
			*/
			int status = postData(id_bus, longitude, latitude );
			Log.v("LoactionSubmission : status", status + "");
			if (status == 200)
				return true;
			else
			return false;
		
		}
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		Log.v("LoactionSubmission : progress status", values[0].toString());
	};

	@Override
	protected void onPostExecute(Boolean result) {
		Log.v("LoactionSubmission : result", result.toString());
	};

	@SuppressLint("DefaultLocale")
	private int postData(int id_bis,float longitude, float latitude) {
		
		//Log.e("DATE","INSIDE POSTDATA"+timeNow);
		
		String URLService = PreferenceManager.getDefaultSharedPreferences(
				FormObjectTransfer.main_activity.getApplicationContext())
				.getString(
						"service_address",
						FormObjectTransfer.main_activity.getResources()
								.getString(R.string.default_service));
		String table_name = "raw_track_data"; //nama table yg mau di update
		String target_post = URLService 
				+ FormObjectTransfer.main_activity.getResources().getString(R.string.extension_service)
				+ table_name;
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(target_post);
		int code = -1;
		List<NameValuePair> nameValuePairs = null;

		try {
			// Add your data
			nameValuePairs = new ArrayList<NameValuePair>(2);

			nameValuePairs.add(new BasicNameValuePair("idUser", FormObjectTransfer.UserID + "")); //userID
			nameValuePairs.add(new BasicNameValuePair("idBus", id_bis + "")); //Bis
			nameValuePairs.add(new BasicNameValuePair("longitude", longitude + "")); //long
			nameValuePairs.add(new BasicNameValuePair("latitude", latitude + "")); //lat
			

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			Log.v("LoactionSubmission : debug", 1 + "");
			Log.v("LoactionSubmission : target", target_post);

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			String responseString = EntityUtils.toString(entity, "iso-8859-1");
			Log.v("async : response string", responseString);
			code = response.getStatusLine().getStatusCode();
			FormObjectTransfer.isReadyToSubmit = false;
			return code;
		} catch (ClientProtocolException e) {
			Log.d("error LoactionSubmission query client protocol", e.getMessage());
			//sqliteBackup(nameValuePairs);
		} catch (IOException e) {
			Log.d("error LoactionSubmission query io exception", e.getMessage());
			Log.d("error LoactionSubmission server URL", target_post);
			//sqliteBackup(nameValuePairs);
		}
		return code;
	}

	

}