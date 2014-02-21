package com.example.android.snake;

import java.util.Vector;

public class BoardMatcher {
	private Piece[][] board;
	private int rows, cols;

	BoardMatcher(Piece[][] board, int rows, int cols) {
		this.board = board;
		this.rows = rows;
		this.cols = cols;
	}

	public Vector<Piece> findMatches() {
		boolean match = false;

		Vector<Piece> matches = new Vector<Piece>();

		for (int i = 0; i < rows; i++)
			matches.addAll(findRowMatches(i));
		for (int i = 0; i < cols; i++)
			matches.addAll(findColMatches(i));

		if (match) {
			for (int i = 0; i < rows; i++)
				for (int j = 0; j < cols; j++)
					if (board[i][j].dying)
						board[i][j].makeDying();
		}

		return matches;
	}

	private Vector<Piece> findRowMatches(int y) {
		Piece.PieceType lastPiece = Piece.PieceType.NONE;
		int num = 0;
		Vector<Piece> matches = new Vector<Piece>();

		for (int j = 0; j < cols; j++) {
			if (lastPiece != Piece.PieceType.NONE && board[y][j].type == lastPiece
					&& !board[y][j].dying) {
				num++;
				if (num >= 3) {
					matches.add(new Piece(y, j));
					for (int k = 0; k < num; k++)
						board[y][j - k].dying = true;
				}
			} else {
				lastPiece = board[y][j].type;
				num = 1;
			}
		}

		return matches;
	}

	private Vector<Piece> findColMatches(int x) {
		Piece.PieceType lastPiece = Piece.PieceType.NONE;
		int num = 0;
		Vector<Piece> matches = new Vector<Piece>();

		for (int i = 0; i < rows; i++) {
			if (lastPiece != Piece.PieceType.NONE && board[i][x].type == lastPiece
					&& !board[i][x].dying) {
				num++;
				if (num >= 3) {
					matches.add(new Piece(i, x));
					for (int k = 0; k < num; k++)
						board[i - k][x].dying = true;
				}
			} else {
				lastPiece = board[i][x].type;
				num = 1;
			}
		}

		return matches;
	}
}
