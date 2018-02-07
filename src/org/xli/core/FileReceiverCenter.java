package org.xli.core;

import java.util.HashMap;
import java.util.Map;

import org.xli.core.Interface.IListener;
import org.xli.core.Interface.IPublisher;
import org.xli.core.Interface.IReceiverSetionFileInfoListener;
import org.xli.model.FileInfo;
import org.xli.model.MessageModel;
import org.xli.model.FileStatus;
import org.xli.model.SetionFileInfo;

/**
 * ��Ҫ���ںϲ����յ����ļ�Ƭ�Σ����������տͻ��˵���
 * 
 * @author xl
 *
 */
public class FileReceiverCenter implements IListener, IPublisher, 
		IReceiverSetionFileInfoListener {
	private Map<String, FileStatus> receiveFileStatusMap;
	private long allFileByte;
	private FileReceiver receiver;
	private IListener listener;
	
	public FileReceiverCenter() {
	}

	//���������տͻ��˵���
	public void startReceiver(String message) {
		allFileByte = 0;
		receiveFileStatusMap = new HashMap<>();
		receiver = new FileReceiver();
		receiver.attachListener(this);
		receiver.attachFileInfoListener(this);
		parseMessageAndInitAllFileStatus(message);
		receiver.setAllFileLen(allFileByte);
		receiver.startUp();
	}
	
	//�����������������͵��ļ���Ϣ
	private void parseMessageAndInitAllFileStatus(String message) {
		String[] files = message.split("\"");
		
		for (String e: files) {
			String fileName = e.split(":")[0];
			String fileLen = e.split(":")[1];
			
			allFileByte += Long.valueOf(fileLen);
			FileStatus fileStatus = new FileStatus();
			fileStatus.setFileInfo(new FileInfo()
					.setTargetFileName(fileName)
					.setFileLen(Long.valueOf(fileLen)));
			
			receiveFileStatusMap.put(fileName, fileStatus);
		}
	}
	
	/**
	 * �ϲ��ļ�����FileReceiver���������ļ��ϲ��������׳���Ϣ����FileReceiverCenter�ӵ���ִ�и÷���
	 * 
	 * @param receiveFileStatus
	 */
	private void mergeAllFile() {
		for (String key: receiveFileStatusMap.keySet()) {
			FileStatus receiveFileStatus = receiveFileStatusMap.get(key);
			receiveFileStatus.mergeFile();
		}
		
		System.err.println("�����ļ��ϲ��ɹ�!");
	}
	
	public long getAllFileByte() {
		return allFileByte;
	}

	@Override
	public void getMessage(MessageModel mess) {
		if (mess.getCommand() == CommandSet.STRING_MESSAGE) {
			System.out.println(mess.getMessage());
		} else if (mess.getCommand() == CommandSet.TO_FILE_MERGE) {
			this.mergeAllFile();
		} else {
			postMessage(mess);
		}
	}
	
	@Override
	public void attachListener(IListener listener) {
		if (this.listener == null) {
			this.listener = listener;
		}
	}

	@Override
	public void removeListener(IListener listener) {
		if (this.listener != null) {
			listener = null;
		}
	}

	@Override
	public void postMessage(MessageModel mess) {
		if (this.listener != null) {
			this.listener.getMessage(mess);
		}
	}

	/**
	 * ���յ�FileReceive�׳���Ƭ���ļ���Ϣ�����в��뵽LinkedList��
	 */
	@Override
	public void getFileInfo(SetionFileInfo setionFile) {
		FileStatus fileStatus = this.receiveFileStatusMap
				.get(setionFile.getFileName()); //��Ҫ����
		//��Ҫ��ȥ����offset����ȥ����Ϊ��ʱ�ļ���
		setionFile.setFileName(setionFile.getFileName() + "." + setionFile.getOffset());
		fileStatus.addReceiveSetionFileInfo(setionFile);
	}
}
