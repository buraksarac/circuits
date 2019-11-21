package org.qunix.circuits.leetcode;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;
import org.qunix.circuits.Circuit;
import org.qunix.circuits.Circuits;

/**
 * 
 * https://leetcode.com/problems/remove-invalid-parentheses/
 * 
 * @author bsarac
 *
 */
public class RemoveInvalidParentheses {

	Circuit<Character> circuit;
	StringBuilder sb = new StringBuilder();
	AtomicInteger lastOpenIndex = new AtomicInteger();

	@Before
	public void setup() {
		circuit = Circuits.biCircuit('(', ')').nested();
		circuit.onOpen(c -> lastOpenIndex.set(sb.length()));
	}

	@Test
	public void test1() {
		test("()())()", "()()()");
	}

	@Test
	public void test2() {
		test("(a)())()", "(a)()()");
	}

	@Test
	public void test3() {
		test(")(", "");
	}

	@Test
	public void test4() {
		test(")(test", "test");
	}

	private void test(String source, String expect) {
		for (char c : source.toCharArray()) {
			circuit.ifAccept(c, sb::append);
		}

		if (circuit.isOpen()) { 
			sb.deleteCharAt(lastOpenIndex.get());
		}

		assertTrue(expect.equals(sb.toString()));
	}
}
