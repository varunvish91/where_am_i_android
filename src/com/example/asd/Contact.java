package com.example.asd;

public class Contact {
	public String name;
	public String phone_number;
	
	public Contact(String name, String phone_number) {
		this.name = name;
		this.phone_number = modify_number(phone_number);
	}
	private String modify_number(String phone_number2) {
		return phone_number2.replaceAll("[^\\d]", "");
	}
}
