package org.qunix.circuits;

/**
 *
 * Circuit condition that uses a pair to manage circuits. first one for opening
 * and the second one for closing circuit <br/>
 * <b>usage:</b><br/>
 * <code>
 * // '{' opens and '}' closes<br/>
 * Circuit< Character> circuit = Circuits.biCircuit('{','}'); 
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
 * If you have a nested data structure i.e. json mark as nested using
 * {@link BiCircuit#nested}
 *
 * @author bsarac
 *
 * @param <T> types 2019-11-21 08:54:57 +0100
 */
public class BiCircuit<T> extends CountableCircuit<T> {

	private boolean nested;
	private long stackSize = 0;
	private T openValue;
	private T closeValue;

	/**
	 * @param circuitState circuitState
	 * @param value        constructor param
	 */
	@SafeVarargs
	BiCircuit(boolean circuitState, T... value) {
		super(circuitState, value);
		this.openValue = this.values.getFirst();
		this.closeValue = this.values.getLast();
	}

	/**
	 *
	 * nested method: marks this circuit as nested, its means i.e. for char stream
	 * of "{a{b{}}" will still keep circuit open and there will be no failure
	 *
	 * If you have a nested data structure i.e. json use this method
	 *
	 *
	 * @return BiCircuit<T>
	 */
	public BiCircuit<T> nested() {
		this.nested = true;
		return this;
	}

	/**
	 * abstract implementation
	 */
	@Override
	protected boolean testInternal(T t, boolean isValid) {

		// check received param opens or closes circuit
		if (isValid) {
			// check if circuit is open
			if (this.open) {
				// it is a close operation
				if (closeValue.equals(t)) {
					this.stateChange = true;
					// if its nested and there is open child reduce stack otherwise close it
					if (!nested || this.stackSize == 0l) {
						this.open = false;
					} else {
						if (--this.stackSize < 0) {
							return false;
						}
					}
				} else if (nested) {
					// its open param, circuit already opened here, just add increase stack
					this.stackSize++;
					this.stateChange = true;
				} else { // circuit is open and we received another open but its not nested so fail
					return false;
				}

			} else {
				// circuit closed and stack empty here check if open param otherwise fail
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
