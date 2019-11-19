package com.netflix.conditionals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public class CircuitCondition<T> {

	private Set<T> values = new HashSet<>();
	private boolean isNull = false;
	protected boolean open = false;
	private Predicate<T> predicate = t -> true;

	protected CircuitCondition(T... value) {
		isNull = value == null;
		if (!isNull) {
			if (value.length == 0) {
				throw new IllegalArgumentException("Condition value can not be empty");
			}
			Arrays.asList(value).forEach(values::add);
		}
	}
	
	public static <T> CircuitCondition<T> of(T... value) {
		return new CircuitCondition<>(value);
	}

	public When when(CircuitCondition<T>... condition) {
		return new When(false, condition);
	}

	protected boolean test(T t) {
		if(this.values.contains(t) || (isNull && t == null)) {
			this.open = !open;
			return true;
		}
		return this.predicate.test(t);
	}

	private enum BuildType {
		SOURCE, TARGET, CIRCUIT;
	}

	public class When {

		private List<CircuitCondition<T>> sources;
		private CircuitCondition<T>[] targets;
		private BuildType buildType;
		private boolean expectClose;
		private boolean or = false;

		private When(boolean or, CircuitCondition<T>... source) {
			checkEmpty(source, "You can not provide null or empty value while using WHEN");
			this.sources = Arrays.asList(source);
			this.or = or;
		}

		public Expect expect() {
			return new Expect();
		}

		private void build() {
			Predicate<T> predicate = t -> true;
			switch (this.buildType) {
			case CIRCUIT:
				for (CircuitCondition<T> source : sources) {
					Predicate<T> circuitPredicate = t -> {
						return source.values.contains(t)
								? (expectClose ? !CircuitCondition.this.open : CircuitCondition.this.open)
								: true;
					};
					predicate = predicate.and(circuitPredicate);
				}
				break;
			case SOURCE:
				for (CircuitCondition<T> source : sources) {
					Predicate<T> sourcePredicate = t -> {
						return source.values.contains(t) ? (expectClose ? !source.open : source.open) : true;
					};
					predicate = predicate.and(sourcePredicate);
				}
				break;

			default:
				for (CircuitCondition<T> source : sources) {
					Predicate<T> sourcePredicate = t -> {
						boolean condition = source.values.contains(t);
						if (condition) {
							for (CircuitCondition<T> target : targets) {
								condition = expectClose ? !target.open : target.open;
								if (!condition) {
									return false;
								}
							}
						}
						return true;
					};
					predicate = predicate.and(sourcePredicate);
				}
				break;
			}

			if (or) {
				CircuitCondition.this.predicate = CircuitCondition.this.predicate.or(predicate);
			} else {
				CircuitCondition.this.predicate = CircuitCondition.this.predicate.and(predicate);
			}

		}

		public class Expect {

			public AndOr circuitClosed() {
				When.this.buildType = BuildType.CIRCUIT;
				When.this.expectClose = true;
				When.this.build();
				return new AndOr();
			}

			public AndOr circuitOpen() {
				When.this.buildType = BuildType.CIRCUIT;
				When.this.expectClose = false;
				When.this.build();
				return new AndOr();
			}

			public AndOr closed() {
				When.this.buildType = BuildType.SOURCE;
				When.this.expectClose = true;
				When.this.build();
				return new AndOr();
			}

			public AndOr open() {
				When.this.buildType = BuildType.SOURCE;
				When.this.expectClose = false;
				When.this.build();
				return new AndOr();
			}

			public AndOr closed(CircuitCondition<T>... target) {
				checkEmpty(target, "You can not provide null or empty value while using WHEN");
				When.this.targets = target;
				When.this.buildType = BuildType.TARGET;
				When.this.expectClose = true;
				When.this.build();
				return new AndOr();
			}

			public AndOr open(CircuitCondition<T>... target) {
				checkEmpty(target, "You can not provide null or empty value while using WHEN");
				When.this.targets = target;
				When.this.buildType = BuildType.TARGET;
				When.this.expectClose = false;
				When.this.build();
				return new AndOr();
			}

			public class AndOr {

				public class Or {
					public When when(CircuitCondition<T>... condition) {
						return new When(true, condition);
					}
				}

				public class And {
					public When when(CircuitCondition<T>... condition) {
						return new When(false, condition);
					}
				}

				public And and() {
					return new And();

				}

				public Or or() {
					return new Or();
				}

			}
		}

	}

	public static class ConditionMismatchException extends RuntimeException {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1387304757053851098L;

		public ConditionMismatchException(String message) {
			super(message);
		}
	}

	private static final <T> void checkEmpty(T[] array, String message) {
		if (array == null || array.length < 1) {
			throw new IllegalArgumentException(message);
		}
	}

}
