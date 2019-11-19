package com.netflix.conditionals;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

import com.netflix.conditionals.CircuitCondition.ConditionMismatchException;

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
		// test 12.054e1 isDouble
		CircuitCondition<Character> digit = CircuitCondition.between('0', '9');
		CircuitCondition<Character> decimal = CircuitCondition.of('.').maxOccurence(1);
		CircuitCondition<Character> exponent = CircuitCondition.of('e').maxOccurence(1);
		
		digit.ignore(decimal,exponent);
		digit.when(decimal).expect().circuitOpen();
		decimal.when(exponent).expect().circuitOpen();

		char[] chars = "12.0e54e1".toCharArray();
		for(char c : chars) {
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
		// accept 12.054e1 isDouble
		CircuitCondition<Character> decimal = CircuitCondition.of('.');
		CircuitCondition<Character> exponent = CircuitCondition.of('e');

		decimal.when(exponent).expect().circuitOpen();

		assertTrue(!decimal.open);
		assertTrue(!exponent.open);
		System.out.println(decimal.toString());
		decimal.accept('e');

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
		CircuitCondition<Character> decimal = CircuitCondition.flipCircuit('.');

		decimal.accept('.');// open circuit
		assertTrue(decimal.open);
		decimal.accept('.');// close circuit
		assertTrue(!decimal.open);
		decimal.accept('.');// open circuit
		assertTrue(decimal.open);
		
	}
	
	@Test
	public void testOpenCircuit() {
		CircuitCondition<Character> decimal = CircuitCondition.openCircuit('.');
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
		CircuitCondition<Character> decimal = CircuitCondition.openFlipCircuit('.');
		assertTrue(decimal.open);
		decimal.accept('.');// close circuit
		assertTrue(!decimal.open);
		decimal.accept('.');// open circuit
		assertTrue(decimal.open);
		decimal.accept('.');// close circuit
		assertTrue(!decimal.open);
		
	}

}
