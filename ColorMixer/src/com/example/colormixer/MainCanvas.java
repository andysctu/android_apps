package com.example.colormixer;

import java.util.ArrayList;
import java.util.List;

import android.R.color;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;


public class MainCanvas extends View {

	
	public float circle_radius = 40f;
	List<Circle> myList = new ArrayList<Circle>();
	int lineColor;
	float lineLength = 0, linePos = 0;
	Paint p = new Paint();

	public MainCanvas(Context context, AttributeSet attrs) {
		// TODO Auto-generated constructor stub
		super(context, attrs);
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.MainCanvas, 0, 0);

		try {
			lineColor = a.getColor(R.styleable.MainCanvas_lineColor,
					Color.GREEN);
		} finally {
			a.recycle();
		}
		
		
		myList.add(new Circle(100.0f, 100.0f, circle_radius, Color.RED));
		myList.add(new Circle(200.0f, 100.0f, circle_radius, Color.BLUE));
		myList.add(new Circle(300.0f, 100.0f, circle_radius, Color.YELLOW));
	}

	public int getLineColor() {
		return lineColor;
	}

	public void setLineColor(int color) {
		lineColor = color;
		invalidate();
		requestLayout();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub

		for (Circle item : myList) {
			p.setColor(item.color);
		    canvas.drawCircle(item.x_coordinate, item.y_coordinate, item.radius, p);
		}
		super.onDraw(canvas);
	}
	
	private class Circle{
		float x_coordinate, y_coordinate, radius;
		int color;
		public Circle(float x, float y, float radius, int c){
			x_coordinate = x;
			y_coordinate = y;
			this.radius = radius;
			color = c;
		}
	}

	@Override
	protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub

	}
	
	private boolean isIntersect(Circle c1, Circle c2){
		return ((Math.abs(c1.x_coordinate - c2.x_coordinate) < circle_radius && Math.abs(c1.y_coordinate - c2.y_coordinate)< circle_radius));
	}
	
	private boolean isTouchIn(float x, float y, Circle c){
		return (Math.abs(c.x_coordinate - x) < c.radius && Math.abs(c.y_coordinate - y) < c.radius);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		
		for (Circle item : myList) {
			if (isTouchIn(event.getX(), event.getY(), item));
		}
		super.invalidate();
		return true;
	}

}
