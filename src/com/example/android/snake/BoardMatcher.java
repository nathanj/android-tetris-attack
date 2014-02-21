package com.example.android.snake;

public class BoardMatcher {
	private Piece[][] board;
	private int rows, cols;

	BoardMatcher(Piece[][] board, int rows, int cols)
	{
		this.board = board;
		this.rows = rows;
		this.cols = cols;
	}

	public boolean findMatches() {
		boolean match = false;

		for (int i = 0; i < rows; i++)
			match |= findRowMatches(i);
		for (int i = 0; i < cols; i++)
			match |= findColMatches(i);

		if (match) {
			for (int i = 0; i < rows; i++)
				for (int j = 0; j < cols; j++)
					if (board[i][j].dying)
						board[i][j].makeDying();

			return true;
		}

		return false;
	}

	private boolean findRowMatches(int y) {
		Piece.PieceType lastPiece = Piece.PieceType.NONE;
		int num = 0;
		boolean match = false;

		for (int j = 0; j < cols; j++) {
			if (lastPiece != Piece.PieceType.NONE && board[y][j].type == lastPiece && !board[y][j].dying) {
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
		Piece.PieceType lastPiece = Piece.PieceType.NONE;
		int num = 0;
		boolean match = false;

		for (int i = 0; i < rows; i++) {
			if (lastPiece != Piece.PieceType.NONE && board[i][x].type == lastPiece && !board[i][x].dying) {
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
}
