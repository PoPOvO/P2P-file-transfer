package com.mec.net.client;

import java.io.IOException;

import java.net.Socket;
import java.net.UnknownHostException;

import com.mec.net.core.Communication;
import com.mec.net.core.ICommunicationListener;
import com.mec.net.core.ICommunicationPublisher;
import com.mec.net.model.CommunicationMessage;
import com.mec.net.model.ConversationClientMessage;
import com.mec.net.model.ECommunicationCommand;
import com.mec.net.model.ENetMessageCommand;

public abstract class MecClient implements ICommunicationListener,
					 ICommunicationPublisher{
	private ConversationClientMessage conversationClientMessage;
	private Socket socket;
	private Communication communication;
	private ICommunicationListener listener;
	private CommunicationMessage communicationMessage;
	
	public abstract void dealResponseMessage(String message);
	
	public MecClient() {
		communicationMessage = new CommunicationMessage();
		conversationClientMessage = new ConversationClientMessage();
	}
	
	public void connectionToServer() {
		try {
			socket = new Socket("192.168.1.211", 54188);
			communication = new Communication();
			communication.addMessageListener(this);
			communication.setSocket(socket);
		} catch (UnknownHostException e) { 
			postMessage("服务器没有开启");
		} catch (IOException e) {
			postMessage("服务器没有开启");
		}
	}
	
	public void stopConnection() {
		if(socket == null) {
			return;
		}
		try {
			this.socket.close();
			this.socket = null;
		} catch (IOException e) {
		}
		
	}
	
	public boolean isConnected() {
		if(socket != null) {
			return true;
		}
		return false;
	}
	
	@Override
	public void getMessage(String message) {
		communicationMessage = communicationMessage.
				stringtoCommunicationMessage(message);
		dealCommunicationMessage(communicationMessage);
	}
	
	private void dealConversationMessage(
			ConversationClientMessage conversationModel) {
		String from = conversationModel.getFrom();
		String to = conversationModel.getTo();
		String command = conversationModel.getCommand();
		String message = conversationModel.getMessage();
		String action = conversationModel.getAction();
		if(command.equals(ENetMessageCommand.ID.toString())) {
			this.communication.setId(message);
			return;
		} else if(command.equals(ENetMessageCommand.OFF_LINE.toString())) {
			postMessage("您已经下线");
			communication.shutdown();
			stopConnection();
			return;
		} else if(command.equals(ENetMessageCommand.RESPONSE.toString())) {
			dealResponseMessage(message);
		}
		
		postMessage(message);
	}

	private void dealCommunicationMessage(CommunicationMessage message) {
		String command = message.getCommand();
		
		if(command.equals(ECommunicationCommand.RECEIVE_OK.toString())) {
			conversationClientMessage = (ConversationClientMessage) 
					conversationClientMessage.
					stringToNetMessage(message.getMessage());
			dealConversationMessage(conversationClientMessage);
		} else if(command.equals(
				ECommunicationCommand.RECRIVE_FAILURE.toString())) {
			String mess = (String) message.getMessage();
			if(mess.equals("正常下线")) {
				postMessage("您:" + mess);
				return;
			} else if(mess.equals("异常下线")) {
				communication.shutdown();
				stopConnection();
				postMessage("服务器异常宕机,停止服务!");
				return;
			}
		} else if(command.equals(ECommunicationCommand.
				SEND_FAILURE.toString())) {
			communication.shutdown();
			stopConnection();
			postMessage("发送功能受损");
			return;
		}
	}
	
	public void sendAction(String action, String message) {
		ConversationClientMessage model = 
				new ConversationClientMessage();
		model.setFrom(this.communication.getId());
		model.setTo("null");
		model.setCommand(ENetMessageCommand.REQUEST.toString());
		model.setAction(action);
		model.setMessage(message);
		
		this.communication.sendMessage(model.toString());
	}

	public void sendMessage(String message) {
		ConversationClientMessage model = 
				new ConversationClientMessage();
		if(message.equals("exit")) {
				model.setFrom(this.communication.getId());
				model.setTo("SERVER");
				model.setCommand(ENetMessageCommand.OFF_LINE.toString());
				model.setAction("null");
				model.setMessage("null");
				this.communication.sendMessage(model.toString());
				return;
		}
		
		model.setFrom(this.communication.getId());
		model.setTo("null");
		model.setCommand(ENetMessageCommand.NORMAL_MESSAGE.toString());
		model.setMessage(message);
		model.setAction("null");
		this.communication.sendMessage(model.toString());
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
}
