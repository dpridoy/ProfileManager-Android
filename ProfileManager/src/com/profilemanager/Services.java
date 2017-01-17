package com.profilemanager;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.IBinder;
import android.os.Vibrator;

public class Services extends Service implements SensorEventListener {
	public SensorManager smg;
	public Sensor sAcc,sPro;
	private float lastX=0,lastY=0,lastZ=0;
	private boolean onSurface=true,front=false,movement=false;
	AudioManager myAM;
	Vibrator vi;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		smg=(SensorManager)getSystemService(SENSOR_SERVICE);
		sAcc=smg.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sPro=smg.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		smg.registerListener(this, sPro,SensorManager.SENSOR_DELAY_NORMAL);
		smg.registerListener(this, sAcc,SensorManager.SENSOR_DELAY_NORMAL);
		
		myAM=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        vi = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		smg.unregisterListener(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return START_STICKY;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
			float x,y,z,firstX,firstY,firstZ;
			x=event.values[0];
			y=event.values[1];
			z=event.values[2];
			firstX=lastX;
			firstY=lastY;
			firstZ=lastZ;
			
			if(x<2.0 && x>(-2.0) && y<2.0 && y>(-2.0) && z<10.4 && z>8.6){
				onSurface=true;
			}
			else{
				onSurface=false;
			}
			if((x>firstX+0.5||x<firstX-0.5)||(y>firstY+0.5||y<firstY-0.5)||(z>firstZ+0.5||z<firstZ-0.5)){
				lastX=x;
				lastY=y;
				lastZ=z;
				movement=true;
			}
			else{
				movement=false;
			}		
		}
		if(event.sensor.getType()==Sensor.TYPE_PROXIMITY){
			if(event.values[0]==0){
				front=true;
			}
			else{
				front=false;
			}
		}
		
		if(front){
			//int max=myAM.getStreamMaxVolume(myAM.STREAM_RING);
			//myAM.setStreamVolume(AudioManager.STREAM_RING,max , 0);
			//vi.vibrate(1000);
			myAM.setRingerMode(myAM.RINGER_MODE_SILENT);
		}
		else if(movement){
			int max=myAM.getStreamMaxVolume(myAM.STREAM_RING);
			myAM.setStreamVolume(AudioManager.STREAM_RING,max , 0);
			//vi.cancel();
		}
		else{
			myAM.setRingerMode(myAM.RINGER_MODE_NORMAL);
		}
	}

	

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
