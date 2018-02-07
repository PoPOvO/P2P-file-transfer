package org.xli.core;

import java.util.LinkedList;
import java.util.List;

import org.xli.core.Interface.IListener;
import org.xli.model.MessageModel;
import org.xli.model.SetionFileInfo;
import org.xli.util.FileReader;

/**
 * 主要用于根据文件分片信息来执行发送，由真正的发送客户端调用
 * 
 * @author xl
 *
 */
public class FileSenderCenter implements IListener {
	protected FileSender sender;
	private long fileTotalByte;
	
	//临时测试使用
	public FileSenderCenter() {
		sender = new FileSender();
		sender.attachListener(this);
		sender.connectToServer();
		fileTotalByte = 0;
	}
	
	//TODO 以后由真正发送客户端调用
	public FileSenderCenter(String message) {
		sender = new FileSender();
		sender.attachListener(this);
		sender.connectToServer();
		fileTotalByte = 0;

		List<SetionFileInfo> list = parseMessage(message);
		System.out.println("开始发送文件");
		sender.sendFileHead(fileTotalByte);
		for (int i = 0; i < list.size(); i++) {
			SetionFileInfo setionFileInfo = list.get(i);
			sender.sendFile(setionFileInfo.getFileName(), setionFileInfo);	
		}
		System.out.println("文件发送完成");
	}
	
	//解析片段文件信息报头
	private List<SetionFileInfo> parseMessage(String message) {
		List<SetionFileInfo> list = new LinkedList<>();
		
		String[] files = message.split("\"");
		for (String e: files) {
			String filePath = e.split(":")[0];
			String fileLen = e.split(":")[1];
			String fileOffset = e.split(":")[2];
			fileTotalByte += Long.valueOf(fileLen);
			
			list.add(new SetionFileInfo()
				.setFileName(filePath)
				.setLength(Long.valueOf(fileLen))
				.setOffSet(Long.valueOf(fileOffset)));
		}
		
		return list;
	}
	
	//临时测试使用
	public void startSendFile(int flag) {
		List<SetionFileInfo> list = null;
		SetionFileInfo setionFileInfo = null;;
		String message = "";
		
		switch (flag) {
			case 1:
				message = FileReader.readFile("bin/sender1.txt");
				break;
			case 2:
				message = FileReader.readFile("bin/sender2.txt");
				break;
			case 3:
				message = FileReader.readFile("bin/sender3.txt");
				break;
		}
		
		list = parseMessage(message);
		
		System.out.println("开始发送文件");
		//发送前先发送文件头
		sender.sendFileHead(fileTotalByte);
		for (int i = 0; i < list.size(); i++) {
			setionFileInfo = list.get(i);
			sender.sendFile(setionFileInfo.getFileName(), setionFileInfo);	
		}
		System.out.println("文件发送完成");
	}
	
	@Override
	public void getMessage(MessageModel mess) {
		if (mess.getCommand() == CommandSet.STRING_MESSAGE) {
			System.out.println("Client:" + mess);	
		}
	}
	
	public static void main(String[] args) {
		new FileSenderCenter().startSendFile(1);
	}
}
