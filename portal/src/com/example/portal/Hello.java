package com.example.portal;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.AdapterView.OnItemClickListener;

import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;


import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SearchView;
import android.widget.Toast;

import android.widget.ListView;
import android.widget.TextView;


public class Hello extends Activity {

	private Context context;
	private Button back;
	private Button refreshList;
	private String helloToken;
	private String nomUser;
	private ListView listViewSystems;
	private String[] mString;
	private TextView message;
	private List<Systeme> liste = new ArrayList<Systeme>();

	JSONArray systemsList = null;
	private EditText searchSystems;
	private String uidPartner;
	private String namePartner;
	

	
	@SuppressWarnings("unused")
	private List<Systeme> getSystemes(final String SetServerString) {
		String name = "";
		String state = "";
		String rssi ="";
		String latitude = "";
		String longitude ="";
		String uid ="";
		List<Systeme> liste = new ArrayList<Systeme>();
		
		//parse reponse					
		try {

			JSONObject mainObject = new JSONObject(SetServerString);												
			systemsList = mainObject.getJSONArray("items");
			message.setText("Nbre systemes =  : " + systemsList.length());

			// looping through All systems
			for(int i = 0; i < systemsList.length() -1 ; i++){

				JSONObject c = systemsList.getJSONObject(i);
				name = c.getString("name");
				state = c.getString("state");

				JSONObject data = c.getJSONObject("data");

				rssi = data.getString("rssiLevel");
				latitude = data.getString("latitude");
				longitude = data.getString("longitude");
				uid =  data.getString("uid");

				Systeme s = new Systeme(name, rssi, latitude,longitude, uid);
				liste.add(s);
			}




		} catch (JSONException e) {
			e.printStackTrace();
			message.setText("Fail! " + e.toString());
		}

		return liste;
	}
	
	private String[] getSystemesString(String SetServerString) {
		String name = "";		
		String rssi ="";
		String latitude = "";
		String longitude ="";
		String uid ="";
		
		
		try {		
			
			JSONObject mainObject = new JSONObject(SetServerString);												
			systemsList = mainObject.getJSONArray("items");
			message.setText("Nbre systemes =  : " + systemsList.length());			
			mString = new String[systemsList.length()  ];
			//System.out.println("Longueur de la mstring " + systemsList.length() + "*******************");
			// cree la liste des objets systems
			//System.out.println("longueur de la liste avant clear =" + liste.size());
			liste.clear();
			//System.out.println("longueur de la liste clear =" + liste.size());
			for(int i = 0; i < systemsList.length() -1 ; i++){

				JSONObject c = systemsList.getJSONObject(i);
				name = c.getString("name");
				uid = c.getString("uid");
				JSONObject data = c.getJSONObject("data");

				rssi = data.getString("rssiLevel");
				latitude = data.getString("latitude");
				longitude = data.getString("longitude");
				mString[i]= name + " (RSSI = " + rssi+")";// + " " + latitude + " " + longitude ; //+ " " + uid ;
				//System.out.println("****mstring" + i + " = " + mString[i]);
				Systeme s = new Systeme(name, rssi, latitude,longitude, uid);
				//System.out.println("longueur de la liste  =" + liste.size());
				liste.add(s);
			}


		} catch (JSONException e) {
			e.printStackTrace();
			System.out.println("error ****" + e.toString());
			message.setText("Fail! " + e.toString());
		}

		return mString;
	}
	
	private String[] getSystemlist(String txtSys){
		//connexion
				String SetServerString = "";  
				String URL = "https://qa-trunk.m2mop.net/api/v1/systems?size=25&company="+uidPartner;
					URL=URL + "&name=" + txtSys + "&states=DEPLOYED&access_token="+ helloToken;
								 
				Connexions connexion = new Connexions();
				SetServerString = connexion.getResponse(URL);
				//recupere la liste des systeme tableau de String
				mString = getSystemesString(SetServerString);				
				return mString;
	}
	
	
		
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hello);

		context = this;
		back = (Button) findViewById(R.id.back);
		refreshList = (Button) findViewById(R.id.button1);
		listViewSystems = (ListView) findViewById(R.id.listView1);
		message      =  (TextView)findViewById(R.id.textView1);
		searchSystems = (EditText)findViewById(R.id.searchSys);
		searchSystems.setText("");
		final String txtSys = searchSystems.getText().toString();
		
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		
		
		//On recupere les données du Bundle
		Bundle objetbunble  = this.getIntent().getExtras();
		if (objetbunble != null && objetbunble.containsKey("token") && objetbunble.containsKey("nomUser") ) {
			helloToken = this.getIntent().getStringExtra("token");
			nomUser =  this.getIntent().getStringExtra("nomUser");
			uidPartner =  this.getIntent().getStringExtra("uidPartner");
			namePartner =  this.getIntent().getStringExtra("namePartner");
			setTitle(nomUser + ":" + namePartner);
		}else {
			setTitle("Error");
		}
		
		
		//construit la liste a afficher
		mString = getSystemlist(txtSys);
		
		//genere la liste a afficher dans la ListView
		final ArrayList<String> list = new ArrayList<String>();
	    for (int i = 0; i < mString.length; ++i) {
	      list.add(mString[i]);
	      System.out.println("mstring"+i + "  " + mString[i]);
	    }
	    final StableArrayAdapter adapter = new StableArrayAdapter(this,android.R.layout.simple_list_item_1, list);
	    
	    listViewSystems.setAdapter(adapter);
	    adapter.notifyDataSetChanged();
		
	    //================================================
	    //Affiche les coordonnees GPS du system selectionne
	    listViewSystems.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				//On récupère le titre de l'Item dans un String
				//String item = (String) listViewSystems.getAdapter().getItem(position);	        							
				Systeme s = liste.get(position) ;		
				//System.out.println(liste.get(position) + "  " + s.getUid() + " " + s.getName());			
				
				Bundle objetbunble = new Bundle();
				objetbunble.putString("uid",s.getUid() );
				objetbunble.putString("namesystem",s.getName() );
				objetbunble.putString("token",helloToken );
				objetbunble.putString("uidPartner",uidPartner );
				Intent intent = new Intent(context, Historique.class);	
				intent.putExtras(objetbunble);
				context.startActivity(intent);
			}
		});




		// Bouton pour refresh liste des systemes
		refreshList.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
						list.clear();
						adapter.notifyDataSetChanged(); 
					    String txtSys = searchSystems.getText().toString();	
					    System.out.println("search +++++++++++" + txtSys);
					    mString = getSystemlist(txtSys);
					    System.out.println("apres get system list ****************");
					    for (int i = 0; i < mString.length; ++i) {
						      list.add(mString[i]);
						    }
					   	adapter.notifyDataSetChanged(); 
			}

		});
		
		
		//Button Back
		back.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				((Activity) context).finish();
			}
		});






	}


	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}


	  private class StableArrayAdapter extends ArrayAdapter<String> {

		    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

		    public StableArrayAdapter(Context context, int textViewResourceId,
		        List<String> objects) {
		      super(context, textViewResourceId, objects);
		      for (int i = 0; i < objects.size(); ++i) {
		        mIdMap.put(objects.get(i), i);
		      }
		    }

		    @Override
		    public long getItemId(int position) {
		      String item = getItem(position);
		      return 1;//mIdMap.get(item);    //donne NPE =====  A CREUSER 
		    }

		    @Override
		    public boolean hasStableIds() {
		      return true;
		    }

		  }

}
