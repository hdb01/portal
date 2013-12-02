package com.example.portal;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class Historique extends Activity  {


	private Context context;
	private ListView listViewHistory;	
	private String uid;
	private String helloToken;
	private Button back;
	private Button viewMap;
	private ArrayList<Object> sd;
	private String namesystem;
	private String uidPartner;




	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.historique);
		back = (Button) findViewById(R.id.back);
		viewMap = (Button) findViewById(R.id.button1);
		context = this;	
		listViewHistory = (ListView) findViewById(R.id.listView1);

		//On recupere les données du Bundle
		Bundle objetbunble  = this.getIntent().getExtras();
		if (objetbunble != null && objetbunble.containsKey("uid") && objetbunble.containsKey("token")) {
			//"namesystem"
			uid = this.getIntent().getStringExtra("uid");	
			helloToken = this.getIntent().getStringExtra("token");
			namesystem= this.getIntent().getStringExtra("namesystem");
			uidPartner=this.getIntent().getStringExtra("uidPartner");
		}else {
			uid = "Error";
			return;
		}
		setTitle("Coordonnées GPS de " + namesystem);
		//List<Object> 
		sd = new ArrayList<Object>();

		for (int i = 0 ; i<10 ; i++){
			sd.add (new SystemData());
		}

		//recupere la liste des Latitude
		getHistoricalData("902",sd);

		//recupere la liste des Longitudes
		getHistoricalData("903",sd);

		//recupere signal strenght
		getHistoricalData("261",sd);
		//getHistoricalData("772",sd);	

		String[] mStringAgregate = new String[10];
		for (int i = 0 ; i<10 ; i++){
			SystemData systemdata = (SystemData) sd.get(i);
			mStringAgregate[i] = systemdata.getTimestamp() + ":" + systemdata.getLatitude() + "," + systemdata.getLongitude() + "(" + systemdata.getSignalQuality()+(")");
		}


		//cree l adapter et l associe
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_2, android.R.id.text2, mStringAgregate);
		listViewHistory.setAdapter(adapter);

		viewMap.performClick();
		back.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				((Activity) context).finish();
			}
		});


		viewMap.setOnClickListener(new OnClickListener() {



			public void onClick(View v) {
				//On créé un objet Bundle, c'est ce qui va nous permetre d'envoyer des données à l'autre Activity
				Bundle objetbunble = new Bundle();

			
				String[] lat =  new String[10] ;
				String[] lon =  new String[10];
				String[] dd = new String[10];
				String[] sq = new String[10];

				for (int i = 0 ; i<10 ; i++){
					SystemData systemdata = (SystemData) sd.get(i);
					lat[i] = systemdata.getLatitude();
					lon[i]=systemdata.getLongitude();
					dd[i]=systemdata.getTimestamp();
					sq[i]=systemdata.getSignalQuality();
				}
				objetbunble.putStringArray("lat", lat);
				objetbunble.putStringArray("lon", lon);
				objetbunble.putStringArray("dd", dd);
				objetbunble.putStringArray("sq", sq);
				Intent intent = new Intent(context, MapPortal.class);	
				intent.putExtras(objetbunble);
				context.startActivity(intent);
			}
		});
	}




	private String[] getHistoricalData(String data, List<Object> sd) {		
		String _data = data;
		String[] mString =null;
		String URL="";
		String SetServerString = "";  

		URL = "https://qa-trunk.m2mop.net/api/v1/systems/" + uid + "/data/" + _data + "/raw?size=10&company="+uidPartner + "&access_token=" + helloToken;

		Connexions connexion = new Connexions();
		SetServerString = connexion.getResponse(URL);


		try {		

			JSONArray latitudeList = new JSONArray(SetServerString);
			mString  = new String[latitudeList.length()  ];				

			// looping through All systems
			for(int i = 0; i < latitudeList.length()  ; i++){
				SystemData systemdata = (SystemData) sd.get(i);
				JSONObject c = (JSONObject) latitudeList.get(i);
				String value = c.getString("value");
				String timestamp = c.getString("timestamp");

				if (data == "902"){
					systemdata.setLatitude(value);
					systemdata.setTimestamp(timestamp);
				}
				if (data == "903"){
					systemdata.setLongitude(value);

				}
				if (data == "261"){
					systemdata.setSignalQuality(value);

				}
				mString[i]= value + " " + timestamp ;	

			}


		} catch (JSONException e) {
			e.printStackTrace();
			//message.setText("Fail! " + e.toString());
		}

		return mString;
	}



}
