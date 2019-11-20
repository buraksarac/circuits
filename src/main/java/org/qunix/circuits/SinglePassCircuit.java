package org.qunix.circuits;

import java.util.List;
import java.util.function.Consumer;

public class SinglePassCircuit<T> extends CircuitCondition<T> {

	@SafeVarargs
	SinglePassCircuit(boolean circuitState, T... value) {
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
				this.openConsumers.forEach(c -> c.accept(t));
				return true;
			}
		}

		List<Consumer<T>> consumers = open ? whileOpenConsumers : whileCloseConsumers;
		consumers.forEach(c -> c.accept(t));

		return this.predicate.test(t);
	}

}
