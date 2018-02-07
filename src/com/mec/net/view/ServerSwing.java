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

import com.mec.net.core.ICommunicationListener;
import com.mec.net.server.MecServer;

public class ServerSwing implements ICommunicationListener{
	private JFrame jfrmMainView;
	private Container container;
	
	private JLabel jlblTopic;
	private JLabel jlblCommand;
	
	private JTextField jtxtCommand;
	private JTextArea jtatMessage;
	
	private JButton jbtnSend;
	
	private MecServer mecServer;
	private JScrollPane jscpMessage;
	
	Font normalFont = new Font("微软雅黑", Font.PLAIN, 16);
	Font buttonFont = new Font("微软雅黑", Font.PLAIN, 14);
	
	public ServerSwing() {
		initView();
		reinitView();
		dealAction();
		mecServer = new MecServer();
		mecServer.addMessageListener(this);
	}
	
	public void closeView() {
		jfrmMainView.dispose();
	}
	
	public void showView() {                                                                                                                                                
		jfrmMainView.setVisible(true);
	}
	
	private void dealAction() {
		jtxtCommand.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String command = e.getActionCommand();
				sendCommand(command);
				jtxtCommand.setText("");
			}
		});
		
		jbtnSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String command = jtxtCommand.getText();
				if(command.equals("")) {
					return;
				}
				sendCommand(command);
				jtxtCommand.setText("");
			}
		});
		
		jfrmMainView.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				mecServer.shutdown();
				if(mecServer.isClosed()) {
					closeView();
				}
			}
		});
		return;
	}
	
	private void sendCommand(String command) {
		if(command.equals("start") || command.equals("st")) {
			if(mecServer.isServerSocketOpen()) {
				addMessage("服务器已经开启了");
				return;
			}
			addMessage("正在建立服务器");
			mecServer.startServer();
			addMessage("服务器已经建立");
		} else if(command.equals("shutdown") || command.equals("sd")) {
			if(mecServer.shutdown()) {
				addMessage("您已经关闭服务器");
			}
		} else if(command.equals("getall")) {
			addMessage(mecServer.getClientList());
		} else {
			addMessage("无效的命令");
		}
	}
	
	private void addMessage(String message) {
		jtatMessage.append(message + "\n"); 
	}
	
	private void reinitView() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = (int) screenSize.getWidth();
		int screenHeight = (int) screenSize.getHeight();
		jfrmMainView.setLocation((screenWidth-jfrmMainView.getWidth())/2, 
				(screenHeight-jfrmMainView.getHeight())/2);
	}
	
	private void initView() {
		jfrmMainView = new JFrame("服务器");
		container = jfrmMainView.getContentPane();
		container.setLayout(null);
		jfrmMainView.setSize(700, 500);
		
		jlblTopic = new JLabel("小霸王服务器");
		jlblTopic.setFont(new Font("微软雅黑", Font.BOLD, 36));
		jlblTopic.setForeground(Color.BLUE);
		jlblTopic.setBounds(490 / 2, 0, 36*(10+2), 36+2);
		container.add(jlblTopic);
		
		jlblCommand = new JLabel("命    令");
		jlblCommand.setFont(normalFont);
		jlblCommand.setBounds(50, 410, 16*(4+2), 16+2);
		jlblCommand.setForeground(Color.BLACK);
		container.add(jlblCommand);
		
		jtxtCommand = new JTextField();
		jtxtCommand.setFont(normalFont);
		jtxtCommand.setBounds(120, 410, 400, 20);
		jtxtCommand.setEditable(true);
		container.add(jtxtCommand);
		
		jtatMessage = new JTextArea();
		jtatMessage.setFont(normalFont);
		jtatMessage.setEditable(false);
		
		jbtnSend = new JButton("发送");
		jbtnSend.setFont(buttonFont);
		jbtnSend.setBounds(540, 405, 80, 30);
		container.add(jbtnSend);
		
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