package chord.gui;

import javax.swing.JTable;
import javax.swing.table.TableModel;

public class FingerTable extends JTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public FingerTable(FingerTableModel model) {
		super(model);
	}
	
	public FingerTable() { }

	@Override
	public void setModel(TableModel dataModel) {
		super.setModel(dataModel);
		
		if (getColumnModel().getColumnCount() > 0) {
			getColumnModel().getColumn(0).setPreferredWidth(5);
		}
	}
}
