package com.example.test3;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;


public class TargetLineView extends View {

	float speedZ;
	Rect box = null;
	int lineColor;
	float lineLength = 0, linePos = 0;
	Paint p = new Paint();

	public TargetLineView(Context context, AttributeSet attrs) {
		// TODO Auto-generated constructor stub
		super(context, attrs);
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.TargetLineView, 0, 0);

		try {
			lineColor = a.getColor(R.styleable.TargetLineView_lineColor,
					Color.GREEN);
		} finally {
			a.recycle();
		}
	
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
	
		lineLength = MainActivity.l.getWidth();
		linePos = MainActivity.l.getHeight() / 2;
		
		if (box == null){
			box = new Rect((int)lineLength/3, (int)linePos/3, (int)(2*lineLength/3), (int)(2*linePos/3));
		}

		p.setColor(Color.GREEN);
		p.setStyle(Paint.Style.STROKE);
		canvas.drawRect(box, p);
		super.onDraw(canvas);
	}

	@Override
	protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub

	}

}
