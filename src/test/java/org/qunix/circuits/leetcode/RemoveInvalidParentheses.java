package org.qunix.circuits.leetcode;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;

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
	LinkedList<Integer> lastOpenIndexes = new LinkedList<>();

	@Before
	public void setup() {
		circuit = Circuits.biCircuit('(', ')').nested();
		circuit.onOpen(c -> lastOpenIndexes.offer(sb.length()));
		circuit.onClose(c -> lastOpenIndexes.poll());
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

	@Test
	public void test5() {
		test("(a)(()))()", "(a)(())()");
	}

	@Test
	public void test6() {
		test("))sad0asd0a)A)SD())D)()((D)AS))S))))", "sad0asd0aASD()D()((D)AS)S");
	}

	@Test
	public void test7() {
		test("", "");
	}

	@Test
	public void test8() {
		test("[", "[");
	}

	@Test
	public void test9() {
		test("(((((((((", "");
	}

	@Test
	public void test10() {
		test("(((a((b(((c(", "abc");
	}

	@Test
	public void test11() {
		test(")))))", "");
	}

	private void test(String source, String expect) {
		for (char c : source.toCharArray()) {
			circuit.ifAccept(c, sb::append);
		}

		if (circuit.isOpen()) {
			while (!lastOpenIndexes.isEmpty()) {
				sb.deleteCharAt(Math.max(0, lastOpenIndexes.pollLast()));
			}
		}

		assertEquals(expect, sb.toString());
	}
}
