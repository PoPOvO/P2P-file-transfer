package com.mec.net.view;

import java.awt.Color;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.mec.net.client.MecClient;
import com.mec.net.core.ICommunicationListener;

public class ClientSwing implements ICommunicationListener{
	private JFrame jfrmMainView;
	private Container container;
	
	private JLabel jlblTopic;
	private JLabel jlblCommand;
	
	private JButton jbtnSend;
	
	private JTextField jtxtCommand;
	private JTextArea jtatMessage;
	
	private JScrollPane jscpMessage;
	
	private MecClient mecClient;
	
	Font normalFont = new Font("微软雅黑", Font.PLAIN, 16);
	Font buttonFont = new Font("微软雅黑", Font.PLAIN, 14);
	
	public ClientSwing() {
		initView();
		reinitView();
		dealAction();
		mecClient = new MecClient() {
			@Override
			public void dealResponseMessage(String message) {
				
			}
		};
		mecClient.addMessageListener(this);
		mecClient.connectionToServer();
	}
	
	private void addMessage(String message) {
		jtatMessage.append(message + "\n");
	}
	
	public void closeView() {
		jfrmMainView.dispose();
	}
	
	public void showView() {
		jfrmMainView.setVisible(true);
	}
	
	private void sendMessage(String message) {
		if(mecClient.isConnected()) {
			if(message.equals("exit") && mecClient.isConnected()) {
				mecClient.sendMessage("exit");
			} else if(!mecClient.isConnected()) {
				addMessage("服务已经停止，请充钱!");
			} else {
				mecClient.sendMessage(message);
			} 
		} else {
			return;
		}
	}
	
	private void dealAction() {
		jtxtCommand.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String message = e.getActionCommand();
				if(message.equals("")) {
					return;
				}
				sendMessage(message);
				jtxtCommand.setText("");
			}
		});
		
		jbtnSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String message = jtxtCommand.getText();
				if(message.equals("")) {
					return;
				}
				sendMessage(message);
				jtxtCommand.setText("");
			}
		});
		
		jfrmMainView.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				mecClient.stopConnection();
				closeView();
			}
		});
	}
	
	private void reinitView() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = (int) screenSize.getWidth();
		int screenHeight = (int) screenSize.getHeight();
		jfrmMainView.setLocation((screenWidth-jfrmMainView.getWidth())/2, 
				(screenHeight-jfrmMainView.getHeight())/2);
	}
	
	private void initView() {
		jfrmMainView = new JFrame("客户端");
		container = jfrmMainView.getContentPane();
		container.setLayout(null);
		jfrmMainView.setSize(700, 500);
		
		jlblTopic = new JLabel("菜鸡客户端");
		jlblTopic.setFont(new Font("微软雅黑", Font.BOLD, 36));
		jlblTopic.setForeground(Color.BLUE);
		jlblTopic.setBounds(490 / 2, 0, 36*(10+2), 36+2);
		container.add(jlblTopic);
		
		jlblCommand = new JLabel("消    息");
		jlblCommand.setFont(normalFont);
		jlblCommand.setBounds(50, 410, 16*(4+2), 16+2);
		jlblCommand.setForeground(Color.BLACK);
		container.add(jlblCommand);
		
		jtxtCommand = new JTextField();
		jtxtCommand.setFont(normalFont);
		jtxtCommand.setBounds(120, 410, 400, 20);
		jtxtCommand.setEditable(true);
		container.add(jtxtCommand);
		
		jbtnSend = new JButton("发送");
		jbtnSend.setFont(buttonFont);
		jbtnSend.setBounds(540, 405, 80, 30);
		container.add(jbtnSend);
		
		jtatMessage = new JTextArea();
		jtatMessage.setFont(normalFont);
		jtatMessage.setEditable(false);
		
		jscpMessage = new JScrollPane(jtatMessage);
		jscpMessage.setFont(normalFont);
		jscpMessage.setBounds(50, 80, 570, 300);
		jscpMessage.setVisible(true);
		container.add(jscpMessage);
		
		jfrmMainView.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}

	@Override
	public void getMessage(String message) {
		addMessage(message);
	}
}
