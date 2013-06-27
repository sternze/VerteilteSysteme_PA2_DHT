package chord.gui;

import java.util.TreeMap;

import javax.swing.table.AbstractTableModel;

import chord.data.ChordNode;

public class NodesTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private TreeMap<Long, ChordNode> nodes;
	
	public NodesTableModel() { }
	
	public NodesTableModel(TreeMap<Long, ChordNode> nodes) {
		this.nodes = nodes;
	}
	
	public String getColumnName(int col) {
		return "Identifier";
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public int getRowCount() {
		return nodes.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		int i = 0;
		
		for (ChordNode node : nodes.values()) {
			if (i == row) {
				return node.getIdentifier();
			}
			
			i++;
		}
		
		return null;
	}
	
	public ChordNode getChordNode(long id) {
		return nodes.get(id);
	}

}
