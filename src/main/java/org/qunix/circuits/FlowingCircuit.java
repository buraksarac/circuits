package org.qunix.circuits;

/**
*
* Circuit condition that opens on parameter match but closes only if there is mismatch  <br/>
* <b>usage:</b><br/>
* <code>
* Circuit< Character> circuit = Circuits.flowing('.');
* <br/>
* circuit.accept('a'); //still closed
* <br/>
* circuit.accept('.'); //opened
* <br/>
* circuit.accept('.'); //still open
* * <br/>
* circuit.accept(';'); //closed
* 
* </code> <br/>
* <br/>
*
* @author bsarac
*
* @param <T> types 2019-11-21 08:54:57 +0100
*/
public class FlowingCircuit<T> extends CountableCircuit<T> {

	/**
	 * @param circuitState
	 * @param value constructor param
	 */
	@SafeVarargs
	FlowingCircuit(boolean circuitState, T... value) {
		super(circuitState, value);
	}

	/**
	 *
	 */
	@Override
	protected boolean testInternal(T t, boolean isValid) {
		if ((isValid && !this.open) || (!isValid && this.open)) {
			this.open = !open;
			this.stateChange = true;
		}
		return true;
	}

}