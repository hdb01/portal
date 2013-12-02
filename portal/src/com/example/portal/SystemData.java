package com.example.portal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SystemData {

	String timestamp ="";
	String latitude ="";
	String longitude = "";
	String signalQuality ="";

	public SystemData(String t, String lat, String lon, String sq){
		timestamp = t;
		latitude =lat;
		longitude =lon;
		signalQuality =sq;		
	}

	public SystemData(){

	}

	public static String convertTimeStampDate (String ts) {
		Timestamp ST;
		java.util.Date date1 = null;
		String s = "";
		System.out.println("===================================");
		System.out.println("ts= " + ts);
		if (ts.equals("") ){
			return "";
		}
		ST = new Timestamp(Long.parseLong(ts));
		System.out.println("ST= " + ST);
		System.out.println("===================================");
		date1 = new java.util.Date(ST.getTime());	
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		s = formatter.format(date1);
		return s;

	}

	public static String convertToFloatString (String s) {
		if (s.equals("") ){
			return "";
		}
		float f = Float.valueOf(s.trim()).floatValue();
		f = f / 100000;
	
		String ss = String.valueOf(f);;
		
		return ss;
	}

	public String getTimestamp()  {
		
		String s = convertTimeStampDate(timestamp);

		return s;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getLatitude() {
		return convertToFloatString(latitude);
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return  convertToFloatString(longitude);
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getSignalQuality() {
		return signalQuality;
	}

	public void setSignalQuality(String signalQuality) {
		this.signalQuality = signalQuality;
	}






}
