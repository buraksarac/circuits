package org.qunix.circuits.leetcode;

import org.junit.Test;
import org.qunix.circuits.CircuitCondition;
import org.qunix.circuits.CircuitCondition.ConditionMismatchException;
import org.qunix.circuits.Circuits;

public class ValidateDouble {

	@Test
	public void testDouble() {
		// test 12.054e1
		CircuitCondition<Character> digit = Circuits.between('0', '9');
		CircuitCondition<Character> decimal = Circuits.singlePass('.');
		CircuitCondition<Character> exponent = Circuits.singlePass('e');

		digit.ignore(decimal, exponent);
		digit.when(decimal).expect().circuitOpen();
		decimal.when(exponent).expect().circuitOpen();

		Circuits<Character> circuits = Circuits.of(digit, decimal, exponent);

		"12.0e541".chars().forEach(c->{
			circuits.accept((char) c);
		});

	}

	@Test(expected = ConditionMismatchException.class)
	public void testDouble2() {
		// test 12.054.1
		CircuitCondition<Character> digit = Circuits.between('0', '9');
		CircuitCondition<Character> decimal = Circuits.singlePass('.');
		CircuitCondition<Character> exponent = Circuits.singlePass('e');

		digit.ignore(decimal, exponent);
		digit.when(decimal).expect().circuitOpen();
		decimal.when(exponent).expect().circuitOpen();

		Circuits<Character> circuits = Circuits.of(digit, decimal, exponent);

		char[] chars = "12.054.1 ".toCharArray();
		for (char c : chars) {
			circuits.accept(c);
		}

	}

	@Test(expected = ConditionMismatchException.class)
	public void testDouble3() {
		// test 12e.054e1
		CircuitCondition<Character> digit = Circuits.between('0', '9');
		CircuitCondition<Character> decimal = Circuits.singlePass('.');
		CircuitCondition<Character> exponent = Circuits.singlePass('e');

		digit.ignore(decimal, exponent);
		digit.when(decimal).expect().circuitOpen();
		decimal.when(exponent).expect().circuitOpen();

		Circuits<Character> circuits = Circuits.of(digit, decimal, exponent);

		char[] chars = "12e.054e1".toCharArray();
		for (char c : chars) {
			circuits.accept(c);
		}

	}
}
