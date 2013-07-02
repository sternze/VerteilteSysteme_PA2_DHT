package chord.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import chord.MyChord;
import chord.data.ChordNode;
import chord.data.MyValue;
import chord.interfaces.IMyChord;
import chord.interfaces.INotifyableComponent;

public class DataInsertPanel extends JPanel implements INotifyableComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField tfValue;
	private JTextField tfIP;
	private JTextField tfPort;
	private JTextField tfServiceName;

	private Component parent;
	
	public DataInsertPanel(Component parent) {
		this.parent = parent;
		
		setBorder(new LineBorder(new Color(0, 0, 0)));
		setPreferredSize(new Dimension(450, 105)); 
		setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel panel_3 = new JPanel();
		add(panel_3);
		
		JLabel label = new JLabel("IP:");
		panel_3.add(label);
		
		tfIP = new JTextField();
		tfIP.setColumns(10);
		panel_3.add(tfIP);
		
		JLabel label_1 = new JLabel("Port:");
		panel_3.add(label_1);
		
		tfPort = new JTextField();
		tfPort.setColumns(4);
		panel_3.add(tfPort);
		
		JLabel lblServicename = new JLabel("ServiceName:");
		panel_3.add(lblServicename);
		
		tfServiceName = new JTextField();
		panel_3.add(tfServiceName);
		tfServiceName.setColumns(10);
		
		JPanel panel = new JPanel();
		add(panel);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel lblNewLabel = new JLabel("Value:");
		panel.add(lblNewLabel);
		
		tfValue = new JTextField();
		tfValue.setPreferredSize(new Dimension(50, 22));
		panel.add(tfValue);
		tfValue.setColumns(20);
		
		JButton btnInsert = new JButton("Insert");
		panel.add(btnInsert);
		btnInsert.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				IMyChord contact = getContact();
				String value = tfValue.getText();
				String[] words = value.split(" ");
				MyValue myValue = new MyValue(value.getBytes(), MyChord.KEYLENGTH);
				
				try {
					contact.insertData(myValue, true);
					
					for (String word : words) {
						myValue = new MyValue(word.getBytes(), MyChord.KEYLENGTH);
						myValue.setData(value.getBytes());
						contact.insertData(myValue, true);
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				contact = null;
			}
		});
		
		JPanel panel_1 = new JPanel();
		add(panel_1);
		
		JButton btnChooseFile = new JButton("Choose File");
		panel_1.add(btnChooseFile);
		btnChooseFile.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
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
			ChordNode current = nt.getSelectedValue();
			tfIP.setText(current.getIp());
			tfPort.setText(current.getPort() + "");
			tfServiceName.setText(current.getServiceName());
		}
	}
	
	
}
