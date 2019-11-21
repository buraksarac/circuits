package org.qunix.circuits;

/**
 *
 * Circuit condition that its expected that its status never be changed, which
 * means given param(s) will be never received, otherwise it will fail <br/>
 * <br/>
 *
 * @author bsarac
 *
 * @param <T> types 2019-11-21 08:54:57 +0100
 */
public class ImmutableCircuit<T> extends Circuit<T> {

	/**
	 * @param circuitState
	 * @param value        constructor param
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