package org.qunix.circuits;

public class BiCircuit<T> extends CountableCircuit<T> {

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
	protected boolean testInternal(T t, boolean isValid) {

		if (isValid) {
			if (this.open) {
				if (closeValue.equals(t)) {
					this.stateChange = true;
					if (!nested || this.stackSize == 0l) {
						this.open = false;
					} else {
						if (--this.stackSize < 0) {
							return false;
						}
					}
				} else if (nested) {
					this.stackSize++;
					this.stateChange = true;
				} else {
					return false;
				}

			} else {
				if (openValue.equals(t)) {
					this.open = true;
					this.stateChange = true;
				} else {
					return false;
				}
			}
		}

		return true;

	}
}
