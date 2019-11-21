package org.qunix.circuits;

/**
 *
 * TODO: Comment
 *
 * @author bsarac
 *
 * @param <T> types
 * 2019-11-21 08:57:57 +0100
 */
public class ImmutableCircuit<T> extends Circuit<T> {

	/**
	 * @param circuitState
	 * @param value constructor param
	 */
	@SafeVarargs
	ImmutableCircuit(boolean circuitState, T... value) {
		super(circuitState, value);
	}

	/**
	 *
	 */
	@Override
	protected boolean testInternal(T t, boolean isValid) {
		return isValid ? false : true;
	}

	/**
	 *
	 */
	@Override
	public Circuit<T> open() {
		throw new UnsupportedOperationException();
	}

	/**
	 *
	 */
	@Override
	public Circuit<T> close() {
		throw new UnsupportedOperationException();
	}
}