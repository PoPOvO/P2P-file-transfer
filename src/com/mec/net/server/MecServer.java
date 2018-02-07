package com.mec.net.server;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;

import com.mec.deal_mapping.core.Running;
import com.mec.net.core.Communication;
import com.mec.net.core.ICommunicationListener;
import com.mec.net.core.ICommunicationPublisher;
import com.mec.net.model.ConversationClientMessage;
import com.mec.net.model.ENetMessageCommand;
import com.mec.net.model.IMessageType;
import com.mec.net.model.INetMessage;

public class MecServer implements ICommunicationPublisher,Runnable,ICommunicationListener{
	private ServerSocket serverSocket;
	private Socket socket;
	private Conversation conversation;
	private ICommunicationListener listener;
	private RequestAction requestAction;
	
	private Running running;
	
	private boolean serverStartup;
	private boolean isOk;
	
	public MecServer() {
		running = new Running();
		conversation = new Conversation();
		conversation.addMessageListener(this);
		requestAction = new RequestAction();
		conversation.setRequestAction(requestAction);
	}
	
	public void startServer() {
		try {
			serverSocket = new ServerSocket(54188);
			Thread thread = new Thread(this);
			thread.start();
			
			this.serverStartup = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isServerSocketOpen() {
		if(serverSocket == null) {
			return false;
		}
		return true;
	}
	
	public boolean isClosed() {
		if(this.serverSocket == null) {
			return true;
		}
		return false;
	}
	
	public boolean shutdown() {
		isOk = true;
		if(this.conversation.isClientEmpty()) {
			stopListenningClientConnected();
			return true;
		}
		postMessage("尚有客户端在线，不能关闭服务器");
		return false;
	}
	
	public void stopListenningClientConnected() {
		this.serverStartup = false;
		try {
			if(this.serverSocket != null) {
				this.serverSocket.close();
				this.serverSocket = null;
			}
		} catch (IOException ioe) {
		} 
	}
	
	public String getClientList() {
		return conversation.getClientList();
	}
	
	public void sendMessage(INetMessage message) {
		conversation.sendMessage(message);
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
		this.listener.getMessage(message);
	}
	
	@Override
	public void run() {
		while(serverStartup) {
			try {
				isOk = false;
				this.socket = serverSocket.accept();
				isOk = true;
				
				Communication communication = new Communication();
				String clientIp = socket.getInetAddress().getHostAddress()
						+ "." + communication.hashCode();
				postMessage("客户" + clientIp + "上线");
				communication.setSocket(socket);
				communication.setId(clientIp);
				communication.addMessageListener(this.conversation);
				conversation.addCommunication(clientIp, communication);
				
				ConversationClientMessage conCliMessage = new 
						ConversationClientMessage();
				conCliMessage.setFrom("SERVER");
				conCliMessage.setTo(clientIp);
				conCliMessage.setCommand(ENetMessageCommand.ID.toString());
				conCliMessage.setAction("null");
				conCliMessage.setMessage(clientIp);
				sendMessage(conCliMessage);
				} catch (IOException e) {
					if(isOk == true) {
						ConversationClientMessage conCliMessage = new 
								ConversationClientMessage();
						conCliMessage.setFrom("SERVER");
						conCliMessage.setTo(IMessageType.BROADCAST);
						conCliMessage.setCommand("null");
						conCliMessage.setAction("null");
						conCliMessage.setMessage("服务器正常关闭");
						sendMessage(conCliMessage);
					} else if(isOk == false) {
						ConversationClientMessage conCliMessage = new 
								ConversationClientMessage();
						conCliMessage.setFrom("SERVER");
						conCliMessage.setTo(IMessageType.BROADCAST);
						conCliMessage.setCommand("null");
						conCliMessage.setAction("null");
						conCliMessage.setMessage("警告！服务器异常宕机！");
						sendMessage(conCliMessage);
					}
				stopListenningClientConnected();
			} 
		}
	}

	@Override
	public void getMessage(String message) {
		postMessage(message);
	}
}
