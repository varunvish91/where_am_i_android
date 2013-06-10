package com.example.asd;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.CellLocation;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class HomeScreenActivity extends Activity implements LocationListener{
	private String reg_id;
	private String my_phone;
	LocationManager locationManager;
	double lat = 0.0;
	double lon = 0.0;
	Object lock = new Object();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_screen_layout);
		ArrayList<Contact> contacts = new ArrayList<Contact>();
		Intent i = getIntent();
		Bundle b = i.getExtras();
		this.my_phone = b.getString("my_phone");
		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		CellLocation.requestLocationUpdate();
		//get my phone number
		
		//get all of the contacts
		
		//cursor with the contacts
		Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
		
		while(cursor.moveToNext()) {
			String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			
			contacts.add(new Contact(name, phoneNumber));
		}
		ListView list = (ListView)findViewById(R.id.contacts);
		final ContactsAdapter adapter = new ContactsAdapter(contacts, this);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
					final Contact item = adapter.getItem(position);
					AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreenActivity.this);
					builder.setMessage("Would you like to request directions to " + item.name + "?").setPositiveButton("yes", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							HomeScreenActivity.this.send_request(item);
						}
					}).setNegativeButton("No", new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					AlertDialog dialog = builder.create();
					dialog.show();
				}
		});
	}
	public void send_request(Contact item) {
		RequestLocation rl = new RequestLocation(item);
		rl.execute();
	}
	class RequestLocation extends AsyncTask<Void, Void, Void>{
		ProgressDialog dialog;
		Contact item;
		Context context;
		public RequestLocation(Contact item) {
			this.item = item;
			
		}
		@Override
		protected void onPostExecute(Void result) {
			Context context = HomeScreenActivity.this;
			CharSequence text = "A request has been sent";
			int duration = Toast.LENGTH_LONG;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		};
		@Override
		protected Void doInBackground(Void... params) {
			try {
					synchronized (lock) {
						if(lat == 0.0 || lon == 0.0) lock.wait();
						JSONObject json = new JSONObject();
						json.put("my_number",my_phone);
						json.put("their_number",item.phone_number);
						json.put("lat",Double.toString(lat));
						json.put("lon", Double.toString(lon));
						HTTPObject object = new HTTPObject("http://192.168.1.94/WhereAMIO/Request_location.php", json.toString());
						object.execute_client_request();
						String req = object.get_response();
						System.out.println(req);
					}
			} 
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
	}
	@Override
	public void onLocationChanged(Location location) {
		synchronized (lock) {
			lat = location.getLatitude();
			lon = location.getLongitude();
			locationManager.removeUpdates(this);
			lock.notify();
		}

	}
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
}
