package org.qunix.circuits;

import java.util.concurrent.atomic.AtomicLong;

abstract class CountableCircuit<T> extends CircuitCondition<T> {

	long maxOccurence = -1;
	long maxOpen = -1;
	long maxClose = -1;
	long currentOccurence = 0l;
	AtomicLong closes = new AtomicLong(0l);
	AtomicLong opens = new AtomicLong(0l);
	FailBehaviour occurenceFailBehaviour = FailBehaviour.FAIL;

	public enum FailBehaviour {
		FAIL, CLOSE;
	}

	CountableCircuit(boolean circuitState, T[] value) {
		super(circuitState, value);
		this.openConsumers.add(this::testOpen);
		this.closeConsumers.add(this::testClose);
		this.preConditions = preConditions.and(this::testOccurence);
	}

	public CircuitCondition<T> maxOccurence(long max, FailBehaviour behaviour) {
		this.maxOccurence = max;
		this.occurenceFailBehaviour = behaviour;
		return this;
	}

	public CircuitCondition<T> maxOpen(long max) {
		this.maxOpen = max;
		return this;
	}

	public CircuitCondition<T> maxClose(long max) {
		this.maxClose = max;
		return this;
	}

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

	private boolean testOpen(T t) {
		return !(this.maxOpen > -1 && this.opens.incrementAndGet() > this.maxOpen);
	}

	private boolean testClose(T t) {
		return !(this.maxClose > -1 && this.closes.incrementAndGet() > this.maxClose);
	}

}
