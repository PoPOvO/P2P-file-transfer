package org.xli.core;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.xli.core.Interface.IListener;
import org.xli.core.Interface.IPublisher;
import org.xli.core.Interface.IReceiveSetionFileInfoPublisher;
import org.xli.core.Interface.IReceiverSetionFileInfoListener;
import org.xli.model.MessageModel;
import org.xli.model.SetionFileInfo;
import org.xli.util.PropertiesReader;

import com.xli.transform.DataTransform;

public class FileReceiver implements IPublisher, Runnable, IReceiveSetionFileInfoPublisher {
	private ServerSocket serverSocket;
	private List<IListener> listenerList;
	private IReceiverSetionFileInfoListener receiverSetionFileInfoListener;
	
	private boolean isStartUp;
	
	private Thread linstenerRequest;
	//������������ʼ�������������رպ����ֹͣ
	private boolean isListening;
	
	private int bufferSize;
	
	//���ںϲ�(��֤ȫ���ļ�����ɹ�ǰ����)
	private volatile long remainAllFileLen;
	//����ֹͣaccept()
	private volatile long allFileLen;
	
	public FileReceiver() {
		isStartUp = false;
		isListening = false;
		listenerList = new ArrayList<>();
		bufferSize = Integer.valueOf(PropertiesReader.getProperty("bufferSize"));
	}
	
	@Override
	public void run() {
		while (isListening) {
			try {
				if (serverSocket == null || serverSocket.isClosed()) {
					break;
				}
				
				Socket socket = serverSocket.accept();
				BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());

				Thread task = new Thread(new Runnable() {
					private int receiveCount = 0;
					
					@Override
					public void run() {
						//һ�����ͷ����͵��ļ����ֽ�
						long fileTotalByte = receiveFileHead(bis);
						postMessage(new MessageModel()
								.setCommand(CommandSet.ONE_SENDER_RECEIVE_ALL_FILE_BYTE_LEN)
								.setMessage(String.valueOf(fileTotalByte)));
						
						synchronized (FileReceiver.class) {
							if ((allFileLen -= fileTotalByte) == 0) { //����ֹͣaccept()
								shutDown();
							}	
						}
						System.err.println("[���ͷ�:" + socket.getInetAddress().toString() 
								+ "-" + socket.hashCode()  
								+ ",�������������ֽ���:" + fileTotalByte + "]");
						
						MessageModel mess = new MessageModel();
						mess.setMessage(socket.getInetAddress().toString() 
								+ "-" + socket.hashCode())
							.setCommand(CommandSet.SENDER_FILE_INFO);
						//��Ҫ����
						while (receiveCount++ < 3) {
							receiveFile(bis, mess);
						}
					
						if (bis != null) {
							try {
								bis.close();
							} catch (IOException e) {
							}
						}
					}
				});
				task.start();
			} catch (IOException e) {
				shutDown();		
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T> T receiveFileHead(BufferedInputStream bis) {
		byte[] fileTotaBytes = new byte[8];
		int fileHeadLen = 8;
		int actualByteCount = 0;
		
		while (fileHeadLen > 0) {
			try {
				actualByteCount = bis.read(fileTotaBytes, actualByteCount, fileTotaBytes.length - actualByteCount);
				fileHeadLen -= actualByteCount;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return (T) Long.valueOf(DataTransform.bytesToLong(fileTotaBytes));
	}
	
	//ÿ�ν�һ���ļ������ܼ���synchronized������ɶ�������߳��Ŷӣ����ʱ
	private void receiveFile(BufferedInputStream bis, MessageModel mess) {
		long fileLen = -1;
		File dir = new File("src/receiver");
		String fileName = "";
		long fileOffset = 0L;
		int actualByteCount = 0;
		
		try {
			int fileHeadLen = bis.read();

			byte[] fileHeadBytes = new byte[fileHeadLen];
			actualByteCount = 0;
			while (fileHeadLen > 0) {
				actualByteCount = bis.read(fileHeadBytes, actualByteCount, fileHeadBytes.length - actualByteCount);
				fileHeadLen -= actualByteCount;
			}
			
			byte[] tempBytes = null;
			tempBytes = Arrays.copyOf(fileHeadBytes, 8);
			fileLen = DataTransform.bytesToLong(tempBytes);

			tempBytes = Arrays.copyOfRange(fileHeadBytes, 8, 16);
			fileOffset = DataTransform.bytesToLong(tempBytes);
			
			tempBytes = Arrays.copyOfRange(fileHeadBytes, 16, fileHeadBytes.length);
			fileName = new String(tempBytes).trim(); //һ���ǡ�fileName.offset������ʽ�����������ļ�Ҳ�ǣ�����fileName��
		} catch (IOException e) {
			e.printStackTrace();
		}
		//�׳��ļ�������Ϣ�����ڸ��´洢�ļ���LinkedList
		postFileInfo(new SetionFileInfo()
				.setFileName(fileName.substring(0, fileName.lastIndexOf(".")))
				.setLength(fileLen)
				.setOffSet(fileOffset));
		
		File file = new File(dir.getAbsolutePath() + File.separator + fileName);
		FileOutputStream fos = null;
		
		try {
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		try {
			byte[] setion = new byte[bufferSize];
			long remainFileLen = fileLen;
			
			actualByteCount = 0;
			while (remainFileLen > 0) {
				actualByteCount = bis.read(setion, 0, setion.length);
				//���������ļ���ʣ���ܳ���
				synchronized (FileReceiver.class) {
					remainAllFileLen -= actualByteCount;
					postMessage(new MessageModel() //�׳������ļ�ʣ���ֽ�����������ʾ����
							.setCommand(CommandSet.ALL_FILE_REMAIN_BYTE_LEN)
							.setMessage("" + remainAllFileLen));	
				}
				//��ǰƬ���ļ���ʣ�೤��
				remainFileLen -= actualByteCount;
				String senderInfo = mess.getMessage().split(":")[0];
				mess.setMessage(senderInfo + ":" + fileName + ":" + fileLen + ":" + remainFileLen); 
				postMessage(mess);	//�׳���ǰ�ļ���Ϣ�������
				fos.write(setion, 0, actualByteCount);
				fos.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
					fos = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			synchronized (FileReceiver.class) {
				if (remainAllFileLen == 0) {
					remainAllFileLen = -1; //ֻ��Ҫ�ϲ�һ��
					postMessage(mess.setCommand(CommandSet.TO_FILE_MERGE));
					System.out.println("�ļ��������");
 				}				
			}
		}
	}
	
	public void setAllFileLen(long allFileLen) {
		this.allFileLen = allFileLen;
		remainAllFileLen = this.allFileLen;
		postMessage(new MessageModel() //�׳������ļ����ȣ�����������������
				.setCommand(CommandSet.ALL_FILE_BYTE_LEN)
				.setMessage(this.allFileLen + ""));
	}
	
	public boolean startUp() {
		isListening = true;
		try {
			serverSocket = new ServerSocket(Integer.valueOf(PropertiesReader.getProperty("server_port")));
			System.out.println("׼�������ļ�");
			linstenerRequest = new Thread(this);
			linstenerRequest.setName("linstenerRequest");
			linstenerRequest.start();
		} catch (IOException e) {
			isStartUp = false;
			return isStartUp;
		}
		
		return isStartUp;
	}
	
	//һ���ļ������з�������accept()�󣬹رշ���������������ٿ���
	public boolean shutDown() {
		if (serverSocket != null && !serverSocket.isClosed()) {
			try {
				serverSocket.close();
				serverSocket = null;
			} catch (IOException e) {
			} finally {
				isListening = false;
			}
		}
		return isStartUp;
	}
	
	@Override
	public void attachListener(IListener listener) {
		if (!listenerList.contains(listener)) {
			listenerList.add(listener);
		}
	}

	@Override
	public void removeListener(IListener listener) {
		if (listenerList.contains(listener)) {
			listenerList.remove(listener);
		}
	}

	@Override
	public void postMessage(MessageModel mess) {
		for (IListener e: listenerList) {
			e.getMessage(mess);
		}
	}

	@Override
	public void postFileInfo(SetionFileInfo setionFile) {
		this.receiverSetionFileInfoListener.getFileInfo(setionFile);
	}

	@Override
	public void attachFileInfoListener(IReceiverSetionFileInfoListener listener) {
		if (this.receiverSetionFileInfoListener == null) {
			this.receiverSetionFileInfoListener = listener;
		}
	}

	@Override
	public void removeFileInfoListener(IReceiverSetionFileInfoListener listener) {
		if (this.receiverSetionFileInfoListener != null) {
			this.receiverSetionFileInfoListener = null;
		}
	}
}
