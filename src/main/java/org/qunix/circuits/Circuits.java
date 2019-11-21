package org.qunix.circuits;

import java.util.function.Consumer;

/**
 *
 * TODO: Comment
 *
 * @author bsarac
 *
 * @param <T> types
 * 2019-11-21 08:57:05 +0100
 */
public class Circuits<T> implements Consumer<T> {

	private Circuit<T>[] conditions;

	/**
	 * @param conditions constructor param
	 */
	@SafeVarargs
	private Circuits(Circuit<T>... conditions) {
		this.conditions = conditions;
	}

	/**
	 *
	 * of method: TODO
	 *
	 * 
	 *
	 *
	 * @param <E>
	 * @param conditions
	 * @return Circuits<E>
	 */
	@SafeVarargs
	public static final <E> Circuits<E> of(Circuit<E>... conditions) {
		Circuit.checkEmpty(conditions, "No condition provided!");
		return new Circuits<>(conditions);
	}

	/**
	 *
	 */
	public void accept(T value) {
		for (Circuit<T> condition : conditions) {
			condition.accept(value);
		}
	}

	/**
	 *
	 * ifAccept method: TODO
	 *
	 * 
	 *
	 *
	 * @param value
	 * @param consumer void
	 */
	public void ifAccept(T value, Consumer<T> consumer) {
		for (Circuit<T> condition : conditions) {
			if (condition.test(value)) {
				consumer.accept(value);
			}
		}
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
		for (Circuit<T> condition : this.conditions) {
			if (condition.isOpen()) {
				return false;
			}
		}

		return true;
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
		for (Circuit<T> condition : this.conditions) {
			if (condition.isClosed()) {
				return false;
			}
		}

		return true;
	}

	/**
	 *
	 * assertClosed method: TODO
	 *
	 * 
	 *
	 *
	 * @throws IllegalStateException void
	 */
	public void assertClosed() throws IllegalStateException {
		for (Circuit<T> condition : this.conditions) {
			if (condition.isOpen()) {
				throw new IllegalStateException("Circuit: " + condition.valueString + " still open!");
			}
		}
	}

	/**
	 *
	 * assertOpen method: TODO
	 *
	 * 
	 *
	 *
	 * @throws IllegalStateException void
	 */
	public void assertOpen() throws IllegalStateException {
		for (Circuit<T> condition : this.conditions) {
			if (condition.isClosed()) {
				throw new IllegalStateException("Circuit: " + condition.valueString + " still closed!");
			}
		}
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
	public static <A> FlowingCircuit<A> flowing(A... value) {
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
	public static <A> FlipCircuit<A> flipping(A... value) {
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
	public static <A> ImmutableCircuit<A> immutable(boolean state, A... value) {
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
	public static <A> SinglePassCircuit<A> singlePass(A... value) {
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
	public static <A> BiCircuit<A> biCircuit(A openValue, A closeValue) {
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
	public static <A> MultiBiCircuit<A> multiBiCircuit(A... value) {
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
	public static FlowingCircuit<Integer> between(int startInclusive, int endInclusive) {
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
	public static FlowingCircuit<Character> between(char startInclusive, char endInclusive) {
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
}
