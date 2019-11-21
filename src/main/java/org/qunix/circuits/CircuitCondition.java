package org.qunix.circuits;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class CircuitCondition<T> implements Predicate<T> {

	LinkedList<T> values = new LinkedList<>();
	Set<T> ignores = new HashSet<>();
	boolean isNull = false;
	boolean open = false;
	Predicate<T> predicate = t -> true;
	BiPredicate<T, Boolean> preConditions = (t, v) -> true;
	BiPredicate<T, Boolean> postConditions = (t, v) -> true;

	List<Consumer<T>> openConsumers = new ArrayList<>();
	List<Consumer<T>> closeConsumers = new ArrayList<>();
	List<Consumer<T>> whileOpenConsumers = new ArrayList<>();
	List<Consumer<T>> whileCloseConsumers = new ArrayList<>();
	String valueString;
	boolean stateChange;

	@SafeVarargs
	CircuitCondition(boolean circuitState, T... value) {
		this.open = circuitState;
		isNull = value == null;
		if (!isNull) {
			if (value.length == 0) {
				throw new IllegalArgumentException("Condition value can not be empty");
			}
			Arrays.asList(value).forEach(values::add);
			valueString = values.stream().map(t -> t.toString()).collect(Collectors.joining(","));
		}
	}

	@SafeVarargs
	static <A> FlowingCircuit<A> of(A... value) {
		return new FlowingCircuit<A>(false, value);
	}

	@SafeVarargs
	static <A> FlowingCircuit<A> flowing(A... value) {
		return new FlowingCircuit<A>(false, value);
	}

	@SafeVarargs
	static <A> FlipCircuit<A> flipping(A... value) {
		return new FlipCircuit<A>(false, value);
	}

	@SafeVarargs
	static <A> ImmutableCircuit<A> immutable(boolean state, A... value) {
		return new ImmutableCircuit<A>(state, value);
	}

	@SafeVarargs
	static <A> SinglePassCircuit<A> singlePass(A... value) {
		return new SinglePassCircuit<A>(false, value);
	}

	static <A> BiCircuit<A> biCircuit(A openValue, A closeValue) {
		return new BiCircuit<A>(false, openValue, closeValue);
	}

	@SafeVarargs
	static <A> MultiBiCircuit<A> multiBiCircuit(A... value) {
		return new MultiBiCircuit<A>(false, value);
	}

	static FlowingCircuit<Integer> between(int startInclusive, int endInclusive) {
		if (endInclusive <= startInclusive) {
			throw new IllegalArgumentException("End value <= start value");
		}
		Integer[] vals = new Integer[endInclusive - startInclusive + 1];
		int counter = 0;
		for (int i = startInclusive; i <= endInclusive; i++) {
			vals[counter++] = i;
		}
		return new FlowingCircuit<Integer>(false, vals);
	}

	static FlowingCircuit<Character> between(char startInclusive, char endInclusive) {
		if (endInclusive <= startInclusive) {
			throw new IllegalArgumentException("End value <= start value");
		}
		Character[] vals = new Character[endInclusive - startInclusive + 1];
		int counter = 0;
		for (int i = startInclusive; i <= endInclusive; i++) {
			vals[counter++] = (char) i;
		}
		return new FlowingCircuit<Character>(false, vals);
	}

	@SafeVarargs
	public final When<T> when(CircuitCondition<T>... condition) {
		return new When<T>(false, condition);
	}

	public CircuitCondition<T> onOpen(Consumer<T> consumer) {
		this.openConsumers.add(consumer);
		return this;
	}

	public CircuitCondition<T> onClose(Consumer<T> consumer) {
		this.closeConsumers.add(consumer);
		return this;
	}

	public CircuitCondition<T> whileOpen(Consumer<T> consumer) {
		this.whileOpenConsumers.add(consumer);
		return this;
	}

	public CircuitCondition<T> whileClosed(Consumer<T> consumer) {
		this.whileCloseConsumers.add(consumer);
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

	public boolean isClosed() {
		return !this.open;
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

	protected abstract boolean testInternal(T t, boolean valid);

	public boolean test(T t) {
		List<Consumer<T>> consumers = open ? whileOpenConsumers : whileCloseConsumers;
		if (!ignores.contains(t)) {
			boolean isValid = this.values.contains(t) || (isNull && t == null);
			if (!this.preConditions.test(t, isValid) || !testInternal(t, isValid)
					|| !this.postConditions.test(t, isValid)) {
				return false;
			}
		}
		consumers = stateChange ? open ? openConsumers : closeConsumers : consumers;
		consumers.forEach(c -> c.accept(t));
		return stateChange ? !(stateChange = false) : this.predicate.test(t);
	}

	void accept(T t) {
		boolean result = this.test(t);
		if (!result) {
			throw new CircuitCondition.ConditionMismatchException("Condition not satisfied : " + this.toString(), t);
		}

	}

	@Override
	public String toString() {
		return this.valueString;
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

	public static class ConditionMismatchException extends IllegalStateException {
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
