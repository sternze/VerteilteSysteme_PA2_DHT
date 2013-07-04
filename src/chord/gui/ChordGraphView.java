package chord.gui;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.apache.commons.collections15.Transformer;

import chord.data.ChordNode;
import chord.interfaces.IChordGraphView;
import chord.interfaces.IChordNode;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

public class ChordGraphView extends UnicastRemoteObject implements IChordGraphView {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String GRAPH_VIEW_SERVICE_NAME = "Graph_View_Service_Name";
	public static final int GRAPH_VIEW_PORT = 7998;
	
	private static JFrame graphView;
	private static TableView tableView;
	private static JMenuBar menuBar;
	private static JMenu view;
	private static JMenuItem miTableView;
	
	private static BasicVisualizationServer<Long,String> vv;
	private static CircleLayout<Long, String> layout;	
	private static Graph<Long, String> g;
	private static TreeMap<Long, IChordNode> nodes;
	private static Transformer<String, Stroke> edgePaint;
	private static Transformer<Long, String> vertexLabel;
	
    /** Creates a new instance of SimpleGraphView */
    public ChordGraphView() throws RemoteException {
        // Graph<V, E> where V is the type of the vertices and E is the type of the edges
        g = new DirectedSparseMultigraph<Long, String>();   
        nodes = new TreeMap<Long, IChordNode>();
    }
    

    public static void main(String[] args) {
		try {
			ChordGraphView sgv = new ChordGraphView();
			
			Registry reg = null;
			
			try {
				reg = LocateRegistry.createRegistry(GRAPH_VIEW_PORT);
				System.out.println(new Date() + " Registry established");
			} catch (IllegalArgumentException ae) {
				System.out.println(new Date() + " No ports avilable");
				System.exit(0);
			} catch (RemoteException e) {
				try {
					reg = LocateRegistry.getRegistry(); 
				} catch (RemoteException e2) {
					System.out.println(new Date() + " Registry could not be established" + e);
					System.exit(0);
				}
			}
			
			reg.rebind(GRAPH_VIEW_SERVICE_NAME, sgv);       // Bind object
			
	        graphView = new JFrame("ChordGraphView");
	        graphView.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        graphView.setVisible(true);  
	        
	        menuBar = new JMenuBar();
	        view = new JMenu("View");
	        miTableView = new JMenuItem("Table view");
	        
	        view.add(miTableView);
	        menuBar.add(view);
	        graphView.setJMenuBar(menuBar);
	        
	        miTableView.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if (!tableView.isVisible()) {
						tableView = new TableView("TableView", nodes);
				        tableView.setVisible(true);
				        tableView.pack();
					}
				}
			});
	        
	        tableView = new TableView("TableView", nodes);
	        tableView.setVisible(true);
	        tableView.pack();
	        
	        float dash[] = { 10.0f };
			final Stroke edgeStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
			
	        edgePaint = new Transformer<String, Stroke>() {
				@Override
				public Stroke transform(String s) {
					return s.contains("successor") ? new BasicStroke() : edgeStroke;
				}
	 		};
	 		
	 		vertexLabel = new Transformer<Long, String>() {

				@Override
				public String transform(Long id) {
					try {
						return nodes.get(id).getIdAndEntryCount();
					}catch (Exception ex) {
						// do nothing
					}
					return null;
				}
	 		};
	 		
	 		layout = new CircleLayout<Long, String>(g);
			layout.setVertexOrder(new ArrayList<Long>(nodes.keySet()));
	        layout.setSize(new Dimension(1000,800)); // sets the initial size of the layout space
	        // The BasicVisualizationServer<V,E> is parameterized by the vertex and edge types
	        
	        vv = new BasicVisualizationServer<Long, String>(layout);
	        vv.setPreferredSize(new Dimension(1000,800)); //Sets the viewing area size
	        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Long>());
	
	 		vv.getRenderContext().setEdgeStrokeTransformer(edgePaint);
	 		vv.getRenderContext().setVertexLabelTransformer(vertexLabel);
	        
	        graphView.getContentPane().remove(vv); 
	        graphView.getContentPane().add(vv); 
	        graphView.pack();
	 		
	        Thread repaint = new Thread() {
	        	@Override
				public void run() {
	        		while (true) {
	        			repaint();
	        			
	        			try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	        		}
	        	}
	        };
	        
	        repaint.start();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}      
    }
 
    public void updateNodes(IChordNode node) {
    	try {
	    	synchronized (nodes) {
	    		if (nodes.containsKey(node.getIdentifier())) {
	    			nodes.remove(node.getIdentifier());
	    		}
	    		       	
	    		nodes.put(node.getIdentifier(), node);
	    		     	
	    		if (tableView != null && tableView.isVisible()) {
	    			tableView.getNodesTable().setModel(new NodesTableModel(nodes));
	    		}
			}
    	} catch (Exception ex) {
    		// do nothing
    	}
    }
    
	private static void repaint() {
		System.gc();
		
		synchronized (nodes) {
			
			//g = new DirectedSparseMultigraph<Long, String>();
			if (nodes.size() > 0) {
				Collection<Long> vertices = (Collection<Long>) g.getVertices();
				
				for (Long id : vertices) {
					if (!nodes.containsKey(id)) {
						g.removeVertex(id);
						g.removeEdge(id + "successor");
						g.removeEdge(id + "predecessor");
					}
				}
				
				try {
					for (IChordNode node: nodes.values()) {
						if (!g.containsVertex(node.getIdentifier())) {
							g.addVertex(node.getIdentifier());
						}
					}
					for (IChordNode node: nodes.values()) {
						if (node.getSuccessor() != null && g.containsVertex(node.getSuccessor().getIdentifier())) {
							g.removeEdge(node.getIdentifier() + "successor");
							g.addEdge(node.getIdentifier() + "successor", node.getIdentifier(), node.getSuccessor().getIdentifier());
						}
						
						if (node.getPredecessor() != null && g.containsVertex(node.getPredecessor().getIdentifier())) {
							g.removeEdge(node.getIdentifier() + "predecessor");
							g.addEdge(node.getIdentifier() + "predecessor", node.getIdentifier(), node.getPredecessor().getIdentifier());
						}
					}
				} catch (Exception ex) {
					// do nothing
				}
				
				layout = new CircleLayout<Long, String>(g);
				layout.setVertexOrder(new ArrayList<Long>(nodes.keySet()));
		        layout.setSize(new Dimension(1000,800)); // sets the initial size of the layout space
		        // The BasicVisualizationServer<V,E> is parameterized by the vertex and edge types
		        
		        vv = new BasicVisualizationServer<Long, String>(layout);
		        vv.setPreferredSize(new Dimension(1000,800)); //Sets the viewing area size
		        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Long>());
		
		 		vv.getRenderContext().setEdgeStrokeTransformer(edgePaint);
		 		vv.getRenderContext().setVertexLabelTransformer(vertexLabel);
		        
		        graphView.getContentPane().remove(vv); 
		        graphView.getContentPane().add(vv); 
		        graphView.pack();
			}
		}
	}


	@Override
	public void registerChordNode(IChordNode node) throws RemoteException {
		updateNodes(node);
	}
}
