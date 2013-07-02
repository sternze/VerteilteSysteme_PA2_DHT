package chord.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import chord.data.MyValue;

public class DataTable extends JTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static DataTableModel myModel;
	private static int selectedIndex;
	
	public DataTable() {
		super();
	}
	
	public DataTable(DataTableModel model) {
		super(model);
		DataTable.myModel = model;
		
		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				DataTable.selectedIndex = rowAtPoint(e.getPoint());
				long id = (long)myModel.getValueAt(selectedIndex, 0);
				Set<MyValue> values = myModel.getValues(id);
				TableView.getValueTable().setModel(new ValueTableModel(values));
				super.mouseClicked(e);
			}
			
		});
	}

	@Override
	public void setModel(TableModel dataModel) {
		if (myModel != null && myModel.getClass() == DataTableModel.class) {
			DataTable.myModel = (DataTableModel) dataModel;
			super.setModel((DataTableModel)myModel);
			
			if (myModel.getRowCount() > 0 && selectedIndex >= 0) {
				this.setRowSelectionInterval(selectedIndex, selectedIndex);
			}
		} else {
			super.setModel(dataModel);
		}
	}

}
