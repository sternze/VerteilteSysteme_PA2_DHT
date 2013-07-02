package chord.gui;

import java.util.Set;

import javax.swing.table.AbstractTableModel;

import chord.data.MyValue;

public class ValueTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Set<MyValue> values;
	
	public ValueTableModel() { }
	
	public ValueTableModel(Set<MyValue> values) {
		this.values = values;
	}

	@Override
	public int getColumnCount() {
		return 1;
	}
	
	public String getColumnName(int col) {
		return "Data";
	}

	@Override
	public int getRowCount() {
		return values.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		int i = 0;
		
		for (MyValue value : values) {
			if (i == row) {
				return value.getData();
			}
			
			i++;
		}
		
		return null;
	}

}
