package edu.umbc.teamawesome.assignment2;

import java.util.Date;
import java.util.Locale;

import android.location.Address;

public class PinInformation {
	int id;
	long time;
	
	double latitude;
	double longitude;
	
	Address address;
	
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Address getAddress() {
		return address;
	}

	public String getAddressString() {
		String addressString = "";
		if(address != null)
		{
			String tempString = address.getAddressLine(0);
			if(tempString != null && !tempString.isEmpty())
			{
				addressString += tempString + ", ";
			}
			
			tempString = address.getAddressLine(1);
			if(tempString != null && !tempString.isEmpty())
			{
				addressString += tempString + ", ";
			}
			
			tempString = address.getLocality();
			if(tempString != null && !tempString.isEmpty())
			{
				addressString += tempString + ", ";
			}
			
			tempString = address.getAdminArea();
			if(tempString != null && !tempString.isEmpty())
			{
				addressString += tempString + " ";
			}
			
			tempString = address.getPostalCode();
			if(tempString != null && !tempString.isEmpty())
			{
				addressString += tempString;
			}
		}
		return addressString;
	}
	
	public void setAddress(Address address) {
		this.address = address;
	}
	
	public String getDate()
	{
		return new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US).format(new Date(getTime()));
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
		return String.valueOf(getId()) + ": " + new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US).format(new Date(getTime()));
	}
}
