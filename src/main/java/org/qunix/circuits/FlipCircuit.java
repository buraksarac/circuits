package org.qunix.circuits;

public class FlipCircuit<T> extends CountableCircuit<T> {

	@SafeVarargs
	FlipCircuit(boolean circuitState, T... value) {
		super(circuitState, value);
	}

	@Override
	protected boolean testInternal(T t, boolean isValid) {
		if (isValid) {
			this.open = !open;
			this.stateChange = true;
		}
		return true;
	}

}
