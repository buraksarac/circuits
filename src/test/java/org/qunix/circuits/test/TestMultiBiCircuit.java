/**
 * 
 */
package org.qunix.circuits.test;

import org.junit.Test;
import org.qunix.circuits.Circuit;
import org.qunix.circuits.Circuit.ConditionMismatchException;
import org.qunix.circuits.Circuits;
import org.qunix.circuits.MultiBiCircuit;

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
		
		biCircuit.accept('s');
		biCircuit.assertClosed();
		biCircuit.accept('.');
		biCircuit.assertClosed();
		biCircuit.accept('[');// open circuit
		biCircuit.assertOpen();
		biCircuit.accept('.');// keep open circuit
		biCircuit.assertOpen();
		biCircuit.accept(']');// close circuit
		biCircuit.assertClosed();
		biCircuit.accept('a');// should be still closed
		biCircuit.assertClosed();
		
		biCircuit.accept('s');
		biCircuit.assertClosed();
		biCircuit.accept('.');
		biCircuit.assertClosed();
		biCircuit.accept('(');// open circuit
		biCircuit.assertOpen();
		biCircuit.accept('.');// keep open circuit
		biCircuit.assertOpen();
		biCircuit.accept(')');// close circuit
		biCircuit.assertClosed();
		biCircuit.accept('a');// should be still closed
		biCircuit.assertClosed();

	}

	@Test(expected = ConditionMismatchException.class)
	public void testMultiBiCircuitFail() {
		Circuit<Character> biCircuit = Circuits.multiBiCircuit('(', ')','[', ']','{', '}');
		biCircuit.accept('s');
		biCircuit.assertClosed();
		biCircuit.accept('.');
		biCircuit.assertClosed();
		biCircuit.accept('{');// open circuit
		biCircuit.assertOpen();
		biCircuit.accept('{');// its not nested we should fail here
	}

	@Test
	public void testNestedMultiBiCircuit() {
		Circuit<Character> biCircuit = Circuits.multiBiCircuit('(', ')','[', ']','{', '}').nested();
		biCircuit.accept('s');
		biCircuit.assertClosed();
		biCircuit.accept('.');
		biCircuit.assertClosed();
		biCircuit.accept('(');// open circuit
		biCircuit.assertOpen();
		biCircuit.accept('.');// keep open circuit
		biCircuit.assertOpen();
		biCircuit.accept('[');// open another 
		biCircuit.assertOpen();
		biCircuit.accept('.');// keep open 
		biCircuit.assertOpen();
		biCircuit.accept(']');// close inner
		biCircuit.assertOpen();
		biCircuit.accept(')');// close outer 
		biCircuit.assertClosed();
		biCircuit.accept('a');// should be still closed
		biCircuit.assertClosed();

	}

	@Test(expected = ConditionMismatchException.class)
	public void testNestedMultiBiCircuitFail() {
		Circuit<Character> biCircuit = Circuits.multiBiCircuit('(', ')','[', ']','{', '}').nested();
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
