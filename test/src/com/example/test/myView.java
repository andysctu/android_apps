package com.example.test;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class myView extends View {

	public static float width = 100, height = 200;
	public static float l = 200, t = 200, r = l + width, b = t + 200;
	public static float center = l + (width / 2);
	public Paint myPaint = new Paint();

	private Activity mParentActivity = null;
	TextView text;

	public myView(Context context, Activity parentActivity) {
		super(context);
		// TODO Auto-generated constructor stub
		mParentActivity = parentActivity;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		
		canvas.drawBitmap(MainActivity.myBitmap, 0, 0, myPaint);

		myPaint.setColor(Color.BLUE);
		myPaint.setStyle(Paint.Style.STROKE);
		myPaint.setStrokeWidth(3);
		canvas.drawRect(l, t, r, b, myPaint);
		myPaint.setColor(Color.GREEN);
		canvas.drawRect(r, t - 40, r + 40, t, myPaint);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if (event.getX() < r && event.getY() < b && event.getX() > l
				&& event.getY() > t) {

			l = event.getX() - (width / 2);
			t = event.getY() - (height / 2);
			r = l + width;
			b = t + height;

		} else if (event.getX() < r + 40 && event.getY() < t
				&& event.getX() > r && event.getY() > t - 40) {
			width = r - l;
			MainActivity.iPixels = width;
			mParentActivity.setContentView(R.layout.fragment_main);
			text = (TextView) mParentActivity.findViewById(R.id.pixelsValue);
			String s = String.format("%s", Float.toString(width));
			text.setText(s);
			MainActivity.myBitmap = null;

			return true;

		} else if (event.getX() < l + (width / 2)) {
			l = event.getX();

		} else if (event.getX() > r - (width / 2)) {
			r = event.getX();

		} 

		width = r - l;
		center = (l + r) / 2;
		super.invalidate();
		return true;
	}

}