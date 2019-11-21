/**
 * 
 */
package org.qunix.circuits.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.qunix.circuits.Circuit;
import org.qunix.circuits.Circuit.ConditionMismatchException;
import org.qunix.circuits.Circuits;
import org.qunix.circuits.ImmutableCircuit;

/**
 *
 * tests for {@link ImmutableCircuit}
 *
 * @author bsarac types 2019-11-21 12:01:45 +0100
 */
public class TestImmutableCircuit {

	@Test
	public void testValid() {
		Circuit<Character> circuit = Circuits.notNull();

		"0123456789".chars().forEach(i -> {
			circuit.accept((char) i);
			assertTrue(circuit.isClosed());
		});
	}

	@Test(expected = ConditionMismatchException.class)
	public void testInValid() {
		Circuit<Character> circuit = Circuits.notNull();

		"0123456789".chars().forEach(i -> {
			circuit.accept((char) i);
			assertTrue(circuit.isClosed());
		});

		circuit.accept(null);
	}

	@Test
	public void testValid2() {
		Circuit<String> circuit = Circuits.immutable("null");

		circuit.accept(null);
	}

	@Test
	public void testValid3() {
		Circuit<Integer> circuit = Circuits.immutable(-1);

		circuit.accept(-2);
	}

	@Test(expected = ConditionMismatchException.class)
	public void testInValid3() {
		Circuit<Integer> circuit = Circuits.immutable(-1);

		circuit.accept(-1);
	}

	@Test(expected = ConditionMismatchException.class)
	public void testInValid2() {
		Circuit<String> circuit = Circuits.immutable("null");

		circuit.accept("null");
	}
}
