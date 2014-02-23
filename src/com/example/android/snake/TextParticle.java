package com.example.android.snake;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

public class TextParticle {
	private String text;
	private float x, y;
	private Paint paint = new Paint();
	private Timer decay_timer;
	private Timer stay_timer;

	public TextParticle(String text, float x, float y) {
		this.text = text;
		this.x = x;
		this.y = y;

		paint.setTextSize(64f);
		paint.setColor(Color.WHITE);
		paint.setStyle(Style.FILL);

		decay_timer = new Timer(500);
		stay_timer = new Timer(1500);
	}

	public void doDraw(Canvas canvas) {
		if (stay_timer.isActive())
			paint.setAlpha(255);
		else
			paint.setAlpha((int) (255 * decay_timer.percentRemaining()));
		canvas.drawText(text, y, x, paint);
	}

	public void update(long ms) {
		if (stay_timer.isActive()) {
			stay_timer.update(ms);
			if (!stay_timer.isActive())
				decay_timer.reset();
		} else {
			decay_timer.update(ms);
		}
	}

	public boolean isActive() {
		return stay_timer.isActive() || decay_timer.isActive();
	}
}
