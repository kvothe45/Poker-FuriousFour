/**
 * Names:  Andrew Hoffman, Chase Revia, Robert Elmore, Ralph E. Beard IV
 * Course #:  1174
 * Date:  
 * Assignment Name: Group Project Poker
 */

package application;

import java.util.ArrayList;
import java.util.Random;

public class Utilities {
	
	/**
	 * This method takes a generic ArrayList and 
	 * randomly shuffles the items in it.
	 * @param <E>
	 * @param list
	 */
	public static <E> void shuffle(ArrayList<E> list) {
		
		Random random = new Random(); // random variable to enable scrambling indexes
		for (int i = list.size(); i > 1; i--) {
			int swappedIndex = random.nextInt(i); // holder for the random index to be swapped with
			if (swappedIndex != i - 1) {
				E tempObject = list.get(i - 1); // temp object to hold the contents in the list to be swapped
				list.set(i - 1, list.get(swappedIndex));
				list.set(swappedIndex, tempObject);
			}
		}
	}
	
	/**
	 * This method takes a generic Array and 
	 * randomly shuffles the items in it.
	 * @param <E>
	 * @param list
	 */
	public static <E> void shuffle(E[] list) {
		
		Random random = new Random(); // random variable to enable scrambling indexes
		for (int i = list.length; i > 1; i--) {
			int swappedIndex = random.nextInt(i); // holder for the random index to be swapped with
			if (swappedIndex != i - 1) {
				E tempObject = list[i - 1]; // temp object to hold the contents in the list to be swapped
				list[i - 1] = list[swappedIndex];
				list[swappedIndex] =  tempObject;
			}
		}
	}
	
	/**
	 * This method takes a generic ArrayList and
	 * sorts it.
	 * @param <E>
	 * @param list
	 */
	public static <E extends Comparable<E>> void sort(ArrayList<E> list) {
		
		E currentMin; // holds the current minimum value in the ArrayList
		int currentMinIndex; // holds the index for the currentMin value
		
		for (int i = 0; i < list.size() - 1; i++) {
			currentMin = list.get(i);
			currentMinIndex = i;
			for(int j = i + 1; j < list.size(); j++) {
				if (currentMin.compareTo(list.get(j)) > 0) {
					currentMin = list.get(j);
					currentMinIndex = j;
				}
				
			}
			
			if (currentMinIndex != i) {
				list.set(currentMinIndex, list.get(i));
				list.set(i, currentMin);
			}
		}
		
	}

}
