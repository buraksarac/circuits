/**
 * 
 */
package org.qunix.circuits.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.qunix.circuits.BiCircuit;
import org.qunix.circuits.Circuit;
import org.qunix.circuits.Circuits;
import org.qunix.circuits.Circuit.ConditionMismatchException;

/**
 *
 * tests for {@link BiCircuit}
 *
 * @author bsarac types 2019-11-21 12:17:11 +0100
 */
public class TestBiCircuit {

	@Test
	public void testBiCircuit() {
		Circuit<Character> biCircuit = Circuits.biCircuit('{', '}');
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

	}

	@Test(expected = ConditionMismatchException.class)
	public void testBiCircuitFail() {
		Circuit<Character> biCircuit = Circuits.biCircuit('{', '}');
		biCircuit.accept('s');
		assertTrue(!biCircuit.isOpen());
		biCircuit.accept('.');
		assertTrue(!biCircuit.isOpen());
		biCircuit.accept('{');// open circuit
		assertTrue(biCircuit.isOpen());
		biCircuit.accept('{');// not nested, we should fail here

	}

	@Test
	public void testNestedBiCircuit() {
		Circuit<Character> biCircuit = Circuits.biCircuit('{', '}').nested();
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
		biCircuit.accept('.');// keep open circuit
		assertTrue(biCircuit.isOpen());
		biCircuit.accept('}');// close inner
		biCircuit.accept('}');// close outer
		assertTrue(!biCircuit.isOpen());
		biCircuit.accept('a');// should be still closed
		assertTrue(!biCircuit.isOpen());

	}

	@Test(expected = ConditionMismatchException.class)
	public void testNestedBiCircuitFail() {
		Circuit<Character> biCircuit = Circuits.biCircuit('{', '}').nested();
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
