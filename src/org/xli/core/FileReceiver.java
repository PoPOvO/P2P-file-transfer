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
	//服务器启动后开始监听，服务器关闭后监听停止
	private boolean isListening;
	
	private int bufferSize;
	
	//用于合并(保证全部文件传输成功前提下)
	private volatile long remainAllFileLen;
	//用于停止accept()
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
						//一个发送方发送的文件总字节
						long fileTotalByte = receiveFileHead(bis);
						postMessage(new MessageModel()
								.setCommand(CommandSet.ONE_SENDER_RECEIVE_ALL_FILE_BYTE_LEN)
								.setMessage(String.valueOf(fileTotalByte)));
						
						synchronized (FileReceiver.class) {
							if ((allFileLen -= fileTotalByte) == 0) { //用于停止accept()
								shutDown();
							}	
						}
						System.err.println("[发送方:" + socket.getInetAddress().toString() 
								+ "-" + socket.hashCode()  
								+ ",待接收数据总字节数:" + fileTotalByte + "]");
						
						MessageModel mess = new MessageModel();
						mess.setMessage(socket.getInetAddress().toString() 
								+ "-" + socket.hashCode())
							.setCommand(CommandSet.SENDER_FILE_INFO);
						//需要更改
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
	
	//每次接一个文件，不能加上synchronized，会造成多个接收线程排队，会耗时
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
			fileName = new String(tempBytes).trim(); //一定是“fileName.offset”的形式，对于整个文件也是，即“fileName”
		} catch (IOException e) {
			e.printStackTrace();
		}
		//抛出文件接收信息，用于更新存储文件的LinkedList
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
				//接收所有文件的剩余总长度
				synchronized (FileReceiver.class) {
					remainAllFileLen -= actualByteCount;
					postMessage(new MessageModel() //抛出所有文件剩余字节数，用于显示进度
							.setCommand(CommandSet.ALL_FILE_REMAIN_BYTE_LEN)
							.setMessage("" + remainAllFileLen));	
				}
				//当前片段文件的剩余长度
				remainFileLen -= actualByteCount;
				String senderInfo = mess.getMessage().split(":")[0];
				mess.setMessage(senderInfo + ":" + fileName + ":" + fileLen + ":" + remainFileLen); 
				postMessage(mess);	//抛出当前文件信息，向界面
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
					remainAllFileLen = -1; //只需要合并一次
					postMessage(mess.setCommand(CommandSet.TO_FILE_MERGE));
					System.out.println("文件接收完毕");
 				}				
			}
		}
	}
	
	public void setAllFileLen(long allFileLen) {
		this.allFileLen = allFileLen;
		remainAllFileLen = this.allFileLen;
		postMessage(new MessageModel() //抛出所有文件长度，用于设置主进度条
				.setCommand(CommandSet.ALL_FILE_BYTE_LEN)
				.setMessage(this.allFileLen + ""));
	}
	
	public boolean startUp() {
		isListening = true;
		try {
			serverSocket = new ServerSocket(Integer.valueOf(PropertiesReader.getProperty("server_port")));
			System.out.println("准备接收文件");
			linstenerRequest = new Thread(this);
			linstenerRequest.setName("linstenerRequest");
			linstenerRequest.start();
		} catch (IOException e) {
			isStartUp = false;
			return isStartUp;
		}
		
		return isStartUp;
	}
	
	//一个文件的所有发送请求都accept()后，关闭服务器，接收完毕再开启
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
