package com.example.android.snake;

public class Timer {
	private long time_remaining;
	private long length;

	Timer(int length) {
		this.length = length;
		this.time_remaining = length;
	}

	public void update(long ms) {
		time_remaining -= ms;
	}

	public boolean isActive() {
		return time_remaining > 0;
	}

	public void reset() {
		time_remaining = length;
	}

	public double percentFinished() {
		return 1.0 * (length - time_remaining) / length;
	}

	public double percentLeft() {
		return 1.0 - percentFinished();
	}

}
