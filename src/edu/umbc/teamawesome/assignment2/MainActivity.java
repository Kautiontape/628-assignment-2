package edu.umbc.teamawesome.assignment2;

import java.util.Date;
import java.util.Locale;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener {
	
	DatabaseHandler db = null;
	
	private Location lastLocation = null;
	private Location newLocation = null;	
	private float[] accel = new float[3];
	private float[] orient_r = new float[9];
	private float[] orient = new float[3];
	private float[] magnet = new float[3];
	private double lx = 0;
	
	private int pins = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		registerLocationListener();
		registerSensorListeners();
		registerInterfaceListeners();
		
		db = new DatabaseHandler(this);
		db.clearAll();
	}
	
	private void newLocation(Location newLocation) {
		if(lastLocation != null) {
			float distance = lastLocation.distanceTo(newLocation);
			if(distance > 100) {
				this.newLocation = newLocation;
				saveInformation();
				this.lastLocation = newLocation;
			}
		} else {
			this.lastLocation = newLocation;
			this.newLocation = newLocation;
			saveInformation();
		}
		
		updatePins();
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
		promptActivity(pin.getId());
		
		pins++;
	}
	
	private void updatePins() {
		ListView lv = (ListView)findViewById(R.id.pinList);
		ArrayAdapter<PinInformation> adapter = new ArrayAdapter<PinInformation>(this, android.R.layout.simple_list_item_1, db.getAllPins());
		lv.setAdapter(adapter);
	}
	
	// http://www.androidsnippets.com/prompt-user-input-with-an-alertdialog
	private void promptActivity(int id) {
		final int pinid = id + 1; // TODO: Figure out why I have to add 1
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("628 Assignment 2");
		alert.setMessage("What are you currently doing?");
		
		final EditText input = new EditText(this);
		alert.setView(input);
		
		alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String activity = input.getText().toString();
				updatePinActivity(pinid, activity);
				updatePins();
			 }
		});

		alert.setNegativeButton("Nothing", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {}
		});
		
		alert.show();
	}
	
	private void updatePinActivity(int pinid, String activity) {
		PinInformation pin = db.getPin(pinid);
		pin.setActivity(activity);
		db.updatePin(pin);
		Log.d(getPackageName(), "Updated pin " + pinid);
	}
	
	private void registerSensorListeners() {
		SensorManager manager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		Sensor accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		Sensor magnetometer = manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		Sensor light = manager.getDefaultSensor(Sensor.TYPE_LIGHT);

		manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
		manager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
		manager.registerListener(this, light, SensorManager.SENSOR_DELAY_UI);
		
		SensorManager.getRotationMatrix(orient_r, null, accel, magnet);
		SensorManager.getOrientation(orient_r, orient);
	}
	
	private void registerInterfaceListeners() {
		ListView lv = (ListView)findViewById(R.id.pinList);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				PinInformation pin = (PinInformation)arg0.getItemAtPosition(position);
				String disp = String.format(Locale.US,
						"Time: %s" + 
						"%n<%f, %f>" +
						"%nAccel: (%.2f, %.2f, %.2f)" +
						"%nOrient: (%.2f, %.2f, %.2f)" +
						"%nLx: %.2f" +
						(pin.getActivity().length() > 0 ? "%nActivity: %s" : ""),
						new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US).format(new Date(pin.getTime())),
						pin.getLongitude(), pin.getLatitude(), pin.getAccel_x(), 
						pin.getAccel_y(), pin.getAccel_z(), pin.getOrient_x(), 
						pin.getOrient_y(), pin.getOrient_z(), pin.getLx(), 
						pin.getActivity()
				);
				Toast.makeText(getApplicationContext(), disp, Toast.LENGTH_SHORT).show();
			}
		});
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
		case Sensor.TYPE_MAGNETIC_FIELD:
			this.magnet[0] = event.values[0];
			this.magnet[1] = event.values[1];
			this.magnet[2] = event.values[2];
		case Sensor.TYPE_LIGHT:
			this.lx = event.values[0];
		default:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
