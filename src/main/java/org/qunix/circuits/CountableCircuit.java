package org.qunix.circuits;

import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * abstract circuit that additionally adds support for counting opens/closes and
 * occurrences of parameters
 *
 * @author bsarac
 *
 * @param <T> types 2019-11-21 08:57:26 +0100
 */
public abstract class CountableCircuit<T> extends Circuit<T> {

	long maxOccurence = -1;
	long maxOpen = -1;
	long maxClose = -1;
	long currentOccurence = 0l;
	long failureCount = 0l;
	AtomicLong closes = new AtomicLong(0l);
	AtomicLong opens = new AtomicLong(0l);
	FailBehaviour occurenceFailBehaviour = FailBehaviour.FAIL;

	/**
	 *
	 * When max occurence barrier reached fail behaviour will be selected through
	 * this marker
	 *
	 * @author bsarac types 2019-11-21 08:57:26 +0100
	 */
	public enum FailBehaviour {

		/**
		 * Terminates stream
		 */
		FAIL,
		/**
		 * Simply closes circuit and let the stream continue, failure count can be
		 * accessed later
		 */
		CLOSE;
	}

	/**
	 * @param circuitState
	 * @param value        constructor param
	 */
	CountableCircuit(boolean circuitState, T[] value) {
		super(circuitState, value);
		this.openConsumers.add(this::testOpen);
		this.closeConsumers.add(this::testClose);
		this.preConditions = preConditions.and(this::testOccurence);
	}

	/**
	 *
	 * maxOccurence method: During the stream any valid parameter increases this
	 * value
	 *
	 * 
	 *
	 *
	 * @param max       -1 is inf
	 * @param behaviour behaviour
	 * @return CircuitCondition<T>
	 */
	public Circuit<T> max(long max, FailBehaviour behaviour) {
		this.maxOccurence = max;
		this.occurenceFailBehaviour = behaviour;
		return this;
	}
	
	/**
	 *
	 * maxOccurence method: During the stream any valid parameter increases this
	 * value, defailt fail behaviour is closing circuit, otherwise use overloaded method
	 *
	 * 
	 *
	 *
	 * @param max       -1 is inf
	 * @param behaviour behaviour
	 * @return CircuitCondition<T>
	 */
	public Circuit<T> max(long max) {
		this.maxOccurence = max;
		this.occurenceFailBehaviour = FailBehaviour.CLOSE;
		return this;
	}

	/**
	 *
	 * getFailureCount method: If FailBehaviour is {@link FailBehaviour#CLOSE} this
	 * value incremented by each fail
	 *
	 * 
	 *
	 *
	 * @return long 0 is no failure or {@link FailBehaviour#FAIL}
	 */
	public long getFailureCount() {
		return failureCount;
	}

	/**
	 *
	 * maxOpen method: Limit how many times this circuit can be opened
	 *
	 * 
	 *
	 *
	 * @param max -1 is inf
	 * @return CircuitCondition<T>
	 */
	public Circuit<T> maxOpen(long max) {
		this.maxOpen = max;
		return this;
	}

	/**
	 *
	 * maxClose method: Limit how many times this circuit can be closed
	 *
	 * 
	 *
	 *
	 * @param max -1 is inf
	 * @return CircuitCondition<T>
	 */
	public Circuit<T> maxClose(long max) {
		this.maxClose = max;
		return this;
	}

	/**
	 *
	 * testOccurence method: internal method to validate occurence count
	 *
	 * 
	 *
	 *
	 * @param t
	 * @param isValid
	 * @return boolean
	 */
	private boolean testOccurence(T t, boolean isValid) {
		if (isValid && this.maxOccurence > -1 && ++this.currentOccurence >= this.maxOccurence) {
			if (this.occurenceFailBehaviour.equals(FailBehaviour.FAIL)) {
				return false;
			} else {
				this.open = false;
				this.stateChange = true;
				failureCount++;
				this.currentOccurence = 0;
			}
		}
		return true;
	}

	/**
	 *
	 * testOpen method: internal method to validate open count
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
	 * testClose method: internal method to validate close count
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
