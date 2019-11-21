/**
 * 
 */
package org.qunix.circuits.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.qunix.circuits.Circuit;
import org.qunix.circuits.Circuit.ConditionMismatchException;
import org.qunix.circuits.Circuits;
import org.qunix.circuits.SinglePassCircuit;

/**
 *
 * tests for {@link SinglePassCircuit}
 *
 * @author bsarac types 2019-11-21 12:07:33 +0100
 */
public class TestSinglePass {

	@Test
	public void testValid() {
		Circuit<Character> circuit = Circuits.singlePass('.');

		circuit.accept(null);
		assertTrue(!circuit.isOpen());
		circuit.accept('.');// open circuit
		assertTrue(circuit.isOpen());
		circuit.accept(null);
		assertTrue(circuit.isOpen());

	}

	@Test(expected = ConditionMismatchException.class)
	public void testFail() {
		Circuit<Character> circuit = Circuits.singlePass('.');

		circuit.accept(null);
		assertTrue(!circuit.isOpen());
		circuit.accept('.');// open circuit
		assertTrue(circuit.isOpen());
		circuit.accept(null);
		assertTrue(circuit.isOpen());
		circuit.accept('.');// open circuit
		assertTrue(circuit.isOpen());

	}
}
