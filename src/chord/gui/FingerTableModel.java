package chord.gui;

import javax.swing.table.AbstractTableModel;

import chord.data.FingerTableEntry;

public class FingerTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private chord.data.FingerTable fingerTable;
	
	public FingerTableModel() { }
	
	public FingerTableModel(chord.data.FingerTable fingerTable) {
		this.fingerTable = fingerTable;
	}

	@Override
	public int getColumnCount() {
		return 7;
	}
	
	public String getColumnName(int col) {
		switch (col) {
			case 0:
				return "Index";
			case 1:
				return "Start";
			case 2:
				return "Intervall";
			case 3:
				return "Intervall";
			case 4:
				return "Node";
			case 5:
				return "Successor";
			case 6:
				return "Predecessor";
			default:
				return "";
		}
	}

	@Override
	public int getRowCount() {
		return fingerTable.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		FingerTableEntry fte = fingerTable.get(row);
		
		switch (col) {
			case 0:
				return fte.getIndex();
			case 1:
				return fte.getStart();
			case 2:
				return fte.getIntervalLowerRange();
			case 3:
				return fte.getIntervalUpperRange();
			case 4:
				return fte.getNode() != null ? fte.getNode().getIdentifier() : null;
			case 5:
				return fte.getSuccessor() != null ? fte.getSuccessor().getIdentifier() : null;
			case 6:
				return fte.getPredecessor() != null ? fte.getPredecessor().getIdentifier() : null;
			default:
				return null;
		}
	}
}
