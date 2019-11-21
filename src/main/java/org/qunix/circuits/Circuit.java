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

/**
 *
 * TODO: Comment
 *
 * @author bsarac
 *
 * @param <T> types
 * 2019-11-21 08:56:21 +0100
 */
public abstract class Circuit<T> implements Predicate<T> {

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

	/**
	 * @param circuitState
	 * @param value constructor param
	 */
	@SafeVarargs
	Circuit(boolean circuitState, T... value) {
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

	/**
	 *
	 * of method: TODO
	 *
	 * 
	 *
	 *
	 * @param <A>
	 * @param value
	 * @return FlowingCircuit<A>
	 */
	@SafeVarargs
	static <A> FlowingCircuit<A> of(A... value) {
		return new FlowingCircuit<A>(false, value);
	}

	/**
	 *
	 * flowing method: TODO
	 *
	 * 
	 *
	 *
	 * @param <A>
	 * @param value
	 * @return FlowingCircuit<A>
	 */
	@SafeVarargs
	static <A> FlowingCircuit<A> flowing(A... value) {
		return new FlowingCircuit<A>(false, value);
	}

	/**
	 *
	 * flipping method: TODO
	 *
	 * 
	 *
	 *
	 * @param <A>
	 * @param value
	 * @return FlipCircuit<A>
	 */
	@SafeVarargs
	static <A> FlipCircuit<A> flipping(A... value) {
		return new FlipCircuit<A>(false, value);
	}

	/**
	 *
	 * immutable method: TODO
	 *
	 * 
	 *
	 *
	 * @param <A>
	 * @param state
	 * @param value
	 * @return ImmutableCircuit<A>
	 */
	@SafeVarargs
	static <A> ImmutableCircuit<A> immutable(boolean state, A... value) {
		return new ImmutableCircuit<A>(state, value);
	}

	/**
	 *
	 * singlePass method: TODO
	 *
	 * 
	 *
	 *
	 * @param <A>
	 * @param value
	 * @return SinglePassCircuit<A>
	 */
	@SafeVarargs
	static <A> SinglePassCircuit<A> singlePass(A... value) {
		return new SinglePassCircuit<A>(false, value);
	}

	/**
	 *
	 * biCircuit method: TODO
	 *
	 * 
	 *
	 *
	 * @param <A>
	 * @param openValue
	 * @param closeValue
	 * @return BiCircuit<A>
	 */
	static <A> BiCircuit<A> biCircuit(A openValue, A closeValue) {
		return new BiCircuit<A>(false, openValue, closeValue);
	}

	/**
	 *
	 * multiBiCircuit method: TODO
	 *
	 * 
	 *
	 *
	 * @param <A>
	 * @param value
	 * @return MultiBiCircuit<A>
	 */
	@SafeVarargs
	static <A> MultiBiCircuit<A> multiBiCircuit(A... value) {
		return new MultiBiCircuit<A>(false, value);
	}

	/**
	 *
	 * between method: TODO
	 *
	 * 
	 *
	 *
	 * @param startInclusive
	 * @param endInclusive
	 * @return FlowingCircuit<Integer>
	 */
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

	/**
	 *
	 * between method: TODO
	 *
	 * 
	 *
	 *
	 * @param startInclusive
	 * @param endInclusive
	 * @return FlowingCircuit<Character>
	 */
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

	/**
	 *
	 * when method: TODO
	 *
	 * 
	 *
	 *
	 * @param condition
	 * @return When<T>
	 */
	@SafeVarargs
	public final When<T> when(Circuit<T>... condition) {
		return new When<T>(false, condition);
	}

	/**
	 *
	 * onOpen method: TODO
	 *
	 * 
	 *
	 *
	 * @param consumer
	 * @return CircuitCondition<T>
	 */
	public Circuit<T> onOpen(Consumer<T> consumer) {
		this.openConsumers.add(consumer);
		return this;
	}

	/**
	 *
	 * onClose method: TODO
	 *
	 * 
	 *
	 *
	 * @param consumer
	 * @return CircuitCondition<T>
	 */
	public Circuit<T> onClose(Consumer<T> consumer) {
		this.closeConsumers.add(consumer);
		return this;
	}

	/**
	 *
	 * whileOpen method: TODO
	 *
	 * 
	 *
	 *
	 * @param consumer
	 * @return CircuitCondition<T>
	 */
	public Circuit<T> whileOpen(Consumer<T> consumer) {
		this.whileOpenConsumers.add(consumer);
		return this;
	}

	/**
	 *
	 * whileClosed method: TODO
	 *
	 * 
	 *
	 *
	 * @param consumer
	 * @return CircuitCondition<T>
	 */
	public Circuit<T> whileClosed(Consumer<T> consumer) {
		this.whileCloseConsumers.add(consumer);
		return this;
	}

	/**
	 *
	 * open method: TODO
	 *
	 * 
	 *
	 *
	 * @return CircuitCondition<T>
	 */
	public Circuit<T> open() {
		this.open = true;
		return this;
	}

	/**
	 *
	 * close method: TODO
	 *
	 * 
	 *
	 *
	 * @return CircuitCondition<T>
	 */
	public Circuit<T> close() {
		this.open = false;
		return this;
	}

	/**
	 *
	 * isOpen method: TODO
	 *
	 * 
	 *
	 *
	 * @return boolean
	 */
	public boolean isOpen() {
		return this.open;
	}

	/**
	 *
	 * isClosed method: TODO
	 *
	 * 
	 *
	 *
	 * @return boolean
	 */
	public boolean isClosed() {
		return !this.open;
	}

	/**
	 *
	 * ignore method: TODO
	 *
	 * 
	 *
	 *
	 * @param value
	 * @return CircuitCondition<T>
	 */
	@SafeVarargs
	public final Circuit<T> ignore(T... value) {
		checkEmpty(value, "Empty or null ignore list");
		Arrays.asList(value).forEach(ignores::add);
		return this;
	}

	/**
	 *
	 * ignore method: TODO
	 *
	 * 
	 *
	 *
	 * @param condition
	 * @return CircuitCondition<T>
	 */
	@SafeVarargs
	public final Circuit<T> ignore(Circuit<T>... condition) {
		checkEmpty(condition, "Empty or null ignore list");
		Arrays.asList(condition).forEach(c -> {
			ignores.addAll(c.values);
		});
		return this;
	}

	/**
	 *
	 * testInternal method: TODO
	 *
	 * 
	 *
	 *
	 * @param t
	 * @param valid
	 * @return boolean
	 */
	protected abstract boolean testInternal(T t, boolean valid);

	/**
	 *
	 */
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

	/**
	 *
	 * accept method: TODO
	 *
	 * 
	 *
	 *
	 * @param t void
	 */
	void accept(T t) {
		boolean result = this.test(t);
		if (!result) {
			throw new Circuit.ConditionMismatchException("Condition not satisfied : " + this.toString(), t);
		}

	}

	/**
	 *
	 */
	@Override
	public String toString() {
		return this.valueString;
	}

	/**
	 *
	 * TODO: Comment
	 *
	 * @author bsarac
	 * types
	 * 2019-11-21 08:56:21 +0100
	 */
	private enum BuildType {
		
		/**
		 * 
		 */
		SOURCE, 
		/**
		 * 
		 */
		TARGET, 
		/**
		 * 
		 */
		CIRCUIT;
	}

	/**
	 *
	 * TODO: Comment
	 *
	 * @author bsarac
	 *
	 * @param <W> types
	 * 2019-11-21 08:56:21 +0100
	 */
	public class When<W extends T> {

		private List<Circuit<T>> sources;
		private Circuit<T>[] targets;
		private BuildType buildType;
		private boolean expectClose;
		private boolean or = false;

		/**
		 * @param or
		 * @param source constructor param
		 */
		@SafeVarargs
		private When(boolean or, Circuit<T>... source) {
			checkEmpty(source, "You can not provide null or empty value while using WHEN");
			this.sources = Arrays.asList(source);
			this.or = or;
		}

		/**
		 *
		 * expect method: TODO
		 *
		 * 
		 *
		 *
		 * @return Expect<W>
		 */
		public Expect<W> expect() {
			return new Expect<W>();
		}

		/**
		 *
		 * build method: TODO
		 *
		 * 
		 *
		 * void
		 */
		private void build() {
			Predicate<T> predicate = t -> true;
			switch (this.buildType) {
			case CIRCUIT:
				for (Circuit<T> source : sources) {
					Predicate<T> circuitPredicate = t -> {
						return source.values.contains(t)
								? (expectClose ? !Circuit.this.open : Circuit.this.open)
								: true;
					};
					predicate = predicate.and(circuitPredicate);
				}
				break;
			case SOURCE:
				for (Circuit<T> source : sources) {
					Predicate<T> sourcePredicate = t -> {
						return source.values.contains(t) ? (expectClose ? !source.open : source.open) : true;
					};
					predicate = predicate.and(sourcePredicate);
				}
				break;

			default:
				for (Circuit<T> source : sources) {
					Predicate<T> sourcePredicate = t -> {
						boolean condition = source.values.contains(t);
						if (condition) {
							for (Circuit<T> target : targets) {
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
				Circuit.this.predicate = Circuit.this.predicate.or(predicate);
			} else {
				Circuit.this.predicate = Circuit.this.predicate.and(predicate);
			}

		}

		/**
		 *
		 * TODO: Comment
		 *
		 * @author bsarac
		 *
		 * @param <E> types
		 * 2019-11-21 08:56:21 +0100
		 */
		public class Expect<E extends T> {

			/**
			 *
			 * circuitClosed method: TODO
			 *
			 * 
			 *
			 *
			 * @return AndOr<E>
			 */
			public AndOr<E> circuitClosed() {
				When.this.buildType = BuildType.CIRCUIT;
				When.this.expectClose = true;
				When.this.build();
				return new AndOr<E>();
			}

			/**
			 *
			 * circuitOpen method: TODO
			 *
			 * 
			 *
			 *
			 * @return AndOr<E>
			 */
			public AndOr<E> circuitOpen() {
				When.this.buildType = BuildType.CIRCUIT;
				When.this.expectClose = false;
				When.this.build();
				return new AndOr<E>();
			}

			/**
			 *
			 * closed method: TODO
			 *
			 * 
			 *
			 *
			 * @return AndOr<E>
			 */
			public AndOr<E> closed() {
				When.this.buildType = BuildType.SOURCE;
				When.this.expectClose = true;
				When.this.build();
				return new AndOr<E>();
			}

			/**
			 *
			 * open method: TODO
			 *
			 * 
			 *
			 *
			 * @return AndOr<E>
			 */
			public AndOr<E> open() {
				When.this.buildType = BuildType.SOURCE;
				When.this.expectClose = false;
				When.this.build();
				return new AndOr<E>();
			}

			/**
			 *
			 * closed method: TODO
			 *
			 * 
			 *
			 *
			 * @param target
			 * @return AndOr<E>
			 */
			@SafeVarargs
			public final AndOr<E> closed(Circuit<T>... target) {
				checkEmpty(target, "You can not provide null or empty value while using WHEN");
				When.this.targets = target;
				When.this.buildType = BuildType.TARGET;
				When.this.expectClose = true;
				When.this.build();
				return new AndOr<E>();
			}

			/**
			 *
			 * open method: TODO
			 *
			 * 
			 *
			 *
			 * @param target
			 * @return AndOr<E>
			 */
			@SafeVarargs
			public final AndOr<E> open(Circuit<T>... target) {
				checkEmpty(target, "You can not provide null or empty value while using WHEN");
				When.this.targets = target;
				When.this.buildType = BuildType.TARGET;
				When.this.expectClose = false;
				When.this.build();
				return new AndOr<E>();
			}

			/**
			 *
			 * TODO: Comment
			 *
			 * @author bsarac
			 *
			 * @param <A> types
			 * 2019-11-21 08:56:21 +0100
			 */
			public class AndOr<A extends T> {

				/**
				 *
				 * TODO: Comment
				 *
				 * @author bsarac
				 * types
				 * 2019-11-21 08:56:21 +0100
				 */
				public class Or {
					/**
					 *
					 * when method: TODO
					 *
					 * 
					 *
					 *
					 * @param condition
					 * @return When<A>
					 */
					@SafeVarargs
					public final When<A> when(Circuit<A>... condition) {
						return new When<>(true, condition);
					}
				}

				/**
				 *
				 * TODO: Comment
				 *
				 * @author bsarac
				 * types
				 * 2019-11-21 08:56:21 +0100
				 */
				public class And {
					/**
					 *
					 * when method: TODO
					 *
					 * 
					 *
					 *
					 * @param condition
					 * @return When<A>
					 */
					@SafeVarargs
					public final When<A> when(Circuit<A>... condition) {
						return new When<>(false, condition);
					}
				}

				/**
				 *
				 * and method: TODO
				 *
				 * 
				 *
				 *
				 * @return And
				 */
				public And and() {
					return new And();

				}

				/**
				 *
				 * or method: TODO
				 *
				 * 
				 *
				 *
				 * @return Or
				 */
				public Or or() {
					return new Or();
				}

			}
		}

	}

	/**
	 *
	 * TODO: Comment
	 *
	 * @author bsarac
	 * types
	 * 2019-11-21 08:56:21 +0100
	 */
	public static class ConditionMismatchException extends IllegalStateException {
		/**
		 * 
		 */
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1387304757053851098L;

		/**
		 * @param message
		 * @param value constructor param
		 */
		public ConditionMismatchException(String message, Object value) {
			super(String.format(message, String.valueOf(value)));
		}
	}

	/**
	 *
	 * TODO: Comment
	 *
	 * @author bsarac
	 * types
	 * 2019-11-21 08:56:21 +0100
	 */
	public static class GateOpenCountOutOfBound extends RuntimeException {
		/**
		 * 
		 */
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1387304757053851098L;

		/**
		 *  constructor param
		 */
		public GateOpenCountOutOfBound() {
			super("Gate opened more than allowed count");
		}
	}

	/**
	 *
	 * checkEmpty method: TODO
	 *
	 * 
	 *
	 *
	 * @param <T>
	 * @param array
	 * @param message void
	 */
	static final <T> void checkEmpty(T[] array, String message) {
		if (array == null || array.length < 1) {
			throw new IllegalArgumentException(message);
		}
	}

}
