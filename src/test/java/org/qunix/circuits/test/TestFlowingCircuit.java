/**
 * 
 */
package org.qunix.circuits.test;

import org.junit.Test;
import org.qunix.circuits.Circuit;
import org.qunix.circuits.Circuits;
import org.qunix.circuits.FlowingCircuit;
import org.qunix.circuits.CountableCircuit.FailBehaviour;

/**
 *
 * tests for {@link FlowingCircuit}
 *
 * @author bsarac types 2019-11-21 13:51:10 +0100
 */
public class TestFlowingCircuit {

	@Test
	public void test() {

		Circuit<Character> circuit = Circuits.flowing('a').max(2, FailBehaviour.CLOSE);
		circuit.accept('b');
		circuit.assertClosed();
		circuit.accept('a');
		circuit.assertOpen();
		circuit.accept('a');
		circuit.assertClosed();
		circuit.accept('a');
		circuit.assertOpen();
		circuit.accept('b');
		circuit.assertClosed();

	}

}
