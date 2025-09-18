package com.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;


public class Sorting {
	public static void main(String[] args) {
	}
	public static <K,V extends Comparable<? super V>> 
	java.util.List<Entry<K, V>> entriesSortedByValues(Map<K,V> map) {

	    java.util.List<Entry<K,V>> sortedEntries = new ArrayList<Entry<K, V>>(map.entrySet());

	    Collections.sort(sortedEntries, 
	            new Comparator<Entry<K,V>>() {
	                @Override
	                public int compare(Entry<K,V> e1, Entry<K,V> e2) {
	                    return e2.getValue().compareTo(e1.getValue());
	                }
	            }
	    );

	    return sortedEntries;
	}
}
