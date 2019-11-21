package org.qunix.circuits.leetcode;

import org.junit.Before;
import org.junit.Test;
import org.qunix.circuits.Circuit;
import org.qunix.circuits.Circuits;

/**
 * https://leetcode.com/problems/valid-parentheses/
 */
public class ValidParentheses {

	Circuits<Character> circuits;

	@Before
	public void init() {
		Circuit<Character> circuit = Circuits.multiBiCircuit('(', ')','[', ']','{', '}').nested();
		

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
