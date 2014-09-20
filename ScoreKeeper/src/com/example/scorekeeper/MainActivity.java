package com.example.scorekeeper;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

	private int p1, p2, remaining, difference;
	private int numReds;
	private boolean p1Turn, colorTurn, redsGone;
	private int num[];
	private boolean lastRed = false;

	public static final int RED = 1;
	public static final int YELLOW = 2;
	public static final int GREEN = 3;
	public static final int BROWN = 4;
	public static final int BLUE = 5;
	public static final int PINK = 6;
	public static final int BLACK = 7;

	ImageView red, yellow, green, brown, blue, pink, black;
	TextView p1Points, p2Points, pointsRemaining, pointDifference, toShoot;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null) {
			setContentView(R.layout.fragment_main);
		} else {
			setContentView(R.layout.game);

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		// Save UI state changes to the savedInstanceState.
		// This bundle will be passed to onCreate if the process is
		// killed and restarted.
		savedInstanceState.putInt("p1p", p1);
		savedInstanceState.putInt("p2p", p2);
		savedInstanceState.putInt("remaining", remaining);
		savedInstanceState.putInt("difference", difference);
		savedInstanceState.putBoolean("turn", p1Turn);
		savedInstanceState.putBoolean("colorTurn", colorTurn);
		savedInstanceState.putIntArray("num", num);
		// etc.
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// Restore UI state from the savedInstanceState.
		// This bundle has also been passed to onCreate.
		p1 = savedInstanceState.getInt("p1p");
		p2 = savedInstanceState.getInt("p2p");
		remaining = savedInstanceState.getInt("remaining");
		difference = savedInstanceState.getInt("difference");
		p1Turn = savedInstanceState.getBoolean("turn");
		colorTurn = savedInstanceState.getBoolean("colorTurn");
		num = savedInstanceState.getIntArray("num");

		p1Points = (TextView) findViewById(R.id.p1Points);
		p2Points = (TextView) findViewById(R.id.p2Points);
		pointsRemaining = (TextView) findViewById(R.id.pointsRemaining);
		pointDifference = (TextView) findViewById(R.id.pointDifference);
		toShoot = (TextView) findViewById(R.id.toShoot);

		p1Points.setText(Integer.toString(p1));
		p2Points.setText(Integer.toString(p2));
		pointsRemaining.setText(Integer.toString(remaining));
		pointDifference.setText(Integer.toString(difference));

		if (p1Turn) {
			toShoot.setText("<- To Shoot   ");
		} else {
			toShoot.setText("     To Shoot ->");
		}
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

	public void newGame(View v) {
		p1 = 0;
		p2 = 0;
		num = new int[8];
		num[0] = 15;

		for (int i = 1; i < num.length; i++) {
			num[i] = 1;
		}

		numReds = 15;
		remaining = numReds * (RED + BLACK) + 27;

		p1Turn = true;
		colorTurn = false;
		redsGone = false;

		setContentView(R.layout.game);
		p1Points = (TextView) findViewById(R.id.p1Points);
		p2Points = (TextView) findViewById(R.id.p2Points);
		pointsRemaining = (TextView) findViewById(R.id.pointsRemaining);
		pointDifference = (TextView) findViewById(R.id.pointDifference);
		toShoot = (TextView) findViewById(R.id.toShoot);
		toShoot.setText("<- To Shoot   ");

		// red = (ImageView) findViewById(R.id.red);
		// red.setImageResource(R.drawable.red);
		//
		// yellow = (ImageView) findViewById(R.id.yellow);
		// yellow.setImageResource(R.drawable.red);

		// red = (ImageView) findViewById(R.id.red);
		// red.setImageResource(R.drawable.red);
		//
		// red = (ImageView) findViewById(R.id.red);
		// red.setImageResource(R.drawable.red);
		//
		// red = (ImageView) findViewById(R.id.red);
		// red.setImageResource(R.drawable.red);
		//
		// red = (ImageView) findViewById(R.id.red);
		// red.setImageResource(R.drawable.red);
		//
		// red = (ImageView) findViewById(R.id.red);
		// red.setImageResource(R.drawable.red);
	}

	public void addPoint(View v) {

		int addPoints = 0;
		switch (v.getId()) {
		case (R.id.red):
			if (!colorTurn && numReds > 0) {
				addPoints = 1;
				numReds--;
				if (numReds == 0) {
					lastRed = true;
				}
			}
			break;
		case (R.id.yellow):
			if (numReds == 0 && num[1] > 0) {
				num[1] = 0;
				addPoints = 2;
			} else if (colorTurn || lastRed) {
				addPoints = 2;

				if (lastRed) {
					lastRed = false;
				}
			}
			break;
		case (R.id.green):
			if (numReds == 0 && num[2] > 0 && num[1] == 0) {
				num[2] = 0;
				addPoints = 3;
			} else if (colorTurn || lastRed) {
				addPoints = 3;
				if (lastRed) {
					lastRed = false;
				}
			}
			break;
		case (R.id.brown):
			if (numReds == 0 && num[3] > 0 && num[2] == 0) {
				num[3] = 0;
				addPoints = 4;
			} else if (colorTurn || lastRed) {
				addPoints = 4;
				if (lastRed) {
					lastRed = false;
				}
			}
			break;
		case (R.id.blue):
			if (numReds == 0 && num[4] > 0 && num[3] == 0) {
				num[4] = 0;
				addPoints = 5;
			} else if (colorTurn || lastRed) {
				addPoints = 5;
				if (lastRed) {
					lastRed = false;
				}
			}
			break;
		case (R.id.pink):
			if (numReds == 0 && num[5] > 0 && num[4] == 0) {
				num[5] = 0;
				addPoints = 6;
			} else if (colorTurn || lastRed) {
				addPoints = 6;
				if (lastRed) {
					lastRed = false;
				}
			}
			break;
		case (R.id.black):
			if (numReds == 0 && num[6] > 0 && num[5] == 0) {
				num[6] = 0;
				addPoints = 7;
			} else if (colorTurn || lastRed) {
				addPoints = 7;
				if (lastRed) {
					lastRed = false;
				}
			}
			break;
		default:
			break;
		}
		if (p1Turn) {
			p1 += addPoints;
			p1Points.setText(Integer.toString(p1));
		} else {
			p2 += addPoints;
			p2Points.setText(Integer.toString(p2));
		}

		if (addPoints == 1) {
			colorTurn = true;
		} else if (addPoints > 1) {
			colorTurn = false;
		}

		if (colorTurn && numReds > 0) {
			remaining = (BLACK) + numReds * (RED + BLACK) + num[1] * YELLOW
					+ num[2] * GREEN + num[3] * BROWN + num[4] * BLUE + num[5]
					* PINK + num[6] * BLACK;
		} else if (numReds <= 0) {
			remaining = num[1] * YELLOW + num[2] * GREEN + num[3] * BROWN
					+ num[4] * BLUE + num[5] * PINK + num[6] * BLACK;
		} else if (!colorTurn) {
			remaining = numReds * 8 + 27;
		}

		if (num[6] == 0) {
			colorTurn = true;
		}
		pointsRemaining.setText(Integer.toString(remaining));

		difference = Math.abs(p1 - p2);
		pointDifference.setText(Integer.toString(difference));

		if (remaining <= 0) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
					WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

			// getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
			// setContentView(R.layout.game_over);
		}

	}

	public void changeTurn(View v) {
		if (p1Turn) {
			p1Turn = false;

			toShoot.setText("   To Shoot ->");
		} else {
			p1Turn = true;
			colorTurn = false;
			toShoot.setText("<- To Shoot   ");
		}
		if (numReds <= 0) {
			colorTurn = true;
		} else {
			colorTurn = false;
		}

	}

}
