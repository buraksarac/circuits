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
 * Main circuit impl. that has core functionalities
 *
 * @author bsarac
 *
 * @param <T> types 2019-11-21 08:56:21 +0100
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
	 * @param circuitState defaultState
	 * @param value        values
	 */
	@SafeVarargs
	Circuit(boolean circuitState, T... value) {
		this.open = circuitState;
		isNull = value == null || value.length == 0;
		if (!isNull) {
			values.addAll(Arrays.asList(value));
			valueString = values.stream().map(t -> t == null ? "" : t.toString()).collect(Collectors.joining(","));
		}
	}

	/**
	 *
	 * when method: Combine validation of this circuit using another circuit state
	 * This method is used to build nested conditions using other circuits
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
	 * onOpen method: each time circuit opened consumer called
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
	 * onClose method: each time circuit closed consumer called
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
	 * whileOpen method: this consumer will be called while circuit state is open
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
	 * whileClosed method: this consumer will be called while circuit state is close
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
	 * open method: open circuit explicitly
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
	 * close method: close circuit explicitly
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
	 * isOpen method: check if open
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
	 * isClosed method: check if closed
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
	 * ignore method: during the stream this values will be ignored so there will be
	 * no failure or circuit state change
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
	 * ignore method: during the stream this condition values will be ignored so
	 * there will be no failure or circuit state change
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
	 * testInternal method: abstract method to test given stream data
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
	 * This method returns a failure status that if circuit conditions are
	 * satisfied. False means a failure occured
	 * 
	 * For reading circuit state use {@link Circuit#isOpen()}
	 * 
	 * @return boolean true if conditions satisfied
	 */
	public boolean test(T t) {
		// check if given param needs to be ignored
		if (ignores.contains(t)) {
			return true;
		}
		// assume by default state not changed
		List<Consumer<T>> consumers = open ? whileOpenConsumers : whileCloseConsumers;
		// check if parameter is a open/close signal
		boolean valid = this.values.contains(t) || (isNull && t == null);
		stateChange = false;
		if ((!valid && !this.predicate.test(t)) // param not belongs to this check WHEN conditions
				|| !this.preConditions.test(t, valid)) { 
			// one of the condition didnt satisfy, fail
			return false;
		}
		if(stateChange) { //one of the precondition changed state
			return true;
		}
		if (!testInternal(t, valid) || !this.postConditions.test(t, valid)) { // check post if any
			// one of the condition didnt satisfy, fail
			return false;
		}
		// notify observers
		consumers = stateChange ? open ? openConsumers : closeConsumers : consumers;
		consumers.forEach(c -> c.accept(t));
		return true;
	}

	/**
	 *
	 * accept method: same behaviour as {@link Circuit#test(Object)} except it will
	 * throw {@link ConditionMismatchException} on failures
	 *
	 * 
	 *
	 *
	 * @param t param
	 */
	public void accept(T t) {
		boolean result = this.test(t);
		if (!result) {
			throw new Circuit.ConditionMismatchException(
					"For parameter " + t + " Condition not satisfied : " + this.toString(), t);
		}

	}

	/**
	 *
	 * if conditions satisfied for the given parameter calls consumer
	 *
	 * 
	 *
	 *
	 * @param value
	 * @param consumer void
	 */
	public void ifAccept(T value, Consumer<T> consumer) {
		if (this.test(value)) {
			consumer.accept(value);
		}
	}

	/**
	 *
	 * assertClosed method: throws {@link IllegalStateException} if circuit is in
	 * open state
	 *
	 * 
	 *
	 *
	 * @throws IllegalStateException void
	 */
	public void assertClosed() throws IllegalStateException {
		if (this.isOpen()) {
			throw new IllegalStateException("Circuit: " + this.valueString + " still open!");
		}
	}

	/**
	 *
	 * assertOpen method: throws {@link IllegalStateException} if is in close state
	 *
	 * 
	 *
	 *
	 * @throws IllegalStateException void
	 */
	public void assertOpen() throws IllegalStateException {
		if (this.isClosed()) {
			throw new IllegalStateException("Circuit: " + this.valueString + " still closed!");
		}
	}

	/**
	 * to string
	 */
	@Override
	public String toString() {
		return this.valueString;
	}

	/**
	 *
	 * Internal enum that marks when().expect().x() behaviour
	 *
	 * @author bsarac types 2019-11-21 08:56:21 +0100
	 */
	private enum BuildType {

		/**
		 * when this circuit receives other circuit parameter checks if other is
		 * open/closed
		 */
		SOURCE,
		/**
		 * when this circuit receives other circuit parameter checks if given another
		 * circuit is open/closed
		 */
		TARGET,
		/**
		 * when this circuit receives other circuit parameter checks if circuit itself
		 * is open/closed
		 */
		CIRCUIT;
	}

	/**
	 *
	 * This class is used to build nested conditions using other circuits
	 *
	 * @author bsarac
	 *
	 * @param <W> types 2019-11-21 08:56:21 +0100
	 */
	public class When<W extends T> {

		private List<Circuit<T>> sources;
		private Circuit<T>[] targets;
		private BuildType buildType;
		private boolean expectClose;
		private boolean or = false;

		/**
		 * @param or
		 * @param source other circuit
		 */
		@SafeVarargs
		private When(boolean or, Circuit<T>... source) {
			checkEmpty(source, "You can not provide null or empty value while using WHEN");
			this.sources = Arrays.asList(source);
			this.or = or;
		}

		/**
		 *
		 * expect method: When other circuit parameter received expect a condition to
		 * satisfy
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
		 * build method: internal method to build a predicate
		 *
		 * 
		 *
		 * void
		 */
		private void build() {
			Predicate<T> predicate = t -> true;
			// visit through build types
			switch (this.buildType) {

			// when this circuit receives other circuit parameter checks if circuit itself
			// is open/closed
			case CIRCUIT:
				// iterate other circuits
				for (Circuit<T> source : sources) {

					Predicate<T> circuitPredicate = t -> {
						// if given param is not other circuit param simply ignore
						return source.values.contains(t) ? (expectClose ? !Circuit.this.open : Circuit.this.open)
								: true;
					};
					// extend predicate
					predicate = predicate.and(circuitPredicate);
				}
				break;
			// when this circuit receives other circuit parameter checks if other is
			// open/closed
			case SOURCE:
				// iterate other circuits
				for (Circuit<T> source : sources) {
					Predicate<T> sourcePredicate = t -> {
						// if given param is not other circuit param simply ignore
						return source.values.contains(t) ? (expectClose ? !source.open : source.open) : true;
					};
					// extend predicate
					predicate = predicate.and(sourcePredicate);
				}
				break;

			default:
				// when this circuit receives other circuit parameter checks if another is
				// open/closed
				// iterate other circuits
				for (Circuit<T> source : sources) {
					Predicate<T> sourcePredicate = t -> {
						boolean condition = source.values.contains(t);
						// given param matches one of the circuits
						if (condition) {
							// iterate through targets
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
		 * Nested builder for {@link When}
		 *
		 * @author bsarac
		 *
		 * @param <E> types 2019-11-21 08:56:21 +0100
		 */
		public class Expect<E extends T> {

			/**
			 *
			 * circuitClosed method: <br/>
			 * 
			 * For given circuit.when(otherCircuit).expect().circuitClosed(); <br/>
			 * means: when circuit receives a param belongs to otherCircuit then it checks
			 * circuit itself is closed
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
			 * circuitOpen method: <br/>
			 * 
			 * For example given circuit.when(otherCircuit).expect().circuitOpen(); <br/>
			 * means: when circuit receives a param belongs to otherCircuit then it checks
			 * circuit itself is open
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
			 * closed method: <br/>
			 * 
			 * For example given circuit.when(otherCircuit).expect().closed(); <br/>
			 * means: when circuit receives a param belongs to otherCircuit then it checks
			 * otherCircuit is closed
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
			 * open method: <br/>
			 * 
			 * For example given circuit.when(otherCircuit).expect().open(); <br/>
			 * means: when circuit receives a param belongs to otherCircuit then it checks
			 * otherCircuit is open
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
			 * closed method: <br/>
			 * 
			 * For example given circuit.when(otherCircuit).expect().closed(anotherCircuit);
			 * <br/>
			 * means: when circuit receives a param belongs to otherCircuit then it checks
			 * anotherCircuit is closed
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
			 * open method: <br/>
			 * 
			 * For example given circuit.when(otherCircuit).expect().open(anotherCircuit);
			 * <br/>
			 * means: when circuit receives a param belongs to otherCircuit then it checks
			 * anotherCircuit is open
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
			 * Class for building And or Conditions
			 *
			 * @author bsarac
			 *
			 * @param <A> types 2019-11-21 08:56:21 +0100
			 */
			public class AndOr<A extends T> {

				/**
				 *
				 * Class for building or Conditions
				 *
				 * @author bsarac types 2019-11-21 08:56:21 +0100
				 */
				public class Or {
					/**
					 *
					 * when method: Combine validation of this circuit using another circuit state
					 * This method is used to build nested conditions using other circuits
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
				 * Class for building and Conditions
				 *
				 * @author bsarac types 2019-11-21 08:56:21 +0100
				 */
				public class And {
					/**
					 *
					 * when method: Combine validation of this circuit using another circuit state
					 * This method is used to build nested conditions using other circuits
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
				 * and method: append another condition to existing one using AND
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
				 * or method: append another condition to existing one using OR
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
	 * This exception is thrown when circuit opened/closed while its not supposed to
	 * have state change or one of the when conditions didnt satisfy
	 *
	 * @author bsarac types 2019-11-21 08:56:21 +0100
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
		 * @param value   constructor param
		 */
		public ConditionMismatchException(String message, Object value) {
			super(String.format(message, String.valueOf(value)));
		}
	}

	/**
	 *
	 * checkEmpty method: utility methods to check array null or empty
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
