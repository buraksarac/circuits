package com.netflix.conditionals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class CircuitCondition<T> {

	LinkedList<T> values = new LinkedList<>();
	long stackSize = 0;
	Set<T> ignores = new HashSet<>();
	boolean isNull = false;
	boolean open = false;
	Predicate<T> predicate = t -> true;
	private StringBuilder valueStr = new StringBuilder();
	private StringBuilder whenStr = new StringBuilder("WHEN TRUE ");
	Consumer<T> openConsumer;
	Consumer<T> closeConsumer;
	long max = -1;
	long currentOccurence = 0;
	boolean biCircuit;

	@SafeVarargs
	CircuitCondition(boolean circuitState, T... value) {
		this.open = circuitState;
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
	public static <A> FlowingCircuitCondition<A> of(A... value) {
		return new FlowingCircuitCondition<A>(false, value);
	}

	@SafeVarargs
	public static <A> FlowingCircuitCondition<A> flowing(A... value) {
		return new FlowingCircuitCondition<A>(false, value);
	}

	@SafeVarargs
	public static <A> FlipCircuitCondition<A> flipping(A... value) {
		return new FlipCircuitCondition<A>(false, value);
	}

	@SafeVarargs
	public static <A> ImmutableCircuitCondition<A> immutable(boolean state, A... value) {
		return new ImmutableCircuitCondition<A>(state, value);
	}

	@SafeVarargs
	public static <A> SinglePassCircuitCondition<A> singlePass(A... value) {
		return new SinglePassCircuitCondition<A>(false, value);
	}

	public static <A> BiCircuitCondition<A> biCircuit(A openValue, A closeValue) {
		return new BiCircuitCondition<A>(false, openValue, closeValue);
	}

	public static FlowingCircuitCondition<Integer> between(int startInclusive, int endInclusive) {
		if (endInclusive <= startInclusive) {
			throw new IllegalArgumentException("End value <= start value");
		}
		Integer[] vals = new Integer[endInclusive - startInclusive + 1];
		int counter = 0;
		for (int i = startInclusive; i <= endInclusive; i++) {
			vals[counter++] = i;
		}
		return new FlowingCircuitCondition<Integer>(false, vals);
	}

	public static FlowingCircuitCondition<Character> between(char startInclusive, char endInclusive) {
		if (endInclusive <= startInclusive) {
			throw new IllegalArgumentException("End value <= start value");
		}
		Character[] vals = new Character[endInclusive - startInclusive + 1];
		int counter = 0;
		for (int i = startInclusive; i <= endInclusive; i++) {
			vals[counter++] = (char) i;
		}
		return new FlowingCircuitCondition<Character>(false, vals);
	}

	@SafeVarargs
	public final When<T> when(CircuitCondition<T>... condition) {
		return new When<T>(false, condition);
	}

	public CircuitCondition<T> onOpen(Consumer<T> consumer) {
		this.openConsumer = consumer;
		return this;
	}

	public CircuitCondition<T> onClose(Consumer<T> consumer) {
		this.closeConsumer = consumer;
		return this;
	}

	public CircuitCondition<T> open() {
		this.open = true;
		return this;
	}

	public CircuitCondition<T> close() {
		this.open = false;
		return this;
	}

	public boolean isOpen() {
		return this.open;
	}

	public CircuitCondition<T> maxOccurence(long max) {
		this.whenStr.append(" AND [max occurence  of ").append(this.valueStr).append(" less or equals to ").append(max)
				.append(" ] ");
		this.max = max;
		return this;
	}

	@SafeVarargs
	public final CircuitCondition<T> ignore(T... value) {
		checkEmpty(value, "Empty or null ignore list");
		Arrays.asList(value).forEach(ignores::add);
		return this;
	}

	@SafeVarargs
	public final CircuitCondition<T> ignore(CircuitCondition<T>... condition) {
		checkEmpty(condition, "Empty or null ignore list");
		Arrays.asList(condition).forEach(c -> {
			ignores.addAll(c.values);
		});
		return this;
	}

	protected abstract boolean test(T t);

	void accept(T t) {
		boolean result = this.test(t);
		if (!result) {
			throw new CircuitCondition.ConditionMismatchException("Condition not satisfied : " + this.toString(), t);
		}

	}

	@Override
	public String toString() {
		return this.valueStr.append("(this), condition(s):").append(this.whenStr).toString();
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
							.append(" received then check ").append(CircuitCondition.this.valueStr)
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
							.append(" received then check ").append(source.valueStr)
							.append(expectClose ? "isClosed ]" : "isOpen ]");
				}
				break;

			default:
				for (CircuitCondition<T> source : sources) {
					CircuitCondition.this.whenStr.append("  [ if ").append(source.valueStr).append(" received then ");
					for (CircuitCondition<T> target : targets) {
						whenStr.append("\n then check [ ").append(target.valueStr)
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

		public ConditionMismatchException(String message, Object value) {
			super(String.format(message, String.valueOf(value)));
		}
	}

	public static class GateOpenCountOutOfBound extends RuntimeException {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1387304757053851098L;

		public GateOpenCountOutOfBound() {
			super("Gate opened more than allowed count");
		}
	}

	static final <T> void checkEmpty(T[] array, String message) {
		if (array == null || array.length < 1) {
			throw new IllegalArgumentException(message);
		}
	}

}
