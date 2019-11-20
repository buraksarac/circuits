package com.netflix.conditionals;

public class FlowingCircuitCondition<T> extends CircuitCondition<T> {

	@SafeVarargs
	FlowingCircuitCondition(boolean circuitState, T... value) {
		super(circuitState, value);
	}

	@Override
	protected boolean test(T t) {
		if (!ignores.contains(t)) {
			boolean stateChange = false;
			if (this.values.contains(t) || (isNull && t == null)) {
				if (this.max > -1 && ++this.currentOccurence > this.max) {
					return false;
				}
				if (!this.open) {
					this.open = !open;
					stateChange = true;
				}
			} else if (this.open) {
				this.open = !open;
				stateChange = true;
			}
			if (stateChange) {
				if (this.open) {
					this.openConsumers.forEach(c -> c.accept(t));
				} else {
					this.closeConsumers.forEach(c -> c.accept(t));
				}
			}

		}

		return this.predicate.test(t);
	}

}