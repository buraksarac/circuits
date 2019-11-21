package org.qunix.circuits;

/**
 *
 * TODO: Comment
 *
 * @author bsarac
 *
 * @param <T> types
 * 2019-11-21 08:57:49 +0100
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