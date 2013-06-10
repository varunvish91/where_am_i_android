package com.example.asd;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.maps.MapView;

public class DisplayLocationActivity extends FragmentActivity implements LocationListener {
	private MapView mapView;
	private UiSettings uisettings;
	private GoogleMap mMap;
	LocationManager locationManager;
	private String sender_phone_num = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_location_layout);
		final Bundle b = getIntent().getExtras();
		final String traveler_name = b.getString("sender_name");
		this.sender_phone_num = b.getString("sender_number");
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Give directions to " + traveler_name +"?").setPositiveButton("Yes", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				DisplayLocationActivity.this.execute_location_display(traveler_name,b);
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
	private void execute_location_display(String traveler_name,Bundle b) {
		double lon = Double.parseDouble(b.getString("lon"));
		double lat = Double.parseDouble(b.getString("lat"));
		setUpMapIfNeeded(lat,lon,traveler_name);
		uisettings = mMap.getUiSettings();
		uisettings.setZoomControlsEnabled(true);
		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		CellLocation.requestLocationUpdate();
	}
	private void setUpMapIfNeeded(double lat,double lon,String name) {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				setUpMap(lat,lon,name);
			}
		}
	}

	private void setUpMap(double lat, double lon,String name) {
		mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title(name + "'s location"));
	}

	@Override
	public void onLocationChanged(Location location) {
		try {
			LatLng latlng = new LatLng(location.getLatitude(),location.getLongitude());
			CameraUpdate center = CameraUpdateFactory.newLatLngZoom(latlng,15);
			mMap.moveCamera(center);
			mMap.addMarker(new MarkerOptions().position(latlng).title("Your  location"));
			locationManager.removeUpdates(DisplayLocationActivity.this);
			GiveDirections directions = new GiveDirections(this.sender_phone_num, location.getLatitude(), location.getLongitude());
			directions.execute();
			
		}
		catch(Exception e){
			e.printStackTrace();
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
	class GiveDirections extends AsyncTask<Void, Void, Void> {
		private double lat;
		private double lon;
		private String phone;
		
		public GiveDirections(String phone, double lat, double lon) {
			this.lat = lat; this.lon = lon; this.phone = phone;
			
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			Toast toast = Toast.makeText(DisplayLocationActivity.this,"Your location has been sent",Toast.LENGTH_LONG);
			toast.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try { 
				TelephonyManager mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
				phone =  mTelephonyMgr.getLine1Number();
				JSONObject json = new JSONObject();
				json.put("phone",this.phone);
				json.put("lat",String.valueOf(lat));
				json.put("lon",String.valueOf(lon));
				json.put("sender",phone);
				HTTPObject object = new HTTPObject("http://192.168.1.94/WhereAMIO/Directions.php", json.toString());
				object.execute_client_request();
				String output = object.get_response();
				System.out.println(output);
			}
			catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}
		
	}
}
