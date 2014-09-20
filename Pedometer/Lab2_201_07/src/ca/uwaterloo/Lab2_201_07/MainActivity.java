package ca.uwaterloo.Lab2_201_07;

import java.util.Arrays;

import ca.uwaterloo.Lab2_201_07.R;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

	// TextViews for titles and for data
	TextView accelTitle, accelData, recAccelTitle, recAccelData;

	// Variables to store record data
	double accelMaxX, accelMaxY, accelMaxZ, smoothedAccelMax;

	LineGraphView accelGraph;

	static double stepcount = 0;

	public static enum State {
		state_down, state_rise, state_up, state_fall
	}

	State currentState = State.state_fall;
	float MAX = 0.7f, MIN = -0.4f, UPPER_THRESHHOLD = 0.20f,
			LOWER_THRESHHOLD = -0.10f;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		LinearLayout layout = (LinearLayout) findViewById(R.id.parent);
		layout.setOrientation(LinearLayout.VERTICAL);

		final Button button = (Button) findViewById(R.id.clearButton);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Clear();
			}
		});

		// Set up graph
		accelGraph = new LineGraphView(getApplicationContext(), 100,
				Arrays.asList("x", "y", "z"));
		accelGraph.setBackgroundColor(Color.BLACK);
		accelGraph.setColor(Color.WHITE);
		layout.addView(accelGraph);
		accelGraph.setVisibility(View.VISIBLE);

		// Initialize record data to 0
		accelMaxX = 0;
		accelMaxY = 0;
		accelMaxZ = 0;
		smoothedAccelMax = 0;

		// Create necessary TextViews
		accelTitle = new TextView(getApplicationContext());
		accelTitle.setText("Acceleration [Linear] (m/s^2)");

		accelData = new TextView(getApplicationContext());

		recAccelTitle = new TextView(getApplicationContext());
		recAccelTitle.setText("Record Acceleration [Linear] (m/s^2)");

		recAccelData = new TextView(getApplicationContext());
		recAccelData.setTextColor(Color.GREEN);

		layout.addView(accelTitle);
		layout.addView(accelData);
		layout.addView(recAccelTitle);
		layout.addView(recAccelData);

		layout.setBackgroundColor(Color.BLACK);

		// Implementing sensors
		SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		// acceleration
		Sensor accelSensor = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		SensorEventListener accelListener = new AccelerationSensorEventListener(
				accelData, recAccelData);
		sensorManager.registerListener(accelListener, accelSensor,
				SensorManager.SENSOR_DELAY_FASTEST);
		return true;
	}

	// Classes for each sensor to handle events, record values, and to update
	// TextViews
	class AccelerationSensorEventListener implements SensorEventListener {
		TextView outputTextView, recordTextView;
		float smoothedAccel = 0;
		float[] data = new float[3];
		long lastStepTime = 0;

		public AccelerationSensorEventListener(TextView accelerationView,
				TextView recAccelerationView) {
			outputTextView = accelerationView;
			recordTextView = recAccelerationView;
		}

		public void FSM(float f) {
			if (f > MAX || f < MIN) {
				currentState = State.state_fall;
				lastStepTime = System.currentTimeMillis();
				return;
			}
			if (f < LOWER_THRESHHOLD && f > MIN
					&& currentState == State.state_fall) {
				currentState = State.state_down;
			}
			if (f > LOWER_THRESHHOLD && f < UPPER_THRESHHOLD
					&& currentState == State.state_down) {
				currentState = State.state_rise;
			}
			if (f > UPPER_THRESHHOLD && f < MAX
					&& currentState == State.state_rise) {
				currentState = State.state_up;

				
			}
			if (f > LOWER_THRESHHOLD && f < UPPER_THRESHHOLD
					&& currentState == State.state_up) {
				currentState = State.state_fall;
				if (System.currentTimeMillis() - lastStepTime > 300) {
					stepcount++;
				}
				lastStepTime = System.currentTimeMillis();
			}

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

				if (Math.abs(x) > Math.abs(accelMaxX)) {
					accelMaxX = x;
				}
				if (Math.abs(y) > Math.abs(accelMaxY)) {
					accelMaxY = y;
				}
				if (Math.abs(z) > Math.abs(accelMaxZ)) {
					accelMaxZ = z;
				}

				data[0] = LOWER_THRESHHOLD;
				data[1] = UPPER_THRESHHOLD;
				smoothedAccel += (z - smoothedAccel) / 40;
				smoothedAccel = smoothedAccel - 0.24f;
				data[2] = smoothedAccel;
				FSM(smoothedAccel);

				if (Math.abs(smoothedAccel) > Math.abs(smoothedAccelMax)) {
					smoothedAccelMax = smoothedAccel;
				}

				dataString = String
						.format(" x: %.2f\n y: %.2f\n z: %.2f\nStep Count: %f\nState: %s\nTime from last check: %d\nMax smoothed Accell: %.2f",
								accelMaxX, accelMaxY, accelMaxZ, stepcount,
								currentState.toString(),
								System.currentTimeMillis() - lastStepTime,
								smoothedAccelMax);
				recordTextView.setText(dataString);

				accelGraph.addPoint(data);
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// Clears graph and resets max values
	public void Clear() {
		accelMaxX = 0;
		accelMaxY = 0;
		accelMaxZ = 0;
		accelGraph.purge();
		smoothedAccelMax = 0;
		stepcount = 0;
	}

	public void ClearSteps(View v) {
		stepcount = 0;
		currentState = State.state_fall;
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
