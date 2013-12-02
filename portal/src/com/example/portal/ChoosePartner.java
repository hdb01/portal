package com.example.portal;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ChoosePartner extends Activity {

	private ChoosePartner context;
	
	private ListView listViewpartners;
	private Button back;
	private String[] listePartners;
	private String token;
	private String nomUser;
	
	private String[] n;
	private String[] u;
	private String nomMyCompany;
	private String uidMyCompany;
	
	
	public void setActivityBackgroundColor(int color) {
		View view = this.getWindow().getDecorView();
		view.setBackgroundColor(color);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_partner);
		context = this;
				
		final Button btnMyCompany=(Button)findViewById(R.id.button2);
		back = (Button) findViewById(R.id.button1);
		listViewpartners = (ListView) findViewById(R.id.listView1);
		setActivityBackgroundColor(Color.LTGRAY);
		//listViewpartners.setBackgroundColor(Color.WHITE);
		
		//On recupere les données du Bundle
		Bundle objetbunble  = this.getIntent().getExtras();
		if (objetbunble != null && objetbunble.containsKey("listePartners") ) {
			listePartners = this.getIntent().getStringArrayExtra("listePartners");
			token =this.getIntent().getStringExtra("token");
			nomUser =this.getIntent().getStringExtra("nomUser");
			nomMyCompany =this.getIntent().getStringExtra("nomMyCompany");
			uidMyCompany =this.getIntent().getStringExtra("uidMyCompany");

		}
		context.setTitle("Choix de la company");
		btnMyCompany.setText( nomMyCompany);
		//creation de 2 liste

		n = new String[listePartners.length  ];
		u = new String[listePartners.length  ];
		
		for (int i = 0 ; i<listePartners.length ; i++){
			int j = listePartners[i].indexOf("/");
			n[i] = listePartners[i].substring(0,j);
			u[i] = listePartners[i].substring(j+1);

		}
	
			
		//cree l adapter et l associe
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, android.R.id.text1, n);
		listViewpartners.setAdapter(adapter);
		
	    //selection dun partner
		listViewpartners.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				
				///String item = (String) listViewpartners.getAdapter().getItem(position);	        							
				System.out.println(n[position] + "  " +u[position]);			
				
				Bundle objetbunble = new Bundle();
			
				objetbunble.putString("token",token );
				objetbunble.putString("nomUser", nomUser);
				objetbunble.putString("uidPartner", u[position]);
				objetbunble.putString("namePartner", n[position]);
				Intent intent = new Intent(context, Hello.class);	
				intent.putExtras(objetbunble);
				context.startActivity(intent);
			}
		});
		
		btnMyCompany.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){ 

				//On créé un objet Bundle, c'est ce qui va nous permetre d'envoyer des données à l'autre Activity
				Bundle objetbunble = new Bundle();
				objetbunble.putString("token",token );
				objetbunble.putString("nomUser", nomUser);
				objetbunble.putString("uidPartner",uidMyCompany);
				objetbunble.putString("namePartner", nomMyCompany);
				Intent intent = new Intent(context, Hello.class);	
					
				intent.putExtras(objetbunble);
				context.startActivity(intent);

			}
		}
				);  
		back.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				((Activity) context).finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.choose_partner, menu);
		return true;
	}

}
