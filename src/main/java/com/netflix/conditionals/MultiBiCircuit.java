package com.netflix.conditionals;

import java.util.List;
import java.util.function.Consumer;

public class MultiBiCircuit<T> extends CircuitCondition<T> {

	private boolean nested;
	private long[] stackSizes;
	private T lastOpened;

	@SafeVarargs
	MultiBiCircuit(boolean circuitState, T... value) {
		super(circuitState, value);
		if ((value.length & 1) == 1) {
			throw new IllegalArgumentException("MultiBiCircuit only accepts even number of args");
		}
		this.stackSizes = new long[value.length >>> 1];

	}

	public MultiBiCircuit<T> nested() {
		this.nested = true;
		return this;
	}

	@Override
	protected boolean test(T t) {
		List<Consumer<T>> consumers = open ? whileOpenConsumers : whileCloseConsumers;
		boolean stateChange = false;
		if (!ignores.contains(t)) {

			if (this.values.contains(t) || (isNull && t == null)) {
				if (this.max > -1 && ++this.currentOccurence > this.max) {
					return false;
				}
				int index = this.values.indexOf(t);
				int stackIndex = index >>> 1;
				if (this.stackSizes[stackIndex] > 0l) {
					if ((index & 1) == 1) { // isOdd
						stateChange = true;
						if (--this.stackSizes[stackIndex] < 0l) {
							return false;
						}
						if (lastOpened != null && nested && this.values.indexOf(lastOpened) + 1 != index) {
							return false;
						}
						lastOpened = null;
						
					} else {
						this.stackSizes[stackIndex]++;
						lastOpened = t;
						stateChange = true;
					}

				} else {
					if ((index & 1) == 0) {
						this.stackSizes[stackIndex]++;
						lastOpened = t;
						stateChange = true;
					} else {
						return false;
					}
				}

			}

		}
		open = false;
		for (long l : this.stackSizes) {
			if (l > 0l) {
				this.open = true;
				break;
			}
		}
		consumers = stateChange ? open ? openConsumers : closeConsumers : consumers;

		consumers.forEach(c -> c.accept(t));

		return stateChange ? stateChange : this.predicate.test(t);
	}
}