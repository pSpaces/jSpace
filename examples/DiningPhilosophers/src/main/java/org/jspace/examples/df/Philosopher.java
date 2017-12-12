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
package org.jspace.examples.df;


import java.util.Random;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

/**
 * @author loreti
 *
 */
public class Philosopher implements Runnable {

	private Space table;
	private int id;
	private Random random;
	private int first;
	private int second;

	public Philosopher(int id, Space table, int left, int right) {
		this.table = table;
		this.id = id;
		this.random = new Random();
		this.first = Math.min(left, right);
		this.second = Math.max(left, right);
	}
	
	@Override
	public void run() {
		try {
			while (true) {
				int sleeptime = random.nextInt(1000);
				System.out.println("PHIL "+this.id+"> I'am thinking! (for "+sleeptime+"s)");
				Thread.sleep(sleeptime);
				System.out.println("PHIL "+this.id+"> I'am hungry!");
				getFork(first);
				getFork(second);
				sleeptime = random.nextInt(1000);
				System.out.println("PHIL "+this.id+"> I'am eating! (for "+sleeptime+"s)");
				Thread.sleep(sleeptime);
				System.out.println("PHIL "+this.id+"> I'am full!");
				releaseFork(first);
				releaseFork(second);
			}
		} catch (InterruptedException e) {
			System.err.println("PHI "+id+" error!");
			e.printStackTrace();
		}
	}

	private void getFork( int i ) throws InterruptedException {
		System.out.println("PHIL "+this.id+"> Getting fork "+i+"!");
		table.get(new ActualField("FORK"), new ActualField(i));
		System.out.println("PHIL "+this.id+"> Fork "+i+" acquired!");
	}

	private void releaseFork( int i ) throws InterruptedException {
		System.out.println("PHIL "+this.id+"> Releasing fork "+i+"!");
		table.put("FORK", i);
		System.out.println("PHIL "+this.id+"> Fork "+i+" released!");
	}


}
