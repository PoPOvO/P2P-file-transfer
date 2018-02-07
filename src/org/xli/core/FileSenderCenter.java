package org.xli.core;

import java.util.LinkedList;
import java.util.List;

import org.xli.core.Interface.IListener;
import org.xli.model.MessageModel;
import org.xli.model.SetionFileInfo;
import org.xli.util.FileReader;

/**
 * ��Ҫ���ڸ����ļ���Ƭ��Ϣ��ִ�з��ͣ��������ķ��Ϳͻ��˵���
 * 
 * @author xl
 *
 */
public class FileSenderCenter implements IListener {
	protected FileSender sender;
	private long fileTotalByte;
	
	//��ʱ����ʹ��
	public FileSenderCenter() {
		sender = new FileSender();
		sender.attachListener(this);
		sender.connectToServer();
		fileTotalByte = 0;
	}
	
	//TODO �Ժ����������Ϳͻ��˵���
	public FileSenderCenter(String message) {
		sender = new FileSender();
		sender.attachListener(this);
		sender.connectToServer();
		fileTotalByte = 0;

		List<SetionFileInfo> list = parseMessage(message);
		System.out.println("��ʼ�����ļ�");
		sender.sendFileHead(fileTotalByte);
		for (int i = 0; i < list.size(); i++) {
			SetionFileInfo setionFileInfo = list.get(i);
			sender.sendFile(setionFileInfo.getFileName(), setionFileInfo);	
		}
		System.out.println("�ļ��������");
	}
	
	//����Ƭ���ļ���Ϣ��ͷ
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
	
	//��ʱ����ʹ��
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
		
		System.out.println("��ʼ�����ļ�");
		//����ǰ�ȷ����ļ�ͷ
		sender.sendFileHead(fileTotalByte);
		for (int i = 0; i < list.size(); i++) {
			setionFileInfo = list.get(i);
			sender.sendFile(setionFileInfo.getFileName(), setionFileInfo);	
		}
		System.out.println("�ļ��������");
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
