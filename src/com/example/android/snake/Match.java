package com.example.android.snake;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class Match {
	Set<Piece> pieces = new TreeSet<Piece>();

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

	public boolean sharesPieceWith(Match o) {
		for (Piece p : o.pieces) {
			if (pieces.contains(p))
				return true;
		}
		return false;
	}

	public void mergeMatch(Match o) {
		pieces.addAll(o.pieces);
	}

	public int chain() {
		int chain = 0;
		for (Piece p : pieces)
			chain = Math.max(chain, p.chain);
		return chain;
	}
}
