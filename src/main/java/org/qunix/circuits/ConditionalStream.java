package org.qunix.circuits;

import java.util.Objects;
import java.util.Spliterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ConditionalStream<T> {

	private Runnable runnable;
	private StreamFeeder<T> feeder;

	private ConditionalStream(Streamable<T> streamable) {
		this.feeder = new StreamFeeder<T>(streamable.getCapacity());
		this.runnable = () -> streamable.stream(feeder);

	}

	public Stream<T> audit(Circuits<T> audit) {
		Objects.requireNonNull(audit);
		ExecutorService executor = Executors.newSingleThreadExecutor();
		try (AutoCloseable ac = executor::shutdown) {
			executor.submit(this.runnable);
			Stream<T> stream = StreamSupport
					.stream(new InfiniteSupplyingSpliterator.OfRef<>(Long.MAX_VALUE, this.feeder, audit), false);
			return stream;
		} catch (Exception e) {
			throw new StreamBuildxception("An error occured due to building stream", e);
		}

	}

	public static final <A> ConditionalStream<A> of(Streamable<A> streamable) {
		Objects.requireNonNull(streamable);

		return new ConditionalStream<A>(streamable);
	}

	private static abstract class InfiniteSupplyingSpliterator<T> implements Spliterator<T> {
		long estimate;

		protected InfiniteSupplyingSpliterator(long estimate) {
			this.estimate = estimate;
		}

		@Override
		public long estimateSize() {
			return estimate;
		}

		@Override
		public int characteristics() {
			return IMMUTABLE | ORDERED | NONNULL;
		}

		private static final class OfRef<T> extends InfiniteSupplyingSpliterator<T> {
			final StreamFeeder<T> feeder;
			final Circuits<T> audit;

			OfRef(long size, StreamFeeder<T> feeder, Circuits<T> audit) {
				super(size);
				this.feeder = feeder;
				this.audit = audit;
			}

			@Override
			public boolean tryAdvance(Consumer<? super T> action) {
				Objects.requireNonNull(action);

				T value = feeder.get();
				if (value == null && feeder.size() == 0) {

					if (feeder.isFailed()) {
						throw feeder.getExeption();
					}
					return false;
				}
				audit.accept(value);
				action.accept(value);
				return true;

			}

			@Override
			public Spliterator<T> trySplit() {
				if (estimate == 0)
					return null;
				return new InfiniteSupplyingSpliterator.OfRef<>(estimate >>>= 1, feeder, audit);
			}
		}
	}

	public static class StreamBuildxception extends RuntimeException {

		/**
		 * 
		 */
		private static final long serialVersionUID = -7516111140569090968L;

		public StreamBuildxception(String message, Throwable t) {
			super(message, t);
		}
	}
}
