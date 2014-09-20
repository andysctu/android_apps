package ca.uwaterloo.Lab4_201_07;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.uwaterloo.Lab4_201_07.R;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

	// TextViews for data
	TextView accelData, recAccelData, magData, recMagData, directions;

	// Number of steps east, north, and total
	static double east = 0, north = 0, stepcount = 0;

	// Arrays to store sensor data
	float[] gravity, magnetic;

	// Angle from North (radians), and calibration
	static float azimuth = 0;
	float calibration = 0, prevAzimuth = 0;

	String message = "Follow the red line", NS, EW;

	int stepsX, stepsY;

	LineGraphView accelGraph;
	MapView mv;

	List<PointF> path = new ArrayList<PointF>();

	PointF userpos;
	public static NavigationalMap map1;
	NavigationalMap map2;
	NavigationalMap map3;
	NavigationalMap map4;
	NavigationalMap map5;
	NavigationalMap map6;
	NavigationalMap map7;
	NavigationalMap map8;
	PositionEventListener pchange;

	public float stepLength = (float) 0.6;

	// States for finite state machine
	public static enum State {
		state_down, state_rise, state_up, state_fall
	}

	State currentState = State.state_fall;

	// Step recognition boundaries
	float MAX = 2.0f, MIN = -2.0f, UPPER_THRESHHOLD = 0.20f,
			LOWER_THRESHHOLD = -0.10f;

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		mv.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		return super.onContextItemSelected(item)
				|| mv.onContextItemSelected(item);
	}

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

		final Button clearButton = (Button) findViewById(R.id.clearButton);
		clearButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Clear();
			}
		});

		final Button calibrateButton = (Button) findViewById(R.id.calibrateButton);
		calibrateButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Calibrate();
			}
		});

		final Button simulateStepButton = (Button) findViewById(R.id.stepButton);
		simulateStepButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				TakeStep();
			}
		});

		final Button simulateUpButton = (Button) findViewById(R.id.upButton);
		simulateUpButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				TakeStep(0);
			}
		});

		final Button simulateStepDown = (Button) findViewById(R.id.downButton);
		simulateStepDown.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				TakeStep(Math.PI);
			}
		});

		final Button simulateStepLeft = (Button) findViewById(R.id.leftButton);
		simulateStepLeft.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				TakeStep(-Math.PI / 2);
			}
		});

		final Button simulateStepRight = (Button) findViewById(R.id.rightButton);
		simulateStepRight.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				TakeStep(Math.PI / 2);
			}
		});

		// Set up map
		mv = new MapView(getApplicationContext(), 400, 300, 23, 23);
		registerForContextMenu(mv);
		map7 = MapLoader.loadMap(getExternalFilesDir(null),
				"Lab-room-peninsula.svg");
		map2 = MapLoader.loadMap(getExternalFilesDir(null),
				"Lab-room-peninsula-16deg.svg");
		map3 = MapLoader.loadMap(getExternalFilesDir(null),
				"Lab-room-peninsula-9.4deg.svg");
		map4 = MapLoader.loadMap(getExternalFilesDir(null),
				"Lab-room-inclined-9.4deg.svg");
		map5 = MapLoader.loadMap(getExternalFilesDir(null),
				"Lab-room-inclined-16deg.svg");
		map6 = MapLoader.loadMap(getExternalFilesDir(null),
				"Lab-room-unconnected.svg");
		map1 = MapLoader.loadMap(getExternalFilesDir(null), "Lab-room.svg");
		mv.setMap(map1);
		userpos = mv.getUserPoint();
		mv.setOriginPoint(new PointF(0, 0));
		mv.setDestinationPoint(new PointF(0, 0));

		pchange = new PositionEventListener();
		mv.addListener(pchange);
		// Set up graph
		accelGraph = new LineGraphView(getApplicationContext(), 100,
				Arrays.asList("x", "y", "z"));
		accelGraph.setBackgroundColor(Color.BLACK);
		accelGraph.setColor(Color.WHITE);
		accelGraph.setVisibility(View.VISIBLE);

		// Create necessary TextViews
		accelData = new TextView(getApplicationContext());
		recAccelData = new TextView(getApplicationContext());
		recAccelData.setTextColor(Color.GREEN);

		magData = new TextView(getApplicationContext());
		recMagData = new TextView(getApplicationContext());
		directions = new TextView(getApplicationContext());
		getDirections();

		layout.addView(recAccelData);
		layout.addView(directions);
		layout.addView(mv);
		// layout.addView(accelGraph);
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

		// magnetic field
		Sensor magFieldSensor = sensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

		SensorEventListener magFieldListener = new MagneticFieldSensorEventListener(
				magData, recMagData);
		sensorManager.registerListener(magFieldListener, magFieldSensor,
				SensorManager.SENSOR_DELAY_NORMAL);

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
					TakeStep();
				}
				lastStepTime = System.currentTimeMillis();
			}
		}

		public void onAccuracyChanged(Sensor s, int i) {
		}

		public void onSensorChanged(SensorEvent se) {
			if (se.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

				mv.setUserPoint(mv.getUserPoint().x, mv.getUserPoint().y);

				// TODO put se.values[0] somewhere useful
				double x = se.values[0];
				double y = se.values[1];
				double z = se.values[2];

				gravity = se.values;

				data[0] = LOWER_THRESHHOLD;
				data[1] = UPPER_THRESHHOLD;
				smoothedAccel += (z - smoothedAccel) / 40;
				smoothedAccel = smoothedAccel - 0.24f;
				data[2] = smoothedAccel;
				FSM(smoothedAccel);

				accelGraph.addPoint(data);

				if (gravity != null && magnetic != null) {
					float R[] = new float[9];
					float I[] = new float[9];
					boolean success = SensorManager.getRotationMatrix(R, I,
							gravity, magnetic);
					if (success) {
						float orientation[] = new float[3];
						SensorManager.getOrientation(R, orientation);

						if (prevAzimuth == 0)
							prevAzimuth = orientation[0];
						azimuth = ((orientation[0] + calibration + prevAzimuth) / 2); // orientation
																						// contains:
						// azimuth, pitch and roll
						prevAzimuth = azimuth;
					}
				}

				// String dataString = String
				// .format("Total Steps: %.0f\nAzimuth: %.2f\nCalibration: %.2f\nSteps East: %.2f\nSteps North: %.2f\n%s\nUser X,Y: %s",
				// stepcount, azimuth * (360 / (2 * 3.14159f)),
				// calibration * (360 / (2 * 3.14159f)), east,
				// north, message,
				// mv.getUserPoint().x + ", "
				// + mv.getUserPoint().y);

				String dataString = String.format("%s\nUser X,Y: %s", message,
						Math.round(mv.getUserPoint().x*100)/100 + ", " + Math.round(mv.getUserPoint().y*100)/100);

				recordTextView.setText(dataString);
			}
		}
	}

	class PositionEventListener implements PositionListener {

		@Override
		public void originChanged(MapView sourceMapView, PointF location) {
			sourceMapView.setUserPoint(location);
			sourceMapView.setOriginPoint(location);
			userpos = mv.getOriginPoint();

			openPoints.clear();
			closedPoints.clear();
			path.clear();
			// findPath(sourceMapView.getUserPoint(), mv.getDestinationPoint());

			if (checkBoundary(mv.getOriginPoint())
					&& checkBoundary(mv.getDestinationPoint())
					&& !checkIslands(mv.getOriginPoint())
					&& !checkIslands(mv.getDestinationPoint())) {
				findPath(mv.getUserPoint(), mv.getDestinationPoint());
			} else {
				path.clear();
			}
			mv.setUserPath(path);
			getDirections();
		}

		public boolean checkBoundary(PointF p) {
			return (p.x > 2 && p.x < 17 && p.y > 2 && p.y < 12);
		}

		public boolean checkIslands(PointF p) {
			return ((p.x > 4.7 && p.x < 6.2 && p.y > 2 && p.y < 8.2)
					|| (p.x > 8.4 && p.x < 10.5 & p.y > 2 && p.y < 8.2) || (p.x > 12.4
					&& p.x < 14.5 && p.y > 2 && p.y < 8.2));
		}

		@Override
		// call this method whenever the destination is changed
		public void destinationChanged(MapView sourceMapView, PointF dest) {
			// TODO Auto-generated method stub
			sourceMapView.setDestinationPoint(dest);

			openPoints.clear();
			closedPoints.clear();
			path.clear();
			// findPath(sourceMapView.getUserPoint(), dest);

			// finds path from origin to destination
			if (checkBoundary(mv.getOriginPoint())
					&& checkBoundary(mv.getDestinationPoint())
					&& !checkIslands(mv.getOriginPoint())
					&& !checkIslands(mv.getDestinationPoint())) {
				findPath(mv.getUserPoint(), dest);
			} else {
				path.clear();
			}

			mv.setUserPath(path);
			getDirections();
		}
	}

	public void findPath2(PointF start, PointF finish) {
		path.clear();
		if (isClearPath(start, finish)) {
			path.add(start);
			path.add(finish);
		} else {
			path.add(start);
			List<InterceptPoint> interceptions = map1.calculateIntersections(
					start, finish);
			PointF currentPoint = start;
			for (int i = 0; i < interceptions.size(); i++) {
				PointF intersectionPoint = interceptions.get(i).getPoint();
				intersectionPoint.y = (float) 9.6;

				currentPoint = intersectionPoint;

				// add the fixed intercept point to the path
				path.add(currentPoint);
				// interceptions.remove(i);
			}
			path.add(finish);
		}
	}

	// clear path first, add start and finish to path
	public void findPath(PointF start, PointF finish) {
		path.clear();
		if (isClearPath(start, finish)) {
			path.add(start);
			path.add(finish);
		} else {
			path.add(start);
			List<InterceptPoint> interceptions = map1.calculateIntersections(
					start, finish);
			PointF currentPoint = start;
			for (int i = 0; i < interceptions.size(); i++) {
				PointF intersectionPoint = interceptions.get(i).getPoint();
				float yIntersect = intersectionPoint.y;
				while (!isClearPath(currentPoint, intersectionPoint)) {
					if (yIntersect <= 8.5) {
						intersectionPoint.y += 0.2;
					} else if (yIntersect >= 10)
						intersectionPoint.y -= 0.2;
					if (intersectionPoint.y < 2 || intersectionPoint.y > 11){
						break;
					}
					
				}
				currentPoint = intersectionPoint;

				// add the fixed intercept point to the path
				path.add(currentPoint);
				interceptions.remove(i);
			}
			path.add(finish);
		}
	}

	List<PointF> openPoints = new ArrayList<PointF>();
	List<PointF> closedPoints = new ArrayList<PointF>();
	float increment = 0.5f;

	public void findPath3(PointF p, PointF finish) {
		// path.clear();
		try {
			if (isClearPath(p, finish)) {
				path.add(p);
				path.add(finish);
			} else {
				if (!closedPoints.contains(p)) {
					closedPoints.add(p);
				}
				if (openPoints.contains(p)) {
					openPoints.remove(p);
				}
				path.add(p);

				PointF a = new PointF(p.x - increment, p.y - increment);
				PointF b = new PointF(p.x, p.y - increment);
				PointF c = new PointF(p.x + increment, p.y - increment);
				PointF d = new PointF(p.x - increment, p.y);
				PointF e = new PointF(p.x + increment, p.y);
				PointF f = new PointF(p.x - increment, p.y + increment);
				PointF g = new PointF(p.x, p.y + increment);
				PointF h = new PointF(p.x + increment, p.y + increment);

				if (!openPoints.contains(a) && !closedPoints.contains(a)) {
					openPoints.add(a);
				}
				if (!openPoints.contains(b) && !closedPoints.contains(a)) {
					openPoints.add(b);
				}
				if (!openPoints.contains(c) && !closedPoints.contains(a)) {
					openPoints.add(c);
				}
				if (!openPoints.contains(d) && !closedPoints.contains(a)) {
					openPoints.add(d);
				}
				if (!openPoints.contains(e) && !closedPoints.contains(a)) {
					openPoints.add(e);
				}
				if (!openPoints.contains(f) && !closedPoints.contains(a)) {
					openPoints.add(f);
				}
				if (!openPoints.contains(g) && !closedPoints.contains(a)) {
					openPoints.add(g);
				}
				if (!openPoints.contains(h) && !closedPoints.contains(a)) {
					openPoints.add(h);
				}

				float closest = 100;
				float temp;
				PointF nextP = null;

				for (int i = openPoints.size() - 8; i < openPoints.size(); i++) {

					for (int j = 0; j < closedPoints.size(); j++) {
						if (closedPoints.get(j) == openPoints.get(i)) {
							// openPoints.remove(i);
							continue;
						}
					}
					// if (closedPoints.contains(openPoints.get(i))) {
					// openPoints.remove(i);
					// continue;
					// }
					// if (openPoints.get(i) != null) {
					if (!map1.calculateIntersections(p, openPoints.get(i))
							.isEmpty()) {
						closedPoints.add(openPoints.get(i));
						// openPoints.remove(i);
						continue;
					}
					// }
					temp = (float) Math
							.sqrt(Math.pow(
									openPoints.get(i).x
											- mv.getDestinationPoint().x, 2)
									+ Math.pow(
											openPoints.get(i).y
													- mv.getDestinationPoint().y,
											2));

					if (temp <= closest) {
						closest = temp;
						nextP = openPoints.get(i);
					}

					closedPoints.add(openPoints.get(i));

				}

				findPath(nextP, finish);
			}
		} catch (StackOverflowError t) {
			path.clear();
			directions.setText("Cannot find path");
			// findPath2(mv.getUserPoint(), finish);
		}
	}

	public void updateUser(PointF current, float angle) {
		PointF next = new PointF((float) (current.x + stepLength
				* Math.sin(angle)), (float) (current.y - stepLength
				* Math.cos(angle)));
		// check if next step is through wall
		if (isClearPath(next, current)) {
			userpos = next;
			mv.setUserPoint(next);
			east += Math.sin(angle);
			north += Math.cos(angle);
		}
		// updates path for every user step
		path.clear();
		closedPoints.clear();
		openPoints.clear();
		findPath(mv.getUserPoint(), mv.getDestinationPoint());
		mv.setUserPath(path);
		if (Math.abs(mv.getUserPoint().x - mv.getDestinationPoint().x) < 0.4
				&& Math.abs(mv.getUserPoint().y - mv.getDestinationPoint().y) < 0.4) {
			message = "You are here";
		} else {
			message = "Follow the red line";
		}
		getDirections();
	}

	public boolean isClearPath(PointF p1, PointF p2) {
		return map1.calculateIntersections(p1, p2).isEmpty();
	}

	class MagneticFieldSensorEventListener implements SensorEventListener {
		TextView outputTextView, recordTextView;

		public MagneticFieldSensorEventListener(TextView outputView,
				TextView recOutputView) {
			outputTextView = outputView;
			recordTextView = recOutputView;
		}

		public void onAccuracyChanged(Sensor s, int i) {
		}

		public void onSensorChanged(SensorEvent se) {
			if (se.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
				// TODO put se.values[0] somewhere useful
				double x = se.values[0];
				double y = se.values[1];
				double z = se.values[2];

				magnetic = se.values;
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

	// Clears graph and resets values
	public void Clear() {
		accelGraph.purge();
		stepcount = 0;
		east = 0;
		north = 0;
		calibration = 0;
		currentState = State.state_fall;

		userpos = mv.getOriginPoint();
		mv.setUserPoint(userpos);
		// mv.invalidate();
		// path = null;
	}

	// Calibrate to north
	public void Calibrate() {
		calibration = -azimuth;
	}

	public void TakeStep() {
		stepcount++;
		updateUser(userpos, azimuth);
	}

	public void TakeStep(double angle) {
		stepcount++;
		updateUser(userpos, (float) angle);
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

	public void getDirections() {

		if (path.size() > 0) {
			if (mv.getUserPoint().x > path.get(1).x) {
				EW = "West";
			} else {
				EW = "East";
			}

			if (mv.getUserPoint().y > path.get(1).y) {
				NS = "North";
			} else {
				NS = "South";
			}

			stepsX = Math
					.abs(Math.round((mv.getUserPoint().x - path.get(1).x) * 2));
			stepsY = Math
					.abs(Math.round((mv.getUserPoint().y - path.get(1).y) * 2));

			directions.setText("Next move: Go " + stepsY + " steps " + NS
					+ " and " + stepsX + " steps " + EW);
		}
	}

}
