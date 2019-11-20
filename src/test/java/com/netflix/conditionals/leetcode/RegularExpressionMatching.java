package com.netflix.conditionals.leetcode;

import org.junit.Test;

import com.netflix.conditionals.CircuitCondition;
import com.netflix.conditionals.Circuits;

/**
 * https://leetcode.com/problems/regular-expression-matching/
 * 
 * @author bsarac
 *
 */
public class RegularExpressionMatching {

	@Test
	public void test() {
		
		CircuitCondition<Character> single = Circuits.between('a', 'z').maxOccurence(1);
		CircuitCondition<Character> zeroOrMore = Circuits.of('.');
		Circuits<Character> circuits = Circuits.of(single,zeroOrMore);
		
		String str = "aa";
		String pattern = "a";
		
		for()
	}
}
