package com.example.datadisplay;

public class data {
	// global acceleration data to be updated
	void setAccelX(double value)
	{
		AccelX = value;
	}
	double AccelX;
	double AccelY;
	double AccelZ;
	// global gyroscope data
	double GyroX;
	double GyroY;
	double GyroZ;
	// global GPS data
	double GPS_lat;
	double GPS_lon;
	double GPS_alt;
	double GPS_accur;
	double GPS_bearing;
	double GPS_speed;
	
	public double Barom_alt;
	
}
