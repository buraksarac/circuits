package org.qunix.circuits;

/**
 *
 * Circuit condition that opens gate only once and fails after if given param(s)
 * re-occured <br/>
 * <b>usage:</b><br/>
 * <code>
* Circuit< Character> circuit = Circuits.singlePass('.');
* <br/>
* circuit.accept('a'); //still closed
* <br/>
* circuit.accept('.'); //opened
* <br/>
* circuit.accept(';'); //still open
* <br/>
* circuit.accept('.'); //failure
* 
* </code> <br/>
 * <br/>
 *
 * @author bsarac
 *
 * @param <T> types 2019-11-21 08:54:57 +0100
 */
public class SinglePassCircuit<T> extends CountableCircuit<T> {

	/**
	 * @param circuitState
	 * @param value        constructor param
	 */
	@SafeVarargs
	SinglePassCircuit(boolean circuitState, T... value) {
		super(circuitState, value);
	}

	/**
	 *
	 */
	@Override
	protected boolean testInternal(T t, boolean isValid) {

		if (isValid) {
			if (this.open) {
				return false;
			}
			this.open = !open;
			this.stateChange = true;
		}

		return true;
	}

}
