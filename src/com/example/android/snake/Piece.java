package com.example.android.snake;

public class Piece implements Comparable<Piece> {
	public final static int TILE_SIZE = 64;

	public enum PieceType {
		NONE, GREEN, RED, BLUE, YELLOW, PURPLE
	}

	PieceType type;
	boolean dying = false;
	boolean selected = false;
	boolean falling = false;
	float speed = 0f;
	int x, y;
	float extra_y = 0f;
	float dying_time = 0f;
	int chain = 1;

	public Piece() {
		type = PieceType.NONE;
	}

	public Piece(PieceType type) {
		this.type = type;
	}

	public Piece(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void setPos(int x, int y) {
		this.x = x;
		this.y = y;
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

	public void makeDying() {
		if (type == PieceType.NONE)
			return;

		if (dying)
			return;

		dying = true;
		dying_time = 0f;
	}

	@Override
	public String toString() {
		return "Piece(" + x + "," + y + ")";
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Piece) {
			return compareTo((Piece) o) == 0;
		}
		return false;
	}

	@Override
	public int compareTo(Piece o) {
		if (x == o.x) {
			return (int) Math.signum(y - o.y);
		}
		return (int) Math.signum(x - o.x);
	}
}