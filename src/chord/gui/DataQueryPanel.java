package chord.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import chord.MyChord;
import chord.data.MyValue;
import chord.interfaces.IChordNode;
import chord.interfaces.IMyChord;
import chord.interfaces.INotifyableComponent;

public class DataQueryPanel extends JPanel implements INotifyableComponent  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField tfQuery;
	private JTextField tfIP;
	private JTextField tfPort;
	private JTextField tfServiceName;

	private Component parent;
	
	public DataQueryPanel(Component parent) {
		this.parent = parent;
		
		setBorder(new LineBorder(new Color(0, 0, 0)));
		setPreferredSize(new Dimension(450, 70)); 
		setLayout(new GridLayout(2, 0, 0, 0));
		
		JPanel panel_1 = new JPanel();
		add(panel_1);
		
		JLabel label = new JLabel("IP:");
		panel_1.add(label);
		
		tfIP = new JTextField();
		tfIP.setColumns(10);
		panel_1.add(tfIP);
		
		JLabel label_1 = new JLabel("Port:");
		panel_1.add(label_1);
		
		tfPort = new JTextField();
		tfPort.setColumns(4);
		panel_1.add(tfPort);
		
		JLabel label_2 = new JLabel("ServiceName:");
		panel_1.add(label_2);
		
		tfServiceName = new JTextField();
		tfServiceName.setColumns(10);
		panel_1.add(tfServiceName);
		
		JPanel panel = new JPanel();
		add(panel);
		
		JLabel lblQuery = new JLabel("Search for:");
		panel.add(lblQuery);
		
		tfQuery = new JTextField();
		panel.add(tfQuery);
		tfQuery.setColumns(20);
		
		JButton btnQuerys = new JButton("Query");
		panel.add(btnQuerys);
		btnQuerys.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				IMyChord contact = getContact();
				String q = tfQuery.getText();
				MyValue query = new MyValue(q.getBytes(), MyChord.KEYLENGTH);
				Set<MyValue> results = null;
				
				try {
					results = contact.query(query.getId());
					
					if (results != null && results.size() > 0) {
						for (MyValue r : results) {
							System.out.println(new String(r.getData()));
						}
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	private IMyChord getContact() {
		try {
			IMyChord contact = (IMyChord) Naming.lookup("rmi://" + tfIP.getText() + ":" + tfPort.getText() + "/" + tfServiceName.getText());
			
			return contact;
		} catch (MalformedURLException | RemoteException | NotBoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}

	@Override
	public void notifyComponent() {
		if (parent.getClass() == NodesTable.class) {
			NodesTable nt = (NodesTable) parent;
			IChordNode current = nt.getSelectedValue();
			try {
				tfIP.setText(current.getIp());
				tfPort.setText(current.getPort() + "");
				tfServiceName.setText(current.getServiceName());
			} catch (Exception ex) {
				// do noting
			}
		}
	}
}
