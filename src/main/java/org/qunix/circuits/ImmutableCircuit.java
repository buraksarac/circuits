package org.qunix.circuits;

public class ImmutableCircuit<T> extends CircuitCondition<T> {

	@SafeVarargs
	ImmutableCircuit(boolean circuitState, T... value) {
		super(circuitState, value);
	}

	@Override
	protected boolean testInternal(T t, boolean isValid) {
		return isValid ? false : true;
	}

	@Override
	public CircuitCondition<T> open() {
		throw new UnsupportedOperationException();
	}

	@Override
	public CircuitCondition<T> close() {
		throw new UnsupportedOperationException();
	}
}