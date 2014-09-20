package com.example.test2;

import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.view.Gravity;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

public class MainActivity extends Activity implements SurfaceTextureListener {
	LineGraphView accelGraph;
	private Camera mCamera;
	private TextureView mTextureView;
	
	Canvas c;
	public Paint myPaint = new Paint();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mTextureView = new TextureView(this);
		mTextureView.setSurfaceTextureListener(this);
		//mTextureView.setAlpha(0.7f);
		
		c = new Canvas();
		myPaint.setColor(Color.BLUE);
		myPaint.setStyle(Paint.Style.STROKE);
		myPaint.setStrokeWidth(3);
		c.drawRect(10, 10, 20, 20, myPaint);
		
		mTextureView.draw(c);
		
		setContentView(mTextureView);
	}

	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture surface, int width,
			int height) {
		mCamera = Camera.open();
		mCamera.setDisplayOrientation(90);
		Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
		LayoutParams params = new FrameLayout.LayoutParams(previewSize.height,
				previewSize.width, Gravity.CENTER);

		mTextureView.setLayoutParams(params);

		try {
			mCamera.setPreviewTexture(surface);
		} catch (IOException t) {
		}

		mCamera.startPreview();

	}

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width,
			int height) {
		// Ignored, the Camera does all the work for us
	}

	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
		mCamera.stopPreview();
		mCamera.release();
		return true;
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture surface) {
		// Update your view here!
	}

	class AccelerationSensorEventListener implements SensorEventListener {
		TextView outputTextView, recordTextView;

		public AccelerationSensorEventListener(TextView accelerationView,
				TextView recAccelerationView) {
			outputTextView = accelerationView;
			recordTextView = recAccelerationView;
		}

		public void onAccuracyChanged(Sensor s, int i) {
		}

		public void onSensorChanged(SensorEvent se) {
			if (se.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				// TODO put se.values[0] somewhere useful
				double x = se.values[0];
				double y = se.values[1];
				double z = se.values[2];

				String dataString = String.format(
						" x: %.2f\n y: %.2f\n z: %.2f", x, y, z);
				outputTextView.setText(dataString);

				accelGraph.addPoint(se.values);
			}
		}

	}
}
