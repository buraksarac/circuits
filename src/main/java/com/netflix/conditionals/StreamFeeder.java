package com.netflix.conditionals;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class StreamFeeder<T> {

	private int capacity;

	private final BlockingQueue<T> queue;

	private RuntimeException exception;
	private boolean failed;

	public StreamFeeder(int capacity) {
		this.capacity = capacity;
		this.queue = new ArrayBlockingQueue<T>(this.capacity);
	}

	public void feed(T value) throws InterruptedException {
		this.queue.offer(value, 200, TimeUnit.MILLISECONDS);
	}

	public T get() {
		try {
			return queue.poll(200, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean isFailed() {
		return this.failed;
	}

	public RuntimeException getExeption() {
		this.failed = true;
		return this.exception;
	}

	public void exception(RuntimeException e) {
		this.exception = e;
	}

	public int size() {
		return this.queue.size();
	}

}
