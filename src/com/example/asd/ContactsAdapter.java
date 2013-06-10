package com.example.asd;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.asd.Contact;

public class ContactsAdapter extends BaseAdapter {
	ArrayList<Contact> contacts;
	private LayoutInflater inflater = null;
	
	public ContactsAdapter(ArrayList<Contact> contacts,Activity a){
		this.contacts = contacts;
		this.inflater  = (LayoutInflater)a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getCount() {
		return contacts.size();
	}

	@Override
	public Contact getItem(int arg0) {
		return contacts.get(arg0);
	
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if(convertView == null) {
			vi = inflater.inflate(R.layout.contact_item, null);
		}
		try {
			TextView name = (TextView)vi.findViewById(R.id.name);
			TextView phone = (TextView)vi.findViewById(R.id.phone_number);
			
			name.setText(contacts.get(position).name);
			phone.setText(contacts.get(position).phone_number);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return vi;
		
	}

}
