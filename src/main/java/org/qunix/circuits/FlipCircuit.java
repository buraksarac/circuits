package org.qunix.circuits;

/**
 *
 * TODO: Comment
 *
 * @author bsarac
 *
 * @param <T> types
 * 2019-11-21 08:57:41 +0100
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
