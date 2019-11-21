package org.qunix.circuits;

import java.util.function.Consumer;

/**
 *
 * Class to create and manage multiple circuit instances
 *
 * @author bsarac
 *
 * @param <T> types 2019-11-21 08:57:05 +0100
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
	 * Returns a {@link Circuits} instance using given parameters, after user can
	 * call accept/ifAccept/isClosed/isOpen methods
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
	 * Feeds all circuits with given parameter
	 */
	public void accept(T value) {
		for (Circuit<T> condition : conditions) {
			condition.accept(value);
		}
	}

	/**
	 *
	 * if all circuits are satisfied for the given parameter calls consumer
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
	 * isClosed method: returns true if all circuits closed
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
	 * isOpen method: returns true if all circuits open
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
	 * assertClosed method: throws {@link IllegalStateException} if any of the
	 * circuit is in open state
	 *
	 * 
	 *
	 *
	 * @throws IllegalStateException void
	 */
	public void assertClosed() throws IllegalStateException {
		for (Circuit<T> condition : this.conditions) {
			condition.assertClosed();
		}
	}

	/**
	 *
	 * assertOpen method: throws {@link IllegalStateException} if any of the circuit
	 * is in close state
	 *
	 * 
	 *
	 *
	 * @throws IllegalStateException void
	 */
	public void assertOpen() throws IllegalStateException {
		for (Circuit<T> condition : this.conditions) {
			condition.assertOpen();
		}
	}

	/**
	 *
	 * returns a Circuit condition that opens on parameter match but closes only if
	 * there is mismatch <br/>
	 * <b>usage:</b><br/>
	 * <code>
	* Circuit< Character> circuit = Circuits.flowing('.');
	* <br/>
	* circuit.accept('a'); //still closed
	* <br/>
	* circuit.accept('.'); //opened
	* <br/>
	* circuit.accept('.'); //still open
	* * <br/>
	* circuit.accept(';'); //closed
	* 
	* </code> <br/>
	 * <br/>
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
	 * returns a Circuit condition that flips its status when its receives one of
	 * the given parameter <br/>
	 * <b>usage:</b><br/>
	 * <code>
	* Circuit< Character> circuit = Circuits.flipping('.');
	* <br/>
	* circuit.accept('a'); //still closed
	* <br/>
	* circuit.accept('.'); //opened
	* <br/>
	* circuit.accept(';'); //still open
	* <br/>
	* circuit.accept('.'); //closed
	* 
	* </code> <br/>
	 * <br/>
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
	 * returns a Circuit condition that its expected that its status never be
	 * changed, which means given param(s) will be never received, otherwise it will
	 * fail <br/>
	 * <br/>
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
	public static <A> ImmutableCircuit<A> immutable(A... value) {
		return new ImmutableCircuit<A>(value);
	}
	
	/**
	 *
	 * returns a Circuit condition that its expected that its status never be
	 * changed, which means null will be never received, otherwise it will
	 * fail <br/>
	 * <br/>
	 *
	 * 
	 *
	 *
	 * @param <A>
	 * @param state
	 * @param value
	 * @return ImmutableCircuit<A>
	 */
	public static <A> ImmutableCircuit<A> notNull() {
		return new ImmutableCircuit<A>();
	}

	/**
	 *
	 * returns a Circuit condition that opens gate only once and fails after if
	 * given param(s) re-occured <br/>
	 * <b>usage:</b><br/>
	 * <code>
	* Circuit< Character> circuit = Circuits.singlePass('.');
	* <br/>
	* circuit.accept('a'); //still closed
	* <br/>
	* circuit.accept('.'); //opened
	* <br/>
	* circuit.accept(';'); //still open
	* <br/>
	* circuit.accept('.'); //failure
	* 
	* </code> <br/>
	 * <br/>
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
	 * returns a Circuit condition that uses a pair to manage circuits. first one
	 * for opening and the second one for closing circuit <br/>
	 * <b>usage:</b><br/>
	 * <code>
	 * // '{' opens and '}' closes<br/>
	 * Circuit< Character> circuit = Circuits.biCircuit('{','}'); 
	 * <br/>
	 * circuit.accept('a'); //still closed
	 * <br/>
	 * circuit.accept('{'); //opened
	 * <br/>
	 * circuit.accept(';'); //still open
	 * <br/>
	 * circuit.accept('{'); //closed
	 * 
	 * </code> <br/>
	 * <br/>
	 * If you have a nested data structure i.e. json, mark as nested using
	 * {@link BiCircuit#nested}
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
	 * returns a Circuit condition that uses multiple pairs to manage its state. For
	 * each pair first one for opening and the second one for closing circuit <br/>
	 * <b>usage:</b><br/>
	 * <code>
	* // '{' opens and '}' closes<br/>
	* Circuit< Character> circuit = Circuits.multiBiCircuit('(', ')','[', ']','{', '}') 
	* <br/>
	* circuit.accept('a'); //still closed
	* <br/>
	* circuit.accept('{'); //opened
	* <br/>
	* circuit.accept(';'); //still open
	* <br/>
	* circuit.accept('{'); //closed
	* 
	* </code> <br/>
	 * <br/>
	 * If you have a nested data structure like json, mark as nested using
	 * {@link MultiBiCircuit#nested}
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
	 * creates values between start (inclusive) and end (inclusive) then returns a
	 * Between object to let user chose circuit type using those values
	 *
	 *
	 * @param startInclusive
	 * @param endInclusive
	 * @return Between<Integer>
	 */
	public static Between<Integer> between(int startInclusive, int endInclusive) {
		return new Between<Integer>(startInclusive, endInclusive);
	}

	/**
	 *
	 * creates values between start (inclusive) and end (inclusive) then returns a
	 * Between object to let user chose circuit type using those values
	 *
	 *
	 * @param startInclusive
	 * @param endInclusive
	 * @return Between<Integer>
	 */
	public static Between<Character> between(char startInclusive, char endInclusive) {
		return new Between<Character>(startInclusive, endInclusive);
	}

	@SuppressWarnings("unchecked")
	public static class Between<B> {

		Object[] vals;

		private Between(B start, B end) {
			if (start instanceof Integer) {
				initInts((int) start, (int) end);
			} else {
				initChars((char) start, (char) end);
			}
		}

		private void initInts(int startInclusive, int endInclusive) {
			if (endInclusive <= startInclusive) {
				throw new IllegalArgumentException("End value <= start value");
			}
			vals = new Object[endInclusive - startInclusive + 1];
			int counter = 0;
			for (int i = startInclusive; i <= endInclusive; i++) {
				vals[counter++] = (int) i;
			}
		}

		private void initChars(char startInclusive, char endInclusive) {
			if (endInclusive <= startInclusive) {
				throw new IllegalArgumentException("End value <= start value");
			}
			vals = new Object[endInclusive - startInclusive + 1];
			int counter = 0;
			for (int i = startInclusive; i <= endInclusive; i++) {
				vals[counter++] = (char) i;
			}
		}

		/**
		 *
		 * returns a Circuit condition that opens on parameter match but closes only if
		 * there is mismatch <br/>
		 * <b>usage:</b><br/>
		 * <code>
		* Circuit< Character> circuit = Circuits.flowing('.');
		* <br/>
		* circuit.accept('a'); //still closed
		* <br/>
		* circuit.accept('.'); //opened
		* <br/>
		* circuit.accept('.'); //still open
		* * <br/>
		* circuit.accept(';'); //closed
		* 
		* </code> <br/>
		 * <br/>
		 *
		 * @return FlowingCircuit<A>
		 */

		public FlowingCircuit<B> flowing() {
			return new FlowingCircuit<B>(false, (B[]) this.vals);
		}

		/**
		 *
		 * returns a Circuit condition that flips its status when its receives one of
		 * the given parameter <br/>
		 * <b>usage:</b><br/>
		 * <code>
		* Circuit< Character> circuit = Circuits.flipping('.');
		* <br/>
		* circuit.accept('a'); //still closed
		* <br/>
		* circuit.accept('.'); //opened
		* <br/>
		* circuit.accept(';'); //still open
		* <br/>
		* circuit.accept('.'); //closed
		* 
		* </code> <br/>
		 * <br/>
		 *
		 * 
		 *
		 *
		 * @param <A>
		 * @param value
		 * @return FlipCircuit<A>
		 */
		public FlipCircuit<B> flipping() {
			return new FlipCircuit<B>(false, (B[]) this.vals);
		}

		/**
		 *
		 * returns a Circuit condition that its expected that its status never be
		 * changed, which means given param(s) will be never received, otherwise it will
		 * fail <br/>
		 * <br/>
		 *
		 * 
		 *
		 *
		 * @return ImmutableCircuit<A>
		 */
		public ImmutableCircuit<B> immutable() {
			return new ImmutableCircuit<B>((B[]) this.vals);
		}

		/**
		 *
		 * returns a Circuit condition that opens gate only once and fails after if
		 * given param(s) re-occured <br/>
		 * <b>usage:</b><br/>
		 * <code>
		* Circuit< Character> circuit = Circuits.singlePass('.');
		* <br/>
		* circuit.accept('a'); //still closed
		* <br/>
		* circuit.accept('.'); //opened
		* <br/>
		* circuit.accept(';'); //still open
		* <br/>
		* circuit.accept('.'); //failure
		* 
		* </code> <br/>
		 * <br/>
		 *
		 * 
		 *
		 *
		 * @return SinglePassCircuit<A>
		 */
		public SinglePassCircuit<B> singlePass() {
			return new SinglePassCircuit<B>(false, (B[]) this.vals);
		}

	}
}
