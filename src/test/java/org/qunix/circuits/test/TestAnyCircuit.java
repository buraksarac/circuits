/**
 * 
 */
package org.qunix.circuits.test;

import org.junit.Test;
import org.qunix.circuits.AnyCircuit;
import org.qunix.circuits.Circuit;
import org.qunix.circuits.Circuits;
import org.qunix.circuits.CountableCircuit.FailBehaviour;

/**
 *
 * tests {@link AnyCircuit}
 *
 * @author bsarac types 2019-11-21 15:08:32 +0100
 */
public class TestAnyCircuit {

	@Test
	public void test() {
		Circuit<Character> circuit = Circuits.<Character>any().max(5, FailBehaviour.CLOSE);
		circuit.assertClosed();
		circuit.accept('a'); // 1
		circuit.assertOpen();
		circuit.accept('1'); // 2
		circuit.assertOpen();
		circuit.accept('/'); // 3
		circuit.assertOpen();
		circuit.accept(':'); // 4
		circuit.assertOpen();
		circuit.accept('}'); // 5
		circuit.assertClosed();
		circuit.accept('|');
		circuit.assertOpen();

	}
}
