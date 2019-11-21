package org.qunix.circuits;

public class SinglePassCircuit<T> extends CountableCircuit<T> {

	@SafeVarargs
	SinglePassCircuit(boolean circuitState, T... value) {
		super(circuitState, value);
	}

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
