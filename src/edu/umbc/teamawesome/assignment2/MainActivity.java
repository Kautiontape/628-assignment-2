package edu.umbc.teamawesome.assignment2;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity implements SensorEventListener {
	
	DatabaseHandler db = null;
	
	private Location lastLocation = null;
	private Location newLocation = null;	
	private double[] accel = new double[3];
	private double[] orient = new double[3];
	private double lx = 0;
	
	private int pins = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		registerLocationListener();
		SensorManager manager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		Sensor accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		Sensor orientation = manager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		Sensor light = manager.getDefaultSensor(Sensor.TYPE_LIGHT);

		manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
		manager.registerListener(this, orientation, SensorManager.SENSOR_DELAY_UI);
		manager.registerListener(this, light, SensorManager.SENSOR_DELAY_UI);
		
		db = new DatabaseHandler(this);
		db.clearAll();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void saveInformation() {
		PinInformation pin = new PinInformation();
		pin.setId(pins);
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
		pin.setActivity("");
		
		Log.d(getPackageName(), "Added new pin");
		
		db.addEntry(pin);
		
		pins++;
	}
	
	private void newLocation(Location newLocation) {
		if(lastLocation != null) {
			float distance = lastLocation.distanceTo(newLocation);
			if(distance > 100) {
				this.newLocation = newLocation;
				saveInformation();
				updateList();
				this.lastLocation = newLocation;
			}
		} else {
			this.lastLocation = newLocation;
			this.newLocation = newLocation;
			saveInformation();
		}
	}
	
	private void updateList() {
		ListView lv = (ListView)findViewById(R.id.pinList);
		ArrayAdapter<PinInformation> adapter = new ArrayAdapter<PinInformation>(getApplicationContext(), android.R.layout.simple_list_item_1, db.getAllPins());
		lv.setAdapter(adapter);
	}
	
	private void registerLocationListener() {
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		
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

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {}

	@Override
	public void onSensorChanged(SensorEvent event) {
		switch (event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
			this.accel[0] = event.values[0];
			this.accel[1] = event.values[1];
			this.accel[2] = event.values[2];
			break;
		case Sensor.TYPE_ORIENTATION:
			this.orient[0] = event.values[0];
			this.orient[1] = event.values[1];
			this.orient[2] = event.values[2];
			break;
		case Sensor.TYPE_LIGHT:
			this.lx = event.values[0];
		default:
			break;
		}
	}

}
