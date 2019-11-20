package org.qunix.circuits;

public interface Streamable<T> extends AutoCloseable {

	public void stream(StreamFeeder<T> stream);
	
	public int getCapacity();
}
