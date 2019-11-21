package org.qunix.circuits.leetcode;

import org.junit.Test;
import org.qunix.circuits.Circuit;
import org.qunix.circuits.Circuit.ConditionMismatchException;
import org.qunix.circuits.Circuits;

public class ValidateDouble {

	@Test
	public void testDouble() {
		// test 12.054e1
		Circuit<Character> digit = Circuits.between('0', '9');
		Circuit<Character> decimal = Circuits.singlePass('.');
		Circuit<Character> exponent = Circuits.singlePass('e');

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
		Circuit<Character> digit = Circuits.between('0', '9');
		Circuit<Character> decimal = Circuits.singlePass('.');
		Circuit<Character> exponent = Circuits.singlePass('e');

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
		Circuit<Character> digit = Circuits.between('0', '9');
		Circuit<Character> decimal = Circuits.singlePass('.');
		Circuit<Character> exponent = Circuits.singlePass('e');

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
