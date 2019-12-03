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
 * {@link MultiBiCircuit#nested}
 *
 * @author bsarac
 *
 * @param <T> types 2019-11-21 08:54:57 +0100
 */
public class MultiBiCircuit<T> extends CountableCircuit<T> {

	private boolean nested;
	private long[] stackSizes;
	private int lastOpened = -1;

	/**
	 * @param circuitState
	 * @param value        constructor param
	 */
	@SafeVarargs
	MultiBiCircuit(boolean circuitState, T... value) {
		super(circuitState, value);
		if (isNull || (value.length & 1) == 1) {
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

		// given param is valid
		if (isValid) {

			// get indexes so we can look its pair or related stack size
			int index = this.values.indexOf(t);
			int stackIndex = index >>> 1;
			if (this.stackSizes[stackIndex] > 0l) { // is open
				if ((index & 1) == 1) { // isOdd so its a close operation
					this.stateChange = true;
					if (--this.stackSizes[stackIndex] < 0l) { // reduce stack
						return false;
					}
					// check if last opened exist and it closes this one
					if (nested && lastOpened != -1 && lastOpened + 1 != index) {
						return false;
					}
					this.lastOpened = -1; // reset

				} else {
					//if not nested and already open there wouldnt be another open
					if (!nested && this.open) {
						return false;
					}
					this.stackSizes[stackIndex]++;
					this.lastOpened = index;
					this.stateChange = true;
				}

			} else {
				if ((index & 1) == 0) { // isEven, open operation
					if (++this.stackSizes[stackIndex] != 1l) { // increase stack
						return false;
					}
					this.lastOpened = index;
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
