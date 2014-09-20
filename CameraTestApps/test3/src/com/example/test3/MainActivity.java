package com.example.test3;

import java.io.IOException;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements SurfaceHolder.Callback {

	public static RelativeLayout l;
	Camera camera;
	SurfaceView surfaceView;
	SurfaceHolder surfaceHolder;
	boolean previewing = false;;
	TextView distanceView;
	EditText heightView;
	private float distance = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Button buttonStartCameraPreview = (Button)
		// findViewById(R.id.startcamerapreview);
		// Button buttonStopCameraPreview = (Button)
		// findViewById(R.id.stopcamerapreview);
		l = (RelativeLayout) findViewById(R.id.layout);
		distanceView = (TextView) findViewById(R.id.distanceview);
		heightView = (EditText) findViewById(R.id.heightview);
		heightView.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (isNumeric(s.toString())) {
					AccelerationSensorEventListener.height = Double
							.parseDouble(s.toString());
				}
			}
		});
		getWindow().setFormat(PixelFormat.UNKNOWN);
		surfaceView = (SurfaceView) findViewById(R.id.surfaceview);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		// acceleration
		Sensor accelSensor = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		SensorEventListener accelListener = new AccelerationSensorEventListener(
				distanceView);
		sensorManager.registerListener(accelListener, accelSensor,
				SensorManager.SENSOR_DELAY_NORMAL);

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		if (!previewing) {
			camera = Camera.open();
			if (camera != null) {
				try {
					camera.setDisplayOrientation(90);
					camera.setPreviewDisplay(surfaceHolder);
					camera.startPreview();
					// set camera to continually auto-focus
					Camera.Parameters params = camera.getParameters();
					params.setFocusMode("continuous-picture");
					camera.setParameters(params);
					previewing = true;
					
					Camera.Parameters p = camera.getParameters();
					double thetaV = Math.toRadians(p.getVerticalViewAngle());
					double thetaH = Math.toRadians(p.getHorizontalViewAngle());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		camera.stopPreview();
		camera.release();
		camera = null;
		previewing = false;
	}

	public void start(View v) {
		setContentView(R.layout.activity_main);
	}

	public static boolean isNumeric(String str) {
		try {
			double d = Double.parseDouble(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}
}
