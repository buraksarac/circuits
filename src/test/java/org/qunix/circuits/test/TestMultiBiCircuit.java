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
		biCircuit.accept('{');// keep open circuit
		assertTrue(biCircuit.isOpen());
		biCircuit.accept('}');// close circuit
		assertTrue(!biCircuit.isOpen());
		biCircuit.accept('a');// should be still closed
		assertTrue(!biCircuit.isOpen());

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
		biCircuit.accept('{');// open circuit
		assertTrue(biCircuit.isOpen());
		biCircuit.accept('.');// keep open circuit
		assertTrue(biCircuit.isOpen());
		biCircuit.accept('}');// close circuit
		biCircuit.accept('}');// close circuit
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
