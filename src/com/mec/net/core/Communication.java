package com.mec.net.core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.mec.net.model.IMessageType;
import com.mec.net.util.MessageChange;
import com.mec.net.model.CommunicationMessage;
import com.mec.net.model.ECommunicationCommand;

public class Communication implements ICommunicationPublisher,Runnable{
	private Socket socket;
	private ICommunicationListener listener;
	private DataInputStream dis;
	private DataOutputStream dos;
	private Thread thread; 
	private String id;
	
	private boolean continueListener;
	private boolean isOk;
	
	public Communication() {
		id = null;
		this.continueListener = false;
		isOk = false;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return this.id;
	}
	
	public void setSocket(Socket socket) {
		if(this.socket != null) {
			return;
		}
		this.socket = socket;
		try {
			this.dis = new DataInputStream(this.socket.getInputStream());
			this.dos = new DataOutputStream(this.socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(this.thread == null) {
			thread = new Thread(this);
			this.continueListener = true;
			this.thread.start();
		}
	}
	
	public void shutdown() {
		isOk = true;
		stopCommunication();
	}
	
	private void stopCommunication() {
		continueListener = false;
		try {
				if(dis != null) { 
					dis.close();
					dis = null;
				}
				if(dos != null) {
					dos.close();
					dos = null;
				}
				if(socket != null && !socket.isClosed()) {
					socket.close();
					socket = null;
				}
			}	catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	public void sendMessage(String message) {
		try {
			this.dos.writeUTF(message);
		} catch (IOException e) {
			postMessage(new CommunicationMessage().setId(id)
					.setCommand(ECommunicationCommand.SEND_FAILURE.toString())
					.setMessage("发送功能异常").toString());
			stopCommunication();
		}
	}

	public String receiveMessage(DataInputStream dis) throws IOException {
		String message = null;
		message = dis.readUTF();
		
		return message;
	}
	
	@Override
	public void addMessageListener(ICommunicationListener listener) {
		this.listener = listener;
	}
	
	@Override
	public void removeMessageListener(ICommunicationListener listener) {
	}
	
	@Override
	public void postMessage(String message) {
		listener.getMessage(message);
	}
	
	@Override
	public void run()  {
		while(continueListener) {
			String message = "";
			try {
				isOk = false;
				message = receiveMessage(dis);
				isOk = true;
				
				postMessage(new CommunicationMessage().setId(id)
						.setCommand(ECommunicationCommand.
								RECEIVE_OK.toString())
						.setMessage(message).toString());
			} catch (IOException e) {
				if(isOk == true) {
					postMessage(new CommunicationMessage().setId(id)
							.setCommand(ECommunicationCommand.
									RECRIVE_FAILURE.toString())
							.setMessage("正常下线").toString());
				} else if(isOk == false) {
					postMessage(new CommunicationMessage().setId(id)
							.setCommand(ECommunicationCommand.
									RECRIVE_FAILURE.toString())
							.setMessage("异常下线").toString());
				} 
				stopCommunication();
			}
		}
		stopCommunication();
	}
}
