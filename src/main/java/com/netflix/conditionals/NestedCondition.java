package com.netflix.conditionals;

public class NestedCondition<T> {

	private CircuitCondition<T>[] conditions;

	@SafeVarargs
	private NestedCondition(CircuitCondition<T>... conditions) {
		this.conditions = conditions;
	}

	@SafeVarargs
	public static final <E> NestedCondition<E> of(CircuitCondition<E>... conditions) {
		CircuitCondition.checkEmpty(conditions, "No condition provided!");
		return new NestedCondition<>(conditions);
	}

	void audit(T value) {
		for (CircuitCondition<T> condition : conditions) {
			if (condition.test(value)) {
				throw new CircuitCondition.ConditionMismatchException(
						"For value : " + value + "Condition not satisfied : " + condition.toString());
			}
		}
	}
}
