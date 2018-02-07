package org.xli.control;


import org.xli.core.FileReceiverCenter;
import org.xli.core.Interface.IListener;
import org.xli.model.MessageModel;
import org.xli.util.FileReader;

public class ReceiverConsole implements IListener {
	private FileReceiverCenter receiverCenter;
	
	public ReceiverConsole() {
		String message = FileReader.readFile("bin/fileInfo.txt");
		receiverCenter = new FileReceiverCenter();
		receiverCenter.attachListener(this);
		receiverCenter.startReceiver(message);
	}
	
	@Override
	public void getMessage(MessageModel mess) {
	}
	
	public static void main(String[] args) {
		new ReceiverConsole();
	}
}
