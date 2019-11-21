package org.qunix.circuits;

public class FlowingCircuit<T> extends CountableCircuit<T> {

	@SafeVarargs
	FlowingCircuit(boolean circuitState, T... value) {
		super(circuitState, value);
	}

	@Override
	protected boolean testInternal(T t, boolean isValid) {
		if ((isValid && !this.open) || (!isValid && this.open)) {
			this.open = !open;
			this.stateChange = true;
		}
		return true;
	}

}