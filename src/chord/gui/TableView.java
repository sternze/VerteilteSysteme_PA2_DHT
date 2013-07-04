package chord.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import chord.interfaces.IChordNode;

public class TableView extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private NodesTable nodesTable;
	private static FingerTable fingerTable;
	private static DataTable dataTable;
	private static ValueTable valueTable;
	private static DataInsertPanel dataInsertPanel;
	private static DataQueryPanel dataQueryPanel;

	public TableView(String title, TreeMap<Long, IChordNode> nodes) {
		super(title);

		getContentPane().setPreferredSize(new Dimension(900, 600));
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPaneNodesTable = new JScrollPane();
		scrollPaneNodesTable.setPreferredSize(new Dimension(150, 2));
		nodesTable = new NodesTable(new NodesTableModel(nodes));
		dataInsertPanel = new DataInsertPanel(nodesTable);
		dataQueryPanel = new DataQueryPanel(nodesTable);
		nodesTable.subscribe(dataInsertPanel);
		nodesTable.subscribe(dataQueryPanel);
		scrollPaneNodesTable.setViewportView(nodesTable);
		getContentPane().add(scrollPaneNodesTable, BorderLayout.WEST);
		
		
		
		JScrollPane scrollPaneFingerTable = new JScrollPane();
		fingerTable = new FingerTable();
		scrollPaneFingerTable.setViewportView(fingerTable);
		
		JScrollPane scrollPaneDataTable = new JScrollPane();
		scrollPaneDataTable.setPreferredSize(new Dimension(150, 2));
		dataTable = new DataTable();
		scrollPaneDataTable.setViewportView(dataTable);
		
		JScrollPane scrollPaneValueTable = new JScrollPane();
		valueTable = new ValueTable();
		scrollPaneValueTable.setViewportView(valueTable);
		
		JPanel panelDataTab = new JPanel();
		panelDataTab.setLayout(new BorderLayout(0, 0));
		panelDataTab.add(scrollPaneDataTable, BorderLayout.WEST);
		panelDataTab.add(scrollPaneValueTable, BorderLayout.CENTER);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("FingerTable", scrollPaneFingerTable);
		tabbedPane.addTab("Data", panelDataTab);

		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new GridLayout(1, 2));
		controlPanel.add(dataInsertPanel);
		controlPanel.add(dataQueryPanel);
		
		getContentPane().add(controlPanel, BorderLayout.NORTH);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
	}

	
	public NodesTable getNodesTable() {
		return nodesTable;
	}
	
	public static FingerTable getFingerTable() {
		return fingerTable;
	}
	
	public static DataTable getDataTable() {
		return dataTable;
	}
	
	public static ValueTable getValueTable() {
		return valueTable;
	}
}
