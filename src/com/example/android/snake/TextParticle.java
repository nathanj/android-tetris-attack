package com.example.android.snake;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

public class TextParticle {
	private String text;
	private float x, y;
	private Paint paint = new Paint();
	private Timer timer;

	public TextParticle(String text, float x, float y) {
		this.text = text;
		this.x = x;
		this.y = y;

		paint.setTextSize(32f);
		paint.setColor(Color.WHITE);
		paint.setStyle(Style.FILL);

		timer = new Timer(1000);
	}

	public void doDraw(Canvas canvas) {
		paint.setAlpha((int) (255 * timer.percentLeft()));
		canvas.drawText(text, y * Piece.TILE_SIZE, x * Piece.TILE_SIZE, paint);
	}

	public void update(long ms) {
		timer.update(ms);
	}

	public boolean isActive() {
		return timer.isActive();
	}

}
