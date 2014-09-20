package com.example.hackathon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity {

	boolean thumbTime = false;
	private static int RESULT_LOAD_IMAGE = 1;
	ImageView imageView;
	Bitmap bitmap;
	Canvas canvas;
	Paint paint;
	float downx = 0, downy = 0, upx = 0, upy = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.main);

		Button buttonLoadImage = (Button) findViewById(R.id.buttonLoadPicture);
		buttonLoadImage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

				startActivityForResult(i, RESULT_LOAD_IMAGE);
			}
		});
	}

	public String path;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
				&& null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();

			ImageView imageView = (ImageView) findViewById(R.id.imgView);
			imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
			path = picturePath;
			
			setContentView(imageView);
			
			Button buttonLoadImage = (Button) findViewById(R.id.buttonLoadPicture);
			buttonLoadImage.setVisibility(View.GONE);
			

		}

	}
	
	private class myView extends View{

		public myView(Context context) {
		 super(context);
		 // TODO Auto-generated constructor stub
		}

		@Override
		protected void onDraw(Canvas canvas) {
		 // TODO Auto-generated method stub
		 Bitmap myBitmap = BitmapFactory.decodeFile(path);
		          canvas.drawBitmap(myBitmap, 0, 0, null);
		        
		          Paint myPaint = new Paint();
		          myPaint.setColor(Color.GREEN);
		          myPaint.setStyle(Paint.Style.STROKE);
		          myPaint.setStrokeWidth(3);
		          canvas.drawRect(10, 10, 100, 100, myPaint);
		}
		  }
	
	
}
