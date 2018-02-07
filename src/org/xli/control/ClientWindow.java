package org.xli.control;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import org.xli.core.CommandSet;
import org.xli.core.FileReceiverCenter;
import org.xli.core.Interface.IListener;
import org.xli.model.MessageModel;
import org.xli.util.FileReader;
import org.xli.util.PropertiesReader;
import org.xli.view.FileSenderPanel;

public class ClientWindow implements IListener {
	private String command;
	private ClientWindow me;
	
	private JFrame jfrmMainWindow;
	
	private JLabel jlblCommanInput;
	private JTextField jtxtfCommandInput;
	
	private Dimension screenDimension;
	private Container contentPane;
	
	//����Map�������� TODO
	private Map<String, String> senderInfoMap;
	private Map<String, ProgressShowThread> threadMap;
	private Map<String, FileSenderPanel> panelMap;
	
	private ProgressShowThread mainProgressThread;
	private int allFileLen;
	private FileSenderPanel allFilefileSenderPanel;
	
	private JDialog jdlgFileReceive;

	public  ClientWindow() {	
		me = this;
		screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
		senderInfoMap = new HashMap<>();
		threadMap = new HashMap<>();
		panelMap = new HashMap<>();
		initWindow();
		dealAction();
		
		jfrmMainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jfrmMainWindow.setResizable(false);
		jfrmMainWindow.setVisible(true);
	}
	
	private void dealAction() {
		jtxtfCommandInput.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}
			@Override
			public void keyReleased(KeyEvent e) {
			}
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					command = jtxtfCommandInput.getText();
					if ("st".equals(command)) {
						//�����ļ�
						//jdlgFileReceive.setVisible(true);
						String message = FileReader.readFile("bin/fileInfo.txt");
						FileReceiverCenter receiverCenter = new FileReceiverCenter();
						receiverCenter.attachListener(me);
						receiverCenter.startReceiver(message);
					}
					jtxtfCommandInput.setText("");
				}
			}
		});
		
		jtxtfCommandInput.addMouseListener(new MouseAdapter() {
			boolean first = true;
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (first) {
					jtxtfCommandInput.setText("");
					first = false;
				}
			}
		});
	}

	private void initWindow() {
		double screenHeight = screenDimension.getHeight();
		double screenWidth = screenDimension.getWidth();
		Font normalFont = new Font("����", Font.PLAIN, 24);
		Font smallNormalFont = new Font("����", Font.PLAIN, 16);

		jfrmMainWindow = new JFrame("���������ƽ���");
		jfrmMainWindow.setLayout(null);
		jfrmMainWindow.setBounds((int)(screenWidth-1200)/2, (int)(screenHeight-700)/2, 1200, 700);
		contentPane = jfrmMainWindow.getContentPane();
		contentPane.setBackground(new Color(208,223,239));
		
		jlblCommanInput = new JLabel("��������");
		jlblCommanInput.setFont(normalFont);
		jlblCommanInput.setBounds(180, 601 , 96, 26);
		contentPane.add(jlblCommanInput);
		
		jtxtfCommandInput = new JTextField("����st׼�������ļ�");
		jtxtfCommandInput.setFont(smallNormalFont);
		jtxtfCommandInput.setBounds((1200-600)/2, 600, 600, 30);
		contentPane.add(jtxtfCommandInput);
		
		jdlgFileReceive = new JDialog(this.jfrmMainWindow, "�ļ����ս���", false);
		jdlgFileReceive.setBounds(((int)screenWidth-600)/2, ((int)screenHeight - 400)/2, 600, 400);
		jdlgFileReceive.setVisible(false);
	}

	@Override
	public void getMessage(MessageModel mess) {
		dealMainProgressBar(mess);
		dealOneSenderProgressBar(mess);
	}
	
	//����һ�������ߵĽ�����,���ڶ���������߳�Ҫ�Ŷ�ˢ�½���������������
	private synchronized void dealOneSenderProgressBar(MessageModel mess) {
		if (mess.getCommand() == CommandSet.SENDER_FILE_INFO) {
			String info[] = mess.getMessage().split(":");
			String senderInfo = info[0];
			String fileName = info[1];
			int fileLen = Integer.valueOf(info[2]);
			int remainFileLen = Integer.valueOf(info[3]);

			//��Ӧĳ�������ߵ�һ�η���
			if (!senderInfoMap.containsKey(senderInfo)) {
				senderInfoMap.put(senderInfo, fileName);
				FileSenderPanel fileSenderPanel = new FileSenderPanel();
				panelMap.put(senderInfo, fileSenderPanel);
				fileSenderPanel.setBounds((1200-400)/2, 50 * senderInfoMap.size(), 400, 50);
				fileSenderPanel.initPanel();
				fileSenderPanel.setFileInfo(senderInfo, fileName); //���÷�������Ϣ
				contentPane.add(fileSenderPanel);
			
				JProgressBar jProgressBar = fileSenderPanel.getJProgressBar();
				jProgressBar.setMaximum(fileLen - Integer.valueOf(PropertiesReader.getProperty("bufferSize")));
				threadMap.put(senderInfo, new ProgressShowThread(jProgressBar));
				threadMap.get(senderInfo)
					.setPercentCount(fileLen - remainFileLen)
					.start();
			}
			
			//��Ӧĳ��Ƭ���ļ�
			if (!senderInfoMap.containsKey(fileName)) {
				senderInfoMap.put(senderInfo, fileName);
				panelMap.get(senderInfo).getJProgressBar().setMaximum(fileLen - Integer.valueOf(PropertiesReader.getProperty("bufferSize")));
				panelMap.get(senderInfo).setFileInfo(senderInfo, fileName);
				threadMap.get(senderInfo).setPercentCount(fileLen - remainFileLen);
			}
			
			threadMap.get(senderInfo).setPercentCount(fileLen - remainFileLen);
		}
	}
	
	//������������
	private void dealMainProgressBar(MessageModel mess) {
		//���������ĳ�ʼ��
		if (mess.getCommand() == CommandSet.ALL_FILE_BYTE_LEN) {
			allFileLen = Integer.valueOf(mess.getMessage());
			allFilefileSenderPanel = new FileSenderPanel();
			allFilefileSenderPanel.setBounds((1200-400)/2, 0, 400, 50);
			allFilefileSenderPanel.initPanel();
			contentPane.add(allFilefileSenderPanel);
		
			JProgressBar bar = allFilefileSenderPanel.getJProgressBar();
			bar.setMaximum(allFileLen-Integer.valueOf(PropertiesReader.getProperty("bufferSize")));
			mainProgressThread = new ProgressShowThread(bar);
			mainProgressThread.start();
		}
		
		//ˢ�½�����
		if (mess.getCommand() == CommandSet.ALL_FILE_REMAIN_BYTE_LEN) {
			int allFIleRemainLen = Integer.valueOf(mess.getMessage());
			allFilefileSenderPanel.setFileInfo("�����ļ����ͽ���", allFileLen - allFIleRemainLen + "/" + allFileLen);
			mainProgressThread.setPercentCount(allFileLen - allFIleRemainLen);
		}
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new ClientWindow();
			}
		});
	}
}
