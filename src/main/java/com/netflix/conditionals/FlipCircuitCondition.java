package com.netflix.conditionals;

public class FlipCircuitCondition<T> extends CircuitCondition<T> {

	@SafeVarargs
	FlipCircuitCondition(boolean circuitState, T... value) {
		super(circuitState, value);
	}

	@Override
	protected boolean test(T t) {
		if (!ignores.contains(t)) {
			if (this.values.contains(t) || (isNull && t == null)) {
				if (this.max > -1 && ++this.currentOccurence > this.max) {
					return false;
				}
				this.open = !open;
				if (this.open && this.openConsumer != null) {
					this.openConsumer.accept(t);
				} else if (this.closeConsumer != null) {
					this.closeConsumer.accept(t);
				}
			}
		}

		return this.predicate.test(t);
	}

}
