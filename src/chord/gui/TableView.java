package chord.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import chord.data.ChordNode;

public class TableView extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private NodesTable nodesTable;
	private static FingerTable fingerTable;

	public TableView(String title, TreeMap<Long, ChordNode> nodes) {
		super(title);
		getContentPane().setPreferredSize(new Dimension(900, 600));
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(150, 2));
		nodesTable = new NodesTable(new NodesTableModel(nodes));
		scrollPane.setViewportView(nodesTable);
		getContentPane().add(scrollPane, BorderLayout.WEST);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		fingerTable = new FingerTable();
		scrollPane_1.setViewportView(fingerTable);
		getContentPane().add(scrollPane_1, BorderLayout.CENTER);
	}

	
	public NodesTable getNodesTable() {
		return nodesTable;
	}
	
	public static FingerTable getFingerTable() {
		return fingerTable;
	}
}
