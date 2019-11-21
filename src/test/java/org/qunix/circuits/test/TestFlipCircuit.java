/**
 * 
 */
package org.qunix.circuits.test;

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
		circuit.assertOpen();
		circuit.accept('.');// close circuit
		circuit.assertClosed();
		circuit.accept(null);// close circuit
		circuit.assertClosed();
		circuit.accept('.');// open circuit
		circuit.assertOpen();

	}
	
	@Test
	public void testNullFlipCircuit() {
		Circuit<Character> circuit = Circuits.flipping('.',null);

		circuit.accept('.');// open circuit
		circuit.assertOpen();
		circuit.accept(null);// close circuit
		circuit.assertClosed();
		circuit.accept('.');// open circuit
		circuit.assertOpen();

	}
	
	@Test
	public void testBetweenFlipCircuit() {
		Circuit<Character> circuit = Circuits.between('a', 'z').flipping();

		circuit.accept('.');// keep closed
		circuit.assertClosed();
		circuit.accept(null);// keep closed
		circuit.assertClosed();
		circuit.accept('b');// open
		circuit.assertOpen();
		circuit.accept('a');// close
		circuit.assertClosed();
		circuit.accept('z');// open
		circuit.assertOpen();
		circuit.accept('a');// close
		circuit.assertClosed();
		circuit.accept('!');// keep closed
		circuit.assertClosed();
		circuit.accept(null);// keep closed
		circuit.assertClosed();

	}
}
