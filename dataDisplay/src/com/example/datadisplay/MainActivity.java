package com.example.datadisplay;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import android.R.bool;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

public class MainActivity extends Activity implements SensorEventListener {
	// oct 24: enable to operate when paused  (check)
	// oct25:  add start new test button
	 SensorManager mSensorManager;
	 Sensor mAccelerometer;
	 Sensor mGyroscope;
	 Sensor mBarometer;
	TextView Accel_X_display;
	TextView Accel_Y_display;
	TextView Accel_Z_display;
	 double mAccelLastX, mAccelLastY, mAccelLastZ;
	 double AccelNoise = .1;

	TextView Gyro_X_display;
	TextView Gyro_Y_display;
	TextView Gyro_Z_display;
	 double mGyroLastX, mGyroLastY, mGyroLastZ;
	 double GyroNoise = .01;	
	
	TextView Barom_alt_display;
	 float pressure;

	 // with no privacy designator only classes in this package can access variables
 	 TextView GPS_lat_display;
	 TextView GPS_lon_display;
	 TextView GPS_alt_display;
	 TextView GPS_acc_display;					
	 TextView GPS_bearing_display;
	 TextView GPS_locs_display;
	 TextView GPS_speed_display;
	 boolean isGPSEnabled = false;
     boolean isNetworkEnabled = false;
     Location location; // location
     final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 1 meters
     final long MIN_TIME_BW_UPDATES = 1000 * 2 * 1; // 2 seconds
     LocationManager locationManager;
    
    String fileName;
    boolean mExternalStorageAvailable = false;
    boolean mExternalStorageWriteable = false;
    File path;
    File file;
    long progStartTime;
    data globalData;
    int buttonPresses = 0;
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.findAllViewsById();
		initializeVariables();
		
		Handler handler = new Handler();
		handler.postDelayed(WriteData, 100);				// write 10 times a second
		handler.postDelayed(PrintToScreen, 200);			// print to screen 5 times a second
	}
	 
	
	// get a handle on the visual text boxes to change their values
	 void findAllViewsById() {
		Accel_X_display = (TextView) findViewById(R.id.Accel_Data_X);
		Accel_Y_display = (TextView) findViewById(R.id.Accel_Data_Y);
		Accel_Z_display = (TextView) findViewById(R.id.Accel_Data_Z);
		
		Gyro_X_display = (TextView) findViewById(R.id.Gyro_Data_X);
		Gyro_Y_display = (TextView) findViewById(R.id.Gyro_Data_Y);
		Gyro_Z_display = (TextView) findViewById(R.id.Gyro_Data_Z);	
		
		Barom_alt_display = (TextView) findViewById(R.id.BAROM_ALT);		
		
		GPS_lat_display = (TextView) findViewById(R.id.GPS_Data_Lat);
		GPS_lon_display = (TextView) findViewById(R.id.GPS_Data_Lon);
		GPS_alt_display = (TextView) findViewById(R.id.GPS_Data_Alt);
		GPS_acc_display = (TextView) findViewById(R.id.GPS_Data_Acc);
		GPS_bearing_display = (TextView) findViewById(R.id.GPS_Data_Bearing);
		GPS_speed_display = (TextView) findViewById(R.id.GPS_Data_Speed);
	}
	
    public void longToast(CharSequence message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
	
	// life cycle management, stop listener when paused, and start when resumed
	protected void onResume() {
		super.onResume();
//		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
	//	mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		//mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
//		mSensorManager.registerListener(this, mBarometer, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
		// don't disable listeners on Pause so app doesn't have to be visible to collect data
	protected void onPause() {
		super.onPause();
//		mSensorManager.unregisterListener(this);
	//	locationManager.removeUpdates(locationListener);
	}

	// Define a listener that responds to location updates
	LocationListener locationListener = new LocationListener() {
	   public void onLocationChanged(Location location) {
	      // Called when a new location is found by the network location provider.
	      makeUseOfNewLocation(location);
	    }

	    public void onStatusChanged(String provider, int status, Bundle extras) {}

	    public void onProviderEnabled(String provider) {}

	    public void onProviderDisabled(String provider) {}
	  };
	
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		
		Sensor sensor = event.sensor;
		
		if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			
			double x = event.values[0];
			double y = event.values[1];
			double z = event.values[2];		
			double deltaX = Math.abs(mAccelLastX - x);
			double deltaY = Math.abs(mAccelLastY - y);
			double deltaZ = Math.abs(mAccelLastZ - z);

			//only register a change if new value is above noise threshold 
			if (deltaX > AccelNoise) globalData.AccelX = x;
			if (deltaY > AccelNoise) globalData.AccelY = y;
			if (deltaZ > AccelNoise) globalData.AccelZ = z;

			mAccelLastX = x;
			mAccelLastY = y;
			mAccelLastZ = z;

		//	writeData();
//	    	printToScreen("Accel");
			
		} else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
			double x = event.values[0];
			double y = event.values[1];
			double z = event.values[2];
			
			double deltaX = Math.abs(mGyroLastX - x);
			double deltaY = Math.abs(mGyroLastY - y);
			double deltaZ = Math.abs(mGyroLastZ - z);
			
			if (deltaX > GyroNoise) globalData.GyroX = x;
			if (deltaY > GyroNoise) globalData.GyroY = y;
			if (deltaZ > GyroNoise) globalData.GyroZ = z;
			
			mGyroLastX = x;
			mGyroLastY = y;
			mGyroLastZ = z;
		
//	    	writeDataToFile();
//	    	printToScreen("Gyro");

		} else if (sensor.getType() == Sensor.TYPE_PRESSURE) {
			
			pressure = event.values[0];
			globalData.Barom_alt = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure);

//	    	writeDataToFile();
//	    	printToScreen("Barom");
		}
	}
	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}
	
	
     void makeUseOfNewLocation(Location location) {
    	globalData.GPS_lat = location.getLatitude();
    	globalData.GPS_lon = location.getLongitude();
    	globalData.GPS_alt = location.getAltitude();
    	globalData.GPS_accur = location.getAccuracy();
    	globalData.GPS_bearing = location.getBearing();
    	globalData.GPS_speed = location.getSpeed();	
//    	writeDataToFile();
//    	printToScreen("GPS");
    }
	
    // print the collect globalData to the screen
    // only print values that have been updated to save time
     void printToScreen()
    {
        	GPS_speed_display.setText(Double.toString(globalData.GPS_speed));
        	GPS_lat_display.setText(Double.toString(globalData.GPS_lat));
        	GPS_lon_display.setText(Double.toString(globalData.GPS_lon));
        	GPS_alt_display.setText(Double.toString(globalData.GPS_alt));
        	GPS_acc_display.setText(Double.toString(globalData.GPS_accur));
        	GPS_bearing_display.setText(Double.toString(globalData.GPS_bearing));

			Accel_X_display.setText(Double.toString(globalData.AccelX));
			Accel_Y_display.setText(Double.toString(globalData.AccelY));
			Accel_Z_display.setText(Double.toString(globalData.AccelZ));	

			Gyro_X_display.setText(Double.toString(globalData.GyroX));
			Gyro_Y_display.setText(Double.toString(globalData.GyroY));
			Gyro_Z_display.setText(Double.toString(globalData.GyroZ));

    		Barom_alt_display.setText(Double.toString(globalData.Barom_alt));
    }

    
     void writeData()
    {
		if(mExternalStorageWriteable && mExternalStorageAvailable)
		{
			 FileWriter fWriter;
			 try{
				  fWriter = new FileWriter(file, true);
	              fWriter.write(constructMsg());
	              fWriter.flush();
	              fWriter.close();
	          }catch(Exception e){
	                  e.printStackTrace();
	          }
		}
    }

	 void writeTitle()
	{	
		 try{
			 FileWriter fWriter = new FileWriter(file, false);		// false to erase all prev data
             fWriter.write("Time(sec),Latitude,Longitude,Altitude(GPS),Bearing,Speed,AccelX,AccelY,AccelZ,GyroX,GyroY,GyroZ,Altitude(Barom)  \n");
             fWriter.flush();
             fWriter.close();
         }catch(Exception e){
                  e.printStackTrace();
         }
	}
	
    public String constructMsg()
    {
    	String tmp = Double.toString(getTime()) + ",";		// time
    	tmp += Double.toString(globalData.GPS_lat) + "," + Double.toString(globalData.GPS_lon) + "," + Double.toString(globalData.GPS_alt) + "," + Double.toString(globalData.GPS_speed) + ","+ Double.toString(globalData.GPS_bearing) + ",";;
    	tmp += Double.toString(globalData.AccelX) + "," +  Double.toString(globalData.AccelY)  + "," + Double.toString(globalData.AccelZ) + ",";
    	tmp += Double.toString(globalData.GyroX) + "," +  Double.toString(globalData.GyroY)  + "," + Double.toString(globalData.GyroZ) + ",";
    	tmp += Double.toString(globalData.Barom_alt);
    	tmp += "\n";
    	return(tmp);
    }
    
     double getTime()
    {	// long cast should give to a thousandth of resolution
    	return(((float)(System.currentTimeMillis() - progStartTime))/1000.0);
    }
	
     boolean canWriteToStorage()
    {
    	String state = Environment.getExternalStorageState();

    	if (Environment.MEDIA_MOUNTED.equals(state)) {
    	    // We can read and write the media
    	    mExternalStorageAvailable = mExternalStorageWriteable = true;
    	    return(true);
    	} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
    	    // We can only read the media
    	    mExternalStorageAvailable = true;
    	    mExternalStorageWriteable = false;
    	    longToast("storage unWritable!!!");
    	    return(false);
    	} else {
    	    // Something else is wrong. It may be one of many other states, but all we need
    	    //  to know is we can neither read nor write
    	    mExternalStorageAvailable = mExternalStorageWriteable = false;
    	    longToast("storage unAvailable and unWritable!!!");
    	    return(false);
    	}
    }
    
	@SuppressLint("SimpleDateFormat")
	void setFileName()
	{
		/*// e.g. oct31_12:51:31
		Calendar c = Calendar.getInstance(); 
		fileName += Integer.toString(c.get(Calendar.MONTH)) + Integer.toString(c.get(Calendar.DAY_OF_MONTH));
		fileName += "_" + Integer.toString(c.get(Calendar.HOUR_OF_DAY)) + ":" + Integer.toString(c.get(Calendar.MINUTE));
		fileName += ":" + Integer.toString(c.get(Calendar.SECOND));
	*/
		
		Date d = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("MMMdd,hhmmss");
		fileName = formatter.format(d);
		fileName += ".csv";
		
		
	}

	void initializeVariables()
	{
		progStartTime = System.currentTimeMillis();		
		path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		setFileName();
		file = new File(path, "/" + fileName);
		if(canWriteToStorage())
			writeTitle();
		globalData = new data();
		
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
		mBarometer = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
		mSensorManager.registerListener(this, mBarometer, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	Handler handler = new Handler();


	 Runnable WriteData = new Runnable() {
	   @Override
	   public void run() {
	      try{
	              writeData();
	            }
	            catch(Exception e){
	                // added try catch block to be sure of uninterupted execution
	            }
	      /* and here comes the "trick" */
	      handler.postDelayed(this, 500);
	   }
	};
	
	 Runnable PrintToScreen = new Runnable() {
		   @Override
		   public void run() {
		      try{
		    	  	printToScreen();
		            }
		            catch(Exception e){
		                // added try catch block to be sure of uninterupted execution
		            }
		      /* and here comes the "trick" */
		      handler.postDelayed(this, 500);
		   }
		};
		
	public void onMyButtonClick(View view)
	{
		// set new fileName and write it's title, reset time
		setFileName();
	//	progStartTime = System.currentTimeMillis();
		file = new File(path, "/" + fileName);
		writeTitle();
		
		String msg = "New file started: " + fileName; 
		longToast(msg);
		buttonPresses++;
		
	}

	
}
