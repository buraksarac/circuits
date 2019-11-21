/**
 * 
 */
package org.qunix.circuits.test;

import org.junit.Test;
import org.qunix.circuits.BiCircuit;
import org.qunix.circuits.Circuit;
import org.qunix.circuits.Circuit.ConditionMismatchException;
import org.qunix.circuits.Circuits;

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
		biCircuit.assertClosed();
		biCircuit.accept('.');
		biCircuit.assertClosed();
		biCircuit.accept('{');// open circuit
		biCircuit.assertOpen();
		biCircuit.accept('.');// keep open circuit
		biCircuit.assertOpen();
		biCircuit.accept('}');// close circuit
		biCircuit.assertClosed();
		biCircuit.accept('a');// should be still closed
		biCircuit.assertClosed();

	}

	@Test(expected = ConditionMismatchException.class)
	public void testBiCircuitFail() {
		Circuit<Character> biCircuit = Circuits.biCircuit('{', '}');
		biCircuit.accept('s');
		biCircuit.assertClosed();
		biCircuit.accept('.');
		biCircuit.assertClosed();
		biCircuit.accept('{');// open circuit
		biCircuit.assertOpen();
		biCircuit.accept('{');// not nested, we should fail here

	}

	@Test
	public void testNestedBiCircuit() {
		Circuit<Character> biCircuit = Circuits.biCircuit('{', '}').nested();
		biCircuit.accept('s');
		biCircuit.assertClosed();
		biCircuit.accept('.');
		biCircuit.assertClosed();
		biCircuit.accept('{');// open circuit
		biCircuit.assertOpen();
		biCircuit.accept('.');// keep open circuit
		biCircuit.assertOpen();
		biCircuit.accept('{');// open another
		biCircuit.assertOpen();
		biCircuit.accept('.');// keep open circuit
		biCircuit.assertOpen();
		biCircuit.accept('}');// close inner
		biCircuit.accept('}');// close outer
		biCircuit.assertClosed();
		biCircuit.accept('a');// should be still closed
		biCircuit.assertClosed();

	}

	@Test(expected = ConditionMismatchException.class)
	public void testNestedBiCircuitFail() {
		Circuit<Character> biCircuit = Circuits.biCircuit('{', '}').nested();
		biCircuit.accept('s');
		biCircuit.assertClosed();
		biCircuit.accept('.');
		biCircuit.assertClosed();
		biCircuit.accept('{');// open circuit
		biCircuit.assertOpen();
		biCircuit.accept('}');// close circuit
		biCircuit.assertClosed();
		biCircuit.accept('}');// close circuit

	}
}
