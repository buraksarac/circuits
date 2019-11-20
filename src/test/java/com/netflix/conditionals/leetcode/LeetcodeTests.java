package com.netflix.conditionals.leetcode;

import org.junit.Test;

import com.netflix.conditionals.CircuitCondition;
import com.netflix.conditionals.Circuits;

public class LeetcodeTests {

	/**
	 *  https://leetcode.com/problems/valid-parentheses/
	 */
	@Test
	public void testValidParentheses() {
		CircuitCondition<Character> paranthesis = Circuits.biCircuit('(', ')').nested();
		CircuitCondition<Character> braces = Circuits.biCircuit('{', '}').nested();
		CircuitCondition<Character> brackets = Circuits.biCircuit('{', '}').nested();
		
		Circuits.of(paranthesis,braces,brackets);
		"()".chars().forEach(i->{
			paranthesis.accept((char) i);
		});
	}
}
