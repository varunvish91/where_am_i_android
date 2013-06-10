package com.example.asd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.CellLocation;

/*   	i.putExtra("sender_name",sender_name);
    	i.putExtra("lat", lat);
    	i.putExtra("lon",lon);*/

public class NavigationActivity extends Activity implements LocationListener {
	LocationManager locationManager;
	double lat;
	double lon;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle b = getIntent().getExtras();
		lat = b.getDouble("lat");
		lon = b.getDouble("lon");
		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		CellLocation.requestLocationUpdate();
		
		
	}

	@Override
	public void onLocationChanged(Location location) {
		locationManager.removeUpdates(NavigationActivity.this);
		double start_addr = location.getLatitude();
		double start_addr_lon = location.getLongitude();
		//start navigation services
		
		String url = "http://maps.google.com/maps?saddr="+start_addr+","+start_addr_lon + "&daddr=" + lat + "," + lon;
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW,Uri.parse(url));
		startActivity(intent);
		

		
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
