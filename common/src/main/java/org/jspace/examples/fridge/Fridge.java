package org.jspace.examples.fridge;

import org.jspace.*;

import java.util.Arrays;
import java.util.List;

public class Fridge {

	    public static void main(String[] argv) throws InterruptedException {
			// Creating a tuple.
			Tuple tuple = new Tuple("milk", 1);
			System.out.println("We just created tuple");
			System.out.println(tuple);

			System.out.println("The fields of ");
			System.out.println(tuple);
			System.out.println(" are ");
			System.out.println(tuple.getElementAt(0));
			System.out.println(" and ");
			System.out.println(tuple.getElementAt(1));

			// Creating a space.
			Space fridge = new SequentialSpace(); // or FIFOSpace, LIFOSpace

			// Adding tuples.
			fridge.put("coffee", 1);
			fridge.put("coffee", 1);
			fridge.put("clean kitchen");
			fridge.put("butter", 2);
			fridge.put("milk", 3);

			// Looking for a tuple.
			Object obj1 = fridge.queryp(new ActualField("clean kitchen"));
			if (obj1 != null) {
				System.out.println("We need to clean the kitchen");
			}

			// Removing a tuple.
			Object obj2 = fridge.getp(new ActualField("clean kitchen"));
			if (obj2 != null) {
				System.out.println("Cleaning...");
			}

			// Looking for a tuple with pattern matching.
			int numberOfBottles;
			Object[] objs3 = fridge.queryp(new ActualField("milk"), new FormalField(Integer.class));
			numberOfBottles = (int)objs3[1];

			// Updating a tuple.
			if (objs3 != null && numberOfBottles <= 10) {
				System.out.println("We plan to buy milk, but not enough...");
				Object[] objs4 = fridge.getp(new ActualField("milk"), new FormalField(Integer.class));
				numberOfBottles = (int)objs4[1];
				fridge.put("milk", numberOfBottles + 1);
			}


			List<Object[]> groceryList = fridge.queryAll(new FormalField(String.class), new FormalField(Integer.class));
			System.out.println("Items to buy: ");
			for (Object[] obj : groceryList) {
				System.out.println(Arrays.toString(obj));
			}
		}

}
