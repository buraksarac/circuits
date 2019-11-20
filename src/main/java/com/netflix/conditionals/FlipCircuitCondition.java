package com.netflix.conditionals;

import java.util.List;
import java.util.function.Consumer;

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
				List<Consumer<T>> consumers = open ? openConsumers : closeConsumers;
				consumers.forEach(c -> c.accept(t));
				return true;

			}
		}
		List<Consumer<T>> consumers = open ? whileOpenConsumers : whileCloseConsumers;
		consumers.forEach(c -> c.accept(t));

		return this.predicate.test(t);
	}

}
