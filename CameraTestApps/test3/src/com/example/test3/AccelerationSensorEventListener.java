package com.example.test3;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;

class AccelerationSensorEventListener implements SensorEventListener {
	TextView outputTextView;
	double theta, distance = 0, defaultAccel;
	public static double height = 140;
	String distanceText;
	

	public AccelerationSensorEventListener(TextView distanceView) {
		outputTextView = distanceView;
	}

	public void onAccuracyChanged(Sensor s, int i) {
	}

	public void onSensorChanged(SensorEvent se) {
		if (se.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			// TODO put se.values[0] somewhere useful

			double z = se.values[2];

			

		}
	}

	public void calculateSpeed(){
		
	}

}