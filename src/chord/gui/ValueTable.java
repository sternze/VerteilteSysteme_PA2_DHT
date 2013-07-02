package chord.gui;

import javax.swing.JTable;
import javax.swing.table.TableModel;

public class ValueTable extends JTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ValueTable(ValueTableModel model) {
		super(model);
	}
	
	public ValueTable() { }

	@Override
	public void setModel(TableModel dataModel) {
		super.setModel(dataModel);
		
		if (getColumnModel().getColumnCount() > 0) {
			getColumnModel().getColumn(0).setPreferredWidth(5);
		}
	}
}
