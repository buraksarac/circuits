package com.netflix.conditionals;

public class BiCircuitCondition<T> extends CircuitCondition<T> {

	private boolean nested;
	private long stackSize = 0;

	@SafeVarargs
	BiCircuitCondition(boolean circuitState, T... value) {
		super(circuitState, value);
	}

	public BiCircuitCondition<T> nested() {
		this.nested = true;
		return this;
	}

	@Override
	protected boolean test(T t) {
		if (!ignores.contains(t)) {
			boolean stateChange = false;
			if (this.values.contains(t) || (isNull && t == null)) {
				if (this.max > -1 && ++this.currentOccurence > this.max) {
					return false;
				}
				if (this.open) {
					if (this.values.getLast().equals(t)) {
						if (!nested || this.stackSize == 0l) {
							this.open = !open;
							stateChange = true;
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
					if (this.values.getFirst().equals(t)) {
						this.open = !open;
						stateChange = true;
					} else {
						return false;
					}
				}

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
