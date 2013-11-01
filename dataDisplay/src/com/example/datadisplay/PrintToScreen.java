package com.example.datadisplay;

import java.util.TimerTask;

import android.os.Looper;

public class PrintToScreen extends TimerTask{

	    // print the collect m.globalData to the screen
		// only print values that have been updated to save time

	@Override
	public void run() {
		Looper.prepare();
		MainActivity m = new MainActivity();			// to use methods in main activity
		
    	m.GPS_speed_display.setText(Double.toString(m.globalData.GPS_speed));
    	m.GPS_lat_display.setText(Double.toString(m.globalData.GPS_lat));
    	m.GPS_lon_display.setText(Double.toString(m.globalData.GPS_lon));
    	m.GPS_alt_display.setText(Double.toString(m.globalData.GPS_alt));
    	m.GPS_acc_display.setText(Double.toString(m.globalData.GPS_accur));
    	m.GPS_bearing_display.setText(Double.toString(m.globalData.GPS_bearing));

		m.Accel_X_display.setText(Double.toString(m.globalData.AccelX));
		m.Accel_Y_display.setText(Double.toString(m.globalData.AccelY));
		m.Accel_Z_display.setText(Double.toString(m.globalData.AccelZ));	

		m.Gyro_X_display.setText(Double.toString(m.globalData.GyroX));
		m.Gyro_Y_display.setText(Double.toString(m.globalData.GyroY));
		m.Gyro_Z_display.setText(Double.toString(m.globalData.GyroZ));

		m.Barom_alt_display.setText(Double.toString(m.globalData.Barom_alt));
		
		Looper.loop();
    }
}
