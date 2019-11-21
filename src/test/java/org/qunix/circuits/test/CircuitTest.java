package org.qunix.circuits.test;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;
import org.qunix.circuits.Circuit;
import org.qunix.circuits.Circuits;
import org.qunix.circuits.Circuit.ConditionMismatchException;

public class CircuitTest {

	@Test
	public void testOpen() {
		Circuit<Character> condition = Circuits.flowing('e');
		condition.accept('e');// open circuit
		assertTrue(condition.isOpen());
		condition.accept('a');// close circuit
		assertTrue(!condition.isOpen());
		condition.open();
		assertTrue(condition.isOpen());
		assertTrue(!condition.isClosed());
	}

	@Test
	public void testClose() {
		Circuit<Character> condition = Circuits.flowing('e');
		condition.accept('a');
		assertTrue(!condition.isOpen());
		condition.accept('e');// open circuit
		assertTrue(condition.isOpen());
		condition.accept('a');// close circuit
		assertTrue(!condition.isOpen());
		condition.close();
		assertTrue(!condition.isOpen());
		assertTrue(condition.isClosed());
	}

	@Test
	public void testManyParamOpen() {
		Circuit<Character> condition = Circuits.flowing('e', 'a');
		condition.accept('a');// open circuit
		assertTrue(condition.isOpen());
		condition.accept('r');// close circuit
		assertTrue(!condition.isOpen());
		condition.accept('a');// open circuit
		assertTrue(condition.isOpen());
		condition = Circuits.flowing('e', 'a');
		condition.accept('e');// open circuit
		assertTrue(condition.isOpen());
	}

	@Test
	public void testManyParamClose() {
		Circuit<Character> condition = Circuits.flipping('e', 'a');
		condition.accept('x');
		assertTrue(!condition.isOpen());
		condition.accept('a');// open circuit
		assertTrue(condition.isOpen());
		condition.accept('e');// close circuit
		assertTrue(!condition.isOpen());
		condition.accept('a');// open circuit
		assertTrue(condition.isOpen());
		condition.accept('a');// close circuit
		assertTrue(!condition.isOpen());
	}

	@Test
	public void testWhen() {
		// test 12e.054e1
		Circuit<Character> digit = Circuits.between('0', '9').flowing();
		Circuit<Character> decimal = Circuits.singlePass('.');
		Circuit<Character> exponent = Circuits.singlePass('e');

		digit.when(decimal).expect().circuitOpen()
			.and().when(exponent).expect().circuitOpen()
			.and().when(exponent).expect().open(decimal);

		Circuits<Character> circuits = Circuits.of(digit, decimal, exponent);

		char[] chars = "12.0e541".toCharArray();
		for (char c : chars) {
			circuits.accept(c);
		}
		circuits.assertOpen();

	}

	@Test(expected = ConditionMismatchException.class)
	public void testWhenFail() {
		// test "12.0e5e41"
		Circuit<Character> digit = Circuits.between('0', '9').flowing();
		Circuit<Character> decimal = Circuits.singlePass('.');
		Circuit<Character> exponent = Circuits.singlePass('e');

		digit.when(decimal).expect().circuitOpen()
		.and().when(exponent).expect().circuitOpen()
		.and().when(exponent).expect().open(decimal);

		Circuits<Character> circuits = Circuits.of(digit, decimal, exponent);

		char[] chars = "12.0e5e41".toCharArray();
		for (char c : chars) {
			circuits.accept(c);
		}
		circuits.assertOpen();

	}

	@Test
	public void testBetween() {
		Circuit<Character> decimal = Circuits.between('0', '9').flipping();
		decimal.accept('8');// open circuit
		assertTrue(decimal.isOpen());

	}

	@Test
	public void testBetweenFail() {
		Circuit<Character> decimal = Circuits.between('0', '9').flowing();
		decimal.accept('e');// open circuit
		assertTrue(!decimal.isOpen());

	}

	@Test
	public void testBetweenInt() {
		Circuit<Integer> decimal = Circuits.between(0, 9).flowing();
		decimal.accept(8);// open circuit
		assertTrue(decimal.isOpen());

	}

	@Test
	public void testBetweenIntFail() {
		Circuit<Integer> decimal = Circuits.between(0, 9).flowing();
		decimal.accept(10);// open circuit
		assertTrue(!decimal.isOpen());

	}

	@Test
	public void testOnOpen() {
		AtomicBoolean isOpen = new AtomicBoolean();
		Circuit<Integer> decimal = Circuits.between(0, 9).flowing();

		decimal.onOpen(i -> isOpen.set(true));
		decimal.accept(8);// open circuit
		assertTrue(decimal.isOpen());
		assertTrue(isOpen.get());
	}

	@Test
	public void testOnOpen2() {
		AtomicBoolean isOpen = new AtomicBoolean();
		Circuit<Integer> decimal = Circuits.between(0, 9).flowing();

		decimal.onOpen(i -> isOpen.set(true));
		decimal.accept(10);// open circuit
		assertTrue(!decimal.isOpen());
		assertTrue(!isOpen.get());
	}

	@Test
	public void testOnClose() {
		AtomicBoolean isOpen = new AtomicBoolean();
		Circuit<Integer> decimal = Circuits.between(0, 9).flowing();

		decimal.onOpen(i -> isOpen.set(true));
		decimal.onClose(i -> isOpen.set(false));
		decimal.accept(8);// open circuit
		decimal.accept(12);// close circuit
		assertTrue(!decimal.isOpen());
		assertTrue(!isOpen.get());
	}

	@Test
	public void testOnClose2() {
		AtomicBoolean isOpen = new AtomicBoolean();
		Circuit<Integer> decimal = Circuits.between(0, 9).flowing();

		decimal.onOpen(i -> isOpen.set(true));
		decimal.onClose(i -> isOpen.set(false));
		decimal.accept(12);// close circuit
		assertTrue(!decimal.isOpen());
		assertTrue(!isOpen.get());
	}

	@Test
	public void testWhileOpen() {
		Circuit<Character> string = Circuits.flipping('"');
		StringBuilder sb = new StringBuilder();
		string.whileOpen(sb::append);
		char[] testValue = "\"this is a test\"".toCharArray();
		for (char c : testValue) {
			string.accept(c);
		}
		assertTrue(!string.isOpen());
		assertTrue("this is a test".equals(sb.toString()));
	}

	@Test
	public void testWhileClose() {
		Circuit<Character> string = Circuits.flipping('"');
		StringBuilder sb = new StringBuilder();
		string.whileClosed(sb::append);
		char[] testValue = "outSideString\"this is a test\"".toCharArray();
		for (char c : testValue) {
			string.accept(c);
		}
		assertTrue(!string.isOpen());
		assertTrue("outSideString".equals(sb.toString()));
	}

}
