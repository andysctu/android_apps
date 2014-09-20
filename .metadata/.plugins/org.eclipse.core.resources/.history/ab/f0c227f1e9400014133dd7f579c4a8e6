package com.example.test4;

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

			distance = calculateDistance(z);
			if (theta == 0.0174532925) {
				distanceText = String.format("Distance to Line: %.2f meters +",
						distance);
			} else {
				distanceText = String.format("Distance to Line: %.2f meters",
						distance);
			}
			outputTextView.setText(distanceText);
			outputTextView.setTextColor(Color.GREEN);

		}
	}

	public double calculateDistance(double g) {
		theta = Math.asin(g / 9.81);
		if (theta <= 0) {
			theta = 0.0174532925;
		}
		double distance = 0.01 * (height * Math.tan(Math.PI / 2 - theta));

		return distance;
	}
}