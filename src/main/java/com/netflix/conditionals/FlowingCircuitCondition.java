package com.netflix.conditionals;

import java.util.List;
import java.util.function.Consumer;

public class FlowingCircuitCondition<T> extends CircuitCondition<T> {

	@SafeVarargs
	FlowingCircuitCondition(boolean circuitState, T... value) {
		super(circuitState, value);
	}

	@Override
	protected boolean test(T t) {
		List<Consumer<T>> consumers =  open ? whileOpenConsumers : whileCloseConsumers;
		boolean stateChange = false;
		if (!ignores.contains(t)) {
			
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

		}
		consumers = stateChange ? open ? openConsumers : closeConsumers : consumers;
		consumers.forEach(c -> c.accept(t));

		return stateChange ? stateChange : this.predicate.test(t);
	}

}