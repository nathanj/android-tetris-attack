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
    private Paint paint = new Paint();

    public Board() {
        rows = 12;
        cols = 6;
        board = new Piece[rows][cols];

        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                board[i][j] = Piece.NONE;

        board[11][0] = Piece.RED;
        board[11][1] = Piece.GREEN;
        board[11][2] = Piece.YELLOW;

        nextRow = new Piece[cols];
        bitmaps = new Bitmap[Piece.values().length];
    }

    public void loadBitmaps(Resources r) {
        loadTile(Piece.GREEN, r.getDrawable(R.drawable.greenstar));
        loadTile(Piece.RED, r.getDrawable(R.drawable.redstar));
        loadTile(Piece.BLUE, r.getDrawable(R.drawable.redstar));
        loadTile(Piece.YELLOW, r.getDrawable(R.drawable.yellowstar));
        loadTile(Piece.PURPLE, r.getDrawable(R.drawable.redstar));
    }

    public void loadTile(Piece piece, Drawable tile) {
        Bitmap bitmap = Bitmap.createBitmap(32, 32, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        tile.setBounds(0, 0, 32, 32);
        tile.draw(canvas);

        System.out.println("setting piece = " + piece + " to bitmap = " + bitmap);
        bitmaps[piece.ordinal()] = bitmap;
    }

    public void doDraw(Canvas canvas) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j] != Piece.NONE) {
                    System.out.println("drawing piece = " + board[i][j]);
                    Bitmap b = pieceToBitmap(board[i][j]);
                    System.out.println("b = " + b);
                    canvas.drawBitmap(b, i * 32, j * 32, paint);
                }
            }
        }
    }

    private Bitmap pieceToBitmap(Piece piece) {
        System.out.println("piece = " + piece + " ordinal = " + piece.ordinal());
        return bitmaps[piece.ordinal()];
    }
}
