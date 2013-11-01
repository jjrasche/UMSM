package com.example.datadisplay;

import java.io.FileWriter;
import java.util.TimerTask;

import android.os.Looper;

public class WriteData extends TimerTask  {

	@Override
	public void run() {
		Looper.prepare();
		MainActivity m = new MainActivity();			// to use methods in main activity
		
    	if(m.mExternalStorageWriteable && m.mExternalStorageAvailable)
    	{
    		 FileWriter fWriter;
    		 try{
    			  fWriter = new FileWriter(m.file, true);
                  fWriter.write(m.constructMsg());
                  fWriter.flush();
                  fWriter.close();
              }catch(Exception e){
                      e.printStackTrace();
              }
    	}
		
		Looper.loop();

	}
}
