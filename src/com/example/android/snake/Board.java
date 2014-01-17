package com.example.android.snake;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

public class Board {
    private Piece[][] board;
    private Piece[] nextRow;
    private int rows, cols;

    private enum Piece {
        NONE, GREEN, RED, BLUE, YELLOW, PURPLE
    }

    private Bitmap[] bitmaps;

    public Board() {
        rows = 12;
        cols = 6;
        board = new Piece[rows][cols];
        nextRow = new Piece[cols];
        bitmaps = new Bitmap[Piece.values().length];
    }

    public void loadBitmaps(Resources r) {
        loadTile(Piece.GREEN, r.getDrawable(R.drawable.redstar));
    }

    public void loadTile(Piece piece, Drawable tile) {
        Bitmap bitmap = Bitmap.createBitmap(32, 32, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        tile.setBounds(0, 0, 32, 32);
        tile.draw(canvas);

        bitmaps[piece.ordinal()] = bitmap;
    }

    public void doDraw(Canvas canvas, Paint paint) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j] != Piece.NONE) {
                    Bitmap b = pieceToBitmap(board[i][j]);
                    canvas.drawBitmap(b, 0, 0, paint);
                }
            }
        }
    }

    private Bitmap pieceToBitmap(Piece piece) {
        switch (piece) {
            case BLUE:
                return null;
            case GREEN:
                return null;
            case NONE:
                return null;
        }
        throw new RuntimeException("Piece " + piece + " not handled.");
    }
}
