package org.qunix.circuits;

import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * TODO: Comment
 *
 * @author bsarac
 *
 * @param <T> types
 * 2019-11-21 08:57:26 +0100
 */
abstract class CountableCircuit<T> extends Circuit<T> {

	long maxOccurence = -1;
	long maxOpen = -1;
	long maxClose = -1;
	long currentOccurence = 0l;
	AtomicLong closes = new AtomicLong(0l);
	AtomicLong opens = new AtomicLong(0l);
	FailBehaviour occurenceFailBehaviour = FailBehaviour.FAIL;

	/**
	 *
	 * TODO: Comment
	 *
	 * @author bsarac
	 * types
	 * 2019-11-21 08:57:26 +0100
	 */
	public enum FailBehaviour {
		
		/**
		 * 
		 */
		FAIL, 
		/**
		 * 
		 */
		CLOSE;
	}

	/**
	 * @param circuitState
	 * @param value constructor param
	 */
	CountableCircuit(boolean circuitState, T[] value) {
		super(circuitState, value);
		this.openConsumers.add(this::testOpen);
		this.closeConsumers.add(this::testClose);
		this.preConditions = preConditions.and(this::testOccurence);
	}

	/**
	 *
	 * maxOccurence method: TODO
	 *
	 * 
	 *
	 *
	 * @param max
	 * @param behaviour
	 * @return CircuitCondition<T>
	 */
	public Circuit<T> maxOccurence(long max, FailBehaviour behaviour) {
		this.maxOccurence = max;
		this.occurenceFailBehaviour = behaviour;
		return this;
	}

	/**
	 *
	 * maxOpen method: TODO
	 *
	 * 
	 *
	 *
	 * @param max
	 * @return CircuitCondition<T>
	 */
	public Circuit<T> maxOpen(long max) {
		this.maxOpen = max;
		return this;
	}

	/**
	 *
	 * maxClose method: TODO
	 *
	 * 
	 *
	 *
	 * @param max
	 * @return CircuitCondition<T>
	 */
	public Circuit<T> maxClose(long max) {
		this.maxClose = max;
		return this;
	}

	/**
	 *
	 * testOccurence method: TODO
	 *
	 * 
	 *
	 *
	 * @param t
	 * @param isValid
	 * @return boolean
	 */
	private boolean testOccurence(T t, boolean isValid) {
		if (isValid && this.maxOccurence > -1 && ++this.currentOccurence > this.maxOccurence) {
			if (this.occurenceFailBehaviour.equals(FailBehaviour.FAIL)) {
				return false;
			} else {
				this.open = false;
			}
		}
		return true;
	}

	/**
	 *
	 * testOpen method: TODO
	 *
	 * 
	 *
	 *
	 * @param t
	 * @return boolean
	 */
	private boolean testOpen(T t) {
		return !(this.maxOpen > -1 && this.opens.incrementAndGet() > this.maxOpen);
	}

	/**
	 *
	 * testClose method: TODO
	 *
	 * 
	 *
	 *
	 * @param t
	 * @return boolean
	 */
	private boolean testClose(T t) {
		return !(this.maxClose > -1 && this.closes.incrementAndGet() > this.maxClose);
	}

}
