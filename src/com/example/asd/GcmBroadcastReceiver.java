package com.example.asd;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;


public class GcmBroadcastReceiver extends BroadcastReceiver {
    static final String TAG = "GCMDemo";
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    Context ctx;
    @Override
    public void onReceive(Context context, Intent intent) {
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        ctx = context;
        String messageType = gcm.getMessageType(intent);
        if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
           // sendNotification("Send error: " + intent.getExtras().toString());
        } 
        else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
           // sendNotification("Deleted messages on server: " + intent.getExtras().toString());
        } 
        else {
        	Bundle b = intent.getExtras();
            int m_type = Integer.parseInt(b.getString("message_type"));
            switch(m_type) {
            case 0:
            	build_message_request(b,ctx);
            	break;
            case 1:
            	buld_navigation_request(b,ctx);
            	break;
            }
        	
        	
        }
        setResultCode(Activity.RESULT_OK);
    }
    
    //starts the navigation 
    private void buld_navigation_request(Bundle b, Context ctx2) {
    	double lat = Double.parseDouble(b.getString("lat"));
    	double lon = Double.parseDouble(b.getString("lon"));
    	String sender = b.getString("sender");
    	String sender_name = this.get_name_from_number(sender);
    	String message = sender_name + " has sent you directions!";
    	Intent i = new Intent(ctx,NavigationActivity.class);
    	i.putExtra("sender_name",sender_name);
    	i.putExtra("lat", lat);
    	i.putExtra("lon",lon);
    	run_notification(i, message, ctx);
    
    }
    private String get_name_from_number(String number) {
    	String sender_name = number;
    	String[] projection = new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME,ContactsContract.PhoneLookup._ID};            
         Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
         Cursor cursor = ctx.getContentResolver().query(contactUri, projection, null, null, null);
         if (cursor.moveToFirst()) {
             // Get values from contacts database:
             String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
             sender_name =      cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
         }
    	return sender_name;
    }
	private void build_message_request(Bundle b,Context context) {
    	String sender = b.getString("sender");
        int message_type = b.getInt("message_type");
        String lon = b.getString("lon");
        String lat = b.getString("lat");
        String sender_name = get_name_from_number(sender);
        Intent i = new Intent(ctx,DisplayLocationActivity.class);
        i.putExtra("sender_name", sender_name);
        i.putExtra("message_type",message_type);
        i.putExtra("lat",lat);
        i.putExtra("lon", lon);
        i.putExtra("sender_number", sender);
        String msg =  "New message received from " + sender_name;
        run_notification(i, msg, ctx);
        
		
	}
    private void run_notification(Intent i,String message,Context context) {
    	  mNotificationManager = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);
          Notification notification = new Notification(R.drawable.ic_launcher,message,System.currentTimeMillis());
          notification.flags = Notification.FLAG_AUTO_CANCEL;
          PendingIntent pendingintent = PendingIntent.getActivity(ctx, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
          notification.setLatestEventInfo(context,"Message", message, pendingintent);
          mNotificationManager.notify(0, notification);
          Uri notification_sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
          Ringtone r = RingtoneManager.getRingtone(ctx.getApplicationContext(), notification_sound);
          r.play();
    	
    }
}