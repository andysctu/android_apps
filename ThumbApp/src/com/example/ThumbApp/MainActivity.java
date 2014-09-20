package com.example.ThumbApp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
	/** Called when the activity is first created. */
	public static LinearLayout l;
	private static final int RESULT_SETUP = 1;
	private static final int RESULT_MEASURE = 2;
	private static final int RESULT_MEASURE2 = 3;
	private int nh;
	private File photoFile;
	private static final String DATA = null;
	public static myView mView;
	public measureView measView;

	static Bitmap myBitmap;
	static String mCurrentPhotoPath;
	static String picturePath;

	public static EditText PhoneHeightToCamera, PhoneHeight, ShoulderHeight;
	public static TextView Pixels;
	public static float iPixels, fPhoneHeightToCamera, fPhoneHeight,
			fShoulderHeight;
	public static float mPixels, distance;

	public void Setup(View v) {
		// Intent i = new Intent(Intent.ACTION_PICK,
		// android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		//
		// startActivityForResult(i, RESULT_SETUP);

		Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		if (i.resolveActivity(getPackageManager()) != null) {
			photoFile = null;
			try {
				photoFile = createImageFile();
			} catch (IOException ex) {
				// Error occurred while creating the File

			}
			// Continue only if the File was successfully created
			if (photoFile != null) {
				i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
				startActivityForResult(i, RESULT_SETUP);

			}
		}
	}

	public void LoadPicture(View v) {
		PhoneHeightToCamera = (EditText) findViewById(R.id.PhoneHeightToCamera);
		String s = PhoneHeightToCamera.getText().toString();
		fPhoneHeightToCamera = Float.parseFloat(s);

		PhoneHeight = (EditText) findViewById(R.id.PhoneHeight);
		s = PhoneHeight.getText().toString();
		fPhoneHeight = Float.parseFloat(s);

		ShoulderHeight = (EditText) findViewById(R.id.ShoulderHeight);
		s = ShoulderHeight.getText().toString();
		fShoulderHeight = Float.parseFloat(s);

		Intent i = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

		startActivityForResult(i, RESULT_MEASURE);
	}

	public void TakePicture(View v) {

		Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		if (i.resolveActivity(getPackageManager()) != null) {
			photoFile = null;
			try {
				photoFile = createImageFile();
			} catch (IOException ex) {
				// Error occurred while creating the File

			}
			// Continue only if the File was successfully created
			if (photoFile != null) {
				i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
				startActivityForResult(i, RESULT_MEASURE2);
			}
		}

	}

	public void Save(View v) {
		Pixels = (TextView) findViewById(R.id.pixelsValue);
		String s = Pixels.getText().toString();
		iPixels = Float.parseFloat(s);

		PhoneHeightToCamera = (EditText) findViewById(R.id.PhoneHeightToCamera);
		s = PhoneHeightToCamera.getText().toString();
		fPhoneHeightToCamera = Float.parseFloat(s);

		PhoneHeight = (EditText) findViewById(R.id.PhoneHeight);
		s = PhoneHeight.getText().toString();
		fPhoneHeight = Float.parseFloat(s);

		ShoulderHeight = (EditText) findViewById(R.id.ShoulderHeight);
		s = ShoulderHeight.getText().toString();
		fShoulderHeight = Float.parseFloat(s);

		SharedPreferences settings = getApplicationContext()
				.getSharedPreferences(DATA, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putFloat("Pixels", iPixels);
		editor.putFloat("Phone Height to Camera", fPhoneHeightToCamera);
		editor.putFloat("Phone Height", fPhoneHeight);
		editor.putFloat("Shoulder Height", fShoulderHeight);
		// Apply the edits!
		editor.apply();
	}

	public void Load(View v) {
		// Get from the SharedPreferences
		SharedPreferences settings = getApplicationContext()
				.getSharedPreferences(DATA, 0);

		iPixels = settings.getFloat("Pixels", 0);

		Pixels = (TextView) findViewById(R.id.pixelsValue);
		String s = String.format("%s", Float.toString(iPixels));
		Pixels.setText(s);

		fPhoneHeightToCamera = settings.getFloat("Phone Height to Camera", 0);

		PhoneHeightToCamera = (EditText) findViewById(R.id.PhoneHeightToCamera);
		s = String.format("%s", Float.toString(fPhoneHeightToCamera));
		PhoneHeightToCamera.setText(s);

		fPhoneHeight = settings.getFloat("Phone Height", 0);

		PhoneHeight = (EditText) findViewById(R.id.PhoneHeight);
		s = String.format("%s", Float.toString(fPhoneHeight));
		PhoneHeight.setText(s);

		fShoulderHeight = settings.getFloat("Shoulder Height", 0);

		ShoulderHeight = (EditText) findViewById(R.id.ShoulderHeight);
		s = String.format("%s", Float.toString(fShoulderHeight));
		ShoulderHeight.setText(s);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RESULT_SETUP && resultCode == RESULT_OK
				&& null != data) {
			Uri selectedImage = data.getData();

			myBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
			// photoFile.delete();

			nh = (int) (myBitmap.getHeight() * (540.0 / myBitmap.getWidth()));
			myBitmap = Bitmap.createScaledBitmap(myBitmap, 540, nh, true);

			// File f = new File(mCurrentPhotoPath);
			// if (f.exists()) {
			// boolean isDel = f.delete();
			// }
			//
			// sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
			// Uri.parse("file://"
			// + Environment.getExternalStorageDirectory())));

			try {

				getContentResolver().delete(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
						MediaStore.Images.Media.DATA + "='"
								+ photoFile.getPath() + "'", null);

				sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
						Uri.parse("file://"
								+ Environment.getExternalStorageDirectory())));

			} catch (Exception e) {
				e.printStackTrace();

			}

			mView = new myView(this, MainActivity.this);

			setContentView(mView);
		}

		if (requestCode == RESULT_MEASURE && resultCode == RESULT_OK
				&& null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			picturePath = cursor.getString(columnIndex);
			cursor.close();

			myBitmap = BitmapFactory.decodeFile(picturePath);

			nh = (int) (myBitmap.getHeight() * (540.0 / myBitmap.getWidth()));
			myBitmap = Bitmap.createScaledBitmap(myBitmap, 540, nh, true);

			setContentView(new measureView(this, MainActivity.this));
		}

		if (requestCode == RESULT_MEASURE2 && resultCode == RESULT_OK) {

			myBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
			nh = (int) (myBitmap.getHeight() * (540.0 / myBitmap.getWidth()));
			myBitmap = Bitmap.createScaledBitmap(myBitmap, 540, nh, true);

			measView = new measureView(this, MainActivity.this);
			setContentView(measView);
		}

	}

	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(imageFileName, /* prefix */
				".jpg", /* suffix */
				storageDir /* directory */
		);

		// Save a file: path for use with ACTION_VIEW intents
		mCurrentPhotoPath = image.getAbsolutePath();
		return image;
	}

}
