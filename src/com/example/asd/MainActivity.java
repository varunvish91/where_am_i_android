package com.example.asd;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class MainActivity extends Activity {
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private static final String PROPERTY_ON_SERVER_EXPIRATION_TIME = "onServerExpirationTimeMs";
	public static final long REGISTRATION_EXPIRY_TIME_MS = 1000 * 3600 * 24 * 7;
	String SENDER_ID = "289917773142";

	static final String TAG = "GCMDemo";
	TextView mDisplay;
	GoogleCloudMessaging gcm;
	AtomicInteger msgId = new AtomicInteger();
	SharedPreferences prefs;
	Context context;
	SharedPreferences mPrefs;
	String regid;
	String phone;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mPrefs = getSharedPreferences("where_am_i", MODE_PRIVATE);
		TelephonyManager mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		phone =  mTelephonyMgr.getLine1Number();
		boolean is_user = mPrefs.getBoolean("user", false);
		context = getApplicationContext();
		regid = getRegistrationId(context);
		if(is_user == false){
			if (regid.length() == 0) {
				registerBackground();
			}
			gcm = GoogleCloudMessaging.getInstance(this);
			SharedPreferences.Editor edit = mPrefs.edit();
			
			edit.putBoolean("user", true);
			edit.commit();
		}
		Intent i = new Intent(this,HomeScreenActivity.class);
		i.putExtra("regid", regid);
		i.putExtra("my_phone", phone);
		startActivity(i);
	}
	private void registerBackground() {
		REGBACKGROUND back = new REGBACKGROUND();
		back.execute();
	}

	private String getRegistrationId(Context context2) {
		final SharedPreferences prefs = getGCMPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.length() == 0) {
			Log.v(TAG, "Registration not found.");
			return "";
		}
		// check if app was updated; if so, it must clear registration id to
		// avoid a race condition if GCM sends a message
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion || isRegistrationExpired()) {
			Log.v(TAG, "App version changed or registration expired.");
			return "";
		}
		return registrationId;
	}

	private boolean isRegistrationExpired() {
		final SharedPreferences prefs = getGCMPreferences(context);
		// checks if the information is not stale
		long expirationTime = prefs.getLong(PROPERTY_ON_SERVER_EXPIRATION_TIME,
				-1);
		return System.currentTimeMillis() > expirationTime;

	}

	private SharedPreferences getGCMPreferences(Context context2) {
		return getSharedPreferences(MainActivity.class.getSimpleName(),
				Context.MODE_PRIVATE);

	}

	private int getAppVersion(Context context2) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}

	}

	class REGBACKGROUND extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			String msg = "";
			try {
				if (gcm == null) {
					gcm = GoogleCloudMessaging.getInstance(context);
				}
				regid = gcm.register(SENDER_ID);
				msg = "Device registered, registration id=" + regid;
				
				// You should send the registration ID to your server over HTTP,
				// so it can use GCM/HTTP or CCS to send messages to your app.

				// For this demo: we don't need to send it because the device
				// will send upstream messages to a server that echo back the
				// message
				// using the 'from' address in the message.

				// Save the regid - no need to register again.
				JSONObject json = new JSONObject();
				json.put("reg_id", regid);
				json.put("phone_number", phone);
				HTTPObject request = new HTTPObject("http://192.168.1.94/WhereAMIO/Register.php", json.toString());
				request.execute_client_request();
				String resp = request.get_response();
				System.out.println(resp);
				setRegistrationId(context, regid);
			
			} 
			catch (IOException ex) {
				msg = "Error :" + ex.getMessage();
			} 
			catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		private void setRegistrationId(Context context, String regId) {
			final SharedPreferences prefs = getGCMPreferences(context);
			int appVersion = getAppVersion(context);
			Log.v(TAG, "Saving regId on app version " + appVersion);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString(PROPERTY_REG_ID, regId);
			editor.putInt(PROPERTY_APP_VERSION, appVersion);
			long expirationTime = System.currentTimeMillis()
					+ REGISTRATION_EXPIRY_TIME_MS;

			Log.v(TAG, "Setting registration expiry time to "
					+ new Timestamp(expirationTime));
			editor.putLong(PROPERTY_ON_SERVER_EXPIRATION_TIME, expirationTime);
			editor.commit();
		}

	}

}
