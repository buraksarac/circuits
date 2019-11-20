package com.netflix.conditionals;

public class SinglePassCircuitCondition<T> extends CircuitCondition<T> {

	@SafeVarargs
	SinglePassCircuitCondition(boolean circuitState, T... value) {
		super(circuitState, value);
	}

	@Override
	protected boolean test(T t) {
		if (!ignores.contains(t)) {
			if (this.values.contains(t) || (isNull && t == null)) {
				if (this.open) {
					return false;
				}
				this.open = !open;
				if (this.openConsumer != null) {
					this.openConsumer.accept(t);
				}
			}
		}

		return this.predicate.test(t);
	}

}
