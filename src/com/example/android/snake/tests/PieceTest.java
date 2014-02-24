package com.example.android.snake.tests;

import java.util.Set;
import java.util.TreeSet;

import junit.framework.TestCase;

import com.example.android.snake.Piece;

public class PieceTest extends TestCase {

	public void testCompare() {
		Piece a = new Piece(0, 0);
		Piece b = new Piece(1, 1);
		Piece c = new Piece(1, 0);
		Piece d = new Piece(0, 1);
		assertEquals(0, a.compareTo(a));
		assertEquals(-1, a.compareTo(b));
		assertEquals(-1, a.compareTo(c));
		assertEquals(-1, a.compareTo(d));

		assertEquals(1, b.compareTo(a));
		assertEquals(0, b.compareTo(b));
		assertEquals(1, b.compareTo(c));
		assertEquals(1, b.compareTo(d));

		assertEquals(1, c.compareTo(a));
		assertEquals(-1, c.compareTo(b));
		assertEquals(0, c.compareTo(c));
		assertEquals(1, c.compareTo(d));

		assertEquals(1, d.compareTo(a));
		assertEquals(-1, d.compareTo(b));
		assertEquals(-1, d.compareTo(c));
		assertEquals(0, d.compareTo(d));
	}

	public void testSet() {
		Set<Piece> set = new TreeSet<Piece>();
		assertEquals(true, set.add(new Piece(0, 0)));
		assertEquals(1, set.size());
		assertEquals(true, set.add(new Piece(1, 1)));
		assertEquals(2, set.size());
		assertEquals(true, set.add(new Piece(2, 1)));
		assertEquals(3, set.size());
		assertEquals(false, set.add(new Piece(1, 1)));
		assertEquals(3, set.size());
	}
}
