package com.processor;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.SwingUtilities;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class MainJFrame extends javax.swing.JFrame {
	private JMenuBar jMenuBar;
	private JMenu jMenuFile;
	private JMenu jMenuEdit;
	private JMenuItem jMenuItemAbout;
	private JMenu jMenuHelp;
	private JMenuItem jMenuItemSave;
	private JButton jButtonRRParse;
	private JButton jButtonRRRead;
	private JButton jButtonRROpen;
	private JTextArea jTextAreaRRT;
	private JScrollPane jScrollPaneRRS;
	private JPanel jPanel1RRB;
	private JScrollPane jScrollPaneRDF;
	private JTextArea jTextAreaRDF;
	private JPanel jTabbedPaneMap;
	private JPanel jTabbedPaneRDF;
	private JPanel jTabbedPaneRDB;
	private JTabbedPane jTabbedPane;
	private JMenuItem jMenuItemExit;

	private JButton button;
	
	private ConnectDB diagCDB;
	
	public static Properties msg;
	
	private JFileChooser file;
	
	private MainJFrame cur;
	
	private processor p;


	/**
	* Auto-generated main method to display this JFrame
	*/
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MainJFrame inst = new MainJFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
				msg = new Properties();
				//processor
				//File file = new File("D:/Project-R2RML processor/Processor/TTL files/TC0012a.ttl");
				//processor p= new processor(null, file);
				//p.DoR2RMap();
			}
		});
	}
	
	public MainJFrame() {
		super();
		cur = this;
		initGUI();
	}
	
	private void initGUI() {
		try {
			diagCDB = new ConnectDB(this);
			file = new JFileChooser();
			
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			{
				jTabbedPane = new JTabbedPane();
				getContentPane().add(jTabbedPane, BorderLayout.CENTER);
				{
					jTabbedPaneRDB = new JPanel();
					button = new JButton("Connect SQL DB");
					button.addActionListener(new ConButtonClick());
					jTabbedPaneRDB.add(button);
					jTabbedPane.addTab("RDB", null, jTabbedPaneRDB, null);
				}
				{
					jTabbedPaneMap = new JPanel();
					jTabbedPane.addTab("R2RML Mapping", null, jTabbedPaneMap, null);
					{
						jPanel1RRB = new JPanel();
						//jTabbedPaneMap.add(jPanel1RRB);
						jTabbedPaneMap.add(jPanel1RRB, BorderLayout.NORTH);
						jPanel1RRB.setPreferredSize(new java.awt.Dimension(775, 60));
						{
							jButtonRROpen = new JButton();
							jPanel1RRB.add(jButtonRROpen);
							jButtonRROpen.setText("Open");
							jButtonRROpen.setPreferredSize(new java.awt.Dimension(80, 35));
							
							jButtonRROpen.addActionListener(new RROpenClick());
						}
						{
							jButtonRRRead = new JButton();
							jPanel1RRB.add(jButtonRRRead);
							jButtonRRRead.setText("Read");
							jButtonRRRead.setPreferredSize(new java.awt.Dimension(80, 35));
							
							jButtonRRRead.addActionListener(new RRReadClick());
						}
						{
							jButtonRRParse = new JButton();
							jPanel1RRB.add(jButtonRRParse);
							jButtonRRParse.setText("Parse");
							jButtonRRParse.setPreferredSize(new java.awt.Dimension(80, 35));
							
							jButtonRRParse.addActionListener(new RRParseClick());
						}
					}
					{
						jScrollPaneRRS = new JScrollPane();
						//jTabbedPaneMap.add(jScrollPaneRRS);
						jTabbedPaneMap.add(jScrollPaneRRS, BorderLayout.WEST);
						jScrollPaneRRS.setPreferredSize(new java.awt.Dimension(775, 435));
						{
							jTextAreaRRT = new JTextArea();
							jScrollPaneRRS.setViewportView(jTextAreaRRT);
						}
					}
				}
				{
					jTabbedPaneRDF = new JPanel();
					jTabbedPane.addTab("RDF", null, jTabbedPaneRDF, null);
					{
						jTextAreaRDF = new JTextArea();
						//jTabbedPaneRDF.add(jTextAreaRDF);						
					}
					{
						//jScrollPaneRDF = new JScrollPane(jTextAreaRDF);
						//jScrollPaneRDF.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
						//jScrollPaneRDF.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
						jScrollPaneRDF = new JScrollPane();
						jScrollPaneRDF.setViewportView(jTextAreaRDF);
						jScrollPaneRDF.setPreferredSize(new java.awt.Dimension(775, 500));
						jTabbedPaneRDF.add(jScrollPaneRDF);
					}
				}
			}
			{
				jMenuBar = new JMenuBar();
				setJMenuBar(jMenuBar);
				{
					jMenuFile = new JMenu();
					jMenuBar.add(jMenuFile);
					jMenuFile.setText("File");
					{
						jMenuItemExit = new JMenuItem();
						jMenuFile.add(jMenuItemExit);
						jMenuItemExit.setText("Exit");
						jMenuItemExit.addActionListener(new MenuExitClick());
					}
				}
				{
					jMenuEdit = new JMenu();
					jMenuBar.add(jMenuEdit);
					jMenuEdit.setText("Edit");
					{
						jMenuItemSave = new JMenuItem();
						jMenuEdit.add(jMenuItemSave);
						jMenuItemSave.setText("Save");
						jMenuItemSave.addActionListener(new MenuSaveClick());
					}
				}
				{
					jMenuHelp = new JMenu();
					jMenuBar.add(jMenuHelp);
					jMenuHelp.setText("Help");
					{
						jMenuItemAbout = new JMenuItem();
						jMenuHelp.add(jMenuItemAbout);
						jMenuItemAbout.setText("About");
						jMenuItemAbout.addActionListener(new MenuAboutClick());
					}
				}
			}
			pack();
			setSize(800, 600);
		} catch (Exception e) {
		    //add your error handling code here
			e.printStackTrace();
		}
	}
	
	// handling events
	
	//RDB "connect" button
	private class ConButtonClick implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			diagCDB.setLocation(600, 300);
			diagCDB.setVisible(true);
			
		}
		
	}
	
	// R2RML "open" button 
	private class RROpenClick implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			file.showOpenDialog(null);
			Properties.fl = file.getSelectedFile();
		}
		
	}
	
	// R2RML "read" button 
		private class RRReadClick implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (Properties.fl != null){
					byte[] buf = new byte[(int)Properties.fl.length()];
					try {
						FileInputStream fls = new FileInputStream(Properties.fl);
						fls.read(buf);
						fls.close();
						jTextAreaRRT.setText(new String(buf));
						
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}
				
			}
			
		}
		// R2RML "read" button 
		private class RRParseClick implements ActionListener{

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						if(Properties.fl == null || Properties.mCon == null){
							if (Properties.fl == null){
								JOptionPane.showMessageDialog(cur, "Please read a TTL file.", "Info", JOptionPane.CLOSED_OPTION);

							}
							if (Properties.mCon == null){
								JOptionPane.showMessageDialog(cur, "Please connect to a DB.", "Info", JOptionPane.CLOSED_OPTION);
							}
						}
						else {
							 p= new processor(Properties.mCon, Properties.fl);
							 List<String> tmp = new ArrayList<String>();
							 tmp = p.DoR2RMap();
							 String txt = "";
							 if(tmp != null){
								 Iterator<String> it = tmp.iterator();
								 while(it.hasNext()){
									 txt = txt + it.next();
								 }
								 jTextAreaRDF.setText(txt);
							 }
							 else{
								 jTextAreaRDF.setText("There is nothing!");
							 }
							 
							 jTabbedPane.setSelectedIndex(2);
							 
						}
						//debug	
						//jTabbedPane.setSelectedIndex(2);
					}
		}
		
		private class MenuExitClick implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				cur.dispose();
				
			}
			
		}
		private class MenuSaveClick implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String strRDF = jTextAreaRDF.getText();
				if(strRDF != ""){
					file.showSaveDialog(null);
					File fout = file.getSelectedFile();
					try {
						FileOutputStream fouts = new FileOutputStream(fout);
						byte[] b = strRDF.getBytes();
						fouts.write(b);
						fouts.close();
					
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				}
				else {
					JOptionPane.showMessageDialog(cur, "No RDF Content to be Save.", "Save", JOptionPane.CLOSED_OPTION);
				}
			}
			
		}
		private class MenuAboutClick implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JOptionPane.showMessageDialog(cur, "R2R Mapping Processor V1.0", "About", JOptionPane.CLOSED_OPTION);
			}
			
		}
		
		public void SetTabPane (int index){
			 jTabbedPane.setSelectedIndex(index);
		}
					

}
