package edu.umbc.teamawesome.assignment2;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

public class PinInformation {
	int id;
	long time;
	
	double latitude;
	double longitude;
	
	float accel_x;
	float accel_y;
	float accel_z;
	
	float orient_x;
	float orient_y;
	float orient_z;
	
	float lx;
	float prox;
	
	String activity;
	
	public PinInformation() {}
	
	public Address getAddress(Context c) {
		
		List<Address> addressList = null;
		Geocoder geocoder = new Geocoder(c, Locale.getDefault());   
		try {
			addressList = geocoder.getFromLocation(getLatitude(), getLongitude(), 1);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		if(addressList != null && !addressList.isEmpty())
		{
			return addressList.get(0);
		}
		return null;
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public float getAccel_x() {
		return accel_x;
	}

	public void setAccel_x(float accel_x) {
		this.accel_x = accel_x;
	}

	public float getAccel_y() {
		return accel_y;
	}

	public void setAccel_y(float accel_y) {
		this.accel_y = accel_y;
	}

	public float getAccel_z() {
		return accel_z;
	}

	public void setAccel_z(float accel_z) {
		this.accel_z = accel_z;
	}

	public float getOrient_x() {
		return orient_x;
	}

	public void setOrient_x(float orient_x) {
		this.orient_x = orient_x;
	}

	public float getOrient_y() {
		return orient_y;
	}

	public void setOrient_y(float orient_y) {
		this.orient_y = orient_y;
	}

	public float getOrient_z() {
		return orient_z;
	}

	public void setOrient_z(float orient_z) {
		this.orient_z = orient_z;
	}

	public float getLx() {
		return lx;
	}

	public void setLx(float lx) {
		this.lx = lx;
	}
	
	public float getProx() {
		return prox;
	}
	
	public void setProx(float prox) {
		this.prox = prox;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}
	
	@Override
	public String toString() {
		if(getActivity() != null && !getActivity().isEmpty())
			return String.valueOf(getId()) + ": " + getActivity() + ": " + new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US).format(new Date(getTime()));
		return String.valueOf(getId()) +  ": " + new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US).format(new Date(getTime()));
	}
	
	public String getSnippet(Context context)
	{
		String address = "";
		Address revGeo = getAddress(context);
		if(revGeo != null) address = revGeo.getAddressLine(0) + "\n" + revGeo.getAddressLine(1);
		
		String time = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US).format(new Date(getTime()));
		String disp = String.format(Locale.US,
				"Time: %s" + 
				(address.length() > 0 ? "%nAddress: " + address : "") +
				"%n<%f, %f>" +
				"%nAcceleration: (%.2f, %.2f, %.2f)" +
				"%nOrientation: (%.2f, %.2f, %.2f)" +
				"%nLx: %.2f" +
				"%nProximity: %.2f" +
				(getActivity().length() > 0 ? "%nActivity: " + getActivity() : ""),
				time,
				getLongitude(), getLatitude(), getAccel_x(), 
				getAccel_y(), getAccel_z(), getOrient_x(), 
				getOrient_y(), getOrient_z(), getLx(), getProx()
		);
		
		return disp;
	}
	
}
