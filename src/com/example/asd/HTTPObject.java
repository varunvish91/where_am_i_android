package com.example.asd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


//Stores all the data for an http request
public class HTTPObject {
	String body;
	DefaultHttpClient httpclient;
	HttpPost post;
	HTTPObject(String url,String json_string){
		try{
			httpclient = new DefaultHttpClient();
	        post = new HttpPost(url);
	        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
	        pairs.add(new BasicNameValuePair("json", json_string));
	        post.setEntity(new UrlEncodedFormEntity(pairs));
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	public void execute_client_request() throws ClientProtocolException, IOException {
		HttpResponse response = httpclient.execute(post);
		HttpEntity entity = response.getEntity();
        this.body = EntityUtils.toString(entity);
		
	}
	public String get_response() { return body; }

}