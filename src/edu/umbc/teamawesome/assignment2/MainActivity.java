package edu.umbc.teamawesome.assignment2;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.view.Menu;

public class MainActivity extends Activity implements LocationListener 
{

	MapFragment mapFragment;
	LocationManager locationManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		GoogleMapOptions options = new GoogleMapOptions();
		
		options.mapType(GoogleMap.MAP_TYPE_HYBRID).compassEnabled(true).rotateGesturesEnabled(true).tiltGesturesEnabled(true);
		mapFragment = MapFragment.newInstance(options);
	
		
		FragmentTransaction fragmentTransaction =
		         getFragmentManager().beginTransaction();
		 fragmentTransaction.add(R.id.map_container, mapFragment);
		 fragmentTransaction.commit();
		
		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 10, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}

}
