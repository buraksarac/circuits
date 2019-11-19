package com.netflix.conditionals;

public class OpenCircuitCondition<T> extends CircuitCondition<T>{

	@SafeVarargs
	private OpenCircuitCondition(T... value) {
		super(value);
		this.open = true;
	}
	
	@SafeVarargs
	public static final <T> CircuitCondition<T> of(T... value) {
		return new OpenCircuitCondition<>(value);
	}
}
