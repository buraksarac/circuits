package org.qunix.circuits;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;
import org.qunix.circuits.CircuitCondition;
import org.qunix.circuits.CircuitCondition.ConditionMismatchException;

public class CircuitTest {

	@Test
	public void testOpen() {
		CircuitCondition<Character> condition = CircuitCondition.of('e');
		condition.accept('e');// open circuit
		assertTrue(condition.open);
	}

	@Test
	public void testOpenFail() {
		CircuitCondition<Character> condition = CircuitCondition.of('e');
		condition.accept('a');// open circuit
		assertTrue(!condition.open);
	}

	@Test
	public void testMultiOpen() {
		CircuitCondition<Character> condition = CircuitCondition.of('e', 'a');
		condition.accept('a');// open circuit
		assertTrue(condition.open);
		condition = CircuitCondition.of('e', 'a');
		condition.accept('e');// open circuit
		assertTrue(condition.open);
	}

	@Test
	public void testMultiOpenFail() {
		CircuitCondition<Character> condition = CircuitCondition.of('e', 'a');
		condition.accept('x');
		assertTrue(!condition.open);
	}

	@Test
	public void testWhen() {
		// test 12.054e1 
		CircuitCondition<Character> digit = CircuitCondition.between('0', '9');
		CircuitCondition<Character> decimal = CircuitCondition.singlePass('.');
		CircuitCondition<Character> exponent = CircuitCondition.singlePass('e');

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
		CircuitCondition<Character> digit = CircuitCondition.between('0', '9');
		CircuitCondition<Character> decimal = CircuitCondition.singlePass('.');
		CircuitCondition<Character> exponent = CircuitCondition.singlePass('e');

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
		CircuitCondition<Character> decimal = CircuitCondition.between('0', '9');
		decimal.accept('8');// open circuit
		assertTrue(decimal.open);

	}

	@Test
	public void testBetweenFail() {
		CircuitCondition<Character> decimal = CircuitCondition.between('0', '9');
		decimal.accept('e');// open circuit
		assertTrue(!decimal.open);

	}

	@Test
	public void testBetweenInt() {
		CircuitCondition<Integer> decimal = CircuitCondition.between(0, 9);
		decimal.accept(8);// open circuit
		assertTrue(decimal.open);

	}

	@Test
	public void testBetweenIntFail() {
		CircuitCondition<Integer> decimal = CircuitCondition.between(0, 9);
		decimal.accept(10);// open circuit
		assertTrue(!decimal.open);

	}

	@Test
	public void testOnOpen() {
		AtomicBoolean isOpen = new AtomicBoolean();
		CircuitCondition<Integer> decimal = CircuitCondition.between(0, 9);

		decimal.onOpen(i -> isOpen.set(true));
		decimal.accept(8);// open circuit
		assertTrue(decimal.open);
		assertTrue(isOpen.get());
	}

	@Test
	public void testOnOpen2() {
		AtomicBoolean isOpen = new AtomicBoolean();
		CircuitCondition<Integer> decimal = CircuitCondition.between(0, 9);

		decimal.onOpen(i -> isOpen.set(true));
		decimal.accept(10);// open circuit
		assertTrue(!decimal.open);
		assertTrue(!isOpen.get());
	}

	@Test
	public void testOnClose() {
		AtomicBoolean isOpen = new AtomicBoolean();
		CircuitCondition<Integer> decimal = CircuitCondition.between(0, 9);

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
		CircuitCondition<Integer> decimal = CircuitCondition.between(0, 9);

		decimal.onOpen(i -> isOpen.set(true));
		decimal.onClose(i -> isOpen.set(false));
		decimal.accept(12);// close circuit
		assertTrue(!decimal.open);
		assertTrue(!isOpen.get());
	}

	@Test
	public void testFlipCircuit() {
		CircuitCondition<Character> decimal = CircuitCondition.flipping('.');

		decimal.accept('.');// open circuit
		assertTrue(decimal.open);
		decimal.accept('.');// close circuit
		assertTrue(!decimal.open);
		decimal.accept('.');// open circuit
		assertTrue(decimal.open);

	}

	@Test
	public void testOpenCircuit() {
		CircuitCondition<Character> decimal = CircuitCondition.flowing('.').open();
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
		CircuitCondition<Character> decimal = CircuitCondition.flipping('.').open();
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
		CircuitCondition<Character> biCircuit = CircuitCondition.biCircuit('{', '}');
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
		CircuitCondition<Character> biCircuit = CircuitCondition.biCircuit('{', '}');
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
		CircuitCondition<Character> biCircuit = CircuitCondition.biCircuit('{', '}').nested();
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
		CircuitCondition<Character> biCircuit = CircuitCondition.biCircuit('{', '}').nested();
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
		CircuitCondition<Character> string = CircuitCondition.flipping('"');
		StringBuilder sb = new StringBuilder();
		string.whileOpen(sb::append);
		char[] testValue = "\"this is a test\"".toCharArray();
		for(char c : testValue) {
			string.accept(c);
		}
		assertTrue(!string.open);
		assertTrue("this is a test".equals(sb.toString()));
	}
	
	@Test
	public void testWhileClose() {
		CircuitCondition<Character> string = CircuitCondition.flipping('"');
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
