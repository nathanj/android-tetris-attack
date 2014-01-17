package com.example.android.snake;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class BoardView extends View {
    public Board board;

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        board = new Board();
        board.loadBitmaps(context.getResources());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        board.doDraw(canvas);
    }
}
