package chord.gui;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Stroke;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import chord.data.ChordNode;
import chord.interfaces.IChordGraphView;
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
	
	private static BasicVisualizationServer<Long,String> vv;
	private static JFrame graphView;
	private static TableView tableView;
	private static CircleLayout<Long, String> layout;	
	private static Graph<Long, String> g;
	private static TreeMap<Long, ChordNode> nodes;
	
    /** Creates a new instance of SimpleGraphView */
    public ChordGraphView() throws RemoteException {
        // Graph<V, E> where V is the type of the vertices and E is the type of the edges
        g = new DirectedSparseMultigraph<Long, String>();   
        nodes = new TreeMap<Long, ChordNode>();
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
	        
	        tableView = new TableView("TableView", nodes);
	        tableView.setVisible(true);
	        tableView.pack();
	        
	        repaint();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}      
    }
 
    public void updateNodes(ChordNode node) {
    	synchronized (nodes) {
    		if (nodes.containsKey(node.getIdentifier())) {
        		nodes.remove(node.getIdentifier());
        	}
        	
        	nodes.put(node.getIdentifier(), node);
        	
        	tableView.getNodesTable().setModel(new NodesTableModel(nodes));
		}
    }

	@Override
	public void pushStatus(ChordNode node) throws RemoteException {
		System.out.println(new Date() + " got push from " + node.getIdentifier());
		
		updateNodes(node);

		repaint();
	}
	
	private static void repaint() {
		
		synchronized (nodes) {
			g = new DirectedSparseMultigraph<Long, String>();
			
			for (ChordNode node: nodes.values()) {
				if (!g.containsVertex(node.getIdentifier())) {
					g.addVertex(node.getIdentifier());
					//nodes.put(node.getIdentifier(), node);
				}
			}
			
			for (ChordNode node: nodes.values()) {
				if (node.getSuccessor() != null && g.containsVertex(node.getSuccessor().getIdentifier())) {
					g.removeEdge(node.getIdentifier() + "successor");
					g.addEdge(node.getIdentifier() + "successor", node.getIdentifier(), node.getSuccessor().getIdentifier());
				}
				
				if (node.getPredecessor() != null && g.containsVertex(node.getPredecessor().getIdentifier())) {
					g.removeEdge(node.getIdentifier() + "predecessor");
					g.addEdge(node.getIdentifier() + "predecessor", node.getIdentifier(), node.getPredecessor().getIdentifier());
				}
			}
		}
		
		
		layout = new CircleLayout<Long, String>(g);
		layout.setVertexOrder(new ArrayList<Long>(nodes.keySet()));
        layout.setSize(new Dimension(1000,800)); // sets the initial size of the layout space
        // The BasicVisualizationServer<V,E> is parameterized by the vertex and edge types
        vv = new BasicVisualizationServer<Long, String>(layout);
        vv.setPreferredSize(new Dimension(1000,800)); //Sets the viewing area size
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Long>());
        
        float dash[] = { 10.0f };
		final Stroke edgeStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
        
 		Transformer<String, Stroke> edgePaint = new Transformer<String, Stroke>() {
			@Override
			public Stroke transform(String s) {
				return s.contains("successor") ? new BasicStroke() : edgeStroke;
			}
 		};
 		
 		vv.getRenderContext().setEdgeStrokeTransformer(edgePaint);
        
        graphView.getContentPane().remove(vv); 
        graphView.getContentPane().add(vv); 
        graphView.pack();
	}
}
