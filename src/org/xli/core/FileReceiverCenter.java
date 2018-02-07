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
 * 主要用于合并接收到的文件片段，由真正接收客户端调用
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

	//由真正接收客户端调用
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
	
	//解析真正服务器发送的文件信息
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
	 * 合并文件，当FileReceiver发觉满足文件合并条件后，抛出信息；由FileReceiverCenter接到并执行该方法
	 * 
	 * @param receiveFileStatus
	 */
	private void mergeAllFile() {
		for (String key: receiveFileStatusMap.keySet()) {
			FileStatus receiveFileStatus = receiveFileStatusMap.get(key);
			receiveFileStatus.mergeFile();
		}
		
		System.err.println("所有文件合并成功!");
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
	 * 接收到FileReceive抛出的片段文件信息并进行插入到LinkedList里
	 */
	@Override
	public void getFileInfo(SetionFileInfo setionFile) {
		FileStatus fileStatus = this.receiveFileStatusMap
				.get(setionFile.getFileName()); //需要处理
		//需要将去掉的offset加上去，作为临时文件名
		setionFile.setFileName(setionFile.getFileName() + "." + setionFile.getOffset());
		fileStatus.addReceiveSetionFileInfo(setionFile);
	}
}
