package com.example.simplecanvas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class DrawSurface extends SurfaceView implements SurfaceHolder.Callback {

	private RenderingThread renderThread;
	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

	private float mouseX, mouseY, radius;
	private boolean drawing = false;

	public DrawSurface(Context context) {
		super(context);
		init();
	}

	protected void doDraw(Canvas canvas) {
		// super.onDraw(canvas);
		if (drawing) {
			canvas.drawRGB(0, 0, 0);
			canvas.drawCircle(mouseX, mouseY, radius, paint);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// return super.onTouchEvent(event);
		int action = event.getAction();
		if (action == MotionEvent.ACTION_MOVE) {
			float x = event.getX();
			float y = event.getY();
			radius = (float) Math.sqrt(Math.pow(x - mouseX, 2)
					+ Math.pow(y - mouseY, 2));
		} else if (action == MotionEvent.ACTION_DOWN) {
			mouseX = event.getX();
			mouseY = event.getY();
			radius = 1;
			drawing = true;
		} else if (action == MotionEvent.ACTION_UP) {
			drawing = false;
		}

		return true;
	}

	private void init() {
		getHolder().addCallback(this);
		renderThread = new RenderingThread(getHolder(), this);

		setFocusable(true); // make sure we get key events

		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(3);
		paint.setColor(Color.WHITE);
	}

	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		renderThread.setRunning(true);
		renderThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		renderThread.setRunning(false);
		while (retry) {
			try {
				renderThread.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}
	}
}