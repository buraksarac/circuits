package com.netflix.conditionals.leetcode;

import org.junit.Before;
import org.junit.Test;

import com.netflix.conditionals.CircuitCondition;
import com.netflix.conditionals.Circuits;

/**
 * https://leetcode.com/problems/valid-parentheses/
 */
public class ValidParentheses {

	Circuits<Character> circuits;

	@Before
	public void init() {
		CircuitCondition<Character> circuit = Circuits.multiBiCircuit('(', ')','[', ']','{', '}').nested();
		

		circuits = Circuits.of(circuit);
	}

	@Test
	public void testValidParentheses() {
		"()".chars().forEach(i -> {
			circuits.accept((char) i);
		});
		circuits.assertClosed();
	}

	@Test
	public void testValidParentheses2() {
		"()[]{}".chars().forEach(i -> {
			circuits.accept((char) i);
		});
		circuits.assertClosed();
	}

	@Test
	public void testValidParentheses3() {
		"{[]}".chars().forEach(i -> {
			circuits.accept((char) i);
		});
		circuits.assertClosed();

	}

	@Test(expected = IllegalStateException.class)
	public void testValidParenthesesFail1() {
		"(]".chars().forEach(i -> {
			circuits.accept((char) i);
		});
		circuits.assertClosed();
	}

	/**
	 * https://leetcode.com/problems/valid-parentheses/
	 */
	@Test(expected = IllegalStateException.class)
	public void testValidParenthesesFail2() {
		"([)]".chars().forEach(i -> {
			circuits.accept((char) i);
		});
		circuits.assertClosed();
	}

	/**
	 * https://leetcode.com/problems/valid-parentheses/
	 */
	@Test(expected = IllegalStateException.class)
	public void testValidParenthesesFail3() {
		"{[]}([)]".chars().forEach(i -> {
			circuits.accept((char) i);
		});
		circuits.assertClosed();
	}
}
