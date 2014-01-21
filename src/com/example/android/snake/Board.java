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
	private int rows = 12, cols = 6;

	private enum PieceType {
		NONE, GREEN, RED, BLUE, YELLOW, PURPLE
	}

	private Bitmap[] bitmaps;
	private Paint paint = new Paint();
	private int tileSize = 48;
	private int xOffset = 20, yOffset = 20;
	private Coordinate selectedPiece = new Coordinate();
	private Random rng = new Random();
	private float accel = 10f; // pixels / sec / sec

	public Board() {
		board = new Piece[rows][cols];

		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				board[i][j] = new Piece();

		for (int i = rows - 8; i < rows; i++)
			generateRandomRow(i);

		nextRow = new Piece[cols];
		bitmaps = new Bitmap[PieceType.values().length];
	}

	private void generateRandomRow(int row) {
		for (int i = 0; i < cols; i++)
			board[row][i] = new Piece(PieceType.values()[rng.nextInt(PieceType.values().length - 1) + 1]);
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
		int selectedX = -1, selectedY = -1;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (board[i][j].type != PieceType.NONE) {
					Bitmap b = pieceToBitmap(board[i][j]);
					if (board[i][j].selected) {
						selectedX = j;
						selectedY = i;
					} else {
						canvas.drawBitmap(b, xOffset + j * tileSize, yOffset + i
							* tileSize + (int) board[i][j].extra_y, paint);
					}
				}
			}
		}

		// Draw the selected piece last so it appears above everything.
		if (selectedX != -1) {
			Bitmap b = pieceToBitmap(board[selectedY][selectedX]);
			b = Bitmap.createScaledBitmap(b,
				(int) (tileSize * 1.5),
				(int) (tileSize * 1.5), false);
			canvas.drawBitmap(b, yOffset + selectedX * tileSize - 12, xOffset + selectedY
				* tileSize - 12, paint);
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

		if (selectedPiece.x >= 0 && selectedPiece.x < cols &&
			selectedPiece.y >= 0 && selectedPiece.y < rows) {
      			board[selectedPiece.y][selectedPiece.x].selected = true;
		} else {
			selectedPiece.y = -1;
			selectedPiece.x = -1;
		}

		System.out.printf("selected x=%d y=%d\n", selectedPiece.x,
				selectedPiece.y);
	}

	public void movePiece(float x, float y) {
		int gx = (int) Math.floor((x - xOffset) / tileSize);
		int gy = (int) Math.floor((y - yOffset) / tileSize);
		if (gy < 0 || gy >= rows || selectedPiece.y < 0 || selectedPiece.y >= rows)
			return;
		if (gx < 0 || gx >= cols || selectedPiece.x < 0 || selectedPiece.x >= cols)
			return;
		if (gx != selectedPiece.x && gy == selectedPiece.y) {
			Piece tmp = board[gy][gx];
			board[gy][gx] = board[selectedPiece.y][selectedPiece.x];
			board[selectedPiece.y][selectedPiece.x] = tmp;
			selectedPiece.x = gx;
			selectedPiece.y = gy;
			//doGravity();
			//findMatches();
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
			findMatches();
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

	private boolean piecesAbove(int row, int col) {
		for (int j = row - 1; j >= 0; j--)
			if (board[j][col].type != PieceType.NONE)
				return true;
		return false;
	}

	public void doGravity() {
		for (int i = 0; i < cols; i++) {
			for (int j = rows - 1; j >= 0; j--) {
				if (board[j][i].type == PieceType.NONE) {
					for (int k = j - 1; k >= 0; k--) {
						if (i == 0 && board[k][i].type != PieceType.NONE && !board[k][i].falling)
							System.out.printf("got a falling piece: i=%d j=%d k=%d type=%s\n", i, j, k, board[k][i].type.toString());
						board[k][i].makeFalling();
					}
				}
			}
		}
	}

	public void newRow() {
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows - 1; j++) {
				board[j][i] = board[j + 1][i];
			}
		}

		generateRandomRow(rows - 1);

		findMatches();
	}

	private class Piece {
		PieceType type;
		boolean dying = false;
		boolean selected = false;
		boolean falling = false;
		float speed = 0f;
		float x, y;
		float extra_y = 0f;

		Piece() {
			type = PieceType.NONE;
		}

		Piece(PieceType type) {
			this.type = type;
		}

		public void makeFalling() {
			if (type == PieceType.NONE)
				return;

			// If already falling nothing to do
			if (falling)
				return;

			falling = true;
			speed = 0f;
		}
	}

	private class Coordinate {
		public int x, y;
	}

	public void deselectPiece() {
		System.out.printf("deselect x=%d y=%d\n", selectedPiece.x, selectedPiece.y);

		if (selectedPiece.x != -1)
			board[selectedPiece.y][selectedPiece.x].selected = false;
		selectedPiece.x = -1;
		selectedPiece.y = -1;

		doGravity();
		findMatches();
	}

	/**
	 * Do an update for a tick.
	 *
	 * @param millis milliseconds since last update
	 */
	public void update(long millis) {
		float dt = millis / 1000f;
		boolean pieceStablized = false;
		for (int i = rows - 1; i >= 0; i--) {
			for (int j = 0; j < cols; j++) {
				Piece p = board[i][j];
				if (p.falling) {
					p.speed += accel * dt;
					p.extra_y += p.speed;

					int real_pos = i + (int) Math.ceil(p.extra_y / tileSize);

					if (real_pos >= rows || (board[real_pos][j].type != PieceType.NONE && !board[real_pos][j].falling)) {
						p.falling = false;
						p.speed = 0f;
						p.extra_y = 0f;
						board[real_pos-1][j] = p;
						board[i][j] = new Piece();
						pieceStablized = true;
					}
					else if (real_pos > i + 1) {
						board[real_pos-1][j] = p;
						board[i][j] = new Piece();
						p.extra_y -= tileSize;
					}
				}
			}
		}

		if (pieceStablized)
			findMatches();
	}
}
