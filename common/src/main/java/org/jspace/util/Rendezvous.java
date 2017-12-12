/*******************************************************************************
 * Copyright (c) 2017 Michele Loreti and the jSpace Developers (see the included 
 * authors file).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
package org.jspace.util;

import java.util.HashMap;

/**
 * This class supports the coordination of threads activities based on rendezvous data synchronization.
 * This mechanism allows two threads to exchange a single datum while synchronizing.
 * 
 * One thread try to receive a value ({@link #call(T)}) for a given tag and waits until another 
 * thread provides a datum with a matching tag ({@link #set(T,V)}). Then, the waiting thread is awaked and the provided value
 * is returned. The tag is deleted just after the synchronization is completed. 
 * 
 * Finally, it is assumed that two synchronizations with the same tag cannot be performed. In case, a {@link IllegalStateException} is
 * thrown.
 *
 * @param <T> data type for tags
 * @param <V> data type for values
 */
public class Rendezvous<T,V> {
	
	/**
	 * Internally tags are mapped to a {@link DataWrapper}. This is an inner class that is used as a
	 * wrapper for values.
	 */
	protected final HashMap<T,DataWrapper> data = new HashMap<>();
		
	/**
	 * Creates a new synchronization map.
	 */
	public Rendezvous( ) {
	}

	/**
	 * Calls for a value associated with tag. Current thread is suspended until another thread 
	 * provides a datum with a matching tag ({@link #set(T,V)}). 
	 * 
	 * @param tag the tag identifying the requested value
	 * @return the value to which the specified tag is mapped.
	 * @throws InterruptedException if any thread interrupted the current thread before or while the current thread was waiting for a value.
	 */
	public V call(T tag) throws InterruptedException {
		DataWrapper wrapper;
		synchronized (data) {
			wrapper = data.get(tag);
			if (wrapper != null) {
				if (!wrapper.flag) {
					throw new IllegalStateException("Duplicated call on tag: "+tag);
				}
			} else {
				wrapper = new DataWrapper( tag );
				data.put(tag, wrapper);
			}
		}
		return wrapper.get();
	}

	/**
	 * Provides a value associated with a tag. All the threads that are waiting for a value on that
	 * tag are notified. An {@link IllegalStateException} is thrown if the tag has not registered in 
	 * the data structure. 
	 * 
	 * @param tag the tag associated with a query
	 * @param value the value 
	 * @throws IllegalArgumentException if the tag is not registered in the data structure 
	 */
	public void set(T tag , V value) {
		synchronized (data) {
			DataWrapper wrapper = data.get(tag);
			if (wrapper != null) {
				if (wrapper.flag) {
					throw new IllegalStateException("Value already set for tag "+tag);
				} 
			} else {
				wrapper = new DataWrapper( tag );
				data.put(tag, wrapper);
			}
			wrapper.set(value);
		}
	}

	/**
	 * Checks if the tag is registered in the Rendezvous.
	 * 
	 * @param tag a tag
	 * @return true if the tag is registered in the Rendezvous.
	 */
	public boolean containsTag(T tag) {
		synchronized (data) {
			return data.containsKey(tag);
		}
	}
	
	/**
	 * An inner data class that implements the synchronization mechanisms between
	 * callers and data providers.
	 */
	class DataWrapper {
		
		/**
		 * Data value associated in the wrapper.
		 */
		private T tag;

		/**
		 * Data value associated in the wrapper.
		 */
		private V value;
		
		/**
		 * Flag that indicates if the wrapper has been already set or not.
		 */
		private boolean flag = false;
		
		/**
		 * Creates an empty (and unset) instance.
		 */
		public DataWrapper( T tag ) {
			this.tag = tag;
		}
		
		/**
		 * Gets the value in the wrapper. Current thread is suspended until another
		 * thread invokes method {@link #set(V)}.
		 * 
		 * @return value in the wrapper.
		 * @throws InterruptedException if any thread interrupted the current thread before or while the current thread was waiting for a value.
		 */
		public synchronized V get() throws InterruptedException {
			while (!flag) {
				wait();
			}
			remove( tag );
			return value;
		}
		
		/**
		 * Sets a value in the wrapper. All the waiting threads are notified.
		 * 
		 * @param value value stored in the wrapper.
		 */
		public synchronized void set(V value) {
			if (this.flag) {
				throw new IllegalStateException("Value already provided!");
			}
			this.value = value;
			this.flag = true;
			notifyAll();
		}
		
	}

	private void remove(T tag) {
		synchronized (data) {
			data.remove(tag);
		}
	}
	
	public boolean canCall( T tag ) {
		synchronized (data) {
			DataWrapper wrapper = data.get(tag);
			return (wrapper == null)||(wrapper.flag);
		}
	}

	public boolean canSet( T tag ) {
		synchronized (data) {
			DataWrapper wrapper = data.get(tag);
			return (wrapper == null)||(!wrapper.flag);
		}
	}
}
