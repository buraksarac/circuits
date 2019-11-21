package org.qunix.circuits;

/**
*
* Circuit condition that flips its status when its receives one of the given parameter <br/>
* <b>usage:</b><br/>
* <code>
* Circuit< Character> circuit = Circuits.flipping('.');
* <br/>
* circuit.accept('a'); //still closed
* <br/>
* circuit.accept('.'); //opened
* <br/>
* circuit.accept(';'); //still open
* <br/>
* circuit.accept('.'); //closed
* 
* </code> <br/>
* <br/>
*
* @author bsarac
*
* @param <T> types 2019-11-21 08:54:57 +0100
*/
public class FlipCircuit<T> extends CountableCircuit<T> {

	/**
	 * @param circuitState
	 * @param value constructor param
	 */
	@SafeVarargs
	FlipCircuit(boolean circuitState, T... value) {
		super(circuitState, value);
	}

	/**
	 *
	 */
	@Override
	protected boolean testInternal(T t, boolean isValid) {
		if (isValid) {
			this.open = !open;
			this.stateChange = true;
		}
		return true;
	}

}
