package com.example.portal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

public class Connexions {
	HttpClient Client ;
	String response ="";    
	
	public Connexions() {
		// TODO Auto-generated constructor stub
	Client = new DefaultHttpClient();
		
	
	}

	public String getResponse(final String uurl) {
		// TODO Auto-generated constructor stub
		
		try
		{
			
			HttpGet httpget = new HttpGet(uurl);
			httpget.addHeader("Content-Type", "application/json");
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			response = Client.execute(httpget, responseHandler);
			//content.setText("RESULT " + SetServerString);
			System.out.println("***** dans getResponse ********");
			System.out.println(response);
			
		}
		catch(Exception ex)
		{
			System.out.println("Fail! ***********" + ex.toString());
			response =  "Fail!" ;
		}     
		
		return (response);
	}

		
	}

	
	

