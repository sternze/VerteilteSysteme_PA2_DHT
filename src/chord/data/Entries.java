package chord.data;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import chord.utils.ChordUtils;

public class Entries {

	private Map<Long, Set<MyValue>> entries = null;

	/**
	 * Creates an empty repository for entries.
	 */
	Entries(){ 
		this.entries = Collections.synchronizedMap(new TreeMap<Long, Set<MyValue>>());
	}

	/**
	 * Stores a set of entries to the local hash table.
	 * 
	 * @param entriesToAdd
	 *            Set of entries to add to the repository.
	 * @throws NullPointerException
	 *             If set reference is <code>null</code>.
	 */
	final void addAll(Set<MyValue> entriesToAdd) {

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
	 * @param entryToAdd
	 *            Entry to add to the repository.
	 * @throws NullPointerException
	 *             If entry to add is <code>null</code>.
	 */
	final void add(MyValue entryToAdd) {
		
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
	 * @param entryToRemove
	 *            Entry to remove from the hash table.
	 * @throws NullPointerException
	 *             If entry to remove is <code>null</code>.
	 */
	final void remove(MyValue entryToRemove) {
		
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
	 * @param id
	 *            ID of entries to be returned.
	 * @throws NullPointerException
	 *             If given ID is <code>null</code>.
	 * @return Set of matching entries. Empty Set if no matching entries are
	 *         available.
	 */
	final Set<MyValue> getEntries(Long id) {

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
	 * 
	 * @param fromID
	 *            Lower bound of IDs; entries matching this ID are NOT included
	 *            in result.
	 * @param toID
	 *            Upper bound of IDs; entries matching this ID ARE included in
	 *            result.
	 * @throws NullPointerException
	 *             If either or both of the given ID references have value
	 *             <code>null</code>.
	 * @return Set of matching entries.
	 */
	final Set<MyValue> getEntriesInInterval(Long fromID, Long toID) {

		if (fromID == null || toID == null) {
			NullPointerException e = new NullPointerException(
					"Neither of the given IDs may have value null!");
			throw e;
		}

		Set<MyValue> result = new HashSet<MyValue>();

		synchronized (this.entries) {
			for (Long nextID : this.entries.keySet()) {
				if (ChordUtils.inRangeOpenIntervall(nextID, fromID, toID)) {
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
	 * 
	 * @param toRemove
	 *            Set of entries to remove from local hash table.
	 * @throws NullPointerException
	 *             If the given set of entries is <code>null</code>.
	 */
	final void removeAll(Set<MyValue> toRemove) {

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
	 * @return Unmodifiable map of all stored entries.
	 */
	final Map<Long, Set<MyValue>> getEntries() {
		return Collections.unmodifiableMap(this.entries);
	}

	/**
	 * Returns the number of stored entries.
	 * 
	 * @return Number of stored entries.
	 */
	final int getNumberOfStoredEntries() {
		return this.entries.size();
	}

	/**
	 * Returns a formatted string of all entries stored in the local hash table.
	 * 
	 * @return String representation of all stored entries.
	 */
	public final String toString() {
		StringBuilder result = new StringBuilder("Entries:\n");
		for (Map.Entry<Long, Set<MyValue>> entry : this.entries.entrySet()) {
			result.append("  key = " + entry.getKey().toString()
					+ ", value = " + entry.getValue() + "\n");
		}
		return result.toString();
	}
}
