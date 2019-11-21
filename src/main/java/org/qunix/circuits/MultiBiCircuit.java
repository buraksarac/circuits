package org.qunix.circuits;

public class MultiBiCircuit<T> extends CountableCircuit<T> {

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
	protected boolean testInternal(T t, boolean isValid) {

		if (isValid) {

			int index = this.values.indexOf(t);
			int stackIndex = index >>> 1;
			if (this.stackSizes[stackIndex] > 0l) {
				if ((index & 1) == 1) { // isOdd
					this.stateChange = true;
					if (--this.stackSizes[stackIndex] < 0l) {
						return false;
					}
					if (this.lastOpened != null && nested && this.values.indexOf(lastOpened) + 1 != index) {
						return false;
					}
					this.lastOpened = null;

				} else {
					this.stackSizes[stackIndex]++;
					this.lastOpened = t;
					this.stateChange = true;
				}

			} else {
				if ((index & 1) == 0) {
					this.stackSizes[stackIndex]++;
					this.lastOpened = t;
					this.stateChange = true;
				} else {
					return false;
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

		return true;
	}
}