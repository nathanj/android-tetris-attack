package com.example.android.snake;

public class Piece {
	public enum PieceType {
		NONE, GREEN, RED, BLUE, YELLOW, PURPLE
	}

	PieceType type;
	boolean dying = false;
	boolean selected = false;
	boolean falling = false;
	float speed = 0f;
	float x, y;
	float extra_y = 0f;
	float dying_time = 0f;

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

	public void makeDying() {
		if (type == PieceType.NONE)
			return;

		if (dying)
			return;

		dying = true;
		dying_time = 0f;
	}
}