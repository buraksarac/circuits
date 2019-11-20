package org.qunix.circuits;

import java.util.List;
import java.util.function.Consumer;

public class FlipCircuit<T> extends CircuitCondition<T> {

	@SafeVarargs
	FlipCircuit(boolean circuitState, T... value) {
		super(circuitState, value);
	}

	@Override
	protected boolean test(T t) {
		
		if (!ignores.contains(t)) {
			if (this.values.contains(t) || (isNull && t == null)) {
				if (this.max > -1 && ++this.currentOccurence > this.max) {
					if(this.behaviour.equals(FailBehaviour.FAIL)){
						return false;
					}else {
						this.open = false;
					}
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
