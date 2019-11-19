package com.netflix.conditionals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CircuitCondition<T> {

	private Set<T> values = new HashSet<>();
	private boolean isNull = false;
	boolean open = false;
	private Predicate<T> predicate = t -> true;
	private StringBuilder valueStr = new StringBuilder();
	private StringBuilder whenStr = new StringBuilder("WHEN TRUE ");
	private Consumer<T> openConsumer;
	private Consumer<T> closeConsumer;
	private boolean flip;

	@SafeVarargs
	CircuitCondition(boolean circuitState, boolean flip, T... value) {
		this.open = circuitState;
		this.flip = flip;
		isNull = value == null;
		if (!isNull) {
			if (value.length == 0) {
				throw new IllegalArgumentException("Condition value can not be empty");
			}
			valueStr.append(" [ ");
			Arrays.asList(value).forEach(values::add);
			String valueString = values.stream().map(t -> t.toString()).collect(Collectors.joining(","));
			valueStr.append(valueString);
			valueStr.append(" ] ");
		}
	}

	@SafeVarargs
	public static <A> CircuitCondition<A> of(A... value) {
		return new CircuitCondition<>(false, false, value);
	}

	@SafeVarargs
	public static <A> CircuitCondition<A> flipCircuit(A... value) {
		return new CircuitCondition<>(false, true, value);
	}

	@SafeVarargs
	public static <A> CircuitCondition<A> openCircuit(A... value) {
		return new CircuitCondition<>(true, false, value);
	}

	@SafeVarargs
	public static <A> CircuitCondition<A> openFlipCircuit(A... value) {
		return new CircuitCondition<>(true, true, value);
	}

	public static CircuitCondition<Integer> between(int startInclusive, int endInclusive) {
		if (endInclusive <= startInclusive) {
			throw new IllegalArgumentException("End value <= start value");
		}
		Integer[] vals = new Integer[endInclusive - startInclusive + 1];
		int counter = 0;
		for (int i = startInclusive; i <= endInclusive; i++) {
			vals[counter++] = i;
		}
		return new CircuitCondition<Integer>(false, false, vals);
	}

	public static CircuitCondition<Character> between(char startInclusive, char endInclusive) {
		if (endInclusive <= startInclusive) {
			throw new IllegalArgumentException("End value <= start value");
		}
		Character[] vals = new Character[endInclusive - startInclusive + 1];
		int counter = 0;
		for (int i = startInclusive; i <= endInclusive; i++) {
			vals[counter++] = (char) i;
		}
		return new CircuitCondition<Character>(false, false, vals);
	}

	@SafeVarargs
	public final When<T> when(CircuitCondition<T>... condition) {
		return new When<T>(false, condition);
	}

	public void onOpen(Consumer<T> consumer) {
		this.openConsumer = consumer;
	}

	public void onClose(Consumer<T> consumer) {
		this.closeConsumer = consumer;
	}

	boolean test(T t) {
		boolean stateChange = false;
		if (this.values.contains(t) || (isNull && t == null)) {
			if(this.flip) {
				this.open = !open;
				stateChange = true;
			}else if(!this.open) {
				this.open = !open;
				stateChange = true;
			}
		} else if (!this.flip && this.open && (!this.values.contains(t) || (isNull && t != null))) {
			this.open = !open;
			stateChange = true;
		}
		if (stateChange && this.open && this.openConsumer != null) {
			this.openConsumer.accept(t);
		} else if (stateChange && !this.open && this.closeConsumer != null) {
			this.closeConsumer.accept(t);
		}
		return this.predicate.test(t);
	}

	void accept(T t) {
		boolean result = this.test(t);
		if (!result) {
			throw new CircuitCondition.ConditionMismatchException(
					"For value : " + t + "Condition not satisfied : " + this.toString());
		}

	}

	@Override
	public String toString() {
		return this.valueStr.append(" => ").append(this.whenStr).toString();
	}

	private enum BuildType {
		SOURCE, TARGET, CIRCUIT;
	}

	public class When<W extends T> {

		private List<CircuitCondition<T>> sources;
		private CircuitCondition<T>[] targets;
		private BuildType buildType;
		private boolean expectClose;
		private boolean or = false;

		@SafeVarargs
		private When(boolean or, CircuitCondition<T>... source) {
			checkEmpty(source, "You can not provide null or empty value while using WHEN");
			this.sources = Arrays.asList(source);
			this.or = or;
		}

		public Expect<W> expect() {
			return new Expect<W>();
		}

		private void build() {
			Predicate<T> predicate = t -> true;
			CircuitCondition.this.whenStr.append(or ? " OR " : " AND ");
			switch (this.buildType) {
			case CIRCUIT:
				for (CircuitCondition<T> source : sources) {
					Predicate<T> circuitPredicate = t -> {
						return source.values.contains(t)
								? (expectClose ? !CircuitCondition.this.open : CircuitCondition.this.open)
								: true;
					};
					predicate = predicate.and(circuitPredicate);
					CircuitCondition.this.whenStr.append("  [ if ").append(source.valueStr)
							.append(" contains($value) check ").append(CircuitCondition.this.valueStr)
							.append(expectClose ? "isClosed ]" : "isOpen ]");
				}
				break;
			case SOURCE:
				for (CircuitCondition<T> source : sources) {
					Predicate<T> sourcePredicate = t -> {
						return source.values.contains(t) ? (expectClose ? !source.open : source.open) : true;
					};
					predicate = predicate.and(sourcePredicate);
					CircuitCondition.this.whenStr.append("  [ if ").append(source.valueStr)
							.append(" contains($value) check ").append(source.valueStr)
							.append(expectClose ? "isClosed ]" : "isOpen ]");
				}
				break;

			default:
				for (CircuitCondition<T> source : sources) {
					CircuitCondition.this.whenStr.append("  [ if ").append(source.valueStr)
							.append(" contains($value) then ");
					for (CircuitCondition<T> target : targets) {
						whenStr.append("\n check [ ").append(target.valueStr)
								.append(expectClose ? "isClosed ]" : "isOpen ]");
					}
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

		public class Expect<E extends T> {

			public AndOr<E> circuitClosed() {
				When.this.buildType = BuildType.CIRCUIT;
				When.this.expectClose = true;
				When.this.build();
				return new AndOr<E>();
			}

			public AndOr<E> circuitOpen() {
				When.this.buildType = BuildType.CIRCUIT;
				When.this.expectClose = false;
				When.this.build();
				return new AndOr<E>();
			}

			public AndOr<E> closed() {
				When.this.buildType = BuildType.SOURCE;
				When.this.expectClose = true;
				When.this.build();
				return new AndOr<E>();
			}

			public AndOr<E> open() {
				When.this.buildType = BuildType.SOURCE;
				When.this.expectClose = false;
				When.this.build();
				return new AndOr<E>();
			}

			@SafeVarargs
			public final AndOr<E> closed(CircuitCondition<T>... target) {
				checkEmpty(target, "You can not provide null or empty value while using WHEN");
				When.this.targets = target;
				When.this.buildType = BuildType.TARGET;
				When.this.expectClose = true;
				When.this.build();
				return new AndOr<E>();
			}

			@SafeVarargs
			public final AndOr<E> open(CircuitCondition<T>... target) {
				checkEmpty(target, "You can not provide null or empty value while using WHEN");
				When.this.targets = target;
				When.this.buildType = BuildType.TARGET;
				When.this.expectClose = false;
				When.this.build();
				return new AndOr<E>();
			}

			public class AndOr<A extends T> {

				public class Or {
					@SafeVarargs
					public final When<A> when(CircuitCondition<A>... condition) {
						return new When<>(true, condition);
					}
				}

				public class And {
					@SafeVarargs
					public final When<A> when(CircuitCondition<A>... condition) {
						return new When<>(false, condition);
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

	static final <T> void checkEmpty(T[] array, String message) {
		if (array == null || array.length < 1) {
			throw new IllegalArgumentException(message);
		}
	}

}
