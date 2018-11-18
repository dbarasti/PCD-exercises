package multiset;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;



/**
 * <p>A MultiSet models a data structure containing elements along with their frequency count i.e., </p>
 * <p>the number of times an element is present in the set.</p>
 * <p>HashMultiSet is a Map-based concrete implementation of the MultiSet concept.</p>
 *
 * <p>MultiSet a = <{1:2}, {2:2}, {3:4}, {10:1}></p>
 * */
public final class HashMultiSet<T, V extends Number> {

	/**
	 *multiSetMap: data structure backing this MultiSet implementation.
	 **/
	private HashMap<T,V> multiSetMap;


	/**
	 * Sole constructor of the class.
	 **/
	public HashMultiSet() {
		multiSetMap = new HashMap<>();
	}


	/**
	 * If not present, adds the element to the data structure, otherwise
	 * simply increments its frequency.
	 *
	 * @param t T: element to include in the multiset
	 *
	 * @return V: frequency count of the element in the multiset
	 * */
	public V addElement(T t) {
		//default return = 1
		Integer i = 1;

		V currentValue = multiSetMap.putIfAbsent(t, (V) i);

		if(currentValue == null)
			return (V) i;
		Integer updatedValue = (Integer) currentValue + i;
		multiSetMap.put(t,(V)updatedValue);
		return (V)updatedValue;
	}

	/**
	 * Check whether the elements is present in the multiset.
	 *
	 * @param t T: element
	 *
	 * @return V: true if the element is present, false otherwise.
	 * */
	public boolean isPresent(T t) {
		return multiSetMap.containsKey(t);
	}

	/**
	 * @param t T: element
	 * @return V: frequency count of parameter t ('0' if not present)
	 * */
	public V getElementFrequency(T t) {
		Integer count = (Integer) multiSetMap.get(t);
		Integer defaultReturn = 0;
		if(count == null)
			return (V) defaultReturn;
		else
			return (V) count;
	}


	/**
	 * Builds a multiset from a source data file. The source data file contains
	 * a number comma separated elements.
	 * Example_1: ab,ab,ba,ba,ac,ac -->  <{ab:2},{ba:2},{ac:2}>
	 * Example 2: 1,2,4,3,1,3,4,7 --> <{1:2},{2:1},{3:2},{4:2},{7:1}>
	 *
	 * @param source Path: source of the multiset
	 * */
	public void buildFromFile(Path source){
		try (Stream<String> stream = Files.lines(source)) {

			stream.forEach((line)->{
				String[] elements = line.split(",");
				for (String el:
						elements
				) {
					addElement((T)el);
					System.out.print("	Key: " + el + " Type: " + el.getClass().toString() + ", Value: " + getElementFrequency((T)el)+ " Type: " + getElementFrequency((T)el).getClass().toString() );
				}
			});

		} catch (IOException e) {
			e.printStackTrace();
		}
		//Integer num = 1;
		//System.out.println(getElementFrequency((T) num));

	}

	/**
	 * Same as before with the difference being the source type.
	 * @param source List<T>: source of the multiset
	 * */
	public void buildFromCollection(List<? extends T> source) {
		source.stream()
				.forEach(el->{
					addElement((T)el);
					System.out.println("	Key: " + el + " Type: " + el.getClass().toString() + ", Value: " + getElementFrequency((T)el)+ " Type: " + getElementFrequency((T)el).getClass().toString() );
				});

	}

	/**
	 * Produces a linearized, unordered version of the MultiSet data structure.
	 * Example: <{1:2},{2:1}, {3:3}> -> 1 1 2 3 3 3
	 *
	 * @return List<T>: linearized version of the multiset represented by this object.
	 */
	public List<T> linearize() {
		List<T> linearizedMap = new ArrayList<>();

		multiSetMap.forEach((k,v)->{
			Integer value = (Integer)v;
			while(value!=0){
				linearizedMap.add(k);
				value--;
			}
		});
		return linearizedMap;
	}


}
