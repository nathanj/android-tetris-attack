package com.example.android.snake;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class BoardView extends View {

	private RefreshHandler redrawHandler = new RefreshHandler();

	class RefreshHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			BoardView.this.update();
			BoardView.this.invalidate();
		}

		public void sleep(long delayMillis) {
			this.removeMessages(0);
			sendMessageDelayed(obtainMessage(0), delayMillis);
		}
	};

	public Board board;
	private int ticks = 0;
	private long lastSec;
	private int secs = 0;
	private long lastTick = 0;

	public BoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setFocusable(true);
		board = new Board();
		board.loadBitmaps(context.getResources());
		update();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		board.doDraw(canvas);
	}

	public void handleTouchEvent(int action, float x, float y) {
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			board.selectPiece(x, y);
			break;
		case MotionEvent.ACTION_MOVE:
			board.movePiece(x, y);
			break;
		case MotionEvent.ACTION_UP:
			board.deselectPiece();
			break;
		}
	}

	public void update() {
		// System.out.println("updating");
		long curr = System.currentTimeMillis();
		if (curr > lastSec + 1000) {
			System.out.println("ticks = " + ticks + " in " + (curr - lastSec)
					+ " ms");
			ticks = 0;
			lastSec = curr;
			secs++;
			if (secs % 5 == 0)
				board.newRow();
		}
		ticks++;
		if (lastTick == 0)
			lastTick = curr;
		board.update(curr - lastTick);
		lastTick = curr;
		redrawHandler.sleep(50);
	}
}
