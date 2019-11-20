package com.netflix.conditionals;

import java.util.List;
import java.util.function.Consumer;

public class BiCircuit<T> extends CircuitCondition<T> {

	private boolean nested;
	private long stackSize = 0;
	private T openValue;
	private T closeValue;

	@SafeVarargs
	BiCircuit(boolean circuitState, T... value) {
		super(circuitState, value);
		this.openValue = this.values.getFirst();
		this.closeValue = this.values.getLast();
	}

	public BiCircuit<T> nested() {
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
				if (this.open) {
					if (closeValue.equals(t)) {
						stateChange = true;
						if (!nested || this.stackSize == 0l) {
							this.open = !open;
						} else {
							if (--this.stackSize < 0) {
								return false;
							}
						}
					} else if (nested) {
						this.stackSize++;
						stateChange = true;
					} else {
						return false;
					}

				} else {
					if (openValue.equals(t)) {
						this.open = !open;
						stateChange = true;
					} else {
						return false;
					}
				}

			}

		}
		consumers = stateChange ? open ? openConsumers : closeConsumers : consumers;

		consumers.forEach(c -> c.accept(t));

		return stateChange ? stateChange : this.predicate.test(t);
	}
}
