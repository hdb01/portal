package com.example.portal;



import java.text.DecimalFormat;
import java.util.ArrayList;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
//- See more at: http://blog.rolandl.fr/1230-android-la-geolocalisation-et-lapi-google-maps-android-v2#sthash.VgPYHi5R.dpuf

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;


@SuppressLint("NewApi")
public class MapPortal extends Activity implements LocationListener{


	private GoogleMap gMap;
	private LocationManager locationManager;
	private Button back;
	private Marker marker;

	private MapPortal context;
	private Marker[] markertable;
	private static int[] rouge;
	private static int[] green;
	private static int[] yellow;

	private LatLng[] GEOPOINTS;

	public  int envoiNumIcon(int j, String rssi){
		int numicon=0;
		Double n = Double.valueOf(rssi);
		int i = 0;
		i = 9-j;

		//green
		if (n > -60){
			numicon =  green[i];
		}
		//yellow
		if (n <= -60  && n> - 90){
			numicon = yellow[i];
		}
		//rouge
		if (n <= -90){
			numicon = rouge[i];
		}

		return numicon;

	}

	

	@SuppressLint("UseValueOf")
	private void initicons(){
		rouge = new int[10];
		green = new int[10];
		yellow = new int[10];

		for (int j = 0; j < 10; j++) {		
			String i=(new Integer(j)).toString();;
			rouge[j] = getResources().getIdentifier( "rouge__"+ i,"drawable", getPackageName());
			//				System.out.println("rouge" + i + rouge[j]);
		}

		for (int j = 0; j < 10; j++) {
			String i=(new Integer(j)).toString();;
			green[j] = getResources().getIdentifier( "green__"+ i,"drawable", getPackageName());
			//	System.out.println("green" + j + green[j]);
		}


		for (int j = 0; j < 10; j++) {
			String i=(new Integer(j)).toString();;
			yellow[j] = getResources().getIdentifier( "yellow_"+ i,"drawable", getPackageName());
			//System.out.println("yellow" + j + yellow[j]);
		}

	}


	@Override
	protected void onCreate(final Bundle savedInstanceState) {

		context = this;
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_map);
		back = (Button) findViewById(R.id.button1);
		gMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
		markertable = new Marker[10];

		initicons();

		try {
			MapsInitializer.initialize(context);
		} catch (GooglePlayServicesNotAvailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Bundle objetbunble  = this.getIntent().getExtras();
		if (objetbunble != null && objetbunble.containsKey("lat") && objetbunble.containsKey("lon")  ){
			String[] lat = this.getIntent().getStringArrayExtra("lat");
			String[] lon = this.getIntent().getStringArrayExtra("lon");
			String[] dd =  this.getIntent().getStringArrayExtra("dd");
			String[] sq =  this.getIntent().getStringArrayExtra("sq");
			setTitle("10 last points GPS");
			//Place les markers	
			for (int i = 0; i<10; i++){
				System.out.println("***********  " + i);
				int numicon = envoiNumIcon(i, sq[i]);
				System.out.println("numicon = " + numicon );
				marker = gMap.addMarker(new MarkerOptions()
				.title("Position" + (9-i) + " : " + lat[i] + "," + lon[i])
				.snippet("RSSI = " + sq[i] + "   Date =" + dd[i])
				.position(new LatLng(Double.parseDouble(lat[i]), Double.parseDouble(lon[i])))        			
				.icon(BitmapDescriptorFactory.fromResource(numicon))
						);

				markertable[i] = marker;
			}

			//trace la ligne
			LatLng polyline;
			ArrayList<LatLng> polylines = new ArrayList<LatLng>();
			for (int i=0; i<10;i++){
				double a1 = Double.parseDouble(lat[i]);
				double a2=Double.parseDouble(lon[i]);
				polyline = new LatLng(a1,a2);
				polylines.add(polyline);		
			}
			gMap.addPolyline(new PolylineOptions().addAll(polylines).width(5.0f).color(Color.BLACK));


			//zoom sur markers
			CameraPosition cameraPosition = new CameraPosition.Builder().target(
					new LatLng(Double.parseDouble(lat[0]), Double.parseDouble(lon[0]))).zoom(8).build();

			gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


			/*//cherche les bounds des markers et zoom 
			LatLngBounds.Builder builder = new LatLngBounds.Builder();
			for (Marker marker : markertable) {
			    builder.include(marker.getPosition());
			}
			LatLngBounds bounds = builder.build();
			int padding = 0; // offset from edges of the map in pixels
			gMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));*/

		}

		back.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				((Activity) context).finish();
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		/*
		//Obtention de la référence du service
		locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

		//Si le GPS est disponible, on s'y abonne
		if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			abonnementGPS();
		}*/
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPause() {
		super.onPause();

		//On appelle la méthode pour se désabonner
		//desabonnementGPS();
	}

	/**
	 * Méthode permettant de s'abonner à la localisation par GPS.
	 */
	public void abonnementGPS() {
		//On s'abonne
		//locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
	}

	/**
	 * Méthode permettant de se désabonner de la localisation par GPS.
	 */
	public void desabonnementGPS() {
		//Si le GPS est disponible, on s'y abonne
		//locationManager.removeUpdates(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onLocationChanged(final Location location) {
		//On affiche dans un Toat la nouvelle Localisation
		/*final StringBuilder msg = new StringBuilder("lat : ");
		msg.append(location.getLatitude());
		msg.append( "; lng : ");
		msg.append(location.getLongitude());

		Toast.makeText(this, msg.toString(), Toast.LENGTH_SHORT).show();

		//Mise à jour des coordonnées
		final LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());     
		gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
		marker.setPosition(latLng);*/
	}




	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onProviderDisabled(final String provider) {
		/*//Si le GPS est désactivé on se désabonne
		if("gps".equals(provider)) {
			desabonnementGPS();
		}      */
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onProviderEnabled(final String provider) {
		/*//Si le GPS est activé on s'abonne
		if("gps".equals(provider)) {
			abonnementGPS();
		}*/
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onStatusChanged(final String provider, final int status, final Bundle extras) { }
	//- See more at: http://blog.rolandl.fr/1230-android-la-geolocalisation-et-lapi-google-maps-android-v2#sthash.VgPYHi5R.dpuf



}
