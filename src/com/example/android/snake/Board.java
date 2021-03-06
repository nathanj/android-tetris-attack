package com.example.android.snake;

import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

public class Board {
	private final static int ROWS = 11, COLUMNS = 6;
	private final static int X_OFFSET = 20, Y_OFFSET = 20;
	private final static float ACCELERATION = 25f; // pixels / sec / sec
	private final static float DYING_TIME = 1500f; // ms
	private final static long NEXT_ROW_TIME = 7500;
	private final static int SELECTED_PIECE_OFFSET_X = 17;
	private final static int SELECTED_PIECE_OFFSET_Y = 17;

	private Piece[][] board;
	private Piece[] nextRow;

	private Bitmap[] bitmaps;
	private Paint paint = new Paint();
	private Coordinate selectedPiece = new Coordinate();
	private Random rng = new Random();
	private long timeTillNextRow = NEXT_ROW_TIME;
	private BoardMatcher matcher;
	private Vector<TextParticle> comboParticles = new Vector<TextParticle>();
	private boolean playerMovedNextRow = false;

	public Board() {
		board = new Piece[ROWS][COLUMNS];

		for (int i = 0; i < ROWS; i++)
			for (int j = 0; j < COLUMNS; j++)
				board[i][j] = new Piece();

		for (int i = ROWS - 8; i < ROWS; i++)
			generateRandomRow(i);

		nextRow = new Piece[COLUMNS];
		generateNextRow();
		bitmaps = new Bitmap[Piece.PieceType.values().length];

		matcher = new BoardMatcher(board, ROWS, COLUMNS);

		// Thread t = new Thread(new NetworkConnection());
		// t.start();
	}

	private void generateRandomRow(int row) {
		for (int i = 0; i < COLUMNS; i++)
			board[row][i] = new Piece(
					Piece.PieceType.values()[rng.nextInt(Piece.PieceType.values().length - 1) + 1]);
	}

	private void generateNextRow() {
		for (int i = 0; i < COLUMNS; i++)
			nextRow[i] = new Piece(
					Piece.PieceType.values()[rng.nextInt(Piece.PieceType.values().length - 1) + 1]);
	}

	public void loadBitmaps(Resources r) {
		loadTile(Piece.PieceType.GREEN, r.getDrawable(R.drawable.greenstar));
		loadTile(Piece.PieceType.RED, r.getDrawable(R.drawable.redstar));
		loadTile(Piece.PieceType.BLUE, r.getDrawable(R.drawable.bluestar));
		loadTile(Piece.PieceType.YELLOW, r.getDrawable(R.drawable.yellowstar));
		loadTile(Piece.PieceType.PURPLE, r.getDrawable(R.drawable.purplestar));
	}

	public void loadTile(Piece.PieceType piece, Drawable tile) {
		Bitmap bitmap = Bitmap.createBitmap(Piece.TILE_SIZE, Piece.TILE_SIZE,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		tile.setBounds(0, 0, Piece.TILE_SIZE, Piece.TILE_SIZE);
		tile.draw(canvas);

		bitmaps[piece.ordinal()] = bitmap;
	}

	public void doDraw(Canvas canvas) {
		int selectedX = -1, selectedY = -1;

		int partialRow = getPartialRow();

		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLUMNS; j++) {
				if (board[i][j].type != Piece.PieceType.NONE) {
					Bitmap b = pieceToBitmap(board[i][j]);
					if (board[i][j].selected) {
						selectedX = j;
						selectedY = i;
					} else {
						paint.setAlpha(255);
						if (board[i][j].dying) {
							paint.setAlpha((int) (255 - 255 * board[i][j].dying_time / DYING_TIME));
						}
						canvas.drawBitmap(b, X_OFFSET + j * Piece.TILE_SIZE, Y_OFFSET + i
								* Piece.TILE_SIZE + (int) board[i][j].extra_y - partialRow, paint);
					}
				}
			}
		}

		for (int j = 0; j < COLUMNS; j++) {
			Bitmap b = pieceToBitmap(nextRow[j]);
			paint.setAlpha(150);
			// canvas.drawBitmap(b, X_OFFSET + j * Piece.TILE_SIZE, Y_OFFSET +
			// ROWS
			// * Piece.TILE_SIZE - partialRow, paint);
			int x = X_OFFSET + j * Piece.TILE_SIZE;
			int y = Y_OFFSET + ROWS * Piece.TILE_SIZE - partialRow;
			b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), Math.max(partialRow, 1));
			canvas.drawBitmap(b, x, y, paint);
		}

		paint.setAlpha(255);

		// Draw the selected piece last so it appears above everything.
		if (selectedX != -1) {
			Bitmap b = pieceToBitmap(board[selectedY][selectedX]);
			b = Bitmap.createScaledBitmap(b, (int) (Piece.TILE_SIZE * 1.5),
					(int) (Piece.TILE_SIZE * 1.5), false);
			canvas.drawBitmap(b, Y_OFFSET + selectedX * Piece.TILE_SIZE - SELECTED_PIECE_OFFSET_X,
					X_OFFSET + selectedY * Piece.TILE_SIZE - SELECTED_PIECE_OFFSET_Y - partialRow,
					paint);
		}

		for (TextParticle tp : comboParticles)
			tp.doDraw(canvas);
	}

	private Bitmap pieceToBitmap(Piece piece) {
		return bitmaps[piece.type.ordinal()];
	}

	/**
	 * Returns the number of pixels that the new row should be displayed.
	 */
	private int getPartialRow() {
		return (int) (Piece.TILE_SIZE * (NEXT_ROW_TIME - timeTillNextRow) / NEXT_ROW_TIME);
	}

	public void selectPiece(float x, float y) {
		selectedPiece.x = (int) Math.floor((x - X_OFFSET) / Piece.TILE_SIZE);
		selectedPiece.y = (int) Math.floor((y - Y_OFFSET + getPartialRow()) / Piece.TILE_SIZE);

		if (selectedPiece.x >= 0 && selectedPiece.x < COLUMNS && selectedPiece.y >= 0
				&& selectedPiece.y < ROWS) {
			board[selectedPiece.y][selectedPiece.x].selected = true;
		} else {
			selectedPiece.y = -1;
			selectedPiece.x = -1;
		}

		// System.out.printf("selected x=%d y=%d\n", selectedPiece.x,
		// selectedPiece.y);
	}

	public void movePiece(float x, float y) {
		int gx = (int) Math.floor((x - X_OFFSET) / Piece.TILE_SIZE);
		if (gx < 0 || gx >= COLUMNS || selectedPiece.x < 0 || selectedPiece.x >= COLUMNS)
			return;
		if (gx != selectedPiece.x) {
			Piece tmp = board[selectedPiece.y][gx];
			board[selectedPiece.y][gx] = board[selectedPiece.y][selectedPiece.x];
			board[selectedPiece.y][selectedPiece.x] = tmp;
			selectedPiece.x = gx;
		}
	}

	public void doGravity() {
		for (int i = 0; i < COLUMNS; i++) {
			for (int j = ROWS - 1; j >= 0; j--) {
				if (board[j][i].type == Piece.PieceType.NONE) {
					for (int k = j - 1; k >= 0; k--) {
						board[k][i].makeFalling();
					}
				}
			}
		}
	}

	/**
	 * Places the new row onto the actual board.
	 * 
	 * Moves all pieces up one row and then places the next row as the last row
	 * on the board.
	 */
	private void makeNextRowReal() {
		for (int i = 0; i < COLUMNS; i++) {
			for (int j = 0; j < ROWS - 1; j++) {
				board[j][i] = board[j + 1][i];
			}
		}

		for (int i = 0; i < COLUMNS; i++)
			board[ROWS - 1][i] = nextRow[i];

		if (selectedPiece.y > 0)
			selectedPiece.y--;
	}

	private class Coordinate {
		public int x, y;
	}

	public void deselectPiece() {
		// System.out.printf("deselect x=%d y=%d\n", selectedPiece.x,
		// selectedPiece.y);

		if (selectedPiece.x != -1)
			board[selectedPiece.y][selectedPiece.x].selected = false;
		selectedPiece.x = -1;
		selectedPiece.y = -1;

		doGravity();
		createComboTextParticles(matcher.findMatches(), false);
	}

	private void createComboTextParticles(Vector<Match> matches, boolean increaseCombo) {
		for (Match m : matches) {
			// System.out.printf("NJ: chain=%d\n", m.chain());
			if (m.chain() > 1)
				comboParticles.add(new TextParticle("x" + m.chain(), Math.max(20, m.topX()
						* Piece.TILE_SIZE + X_OFFSET), Math.max(20, m.topY() * Piece.TILE_SIZE
						+ Y_OFFSET - getPartialRow())));
			if (m.size() > 3)
				comboParticles.add(new TextParticle("+" + m.size(), Math.max(20, m.topX()
						* Piece.TILE_SIZE + X_OFFSET), Math.max(20, m.topY() * Piece.TILE_SIZE
						+ Y_OFFSET - getPartialRow() - 150)));

		}
	}

	/**
	 * Do an update for a tick.
	 * 
	 * @param millis
	 *            milliseconds since last update
	 */
	public void update(long millis) {
		float dt = millis / 1000f;
		boolean pieceStablized = false;
		boolean pieceDisappeared = false;
		boolean movingRow = true;
		boolean foundMatch = false;

		for (int i = ROWS - 1; i >= 0; i--) {
			for (int j = 0; j < COLUMNS; j++) {
				Piece p = board[i][j];
				if (p.falling) {
					movingRow = false;
					// System.out.printf("falling i=%d, j=%d\n", i, j);
					p.speed += ACCELERATION * dt;
					p.extra_y += p.speed;

					int real_pos = i + (int) Math.ceil(p.extra_y / Piece.TILE_SIZE);

					if (real_pos >= ROWS
							|| (board[real_pos][j].type != Piece.PieceType.NONE && !board[real_pos][j].falling)) {
						p.falling = false;
						p.speed = 0f;
						p.extra_y = 0f;
						board[real_pos - 1][j] = p;
						board[i][j] = new Piece();
						pieceStablized = true;
					} else if (real_pos > i + 1) {
						board[real_pos - 1][j] = p;
						board[i][j] = new Piece();
						p.extra_y -= Piece.TILE_SIZE;
					}
				}
				if (p.dying) {
					movingRow = false;
					// System.out.printf("dying i=%d, j=%d\n", i, j);
					p.dying_time += millis;
					if (p.dying_time > DYING_TIME) {
						pieceDisappeared = true;
						board[i][j].type = Piece.PieceType.NONE;
						p.dying = false;
						setAllAboveToChain(i, j, board[i][j].chain + 1);
					}
				}
			}
		}

		if (movingRow) {
			timeTillNextRow -= millis;

			if (timeTillNextRow <= 0) {
				moveNextRow();
				pieceStablized = true;
			}

			if (playerMovedNextRow) {
				playerMovedNextRow = false;
				pieceStablized = true;
			}
		}

		if (pieceStablized) {
			Vector<Match> matches = matcher.findMatches();
			foundMatch = matches.size() > 0;
			createComboTextParticles(matches, true);
			resetChainOnStablePieces();
		}

		if (pieceDisappeared || foundMatch)
			doGravity();

		updateParticles(millis);
	}

	public void moveNextRow() {
		makeNextRowReal();
		generateNextRow();
		timeTillNextRow = NEXT_ROW_TIME;
		playerMovedNextRow = true;
	}

	private void setAllAboveToChain(int row, int col, int chain) {
		for (int i = row - 1; i >= 0; i--)
			if (board[i][col].type != Piece.PieceType.NONE && !board[i][col].dying)
				board[i][col].chain = Math.max(board[i][col].chain, chain);
	}

	private void resetChainOnStablePieces() {
		for (int i = ROWS - 1; i >= 0; i--) {
			for (int j = 0; j < COLUMNS; j++) {
				Piece p = board[i][j];
				if (p.type != Piece.PieceType.NONE && !p.falling && !p.dying && p.chain > 1) {
					// System.out.printf("NJ: reseting chain on %d,%d   orig=%d\n",
					// i, j, p.chain);
					p.chain = 1;
				}
			}
		}
	}

	private void updateParticles(long millis) {
		Iterator<TextParticle> iter = comboParticles.iterator();
		while (iter.hasNext()) {
			TextParticle tp = iter.next();
			tp.update(millis);
			if (!tp.isActive())
				iter.remove();
		}
	}
}
