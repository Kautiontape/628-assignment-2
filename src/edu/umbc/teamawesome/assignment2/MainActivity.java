package edu.umbc.teamawesome.assignment2;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Criteria;

import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.TabListener;
import android.app.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener  {
	
	DatabaseHandler db = null;
	
	private Location lastLocation = null;
	private Location newLocation = null;	
	private float[] accel = new float[3];
	private float[] orient_r = new float[9];
	private float[] orient = new float[3];
	private float[] magnet = new float[3];
	private float lx = 0;
	private float prox = 0;
	private String activity = "";
	
	private ArrayAdapter<PinInformation> pinListAdapter;
	
	private int pins = 0;

	private static int defaultZoomLevel = 18;
	
	GoogleMap map;
	MapFragment mapFragment;
	LocationManager locationManager;
	List<PinInformation> pinList = new ArrayList<PinInformation>();
	//ArrayAdapter<PinInformation> lv_adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//createTabBar();
        
		registerLocationListener();
		registerSensorListeners();
		registerInterfaceListeners();
		
		createMap();
		
		instantiateFromDatabase();
		updatePins();
	}
	
	private void newLocation(Location newLocation) 
	{
		if(lastLocation != null) 
		{
			float distance = lastLocation.distanceTo(newLocation);
			if(distance > 100) {
				this.newLocation = newLocation;
				saveInformation();
				this.lastLocation = newLocation;
			}
		}
		else 
		{
			// we set need to set both locations to avoid null pointer exceptions in code above
			// will represent no movement, so not a semantic error
			this.lastLocation = newLocation;
			this.newLocation = newLocation;
			saveInformation();
		}
	}
	
	private void saveInformation() {		
		PinInformation pin = new PinInformation();
		pin.setId(++pins); // Pin count is updated here
		pin.setTime(System.currentTimeMillis());
		pin.setLongitude(newLocation.getLongitude());
		pin.setLatitude(newLocation.getLatitude());
		pin.setAccel_x(accel[0]);
		pin.setAccel_y(accel[1]);
		pin.setAccel_z(accel[2]);
		pin.setOrient_x(orient[0]);
		pin.setOrient_y(orient[1]);
		pin.setOrient_z(orient[2]);
		pin.setLx(lx);
		pin.setProx(prox);
		pin.setActivity(activity);
		
		Log.d(getPackageName(), "Added new pin");
		
		db.addEntry(pin);
		pinList.add(pin);
		pinListAdapter.add(pin);
		pinListAdapter.notifyDataSetChanged();
		addMarker(pin);
	}
	
	private void addMarker(PinInformation pin)
	{
		MarkerOptions marker = new MarkerOptions();
		marker.draggable(false);
		marker.position(new LatLng(pin.getLatitude(), pin.getLongitude()));
		marker.title(pin.activity.length() > 0 ? pin.activity : "Marker");
		
		String address = "";
		Address revGeo = pin.getAddress(this);
		if(revGeo != null) address = revGeo.getAddressLine(0) + "\n" + revGeo.getAddressLine(1);
		
		String time = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US).format(new Date(pin.getTime()));
		String disp = String.format(Locale.US,
				"Time: %s" + 
				(address.length() > 0 ? "%nAddress: " + address : "") +
				"%n<%f, %f>" +
				"%nAcceleration: (%.2f, %.2f, %.2f)" +
				"%nOrientation: (%.2f, %.2f, %.2f)" +
				"%nLx: %.2f" +
				"%nProximity: %.2f" +
				(pin.getActivity().length() > 0 ? "%nActivity: " + pin.getActivity() : ""),
				time,
				pin.getLongitude(), pin.getLatitude(), pin.getAccel_x(), 
				pin.getAccel_y(), pin.getAccel_z(), pin.getOrient_x(), 
				pin.getOrient_y(), pin.getOrient_z(), pin.getLx(), pin.getProx()
		);
		marker.snippet(disp);
		
		map.addMarker(marker);
	}
	
	private void updatePins() 
	{
		pinList = db.getAllPins();
		ListView lv = (ListView)findViewById(R.id.pinList);
		
		if(pinListAdapter == null)
		{
			pinListAdapter = new ArrayAdapter<PinInformation>(this, android.R.layout.simple_list_item_1, pinList);
		}
		else
		{
			pinListAdapter.clear();
			pinListAdapter.addAll(pinList);
			pinListAdapter.notifyDataSetChanged();
		}
		
		if(lv != null && lv.getAdapter() == null)
		{
			lv.setAdapter(pinListAdapter);
		}
		
		if(map != null && pinList != null)
		{
			map.clear();
			
			for(PinInformation pin: pinList)
			{
				addMarker(pin);
			}
		}
	}
	
	// http://www.androidsnippets.com/prompt-user-input-with-an-alertdialog
	private void promptActivity() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("628 Assignment 2");
		alert.setMessage("What are you currently doing?");
		
		final EditText input = new EditText(this);
		alert.setView(input);
		
		alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				activity = input.getText().toString();
				updateActivityView();
			 }
		});

		alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {}
		});
		
		alert.show();
	}
	
	private void registerSensorListeners() {
		SensorManager manager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		Sensor accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		Sensor magnetometer = manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		Sensor light = manager.getDefaultSensor(Sensor.TYPE_LIGHT);
		Sensor proximity = manager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

		manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		manager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
		manager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL);
		manager.registerListener(this, proximity, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	private void registerInterfaceListeners() {	
//		ListView lv = (ListView)findViewById(R.id.pinList);
//		lv_adapter = new ArrayAdapter<PinInformation>(this, android.R.layout.simple_list_item_1, pinList);
//		lv.setAdapter(lv_adapter);
		
		Button buttonClear = (Button)findViewById(R.id.buttonClear);
		buttonClear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				db.clearAll();
				updatePins();
			}
		});
		
		TextView activityText = (TextView)findViewById(R.id.textActivity);
		activityText.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				promptActivity();
			}
		});
	}
	
	private void registerLocationListener() {
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		
		LocationListener locationListener = new LocationListener() {
			
			@Override
			public void onLocationChanged(Location location) {
				newLocation(location);
			}
			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {}
			
			@Override
			public void onProviderEnabled(String provider) {}
			
			@Override
			public void onProviderDisabled(String provider) {}
		};
		
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
	}
	
	private void createMap() {		
		GoogleMapOptions options = new GoogleMapOptions();
		
		options.mapType(GoogleMap.MAP_TYPE_NORMAL).compassEnabled(true).rotateGesturesEnabled(true).tiltGesturesEnabled(true);
		
		if(mapFragment == null)
		{
			mapFragment = MapFragment.newInstance(options);
		
			FragmentTransaction fragmentTransaction =
					getFragmentManager().beginTransaction();
			fragmentTransaction.add(R.id.map_container, mapFragment);
			fragmentTransaction.commit();
		}
	}
	
	private void instantiateFromDatabase() {		
		db = new DatabaseHandler(this);
		pins = db.getPinCount();
		if(pins > 0) {
			PinInformation lastPin = db.getPin(pins);
			Location loc = new Location(LOCATION_SERVICE);
			loc.setLatitude(lastPin.getLatitude());
			loc.setLongitude(lastPin.getLongitude());
			newLocation = lastLocation = loc;
			activity = lastPin.getActivity();
			updateActivityView();
		}
	}
	
	@SuppressWarnings("unused")
	private void createTabBar() {

		final ActionBar bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
		
        bar.addTab(bar.newTab()
                .setText("Map")
                .setTabListener(new TabListener() {
					
					@Override
					public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {

						if(mapFragment != null && mapFragment.isVisible())
						{
							FragmentTransaction fragmentTransaction =
									getFragmentManager().beginTransaction();
							fragmentTransaction.hide(mapFragment);
							fragmentTransaction.commit();
						}
					}

					@Override
					public void onTabSelected(Tab arg0, FragmentTransaction arg1) {
						if(mapFragment != null && !mapFragment.isVisible())
						{
							FragmentTransaction fragmentTransaction =
									getFragmentManager().beginTransaction();
							fragmentTransaction.show(mapFragment);
							fragmentTransaction.commit();
						}
					}
					
					@Override
					public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
						
					}
				}));
        bar.addTab(bar.newTab()
                .setText("Pins")
                .setTabListener(new TabListener() {
					
					@Override
					public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
					}
					
					@Override
					public void onTabSelected(Tab arg0, FragmentTransaction arg1) {
					}
					
					@Override
					public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
					}
				}));
	}
	
	private void updateActivityView() 
	{
		TextView t = (TextView)findViewById(R.id.textActivity);
		if(activity.trim().length() == 0) 
		{
			t.setText(R.string.noactivity);
			t.setTypeface(null, Typeface.ITALIC);
		} 
		else 
		{
			t.setText(activity);
			t.setTypeface(null, Typeface.NORMAL);
		}		
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {}

	@Override
	public void onSensorChanged(SensorEvent event) 
	{
		switch (event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
			this.accel[0] = event.values[0];
			this.accel[1] = event.values[1];
			this.accel[2] = event.values[2];
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			this.magnet[0] = event.values[0];
			this.magnet[1] = event.values[1];
			this.magnet[2] = event.values[2];
			break;
		case Sensor.TYPE_LIGHT:
			this.lx = event.values[0];
			break;
		case Sensor.TYPE_PROXIMITY:
			this.prox = event.values[0];
			break;
		default:
			break;
		}
		
		SensorManager.getRotationMatrix(orient_r, null, accel, magnet);
		SensorManager.getOrientation(orient_r, orient);		

		
	}

	@Override
	public void onResume()
	{
		updatePins();

		if(map == null && mapFragment != null)
		{
			map = mapFragment.getMap();
			map.setMyLocationEnabled(true);
			
			map.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));
			
			updatePins();
		}
		
		Location currentLocation = null;
		if(locationManager != null)
		{
			currentLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), true));
			
			if(currentLocation != null)
			{
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), defaultZoomLevel));
			}
		}
		
	    super.onResume();
	}	
}
