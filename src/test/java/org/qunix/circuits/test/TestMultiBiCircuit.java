/**
 * 
 */
package org.qunix.circuits.test;

import static org.junit.Assert.assertTrue;

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
		Circuit<Character> circuit = Circuits.multiBiCircuit('(', ')', '[', ']', '{', '}');
		circuit.accept('s');
		circuit.assertClosed();
		circuit.accept('.');
		circuit.assertClosed();
		circuit.accept('{');// open circuit
		circuit.assertOpen();
		circuit.accept('.');// keep open circuit
		circuit.assertOpen();
		circuit.accept('}');// close circuit
		circuit.assertClosed();
		circuit.accept('a');// should be still closed
		circuit.assertClosed();

		circuit.accept('s');
		circuit.assertClosed();
		circuit.accept('.');
		circuit.assertClosed();
		circuit.accept('[');// open circuit
		circuit.assertOpen();
		circuit.accept('.');// keep open circuit
		circuit.assertOpen();
		circuit.accept(']');// close circuit
		circuit.assertClosed();
		circuit.accept('a');// should be still closed
		circuit.assertClosed();

		circuit.accept('s');
		circuit.assertClosed();
		circuit.accept('.');
		circuit.assertClosed();
		circuit.accept('(');// open circuit
		circuit.assertOpen();
		circuit.accept('.');// keep open circuit
		circuit.assertOpen();
		circuit.accept(')');// close circuit
		circuit.assertClosed();
		circuit.accept('a');// should be still closed
		circuit.assertClosed();

	}

	@Test
	public void testMultiBiCircuit2() {
		Circuit<Character> circuit = Circuits.multiBiCircuit('(', ')', '[', ']', '{', '}');

		"()[]{}".chars().forEach(i->circuit.accept((char) i));
		assertTrue(circuit.isClosed());

	}
	
	@Test
	public void testMultiBiCircuit3() {
		Circuit<Character> circuit = Circuits.multiBiCircuit('(', ')', '[', ']', '{', '}');

		"(){}".chars().forEach(i->circuit.accept((char) i));
		assertTrue(circuit.isClosed());

	}
	
	@Test
	public void testMultiBiCircuit4() {
		Circuit<Character> circuit = Circuits.multiBiCircuit('(', ')', '[', ']', '{', '}');

		"[]".chars().forEach(i->circuit.accept((char) i));
		assertTrue(circuit.isClosed());

	}
	
	@Test
	public void testMultiBiCircuit5() {
		Circuit<Character> circuit = Circuits.multiBiCircuit('(', ')', '[', ']', '{', '}');

		"([]){}".chars().forEach(i->circuit.accept((char) i));
		assertTrue(circuit.isClosed());

	}
	
	@Test
	public void testMultiBiCircuit6() {
		Circuit<Character> circuit = Circuits.multiBiCircuit('(', ')', '[', ']', '{', '}');

		"([]{})".chars().forEach(i->circuit.accept((char) i));
		assertTrue(circuit.isClosed());

	}
	
	@Test
	public void testMultiBiCircuit7() {
		Circuit<Character> circuit = Circuits.multiBiCircuit('(', ')', '[', ']', '{', '}');

		"({}[])".chars().forEach(i->circuit.accept((char) i));
		assertTrue(circuit.isClosed());

	}
	
	@Test
	public void testMultiBiCircuit8() {
		Circuit<Character> circuit = Circuits.multiBiCircuit('(', ')', '[', ']', '{', '}');

		"({[]})".chars().forEach(i->circuit.accept((char) i));
		assertTrue(circuit.isClosed());

	}


	@Test(expected = ConditionMismatchException.class)
	public void testMultiBiCircuitFail() {
		Circuit<Character> circuit = Circuits.multiBiCircuit('(', ')', '[', ']', '{', '}');
		circuit.accept('s');
		circuit.assertClosed();
		circuit.accept('.');
		circuit.assertClosed();
		circuit.accept('{');// open circuit
		circuit.assertOpen();
		circuit.accept('{');// its not nested we should fail here
	}

	@Test
	public void testNestedMultiBiCircuit() {
		Circuit<Character> circuit = Circuits.multiBiCircuit('(', ')', '[', ']', '{', '}').nested();
		circuit.accept('s');
		circuit.assertClosed();
		circuit.accept('.');
		circuit.assertClosed();
		circuit.accept('(');// open circuit
		circuit.assertOpen();
		circuit.accept('.');// keep open circuit
		circuit.assertOpen();
		circuit.accept('[');// open another
		circuit.assertOpen();
		circuit.accept('.');// keep open
		circuit.assertOpen();
		circuit.accept(']');// close inner
		circuit.assertOpen();
		circuit.accept(')');// close outer
		circuit.assertClosed();
		circuit.accept('a');// should be still closed
		circuit.assertClosed();

	}

	@Test(expected = ConditionMismatchException.class)
	public void testNestedMultiBiCircuitFail() {
		Circuit<Character> circuit = Circuits.multiBiCircuit('(', ')', '[', ']', '{', '}').nested();
		circuit.accept('s');
		circuit.assertClosed();
		circuit.accept('.');
		circuit.assertClosed();
		circuit.accept('{');// open circuit
		circuit.assertOpen();
		circuit.accept('}');// close circuit
		circuit.assertClosed();
		circuit.accept('}');// close circuit

	}
}
