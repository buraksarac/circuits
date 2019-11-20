package com.netflix.conditionals;

public class ImmutableCircuitCondition<T> extends CircuitCondition<T> {

	@SafeVarargs
	ImmutableCircuitCondition(boolean circuitState, T... value) {
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