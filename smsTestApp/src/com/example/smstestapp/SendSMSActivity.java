package com.example.smstestapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SendSMSActivity extends Activity {

	public static final String SAVED_INFO = "SavedInfo";
	public static final String MY_PHONE_NUMBER = "MyPhoneNum";
	public static final String MY_NAME = "MyName";

	SharedPreferences prefs;

	Player me = new Player(null, null);
	Player[] friends;

	TextView myName;
	TextView myNum;
	EditText changeMyNum;
	EditText changeMyName;

	EditText friendNum;
	EditText message;

	Button buttonSend, buttonSetName, playButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);

		prefs = getPreferences(0);
		me.name = prefs.getString(MY_NAME, null);
		me.phoneNum = prefs.getString(MY_PHONE_NUMBER, null);

		if (me.phoneNum == null) {

			TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			String mPhoneNumber = tMgr.getLine1Number();
			me.phoneNum = mPhoneNumber;
		}

		myName = (TextView) findViewById(R.id.textViewMyName);
		myName.setText("My Name : " + me.name);

		myNum = (TextView) findViewById(R.id.textViewMyNumber);
		myNum.setText("My Number : " + me.phoneNum);

		changeMyName = (EditText) findViewById(R.id.editTextMyName);

		changeMyNum = (EditText) findViewById(R.id.editTextPhoneNo);

		friendNum = (EditText) findViewById(R.id.friendNum);
		message = (EditText) findViewById(R.id.message);

		buttonSend = (Button) findViewById(R.id.buttonSend);
		buttonSetName = (Button) findViewById(R.id.buttonSetName);

		buttonSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String phoneNo;
				String sms;
				
				if (friendNum.getText().toString().isEmpty()) {
					phoneNo = "+14168319895";
				}
				else {
					phoneNo = friendNum.getText().toString();
				}
				
				if (message.getText().toString().isEmpty()){
					sms = "test message";
				}
				else{
					sms = message.getText().toString();
				}
				

				try {
					SmsManager smsManager = SmsManager.getDefault();
					smsManager.sendTextMessage(phoneNo, null, sms, null, null);
					Toast.makeText(getApplicationContext(), "SMS Sent!",
							Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(),
							"SMS faild, please try again later!",
							Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}

			}
		});

		buttonSetName.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!changeMyName.getText().toString().isEmpty()) {
					me.name = changeMyName.getText().toString();
					myName.setText("My Name : " + me.name);
				}
				if (!changeMyNum.getText().toString().isEmpty()) {
					me.phoneNum = changeMyNum.getText().toString();
					myNum.setText("My Number : " + me.phoneNum);
				}

				SharedPreferences.Editor editor = prefs.edit();
				editor.putString(MY_PHONE_NUMBER, me.phoneNum);
				editor.putString(MY_NAME, me.name);

				editor.commit();
			}
		});
	}

	// final ListView listview = (ListView) findViewById(R.id.listview);
	// String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
	// "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
	// "Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
	// "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
	// "Android", "iPhone", "WindowsMobile" };
	//
	// final ArrayList<String> list = new ArrayList<String>();
	// for (int i = 0; i < values.length; ++i) {
	// list.add(values[i]);
	// }
	// final StableArrayAdapter adapter = new StableArrayAdapter(this,
	// android.R.layout.simple_list_item_1, list);
	// listview.setAdapter(adapter);
	//
	// listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	//
	// @Override
	// public void onItemClick(AdapterView<?> parent, final View view,
	// int position, long id) {
	// final String item = (String) parent.getItemAtPosition(position);
	// view.animate().setDuration(2000).alpha(0)
	// .withEndAction(new Runnable() {
	// @Override
	// public void run() {
	// list.remove(item);
	// adapter.notifyDataSetChanged();
	// view.setAlpha(1);
	// }
	// });
	// }
	//
	// });
	// }

	// add to xml
	// <ListView
	// android:id="@+id/listview"
	// android:layout_width="wrap_content"
	// android:layout_height="wrap_content" />

	@Override
	public void onStop() {
		super.onStop();

		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(MY_PHONE_NUMBER, me.phoneNum);
		editor.putString(MY_NAME, me.name);

		editor.commit();

	}

	private class StableArrayAdapter extends ArrayAdapter<String> {

		HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

		public StableArrayAdapter(Context context, int textViewResourceId,
				List<String> objects) {
			super(context, textViewResourceId, objects);
			for (int i = 0; i < objects.size(); ++i) {
				mIdMap.put(objects.get(i), i);
			}
		}

		@Override
		public long getItemId(int position) {
			String item = getItem(position);
			return mIdMap.get(item);
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

	}

}
