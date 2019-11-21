package org.qunix.circuits;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;
import org.qunix.circuits.Circuit;
import org.qunix.circuits.Circuit.ConditionMismatchException;

public class CircuitTest {

	@Test
	public void testOpen() {
		Circuit<Character> condition = Circuit.of('e');
		condition.accept('e');// open circuit
		assertTrue(condition.open);
	}

	@Test
	public void testOpenFail() {
		Circuit<Character> condition = Circuit.of('e');
		condition.accept('a');// open circuit
		assertTrue(!condition.open);
	}

	@Test
	public void testMultiOpen() {
		Circuit<Character> condition = Circuit.of('e', 'a');
		condition.accept('a');// open circuit
		assertTrue(condition.open);
		condition = Circuit.of('e', 'a');
		condition.accept('e');// open circuit
		assertTrue(condition.open);
	}

	@Test
	public void testMultiOpenFail() {
		Circuit<Character> condition = Circuit.of('e', 'a');
		condition.accept('x');
		assertTrue(!condition.open);
	}

	@Test
	public void testWhen() {
		// test 12.054e1 
		Circuit<Character> digit = Circuit.between('0', '9');
		Circuit<Character> decimal = Circuit.singlePass('.');
		Circuit<Character> exponent = Circuit.singlePass('e');

		digit.ignore(decimal, exponent);
		digit.when(decimal).expect().circuitOpen();
		decimal.when(exponent).expect().circuitOpen();

		char[] chars = "12.0e541".toCharArray();
		for (char c : chars) {
			digit.accept(c);
			decimal.accept(c);
			exponent.accept(c);
		}
		assertTrue(digit.open);
		assertTrue(decimal.open);
		assertTrue(exponent.open);

	}

	@Test(expected = ConditionMismatchException.class)
	public void testWhenFail() {
		// test 12.054e1 
		Circuit<Character> digit = Circuit.between('0', '9');
		Circuit<Character> decimal = Circuit.singlePass('.');
		Circuit<Character> exponent = Circuit.singlePass('e');

		digit.ignore(decimal, exponent).when(decimal).expect().circuitOpen();
		decimal.when(exponent).expect().circuitOpen();

		char[] chars = "12.0e5e41".toCharArray();
		for (char c : chars) {
			digit.accept(c);
			decimal.accept(c);
			exponent.accept(c);
		}
		assertTrue(digit.open);
		assertTrue(decimal.open);
		assertTrue(exponent.open);

	}

	@Test
	public void testBetween() {
		Circuit<Character> decimal = Circuit.between('0', '9');
		decimal.accept('8');// open circuit
		assertTrue(decimal.open);

	}

	@Test
	public void testBetweenFail() {
		Circuit<Character> decimal = Circuit.between('0', '9');
		decimal.accept('e');// open circuit
		assertTrue(!decimal.open);

	}

	@Test
	public void testBetweenInt() {
		Circuit<Integer> decimal = Circuit.between(0, 9);
		decimal.accept(8);// open circuit
		assertTrue(decimal.open);

	}

	@Test
	public void testBetweenIntFail() {
		Circuit<Integer> decimal = Circuit.between(0, 9);
		decimal.accept(10);// open circuit
		assertTrue(!decimal.open);

	}

	@Test
	public void testOnOpen() {
		AtomicBoolean isOpen = new AtomicBoolean();
		Circuit<Integer> decimal = Circuit.between(0, 9);

		decimal.onOpen(i -> isOpen.set(true));
		decimal.accept(8);// open circuit
		assertTrue(decimal.open);
		assertTrue(isOpen.get());
	}

	@Test
	public void testOnOpen2() {
		AtomicBoolean isOpen = new AtomicBoolean();
		Circuit<Integer> decimal = Circuit.between(0, 9);

		decimal.onOpen(i -> isOpen.set(true));
		decimal.accept(10);// open circuit
		assertTrue(!decimal.open);
		assertTrue(!isOpen.get());
	}

	@Test
	public void testOnClose() {
		AtomicBoolean isOpen = new AtomicBoolean();
		Circuit<Integer> decimal = Circuit.between(0, 9);

		decimal.onOpen(i -> isOpen.set(true));
		decimal.onClose(i -> isOpen.set(false));
		decimal.accept(8);// open circuit
		decimal.accept(12);// close circuit
		assertTrue(!decimal.open);
		assertTrue(!isOpen.get());
	}

	@Test
	public void testOnClose2() {
		AtomicBoolean isOpen = new AtomicBoolean();
		Circuit<Integer> decimal = Circuit.between(0, 9);

		decimal.onOpen(i -> isOpen.set(true));
		decimal.onClose(i -> isOpen.set(false));
		decimal.accept(12);// close circuit
		assertTrue(!decimal.open);
		assertTrue(!isOpen.get());
	}

	@Test
	public void testFlipCircuit() {
		Circuit<Character> decimal = Circuit.flipping('.');

		decimal.accept('.');// open circuit
		assertTrue(decimal.open);
		decimal.accept('.');// close circuit
		assertTrue(!decimal.open);
		decimal.accept('.');// open circuit
		assertTrue(decimal.open);

	}

	@Test
	public void testOpenCircuit() {
		Circuit<Character> decimal = Circuit.flowing('.').open();
		assertTrue(decimal.open);
		decimal.accept('a');// close circuit
		assertTrue(!decimal.open);
		decimal.accept('.');// open circuit
		assertTrue(decimal.open);
		decimal.accept('e');// close circuit
		assertTrue(!decimal.open);

	}

	@Test
	public void testOpenFlipCircuit() {
		Circuit<Character> decimal = Circuit.flipping('.').open();
		assertTrue(decimal.open);
		decimal.accept('.');// close circuit
		assertTrue(!decimal.open);
		decimal.accept('.');// open circuit
		assertTrue(decimal.open);
		decimal.accept('.');// close circuit
		assertTrue(!decimal.open);

	}

	@Test
	public void testBiCircuit() {
		Circuit<Character> biCircuit = Circuit.biCircuit('{', '}');
		biCircuit.accept('s');
		assertTrue(!biCircuit.open);
		biCircuit.accept('.');
		assertTrue(!biCircuit.open);
		biCircuit.accept('{');// open circuit
		assertTrue(biCircuit.open);
		biCircuit.accept('.');// keep open circuit
		assertTrue(biCircuit.open);
		biCircuit.accept('}');// close circuit
		assertTrue(!biCircuit.open);
		biCircuit.accept('a');// should be still closed
		assertTrue(!biCircuit.open);

	}

	@Test(expected = ConditionMismatchException.class)
	public void testBiCircuitFail() {
		Circuit<Character> biCircuit = Circuit.biCircuit('{', '}');
		biCircuit.accept('s');
		assertTrue(!biCircuit.open);
		biCircuit.accept('.');
		assertTrue(!biCircuit.open);
		biCircuit.accept('{');// open circuit
		assertTrue(biCircuit.open);
		biCircuit.accept('{');// keep open circuit
		assertTrue(biCircuit.open);
		biCircuit.accept('}');// close circuit
		assertTrue(!biCircuit.open);
		biCircuit.accept('a');// should be still closed
		assertTrue(!biCircuit.open);

	}

	@Test
	public void testNestedBiCircuit() {
		Circuit<Character> biCircuit = Circuit.biCircuit('{', '}').nested();
		biCircuit.accept('s');
		assertTrue(!biCircuit.open);
		biCircuit.accept('.');
		assertTrue(!biCircuit.open);
		biCircuit.accept('{');// open circuit
		assertTrue(biCircuit.open);
		biCircuit.accept('.');// keep open circuit
		assertTrue(biCircuit.open);
		biCircuit.accept('{');// open circuit
		assertTrue(biCircuit.open);
		biCircuit.accept('.');// keep open circuit
		assertTrue(biCircuit.open);
		biCircuit.accept('}');// close circuit
		biCircuit.accept('}');// close circuit
		assertTrue(!biCircuit.open);
		biCircuit.accept('a');// should be still closed
		assertTrue(!biCircuit.open);

	}

	@Test(expected = ConditionMismatchException.class)
	public void testNestedBiCircuitFail() {
		Circuit<Character> biCircuit = Circuit.biCircuit('{', '}').nested();
		biCircuit.accept('s');
		assertTrue(!biCircuit.open);
		biCircuit.accept('.');
		assertTrue(!biCircuit.open);
		biCircuit.accept('{');// open circuit
		assertTrue(biCircuit.open);
		biCircuit.accept('}');// close circuit
		assertTrue(!biCircuit.open);
		biCircuit.accept('}');// close circuit

	}
	
	@Test
	public void testWhileOpen() {
		Circuit<Character> string = Circuit.flipping('"');
		StringBuilder sb = new StringBuilder();
		string.whileOpen(sb::append);
		//string.onOpen(System.out::println);
		char[] testValue = "\"this is a test\"".toCharArray();
		for(char c : testValue) {
			string.accept(c);
		}
		assertTrue(!string.open);
		assertTrue("this is a test".equals(sb.toString()));
	}
	
	@Test
	public void testWhileClose() {
		Circuit<Character> string = Circuit.flipping('"');
		StringBuilder sb = new StringBuilder();
		string.whileClosed(sb::append);
		char[] testValue = "outSideString\"this is a test\"".toCharArray();
		for(char c : testValue) {
			string.accept(c);
		}
		assertTrue(!string.open);
		assertTrue("outSideString".equals(sb.toString()));
	}

}
