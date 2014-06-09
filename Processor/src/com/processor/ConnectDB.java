package com.processor;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
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
public class ConnectDB extends javax.swing.JDialog {
	private JPanel jPanelText;
	private JPanel jPanelLabel;
	private JTextField jTFDriver;
	private JTextField jTFUrl;
	private JLabel jLabelPW;
	private JLabel jLabelUN;
	private JLabel jLabelURL;
	private JLabel jLabelDriver;
	private JButton jButtonCancel;
	private JButton jButtonOK;
	private JTextField jTFPassword;
	private JTextField jTFName;

	/**
	* Auto-generated main method to display this JDialog
	*/
	
	MainJFrame parent;
	javax.swing.JDialog cur;
		
	public ConnectDB(JFrame frame) {
		super(frame);
		parent = (MainJFrame) frame;
		cur = this;
		initGUI();
	}
	
	private void initGUI() {
		try {
			{
				jPanelText = new JPanel();
				getContentPane().add(jPanelText, BorderLayout.EAST);
				jPanelText.setPreferredSize(new java.awt.Dimension(343, 362));
			}
			{
				jPanelLabel = new JPanel();
				getContentPane().add(jPanelLabel, BorderLayout.WEST);
				jPanelLabel.setPreferredSize(new java.awt.Dimension(211, 162));
				{
					jLabelDriver = new JLabel();
					jPanelLabel.add(jLabelDriver);
					jLabelDriver.setText("DB Driver");
					jLabelDriver.setPreferredSize(new java.awt.Dimension(190, 24));
					jLabelDriver.setHorizontalTextPosition(SwingConstants.RIGHT);
					jLabelDriver.setHorizontalAlignment(SwingConstants.RIGHT);
				}
				{
					jLabelURL = new JLabel();
					jPanelLabel.add(jLabelURL);
					jLabelURL.setText("DB URL");
					jLabelURL.setPreferredSize(new java.awt.Dimension(190, 24));
					jLabelURL.setHorizontalAlignment(SwingConstants.RIGHT);
				}
				{
					jLabelUN = new JLabel();
					jPanelLabel.add(jLabelUN);
					jLabelUN.setText("UserName");
					jLabelUN.setPreferredSize(new java.awt.Dimension(190, 24));
					jLabelUN.setHorizontalAlignment(SwingConstants.RIGHT);
				}
				{
					jLabelPW = new JLabel();
					jPanelLabel.add(jLabelPW);
					jLabelPW.setText("PassWord");
					jLabelPW.setHorizontalAlignment(SwingConstants.RIGHT);
					jLabelPW.setPreferredSize(new java.awt.Dimension(190, 24));
				}
			}
			{
				jTFDriver = new JTextField();
				//getContentPane().add(jTFDriver, BorderLayout.CENTER);
				jPanelText.add(jTFDriver);
				jTFDriver.setText("com.mysql.jdbc.Driver");
				jTFDriver.setPreferredSize(new java.awt.Dimension(340, 24));
			}
			{
				jTFUrl = new JTextField();
				jPanelText.add(jTFUrl);
				jTFUrl.setText("jdbc:mysql://localhost:3306/d000");
				jTFUrl.setPreferredSize(new java.awt.Dimension(340, 24));
			}
			{
				jTFName = new JTextField();
				jPanelText.add(jTFName);
				jTFName.setText("root");
				jTFName.setPreferredSize(new java.awt.Dimension(340, 24));
			}
			{
				jTFPassword = new JTextField();
				jPanelText.add(jTFPassword);
				jTFPassword.setText("123456");
				jTFPassword.setPreferredSize(new java.awt.Dimension(340, 24));
			}
			{
				jButtonOK = new JButton();
				jPanelText.add(jButtonOK);
				jButtonOK.setText("OK");
				jButtonOK.addActionListener(new OKButtonClick());
				jButtonOK.setPreferredSize(new java.awt.Dimension(80, 24));
			}
			{
				jButtonCancel = new JButton();
				jPanelText.add(jButtonCancel);
				jButtonCancel.setText("Cancel");
				jButtonCancel.addActionListener(new CancelButtonClick());
				jButtonCancel.setPreferredSize(new java.awt.Dimension(80, 24));
			}
			{
				this.setSize(550, 140);
			}
			setSize(600, 200);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//listeners
	private class OKButtonClick implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			Properties.mDBDriver = jTFDriver.getText();
			Properties.mDBUrl = jTFUrl.getText();
			Properties.mUsername = jTFName.getText();
			Properties.mPassword = jTFPassword.getText();
			
			//debug
			System.out.println(Properties.mDBDriver);
			System.out.println(Properties.mDBUrl);
			System.out.println(Properties.mPassword);
			System.out.println(Properties.mUsername);
			
			//Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver");
			//con=DriverManager.getConnection(SQLUrl, LoginName, LoginPwd);
			try {
				Class.forName(Properties.mDBDriver);
				Properties.mCon = DriverManager.getConnection(Properties.mDBUrl, Properties.mUsername, Properties.mPassword);
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				JOptionPane.showMessageDialog(cur, e1.toString(), "Warning", JOptionPane.CLOSED_OPTION);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				JOptionPane.showMessageDialog(cur, e1.toString(), "Warning", JOptionPane.CLOSED_OPTION);
			}
			//(MainJFrame)parent.
			parent.SetTabPane(1);
			cur.dispose();
		}
		
	}
	
	private class CancelButtonClick implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			cur.dispose();
		}
		
	}

}
