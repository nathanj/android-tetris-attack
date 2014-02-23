package com.example.android.snake;

import java.util.Collection;
import java.util.Vector;

public class Match {
	Vector<Piece> pieces = new Vector<Piece>();

	public void addPiece(Piece p) {
		pieces.add(p);
	}

	public int size() {
		return pieces.size();
	}

	public void addAllPieces(Collection<? extends Piece> collection) {
		pieces.addAll(collection);
	}

	public float topX() {
		float x = Float.MAX_VALUE;
		for (Piece p : pieces) {
			if (p.x < x)
				x = p.x;
		}
		return x;
	}

	public float topY() {
		float y = Float.MAX_VALUE;
		for (Piece p : pieces) {
			if (p.y < y)
				y = p.y;
		}
		return y;
	}
}
