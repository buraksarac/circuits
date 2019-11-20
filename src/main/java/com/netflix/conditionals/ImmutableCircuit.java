package com.netflix.conditionals;

public class ImmutableCircuit<T> extends CircuitCondition<T> {

	@SafeVarargs
	ImmutableCircuit(boolean circuitState, T... value) {
		super(circuitState, value);
	}

	@Override
	protected boolean test(T t) {
		if (!ignores.contains(t)) {
			if (this.values.contains(t) || (isNull && t == null)) {
				return false;
			}
		}

		return this.predicate.test(t);
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