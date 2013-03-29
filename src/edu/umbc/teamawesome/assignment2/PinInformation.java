package edu.umbc.teamawesome.assignment2;

public class PinInformation {
	int id;
	long time;
	
	double latitude;
	double longitude;
	
	double accel_x;
	double accel_y;
	double accel_z;
	
	double orient_x;
	double orient_y;
	double orient_z;
	
	double lx;
	
	String activity;
	
	public PinInformation() {}

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

	public double getAccel_x() {
		return accel_x;
	}

	public void setAccel_x(double accel_x) {
		this.accel_x = accel_x;
	}

	public double getAccel_y() {
		return accel_y;
	}

	public void setAccel_y(double accel_y) {
		this.accel_y = accel_y;
	}

	public double getAccel_z() {
		return accel_z;
	}

	public void setAccel_z(double accel_z) {
		this.accel_z = accel_z;
	}

	public double getOrient_x() {
		return orient_x;
	}

	public void setOrient_x(double orient_x) {
		this.orient_x = orient_x;
	}

	public double getOrient_y() {
		return orient_y;
	}

	public void setOrient_y(double orient_y) {
		this.orient_y = orient_y;
	}

	public double getOrient_z() {
		return orient_z;
	}

	public void setOrient_z(double orient_z) {
		this.orient_z = orient_z;
	}

	public double getLx() {
		return lx;
	}

	public void setLx(double lx) {
		this.lx = lx;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}
	
	@Override
	public String toString() {
		return "<" + this.getLongitude() + ", " + this.getLatitude() + ">";
	}
}
