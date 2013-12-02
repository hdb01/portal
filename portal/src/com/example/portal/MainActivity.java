package com.example.portal;


import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.String;

import android.location.GpsStatus.Listener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity  {

	protected static final int VISIBLE = 0;
	protected static final int INVISIBLE = 1;
	private EditText email,pass;
	private TextView txtMsg;
	private Button btnOk;
	private Button openNew;
	private Button btnExit;
	private ImageView iv;
	private ProgressBar mProgress;
	private Context context;
	private String token="";
	private User currentUser;
	protected String[] listePartners;
	private Drawable drawable;
	public static final int MSG_IND = 0;
	public static final int MSG_UI = 1;
	public static final int MSG_IMG = 2;
	public static final int MSG_BTNOK = 3;
	public static final int MSG_BTNNEXT = 4;



	public Drawable ImageOperations(MainActivity mainActivity, String url, String saveFilename) {
		try {
			InputStream is = (InputStream) this.fetch(url);
			Drawable d = Drawable.createFromStream(is, null);
			return d;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Object fetch(String address) throws MalformedURLException,IOException {
		URL url = new URL(address);
		System.out.println("----------------------");
		System.out.println(address);
		Object content = url.getContent();
		return content;
	}

	private void getDetailsUser(String username, String token){
		//cherche details user
		String URL1 = "http://qa-trunk.m2mop.net/api/v1/users?";
		URL1 = URL1 + "email=" + username + "&access_token=" + token;

		String SetServerString = "";   
		String nom = "";
		String tel = "";
		String image = "";

		Connexions connexion = new Connexions();
		
		SetServerString = connexion.getResponse(URL1);		      			     	     


		//parse reponse					
		try {
			JSONArray userList = null;
			JSONObject mainObject = new JSONObject(SetServerString);												
			userList = mainObject.getJSONArray("items");								

			// Cherche le nom
			for(int i = 0; i < userList.length()  ; i++){
				JSONObject c = userList.getJSONObject(i);
				if (c.getString("email").equals(email.getText().toString()) )
				{					        	
					nom = c.getString("name").toString();
					tel = c.getString("phoneNumber").toString();
					//Cherche info  company name et uid
					JSONObject company = c.getJSONObject("company");
					String uidcompany = company.getString("uid");
					String namecompany = company.getString("name");
					//Cherche info  image
					JSONObject pictures = c.getJSONObject("picture");
					image = pictures.getString("normal");
					currentUser = new User(nom, tel, image, token, uidcompany, namecompany);

					String urlImage = "https://qa-trunk.airvantage.net/api/v1" + currentUser.getImage() + "?&access_token=" + token ;
					// Récupération du Drawable avec l'URL associée.
					drawable = ImageOperations(this, urlImage,"");

				} 	
				connexion=null;
			}


		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}

	}

	private String[] getListePartners(){

		String URL1 = "http://qa-trunk.m2mop.net/api/v1/partners?size=10&asc=name&company=" + currentUser.getUidCompany();
		URL1 = URL1 +  "&access_token=" + currentUser.getToken();

		String SetServerString = "";   

		String[] listePartners = null;
		Connexions connexion = new Connexions();
		SetServerString = connexion.getResponse(URL1);		      			     	     

		//parse reponse					
		try {
			JSONArray compList = null;
			JSONObject mainObject = new JSONObject(SetServerString);												
			compList = mainObject.getJSONArray("items");								
			listePartners = new String[compList.length()  ];
			// Cherche le nom
			for(int i = 0; i < compList.length()  ; i++){
				JSONObject c = compList.getJSONObject(i);
				listePartners[i] = c.getString("name").toString() +"/"+c.getString("uid").toString();	
				System.out.println(listePartners[i]);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return listePartners;
	}



	private String getToken(String _username, String  _password){
		String URL ="";
		String username = _username;
		String password = _password;
		String token ="";
		URL="http://qa-trunk.m2mop.net/api/oauth/token?client_id=085d82466f824079a829f301b8a1f492&grant_type=password&" ;
		URL=URL + "username=" + username + "&password=" + password + "&client_secret=6854fa72f98e492db163458085d5b89c";


		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = 
					new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		JSONParser jParser = new JSONParser();		
		JSONObject json = jParser.getJSONFromUrl(URL);
		try {					
			token = json.getString("access_token");										

		} catch (JSONException e) {
			e.printStackTrace();

		}
		return token;
	}



	// Define the Handler that receives messages from the thread and update the progress
	private final Handler handler = new Handler() {
		@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg) {
			String msgtt = (String) msg.obj;
			switch (msg.what) {
			case MSG_IND:   	   
				Toast.makeText( context, (String) msg.obj,  Toast.LENGTH_SHORT).show();	
				mProgress.setVisibility(VISIBLE);
				break;
			case MSG_UI:
				String m = (String) msg.obj ;
				int i =  Integer.parseInt(m);
				mProgress.setProgress(i);
				if (m.equals("1")){
					m = "get token ...";// token);
				}
				if (m.equals("4")){
					m = "get user details ...";// token);
				}
				if (m.equals("7")){
					m = "get partners list ...";// token);
				}
				if (m.equals("10")){
					m = "";// token);
					txtMsg.setText( "Nom : " + currentUser.getNom() + "\n" + "Phone : " + currentUser.getPhone() + "\n"+ "Company : " + currentUser.getNameCompany());			    	
					iv.setImageDrawable(drawable);
				}

				txtMsg1.setText(m); //txtMsg.setText((String) msg.obj);
				break;

			case MSG_BTNOK:
				if (msgtt.equals("1")){
					btnOk.setVisibility(8);
					openNew.setVisibility(VISIBLE);
					mProgress.setVisibility(8);
				} else {
					btnOk.setVisibility(0);
					openNew.setVisibility(8);
					mProgress.setVisibility(8);
				}
				break;	
			}
		}
	};
	protected OnClickListener contextBtn;
	private TextView txtMsg1;


	public void setActivityBackgroundColor(int color) {
		View view = this.getWindow().getDecorView();
		view.setBackgroundColor(color);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		context = this;
		txtMsg1      =  (TextView)findViewById(R.id.textView1);
		txtMsg      =  (TextView)findViewById(R.id.textView2);
		email      =  (EditText)findViewById(R.id.email);
		pass       =  (EditText)findViewById(R.id.password);
		btnOk=(Button)findViewById(R.id.Ok);
		openNew=(Button)findViewById(R.id.button1);
		btnExit=(Button)findViewById(R.id.button2);
		iv = (ImageView) this.findViewById(R.id.imageView1);
		mProgress = (ProgressBar) findViewById(R.id.progressBar1);
		mProgress.setProgress(0);
		mProgress.setMax(10);

		txtMsg.setText("");

		setActivityBackgroundColor(Color.GRAY);
		txtMsg.setTextColor(Color.WHITE);
		email.setTextColor(Color.WHITE);

		btnOk.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){ 
				final String username = email.getText().toString();
				final String password = pass.getText().toString();

				contextBtn = this;

				//*********************************
				// AVEC THREAD  : pb d'affichage de l image 

				Thread background = new Thread(new Runnable() {
					// After call for background.start this run method call
					public void run() {
						try {
							Message msg = null;
							msg = handler.obtainMessage(MSG_IND, "Connexion au Portal");
							handler.sendMessage(msg);
							String token = getToken(username, password);;                   

							if (!token.equals("")){
								//token
								msg = handler.obtainMessage(MSG_UI,"1");
								handler.sendMessage(msg);							
								//details user
								msg = handler.obtainMessage(MSG_UI,"4");// "détails utilisateur...");
								handler.sendMessage(msg);

								getDetailsUser(username, token);

								//liste des companies du currentUser					
								msg = handler.obtainMessage(MSG_UI, "7");//"liste des partners...");
								handler.sendMessage(msg);
								listePartners = getListePartners();
								//fin 
								msg=handler.obtainMessage(MSG_UI, "10" );//m);
								handler.sendMessage(msg);
								msg = handler.obtainMessage(MSG_BTNOK, "1");
								handler.sendMessage(msg);
							} else {
								msg = handler.obtainMessage(MSG_IND, "probleme de Connexion");
								handler.sendMessage(msg);
							}



						} catch (Throwable t) {
							// just end the background thread
							System.out.println("Animation *******************" + "Thread  exception " + t);
													}
					}

				});


				background.start();  //After call start method thread called run Method

				//*********************************


				//*********************************
				// CA MARCHE ..SANS THREAD
				//recupere le token
				/*
				String token = getToken(username, password);

				if (token.equals("")){
					txtMsg.setText("Wrong Login/password" );
					pass.setText("");
					email.setText("");
					return;
				}

				openNew.setVisibility(VISIBLE);

				//Affiche les details du user;
				txtMsg.setText("détails utilisateur...");
				getDetailsUser(username, token);

				//cherche l image du user
				txtMsg.setText("recherche de l'image....");
				String urlImage = "https://qa-trunk.airvantage.net/api/v1" + currentUser.getImage() + "?&access_token=" + token ;
				// Récupération du Drawable avec l'URL associée.
				Drawable drawable = ImageOperations(this, urlImage,"");
				// Placement du Drawable sur l'ImageView.
				iv.setImageDrawable(drawable);


				//liste des companies du currentUser					
				txtMsg.setText("liste des partners....");
				listePartners = getListePartners();


				//cache bouton OK
				txtMsg.setText(currentUser.getNom() + "\nPhone:" + currentUser.getPhone() + "\n"+ currentUser.getNameCompany());
				btnOk.setVisibility(INVISIBLE);

				 */
				//*********************************

			}

		});  


		openNew.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){ 

				//On créé un objet Bundle, c'est ce qui va nous permetre d'envoyer des données à l'autre Activity
				Bundle objetbunble = new Bundle();

				objetbunble.putString("token", currentUser.getToken());
				objetbunble.putString("nomUser", currentUser.getNom());
				objetbunble.putString("nomMyCompany", currentUser.getNameCompany());
				objetbunble.putString("uidMyCompany", currentUser.getUidCompany());
				objetbunble.putStringArray("listePartners", listePartners);
				Intent intent = new Intent(context, ChoosePartner.class);	
				intent.putExtras(objetbunble);
				context.startActivity(intent);

			}
		});  

		btnExit.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){ 

				System.exit(0);

			}
		}
				);  



	}


}
