package chord.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import chord.data.ChordNode;

public class NodesTable extends JTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static NodesTableModel myModel;
	private static int selectedIndex;
	
	public NodesTable () { }
	
	public NodesTable(final NodesTableModel model) {
		super(model);
		NodesTable.myModel = model;
		
		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				NodesTable.selectedIndex = rowAtPoint(e.getPoint());
				long id = (long)myModel.getValueAt(selectedIndex, 0);
				ChordNode node = myModel.getChordNode(id);
				TableView.getFingerTable().setModel(new FingerTableModel(node.getFingerTable()));
				TableView.getDataTable().setModel(new DataTableModel(node.getEntries()));
				super.mouseClicked(e);
			}
			
		});
	}

	@Override
	public void setModel(TableModel dataModel) {
		NodesTable.myModel = (NodesTableModel) dataModel;
		super.setModel((NodesTableModel)myModel);
		
		if (myModel.getRowCount() > 0 && selectedIndex >= 0) {
			this.setRowSelectionInterval(selectedIndex, selectedIndex);
		}
	}
}
