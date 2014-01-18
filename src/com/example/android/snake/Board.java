package com.example.android.snake;

import java.util.Random;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

public class Board {
	private Piece[][] board;
	private Piece[] nextRow;
	private int rows, cols;

	private enum PieceType {
		NONE, GREEN, RED, BLUE, YELLOW, PURPLE
	}

	private Bitmap[] bitmaps;
	private Paint paint = new Paint();
	private int tileSize = 48;
	private int xOffset = 20, yOffset = 20;
	private Coordinate selectedPiece = new Coordinate();
	private Random rng = new Random();

	public Board() {
		rows = 12;
		cols = 6;
		board = new Piece[rows][cols];

		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				board[i][j] = new Piece();
		
		for (int i = rows - 8; i < rows; i++)
			for (int j = 0; j < cols; j++)
				board[i][j].type = PieceType.values()[rng.nextInt(PieceType.values().length-1)+1];

		nextRow = new Piece[cols];
		bitmaps = new Bitmap[PieceType.values().length];
	}

	public void loadBitmaps(Resources r) {
		loadTile(PieceType.GREEN, r.getDrawable(R.drawable.greenstar));
		loadTile(PieceType.RED, r.getDrawable(R.drawable.redstar));
		loadTile(PieceType.BLUE, r.getDrawable(R.drawable.bluestar));
		loadTile(PieceType.YELLOW, r.getDrawable(R.drawable.yellowstar));
		loadTile(PieceType.PURPLE, r.getDrawable(R.drawable.purplestar));
	}

	public void loadTile(PieceType piece, Drawable tile) {
		Bitmap bitmap = Bitmap.createBitmap(tileSize, tileSize,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		tile.setBounds(0, 0, tileSize, tileSize);
		tile.draw(canvas);

		// System.out.println("setting piece = " + piece + " to bitmap = " +
		// bitmap);
		bitmaps[piece.ordinal()] = bitmap;
	}

	public void doDraw(Canvas canvas) {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (board[i][j].type != PieceType.NONE) {
					// System.out.println("drawing piece = " + board[i][j]);
					Bitmap b = pieceToBitmap(board[i][j]);
					// System.out.println("b = " + b);
					canvas.drawBitmap(b, yOffset + j * tileSize, xOffset + i
							* tileSize, paint);
				}
			}
		}
	}

	private Bitmap pieceToBitmap(Piece piece) {
		// System.out.println("piece = " + piece + " ordinal = " +
		// piece.ordinal());
		return bitmaps[piece.type.ordinal()];
	}

	public void selectPiece(float x, float y) {
		selectedPiece.x = (int) Math.floor((x - xOffset) / tileSize);
		selectedPiece.y = (int) Math.floor((y - yOffset) / tileSize);

		System.out.printf("selected x=%d y=%d\n", selectedPiece.x,
				selectedPiece.y);
	}

	public void movePiece(float x, float y) {
		int gx = (int) Math.floor((x - xOffset) / tileSize);
		int gy = (int) Math.floor((y - yOffset) / tileSize);
		if (gy < 0 || gy >= 12 || selectedPiece.y < 0 || selectedPiece.y >= 12)
			return;
		if (gx < 0 || gx >= 6 || selectedPiece.x < 0 || selectedPiece.x >= 6)
			return;
		if (gx != selectedPiece.x && gy == selectedPiece.y) {
			Piece tmp = board[gy][gx];
			board[gy][gx] = board[selectedPiece.y][selectedPiece.x];
			board[selectedPiece.y][selectedPiece.x] = tmp;
			selectedPiece.x = gx;
			selectedPiece.y = gy;
			doGravity();
			findMatches();
		}
	}

	public void findMatches() {
		boolean match = false;

		for (int i = 0; i < rows; i++)
			match |= findRowMatches(i);
		for (int i = 0; i < cols; i++)
			match |= findColMatches(i);

		if (match) {
			for (int i = 0; i < rows; i++)
				for (int j = 0; j < cols; j++)
					if (board[i][j].dying)
						board[i][j].type = PieceType.NONE;

			doGravity();
			//findMatches();
		}
	}

	private boolean findRowMatches(int y) {
		PieceType lastPiece = PieceType.NONE;
		int num = 0;
		boolean match = false;

		for (int j = 0; j < cols; j++) {
			if (lastPiece != PieceType.NONE && board[y][j].type == lastPiece) {
				num++;
				if (num >= 3) {
					match = true;
					for (int k = 0; k < num; k++)
						board[y][j - k].dying = true;
				}
			} else {
				lastPiece = board[y][j].type;
				num = 1;
			}
		}

		return match;
	}

	private boolean findColMatches(int x) {
		PieceType lastPiece = PieceType.NONE;
		int num = 0;
		boolean match = false;

		for (int i = 0; i < rows; i++) {
			if (lastPiece != PieceType.NONE && board[i][x].type == lastPiece) {
				num++;
				if (num >= 3) {
					match = true;
					for (int k = 0; k < num; k++)
						board[i - k][x].dying = true;
				}
			} else {
				lastPiece = board[i][x].type;
				num = 1;
			}
		}

		return match;
	}

	public void doGravity() {
		System.out.println("doGravity()");
		for (int i = 0; i < cols; i++) {
			for (int j = rows - 1; j >= 0; j--) {
				if (board[j][i].type == PieceType.NONE) {
					for (int k = j; k > 0; k--) {
						board[k][i] = board[k - 1][i];
					}
				}
			}
		}
	}

	public void newRow() {
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows-1; j++) {
				board[j][i] = board[j+1][i];
			}
		}

		for (int i = 0; i < cols; i++)
			board[rows-1][i] = new Piece(PieceType.values()[rng.nextInt(PieceType.values().length-1)+1]);

		//findMatches();
	}

	private class Piece {
		PieceType type;
		boolean dying = false;

		Piece() {
			type = PieceType.NONE;
		}

		Piece(PieceType type) {
			this.type = type;
		}
	}
	
	private class Coordinate {
		public int x, y;
	}

	public void deselectPiece() {
		selectedPiece.x = -1;
		selectedPiece.y = -1;
	}
}
