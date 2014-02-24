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

	public Vector<Match> findMatches() {
		Vector<Match> matches = new Vector<Match>();

		for (int i = 0; i < rows; i++)
			matches.addAll(findRowMatches(i));
		for (int i = 0; i < cols; i++)
			matches.addAll(findColMatches(i));

		combineMatches(matches);

		if (matches.size() > 0) {
			for (int i = 0; i < rows; i++)
				for (int j = 0; j < cols; j++)
					if (board[i][j].dying)
						board[i][j].makeDying();
		}

		return matches;
	}

	private Vector<Match> findRowMatches(int y) {
		Piece.PieceType lastPiece = Piece.PieceType.NONE;
		int num = 0;
		Vector<Match> matches = new Vector<Match>();
		Match currentMatch = null;

		for (int j = 0; j < cols; j++) {
			if (lastPiece != Piece.PieceType.NONE && board[y][j].type == lastPiece
					&& !board[y][j].dying) {
				num++;
				if (num >= 3) {
					if (currentMatch == null) {
						currentMatch = new Match();
						matches.add(currentMatch);
						for (int k = 1; k < num; k++) {
							board[y][j - k].setPos(y, j - k);
							currentMatch.addPiece(board[y][j - k]);
							board[y][j - k].dying = true;
						}
					}
					board[y][j].setPos(y, j);
					currentMatch.addPiece(board[y][j]);
					board[y][j].dying = true;
				}
			} else {
				lastPiece = board[y][j].type;
				num = 1;
				currentMatch = null;
			}
		}

		return matches;
	}

	private Vector<Match> findColMatches(int x) {
		Piece.PieceType lastPiece = Piece.PieceType.NONE;
		int num = 0;
		Vector<Match> matches = new Vector<Match>();
		Match currentMatch = null;

		for (int i = 0; i < rows; i++) {
			if (lastPiece != Piece.PieceType.NONE && board[i][x].type == lastPiece
					&& !board[i][x].dying) {
				num++;
				if (num >= 3) {
					if (currentMatch == null) {
						currentMatch = new Match();
						matches.add(currentMatch);
						for (int k = 1; k < num; k++) {
							board[i - k][x].setPos(i - k, x);
							currentMatch.addPiece(board[i - k][x]);
							board[i - k][x].dying = true;
						}
					}
					board[i][x].setPos(i, x);
					currentMatch.addPiece(board[i][x]);
					board[i][x].dying = true;
				}
			} else {
				lastPiece = board[i][x].type;
				num = 1;
				currentMatch = null;
			}
		}

		return matches;
	}

	private void combineMatches(Vector<Match> matches) {
		for (int i = 0; i < matches.size(); i++) {
			for (int j = 0; j < matches.size() && j > i; j++) {
				Match a = matches.get(i);
				Match b = matches.get(j);
				if (a.sharesPieceWith(b)) {
					a.mergeMatch(b);
					matches.remove(j);
					j--;
				}
			}
		}
	}
}
