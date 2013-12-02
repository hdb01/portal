package com.example.portal;

public class Systeme {
	private String name;
	private String rssi;
	private String latitude ;
	private String longitude ;
	private String uid;
	 
	public Systeme(String _name, String _rssi, String _latitude , String _longitude, String _uid ) {
	this.name = _name;
	this.rssi =  _rssi;
	this.latitude =  _latitude;
	this.longitude =  _longitude;
	this.uid = _uid;
	 }

	public String getName() {
		return name;
	}

	public String getRssi() {
		return rssi;
	}

	public String getLatitude() {
		return latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public Systeme getSysteme() {		
		return this;		
	}
	
	public String getUid(){
		return this.uid;
	}

}
