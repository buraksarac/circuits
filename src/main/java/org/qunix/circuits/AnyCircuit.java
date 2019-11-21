/**
 * 
 */
package org.qunix.circuits;

/**
 *
 * Circuit accepts any type
 *
 * @author bsarac types 2019-11-21 15:00:58 +0100
 */
public class AnyCircuit<T> extends FlowingCircuit<T> {

	/**
	 * @param circuitState
	 * @param value        constructor param
	 */
	@SafeVarargs
	AnyCircuit(T... vals) {
		super(false, vals);
		this.preValidation = true;
	}

}
