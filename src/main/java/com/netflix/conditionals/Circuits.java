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

	@SafeVarargs
	public static <A> FlowingCircuitCondition<A> of(A... value) {
		return CircuitCondition.of(value);
	}

	@SafeVarargs
	public static <A> FlowingCircuitCondition<A> flowing(A... value) {
		return CircuitCondition.flowing(value);
	}

	@SafeVarargs
	public static <A> FlipCircuitCondition<A> flipping(A... value) {
		return CircuitCondition.flipping(value);
	}

	@SafeVarargs
	public static <A> ImmutableCircuitCondition<A> immutable(boolean state, A... value) {
		return CircuitCondition.immutable(state, value);
	}

	@SafeVarargs
	public static <A> SinglePassCircuitCondition<A> singlePass(A... value) {
		return CircuitCondition.singlePass(value);
	}

	public static <A> BiCircuitCondition<A> biCircuit(A openValue, A closeValue) {
		return CircuitCondition.biCircuit(openValue, closeValue);
	}

	public static FlowingCircuitCondition<Integer> between(int startInclusive, int endInclusive) {
		return CircuitCondition.between(startInclusive, endInclusive);
	}

	public static FlowingCircuitCondition<Character> between(char startInclusive, char endInclusive) {
		return CircuitCondition.between(startInclusive, endInclusive);
	}
}
