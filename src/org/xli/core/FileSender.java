package org.xli.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.xli.core.Interface.IListener;
import org.xli.core.Interface.IPublisher;
import org.xli.model.MessageModel;
import org.xli.model.SetionFileInfo;
import org.xli.util.PropertiesReader;

import com.xli.transform.DataTransform;

public class FileSender implements IPublisher {
	private List<IListener> listenerList;

	private Socket socket;
	private BufferedOutputStream bos;
	
	private int bufferSize;  //32k
	
	public FileSender() {
		this.listenerList = new ArrayList<>();
		bufferSize = Integer.valueOf(PropertiesReader.getProperty("bufferSize"));
	}
	
	public void connectToServer() {
		try {
			this.socket = new Socket(PropertiesReader.getProperty("receiverServerIP"), Integer.valueOf(PropertiesReader.getProperty("server_port")));
			this.bos = new BufferedOutputStream(socket.getOutputStream());
		} catch (UnknownHostException e) {
		} catch (IOException e) {
		}
	}
	
	public void sendFileHead(long fileTotalByte) {
		if (bos == null) {
			return;
		}
		
		byte[] fileTotalBytes = DataTransform.longToBytes(fileTotalByte);
		try {
			bos.write(fileTotalBytes, 0, fileTotalBytes.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendFile(String filePath, SetionFileInfo setionFileInfo) {
		File file = new File(filePath);
		
		if (!file.exists()) {
			return;
		}
		
		long fileLen = setionFileInfo.getLength();
		try {
			int fileSeparator = filePath.lastIndexOf("/");
			String fileName = fileSeparator > 0 ? filePath.substring(fileSeparator + 1) : filePath;
			byte[] fileNameBytes = (fileName + "." + setionFileInfo.getOffset()).getBytes();

			//����С��ͷ����
			bos.write(fileNameBytes.length + 16);
			try { //��֤���ͳɹ�
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}
			//����Ƭ���ļ�����
			bos.write(DataTransform.longToBytes(setionFileInfo.getLength()));
			//�����ļ�ƫ���� 
			bos.write(DataTransform.longToBytes(setionFileInfo.getOffset()));
			//�����ļ���
			bos.write(fileNameBytes, 0, fileNameBytes.length);
			bos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		if (fis != null) {
			try {
				long remainFileLen = fileLen;
				int byteCount = 0;

				fis.skip(setionFileInfo.getOffset());
				while (remainFileLen > 0) {
					byte[] setion = new byte[remainFileLen > bufferSize ? bufferSize : (int) (remainFileLen % bufferSize)];
					byteCount = fis.read(setion, 0, setion.length);
					remainFileLen -= byteCount;
					bos.write(setion, 0, byteCount);
					bos.flush();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//�����߷����������ݺ�����1s��������Ϊ����ԭ�����ɵġ���-�ա���һ�£�����Connect Reset�쳣
		try { 
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
}
