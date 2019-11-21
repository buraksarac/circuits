/**
 * 
 */
package org.qunix.circuits.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.qunix.circuits.Circuit;
import org.qunix.circuits.Circuits;
import org.qunix.circuits.MultiBiCircuit;
import org.qunix.circuits.Circuit.ConditionMismatchException;

/**
 *
 * tests for {@link MultiBiCircuit}
 *
 * @author bsarac types 2019-11-21 12:18:33 +0100
 */
public class TestMultiBiCircuit {

	
	
	@Test
	public void testMultiBiCircuit() {
		Circuit<Character> biCircuit = Circuits.multiBiCircuit('(', ')','[', ']','{', '}');
		biCircuit.accept('s');
		assertTrue(!biCircuit.isOpen());
		biCircuit.accept('.');
		assertTrue(!biCircuit.isOpen());
		biCircuit.accept('{');// open circuit
		assertTrue(biCircuit.isOpen());
		biCircuit.accept('.');// keep open circuit
		assertTrue(biCircuit.isOpen());
		biCircuit.accept('}');// close circuit
		assertTrue(!biCircuit.isOpen());
		biCircuit.accept('a');// should be still closed
		assertTrue(!biCircuit.isOpen());
		
		biCircuit.accept('s');
		assertTrue(!biCircuit.isOpen());
		biCircuit.accept('.');
		assertTrue(!biCircuit.isOpen());
		biCircuit.accept('[');// open circuit
		assertTrue(biCircuit.isOpen());
		biCircuit.accept('.');// keep open circuit
		assertTrue(biCircuit.isOpen());
		biCircuit.accept(']');// close circuit
		assertTrue(!biCircuit.isOpen());
		biCircuit.accept('a');// should be still closed
		assertTrue(!biCircuit.isOpen());
		
		biCircuit.accept('s');
		assertTrue(!biCircuit.isOpen());
		biCircuit.accept('.');
		assertTrue(!biCircuit.isOpen());
		biCircuit.accept('(');// open circuit
		assertTrue(biCircuit.isOpen());
		biCircuit.accept('.');// keep open circuit
		assertTrue(biCircuit.isOpen());
		biCircuit.accept(')');// close circuit
		assertTrue(!biCircuit.isOpen());
		biCircuit.accept('a');// should be still closed
		assertTrue(!biCircuit.isOpen());

	}

	@Test(expected = ConditionMismatchException.class)
	public void testMultiBiCircuitFail() {
		Circuit<Character> biCircuit = Circuits.multiBiCircuit('(', ')','[', ']','{', '}');
		biCircuit.accept('s');
		assertTrue(!biCircuit.isOpen());
		biCircuit.accept('.');
		assertTrue(!biCircuit.isOpen());
		biCircuit.accept('{');// open circuit
		assertTrue(biCircuit.isOpen());
		biCircuit.accept('{');// its not nested we should fail here
	}

	@Test
	public void testNestedMultiBiCircuit() {
		Circuit<Character> biCircuit = Circuits.multiBiCircuit('(', ')','[', ']','{', '}').nested();
		biCircuit.accept('s');
		assertTrue(!biCircuit.isOpen());
		biCircuit.accept('.');
		assertTrue(!biCircuit.isOpen());
		biCircuit.accept('{');// open circuit
		assertTrue(biCircuit.isOpen());
		biCircuit.accept('.');// keep open circuit
		assertTrue(biCircuit.isOpen());
		biCircuit.accept('{');// open another 
		assertTrue(biCircuit.isOpen());
		biCircuit.accept('.');// keep open 
		assertTrue(biCircuit.isOpen());
		biCircuit.accept('}');// close inner
		assertTrue(biCircuit.isOpen());
		biCircuit.accept('}');// close outer 
		assertTrue(!biCircuit.isOpen());
		biCircuit.accept('a');// should be still closed
		assertTrue(!biCircuit.isOpen());

	}

	@Test(expected = ConditionMismatchException.class)
	public void testNestedMultiBiCircuitFail() {
		Circuit<Character> biCircuit = Circuits.multiBiCircuit('(', ')','[', ']','{', '}').nested();
		biCircuit.accept('s');
		assertTrue(!biCircuit.isOpen());
		biCircuit.accept('.');
		assertTrue(!biCircuit.isOpen());
		biCircuit.accept('{');// open circuit
		assertTrue(biCircuit.isOpen());
		biCircuit.accept('}');// close circuit
		assertTrue(!biCircuit.isOpen());
		biCircuit.accept('}');// close circuit

	}
}
