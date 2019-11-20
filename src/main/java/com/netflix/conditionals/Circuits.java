package com.netflix.conditionals;

import java.util.function.Consumer;

public class Circuits<T> implements Consumer<T> {

	private CircuitCondition<T>[] conditions;

	@SafeVarargs
	private Circuits(CircuitCondition<T>... conditions) {
		this.conditions = conditions;
	}

	@SafeVarargs
	public static final <E> Circuits<E> of(CircuitCondition<E>... conditions) {
		CircuitCondition.checkEmpty(conditions, "No condition provided!");
		return new Circuits<>(conditions);
	}

	public void accept(T value) {
		for (CircuitCondition<T> condition : conditions) {
			condition.accept(value);
		}
	}
	
	public void ifAccept(T value, Consumer<T> consumer) {
		for (CircuitCondition<T> condition : conditions) {
			if(condition.test(value)) {
				consumer.accept(value);
			}
		}
	}

	public boolean isClosed() {
		for (CircuitCondition<T> condition : this.conditions) {
			if (condition.isOpen()) {
				return false;
			}
		}

		return true;
	}

	public boolean isOpen() {
		for (CircuitCondition<T> condition : this.conditions) {
			if (condition.isClosed()) {
				return false;
			}
		}

		return true;
	}

	public void assertClosed() throws IllegalStateException {
		for (CircuitCondition<T> condition : this.conditions) {
			if (condition.isOpen()) {
				throw new IllegalStateException("Circuit: " + condition.valueStr.toString() + " still open!");
			}
		}
	}

	public void assertOpen() throws IllegalStateException {
		for (CircuitCondition<T> condition : this.conditions) {
			if (condition.isClosed()) {
				throw new IllegalStateException("Circuit: " + condition.valueStr.toString() + " still closed!");
			}
		}
	}

	@SafeVarargs
	public static <A> FlowingCircuit<A> of(A... value) {
		return CircuitCondition.of(value);
	}

	@SafeVarargs
	public static <A> FlowingCircuit<A> flowing(A... value) {
		return CircuitCondition.flowing(value);
	}

	@SafeVarargs
	public static <A> FlipCircuit<A> flipping(A... value) {
		return CircuitCondition.flipping(value);
	}

	@SafeVarargs
	public static <A> ImmutableCircuit<A> immutable(boolean state, A... value) {
		return CircuitCondition.immutable(state, value);
	}

	@SafeVarargs
	public static <A> SinglePassCircuit<A> singlePass(A... value) {
		return CircuitCondition.singlePass(value);
	}

	public static <A> BiCircuit<A> biCircuit(A openValue, A closeValue) {
		return CircuitCondition.biCircuit(openValue, closeValue);
	}

	@SafeVarargs
	public static <A> MultiBiCircuit<A> multiBiCircuit(A... value) {
		return CircuitCondition.multiBiCircuit(value);
	}

	public static FlowingCircuit<Integer> between(int startInclusive, int endInclusive) {
		return CircuitCondition.between(startInclusive, endInclusive);
	}

	public static FlowingCircuit<Character> between(char startInclusive, char endInclusive) {
		return CircuitCondition.between(startInclusive, endInclusive);
	}
}
