package chord.gui;

import java.util.Set;

import javax.swing.table.AbstractTableModel;

import chord.data.Entries;
import chord.data.MyValue;

public class DataTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Entries entries;
	
	public DataTableModel(Entries entries) {
		this.entries = entries;
	}
	
	@Override
	public int getColumnCount() {
		return 1;
	}

	public String getColumnName(int col) {
		return "Key";
	}
	
	@Override
	public int getRowCount() {
		if (entries != null) {
			return entries.getNumberOfStoredEntries();
		} else {
			return 0;
		}
	}

	@Override
	public Object getValueAt(int row, int cols) {
		int i = 0;
		
		for (Long key : entries.getEntries().keySet()) {
			if (i == row) {
				return key;
			}
			
			i++;
		}
		
		return null;
	}
	
	public Set<MyValue> getValues(long id) {
		return entries.getEntries(id);
	}

}
