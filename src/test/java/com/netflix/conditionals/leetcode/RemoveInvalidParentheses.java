package com.netflix.conditionals.leetcode;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.netflix.conditionals.CircuitCondition;
import com.netflix.conditionals.Circuits;

/**
 * 
 * https://leetcode.com/problems/remove-invalid-parentheses/
 * 
 * @author bsarac
 *
 */
public class RemoveInvalidParentheses {

	@Test
	public void test1() {
		CircuitCondition<Character> parantheses = Circuits.biCircuit('(', ')').nested();
		Circuits<Character> circuits = Circuits.of(parantheses);

		StringBuilder sb = new StringBuilder();

		"()())()".chars().forEach(i -> {
			circuits.ifAccept((char) i, sb::append);
		});

		assertTrue("()()()".equals(sb.toString()));
	}

	@Test
	public void test2() {
		CircuitCondition<Character> parantheses = Circuits.biCircuit('(', ')').nested();
		Circuits<Character> circuits = Circuits.of(parantheses);

		StringBuilder sb = new StringBuilder();

		"(a)())()".chars().forEach(i -> {
			circuits.ifAccept((char) i, sb::append);
		});

		assertTrue("(a)()()".equals(sb.toString()));
	}

	@Test
	public void test3() {
		CircuitCondition<Character> parantheses = Circuits.biCircuit('(', ')').nested();
		Circuits<Character> circuits = Circuits.of(parantheses);

		StringBuilder sb = new StringBuilder();
		AtomicInteger count = new AtomicInteger();
		AtomicInteger lastOpenIndex = new AtomicInteger();
		for (char c : ")(".toCharArray()) {
			circuits.ifAccept(c, ch->{
				sb.append(ch);
				if(ch == '(') {
					lastOpenIndex.set(count.get());
				}
				count.getAndIncrement();
			});
		}

		if (circuits.isOpen()) {
			sb.deleteCharAt(lastOpenIndex.get());
		}

		assertTrue("".equals(sb.toString()));
	}
}
