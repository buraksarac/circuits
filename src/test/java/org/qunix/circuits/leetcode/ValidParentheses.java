package org.qunix.circuits.leetcode;

import org.junit.Before;
import org.junit.Test;
import org.qunix.circuits.Circuits;

/**
 * https://leetcode.com/problems/valid-parentheses/
 */
public class ValidParentheses {

	org.qunix.circuits.Circuit<Character> circuit;

	@Before
	public void init() {
		circuit = Circuits.multiBiCircuit('(', ')', '[', ']', '{', '}').nested();
	}

	@Test
	public void testValidParentheses() {
		"()".chars().forEach(i -> {
			circuit.accept((char) i);
		});
		circuit.assertClosed();
	}

	@Test
	public void testValidParentheses2() {
		"()[]{}".chars().forEach(i -> {
			circuit.accept((char) i);
		});
		circuit.assertClosed();
	}

	@Test
	public void testValidParentheses3() {
		"{[]}".chars().forEach(i -> {
			circuit.accept((char) i);
		});
		circuit.assertClosed();

	}

	@Test(expected = IllegalStateException.class)
	public void testValidParenthesesFail1() {
		"(]".chars().forEach(i -> {
			circuit.accept((char) i);
		});
		circuit.assertClosed();
	}

	/**
	 * https://leetcode.com/problems/valid-parentheses/
	 */
	@Test(expected = IllegalStateException.class)
	public void testValidParenthesesFail2() {
		"([)]".chars().forEach(i -> {
			circuit.accept((char) i);
		});
		circuit.assertClosed();
	}

	/**
	 * https://leetcode.com/problems/valid-parentheses/
	 */
	@Test(expected = IllegalStateException.class)
	public void testValidParenthesesFail3() {
		"{[]}([)]".chars().forEach(i -> {
			circuit.accept((char) i);
		});
		circuit.assertClosed();
	}
}
