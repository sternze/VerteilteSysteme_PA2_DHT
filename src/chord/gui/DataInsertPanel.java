package chord.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import chord.MyChord;
import chord.data.MyValue;
import chord.interfaces.IChordNode;
import chord.interfaces.IMyChord;
import chord.interfaces.INotifyableComponent;

public class DataInsertPanel extends JPanel implements INotifyableComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Set<MyValue> data;
	
	private JTextField tfValue;
	private JTextField tfIP;
	private JTextField tfPort;
	private JTextField tfServiceName;
	private JRadioButton rdbSingleInsert;
	private JRadioButton rdbListInsert;
	private final JFileChooser fc;

	private Component parent;
	
	public DataInsertPanel(final Component parent) {
		this.parent = parent;
		this.fc = new JFileChooser();
		
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
				
				if (rdbSingleInsert.isSelected()) {
					try {
						contact.insertData(myValue);
						
						for (String word : words) {
							myValue = new MyValue(word.getBytes(), MyChord.KEYLENGTH);
							myValue.setData(value.getBytes());
							contact.insertData(myValue);
						}
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (rdbListInsert.isSelected()) {
					HashSet<MyValue> values = new HashSet<MyValue>();
					values.add(myValue);
					
					for (String word : words) {
						myValue = new MyValue(word.getBytes(), MyChord.KEYLENGTH);
						myValue.setData(value.getBytes());
						values.add(myValue);
					}
					
					try {
						contact.insertData(values);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				contact = null;
			}
		});
		
		JPanel panel_1 = new JPanel();
		add(panel_1);
		
		JButton btnChooseFile = new JButton("Choose File");
		panel_1.add(btnChooseFile);
		
		rdbSingleInsert = new JRadioButton("single insert");
		panel_1.add(rdbSingleInsert);
		
		rdbListInsert = new JRadioButton("list insert");
		rdbListInsert.setSelected(true);
		
		ButtonGroup bg = new ButtonGroup();
		bg.add(rdbListInsert);
		bg.add(rdbSingleInsert);
		
		panel_1.add(rdbListInsert);
		btnChooseFile.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int returnVal = fc.showOpenDialog(parent);

		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
		            int readIntervall = 100;
		            int round = 0;
		            boolean endOfFile = false;
		            long size = 0;
		            
		            try {
		            	while (!endOfFile && round * readIntervall < 100) {
		            		data = new HashSet<MyValue>();
							InputStream    inputStream = new FileInputStream(file);
							BufferedReader bufferdReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));;
				            String         line;
				            MyValue value;
				            int currentLine = 0;
				            bufferdReader.readLine();	// skip the first line
				            
				            for (int i = 0; i < round * readIntervall; i++) { // go to start of the next intervall
				            	bufferdReader.readLine();
				            }
				            
				            while ((line = bufferdReader.readLine()) != null && currentLine < readIntervall) {
				                String[] values = line.split("|");
				                
				                value = new MyValue(line.getBytes(), MyChord.KEYLENGTH);
				                data.add(value);
				                
				                for (String s : values) {
				                	String[] words = s.split(" ");
				                	
				                	value = new MyValue(s.getBytes(), MyChord.KEYLENGTH);
				                	value.setData(line.getBytes());
				                	data.add(value);
				                	
				                	for (String word : words) {
				                		value = new MyValue(word.getBytes(), MyChord.KEYLENGTH);
				                		value.setData(line.getBytes());
				                		data.add(value);
				                	}
				                }
				                
				                currentLine++;
				            }
				            
				            if (line == null) {
				            	endOfFile = true;
				            }
	
				            size += data.size();
				            System.out.println("round " + round + " done, size: " + size);
				            
				            round++; // round done
	
				            bufferdReader.close();
				            bufferdReader = null;
				            inputStream = null;
				            
				            Thread insertThread = new Thread() {

								@Override
								public void run() {
									Set<MyValue> data = DataInsertPanel.data;
									
									 IMyChord contact = getContact();
						            	
					            	try {
										for (MyValue v : data) {
											contact.insertData(v);
										}
									} catch (RemoteException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
					            	
					            	contact = null;
								}
				            	
				            };
				            
				            insertThread.start();
				            /*
				            if (rdbListInsert.isSelected()) {
				            	IMyChord contact = getContact();
				            
				            	try {
									contact.insertData(data, cbMakeReplication.isSelected());
								} catch (RemoteException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
				            	
				            	contact = null;
				            } else if (rdbSingleInsert.isSelected()) {
				            	
				            }*/
		            	}
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
		            
		            System.out.println("finished after " + round + " rounds, size: " + size);
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
				// do nothing
			}
		}
	}
	
	
}
