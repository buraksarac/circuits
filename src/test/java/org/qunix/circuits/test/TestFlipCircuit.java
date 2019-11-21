/**
 * 
 */
package org.qunix.circuits.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.qunix.circuits.Circuit;
import org.qunix.circuits.Circuits;
import org.qunix.circuits.FlipCircuit;

/**
 *
 * Tests for {@link FlipCircuit}
 *
 * @author bsarac types 2019-11-21 11:51:53 +0100
 */
public class TestFlipCircuit {

	@Test
	public void testFlipCircuit() {
		Circuit<Character> circuit = Circuits.flipping('.');

		circuit.accept('.');// open circuit
		assertTrue(circuit.isOpen());
		circuit.accept('.');// close circuit
		assertTrue(!circuit.isOpen());
		circuit.accept(null);// close circuit
		assertTrue(!circuit.isOpen());
		circuit.accept('.');// open circuit
		assertTrue(circuit.isOpen());

	}
	
	@Test
	public void testNullFlipCircuit() {
		Circuit<Character> circuit = Circuits.flipping('.',null);

		circuit.accept('.');// open circuit
		assertTrue(circuit.isOpen());
		circuit.accept(null);// close circuit
		assertTrue(!circuit.isOpen());
		circuit.accept('.');// open circuit
		assertTrue(circuit.isOpen());

	}
	
	@Test
	public void testBetweenFlipCircuit() {
		Circuit<Character> circuit = Circuits.between('a', 'z').flipping();

		circuit.accept('.');// keep closed
		assertTrue(!circuit.isOpen());
		circuit.accept(null);// keep closed
		assertTrue(!circuit.isOpen());
		circuit.accept('b');// open
		assertTrue(circuit.isOpen());
		circuit.accept('a');// close
		assertTrue(!circuit.isOpen());
		circuit.accept('z');// open
		assertTrue(circuit.isOpen());
		circuit.accept('a');// close
		assertTrue(!circuit.isOpen());
		circuit.accept('!');// keep closed
		assertTrue(!circuit.isOpen());
		circuit.accept(null);// keep closed
		assertTrue(!circuit.isOpen());

	}
}
