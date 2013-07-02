package chord.data;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import chord.utils.ChordUtils;

public class Entries implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Map<Long, Set<MyValue>> entries = null;

	/**
	 * Creates an empty repository for entries.
	 */
	public Entries(){ 
		this.entries = Collections.synchronizedMap(new TreeMap<Long, Set<MyValue>>());
	}

	/**
	 * Stores a set of entries to the local hash table.
	 * 
	 */
	public void addAll(Set<MyValue> entriesToAdd) {

		if (entriesToAdd == null) {
			NullPointerException e = new NullPointerException(
					"Set of entries to be added to the local hash table may "
							+ "not be null!");
			throw e;
		}

		for (MyValue nextEntry : entriesToAdd) {
			this.add(nextEntry);
		}
	}

	/**
	 * Stores one entry to the local hash table.
	 * 
	 */
	public void add(MyValue entryToAdd) {
		
		if (entryToAdd == null) {
			NullPointerException e = new NullPointerException(
					"Entry to add may not be null!");
			throw e;
		}

		Set<MyValue> values;
		synchronized (this.entries) {
			if (this.entries.containsKey(entryToAdd.getId())) {
				values = this.entries.get(entryToAdd.getId());
			} else {
				values = new HashSet<MyValue>();
				this.entries.put(entryToAdd.getId(), values);
			}
			values.add(entryToAdd);
		}
	}

	/**
	 * Removes the given entry from the local hash table.
	 * 
	 */
	public void remove(MyValue entryToRemove) {
		
		if (entryToRemove == null) {
			NullPointerException e = new NullPointerException(
					"Entry to remove may not be null!");
			throw e;
		}

		synchronized (this.entries) {
			if (this.entries.containsKey(entryToRemove.getId())) {
				Set<MyValue> values = this.entries.get(entryToRemove.getId());
				values.remove(entryToRemove);
				if (values.size() == 0) {
					this.entries.remove(entryToRemove.getId());
				}
			}
		}
	}

	/**
	 * Returns a set of entries matching the given ID. If no entries match the
	 * given ID, an empty set is returned.
	 * 
	 */
	public Set<MyValue> getEntries(Long id) {

		if (id == null) {
			NullPointerException e = new NullPointerException(
					"ID to find entries for may not be null!");
			throw e;
		}
		synchronized (this.entries) {
			/*
			 * This has to be synchronized as the test if the map contains a set
			 * associated with id can succeed and then the thread may hand
			 * control over to another thread that removes the Set belonging to
			 * id. In that case this.entries.get(id) would return null which
			 * would break the contract of this method.
			 */
			if (this.entries.containsKey(id)) {
				Set<MyValue> entriesForID = this.entries.get(id);
				/*
				 * Return a copy of the set to avoid modification of Set stored
				 * in this.entries from outside this class. (Avoids also
				 * modifications concurrent to iteration over the Set by a
				 * client of this class.
				 */
				return new HashSet<MyValue>(entriesForID);
			}
		}
		return new HashSet<MyValue>();
	}

	/**
	 * Returns all entries in interval, excluding lower bound, but including
	 * upper bound
	 */
	public Set<MyValue> getEntriesInInterval(Long fromID, Long toID) {

		if (fromID == null || toID == null) {
			NullPointerException e = new NullPointerException(
					"Neither of the given IDs may have value null!");
			throw e;
		}

		Set<MyValue> result = new HashSet<MyValue>();

		synchronized (this.entries) {
			for (Long nextID : this.entries.keySet()) {
				if (ChordUtils.inRangeLeftOpenIntervall(nextID, fromID, toID)) {
					Set<MyValue> entriesForID = this.entries.get(nextID);
					for (MyValue entryToAdd : entriesForID) {
						result.add(entryToAdd);
					}
				}
			}
		}

		// add entries matching upper bound
		result.addAll(this.getEntries(toID));

		return result;
	}

	/**
	 * Removes the given entries from the local hash table.
	 */
	public void removeAll(Set<MyValue> toRemove) {

		if (toRemove == null) {
			NullPointerException e = new NullPointerException(
					"Set of entries may not have value null!");
			throw e;
		}

		for (MyValue nextEntry : toRemove) {
			this.remove(nextEntry);
		}
	}

	/**
	 * Returns an unmodifiable map of all stored entries.
	 * 
	 */
	public Map<Long, Set<MyValue>> getEntries() {
		return Collections.unmodifiableMap(this.entries);
	}

	/**
	 * Returns the number of stored entries.
	 */
	public int getNumberOfStoredEntries() {
		return this.entries.size();
	}

	/**
	 * Returns a formatted string of all entries stored in the local hash table.
	 */
	public String toString() {
		StringBuilder result = new StringBuilder("Entries:\n");
		for (Map.Entry<Long, Set<MyValue>> entry : this.entries.entrySet()) {
			result.append("  key = " + entry.getKey().toString()
					+ ", value = " + entry.getValue() + "\n");
		}
		return result.toString();
	}
}
