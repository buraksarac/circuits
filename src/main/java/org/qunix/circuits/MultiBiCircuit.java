package org.qunix.circuits;

/**
 *
 * Circuit condition that uses multiple pairs to manage its state. For each pair
 * first one for opening and the second one for closing circuit <br/>
 * <b>usage:</b><br/>
 * <code>
* // '{' opens and '}' closes<br/>
* Circuit< Character> circuit = Circuits.multiBiCircuit('(', ')','[', ']','{', '}') 
* <br/>
* circuit.accept('a'); //still closed
* <br/>
* circuit.accept('{'); //opened
* <br/>
* circuit.accept(';'); //still open
* <br/>
* circuit.accept('{'); //closed
* 
* </code> <br/>
 * <br/>
 * If you have a nested data structure like json mark as nested using
 * {@link BiCircuit#nested}
 *
 * @author bsarac
 *
 * @param <T> types 2019-11-21 08:54:57 +0100
 */
public class MultiBiCircuit<T> extends CountableCircuit<T> {

	private boolean nested;
	private long[] stackSizes;
	private T lastOpened;

	/**
	 * @param circuitState
	 * @param value        constructor param
	 */
	@SafeVarargs
	MultiBiCircuit(boolean circuitState, T... value) {
		super(circuitState, value);
		if ((value.length & 1) == 1) {
			throw new IllegalArgumentException("MultiBiCircuit only accepts even number of args");
		}
		this.stackSizes = new long[value.length >>> 1];

	}

	/**
	 *
	 * nested method: marks this circuit as nested, its means i.e. for char stream
	 * of "{a{b{}}" will still keep circuit open and there will be no failure
	 *
	 * If you have a nested data structure i.e. json use this method
	 *
	 * 
	 *
	 *
	 * @return MultiBiCircuit<T>
	 */
	public MultiBiCircuit<T> nested() {
		this.nested = true;
		return this;
	}

	/**
	 *
	 */
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