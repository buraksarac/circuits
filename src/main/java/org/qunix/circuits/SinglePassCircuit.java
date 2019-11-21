package org.qunix.circuits;

/**
 *
 * TODO: Comment
 *
 * @author bsarac
 *
 * @param <T> types
 * 2019-11-21 08:58:16 +0100
 */
public class SinglePassCircuit<T> extends CountableCircuit<T> {

	/**
	 * @param circuitState
	 * @param value constructor param
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
