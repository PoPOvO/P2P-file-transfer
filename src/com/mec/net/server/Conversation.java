package com.mec.net.server;

import java.util.HashMap;

import java.util.Map;

import com.mec.deal_mapping.model.IRequestAction;
import com.mec.net.core.Communication;
import com.mec.net.core.ICommunicationListener;
import com.mec.net.core.ICommunicationPublisher;
import com.mec.net.model.CommunicationMessage;
import com.mec.net.model.ConversationClientMessage;
import com.mec.net.model.ECommunicationCommand;
import com.mec.net.model.ENetMessageCommand;
import com.mec.net.model.INetMessage;

public class Conversation implements ICommunicationPublisher, 
			ICommunicationListener{
	private ConversationClientMessage conversationClientMessage;
	private CommunicationMessage communicationMessage;
	private static Map<String, Communication> communicationMap;
	private ICommunicationListener listener;
	private IRequestAction iRequestAction;
	
	static {
		communicationMap = new HashMap<String, Communication>();
	}
	
	public Conversation() {
		conversationClientMessage = new ConversationClientMessage();
		communicationMessage = new CommunicationMessage();
	}
	
	public void setRequestAction(IRequestAction iRequestAction) {
		this.iRequestAction = iRequestAction;
	}
	
	public String getClientList() {
		String clientList = "目前在线的客户端:\n";
		for(String client : communicationMap.keySet()) {
			clientList += client + "\n";
		}
		return clientList;
	}
	
	public boolean isClientEmpty() {
		return communicationMap.isEmpty();
	}
	
	public void addCommunication(String ip, Communication communication) {
		communicationMap.put(ip, communication);
	}
	
	public void sendMessage(INetMessage message) {
		String command = message.getCommand();
		if(command.equals(ENetMessageCommand.NORMAL_MESSAGE.toString())
			|| command.equals(ENetMessageCommand.ID.toString())
			|| command.equals(ENetMessageCommand.OFF_LINE.toString())
			|| command.equals(ENetMessageCommand.RESPONSE.toString())) {
			if(communicationMap.containsKey(message.getTo())) {
				communicationMap.get(message.getTo()).
				sendMessage(message.toString());
			} else {
				postMessage("客户" + message.getTo() + "不存在");
				return;
			}
		}	
	}

	private void dealCommunicationMessage(CommunicationMessage message) {
		String command = message.getCommand();
		
		if(command.equals(ECommunicationCommand.RECEIVE_OK.toString())) {
			conversationClientMessage = 
					(ConversationClientMessage) conversationClientMessage.
					stringToNetMessage(message.getMessage());
			dealConversationMessage(conversationClientMessage);
		} else if(command.equals(ECommunicationCommand.RECRIVE_FAILURE.toString())) {
			postMessage("客户端:" + message.getId() + message.getMessage());
			communicationMap.remove(message.getId());
		} else if(command.equals(ECommunicationCommand.SEND_FAILURE.toString())) {
			postMessage("客户端:" + message.getId() + message.getMessage());
			communicationMap.remove(message.getId());
		}
	}
	
	private void dealConversationMessage(
			ConversationClientMessage conversationModel) {
		String from = conversationModel.getFrom();
		String to = conversationModel.getTo();
		String command = conversationModel.getCommand();
		String message = conversationModel.getMessage();
		String action = conversationModel.getAction();
		if(command.equals(ENetMessageCommand.OFF_LINE.toString())) {
			Communication communication = communicationMap.get(from);
			communication.sendMessage(new ConversationClientMessage()
					.setFrom("SERVER")
					.setTo(from)
					.setAction("null")
					.setCommand(ENetMessageCommand.OFF_LINE.toString())
					.setMessage("null").toString());
			postMessage("客户端:" + from + "正常下线");
			communication.shutdown();
			communicationMap.remove(from);
			return;
		} else if(command.equals(
				ENetMessageCommand.NORMAL_MESSAGE.toString())) {
			postMessage("来自" + from + "的消息:" + message);
		} else if(command.equals(
				ENetMessageCommand.REQUEST.toString())) {
			if(this.iRequestAction == null) {
				return;
			}
			String returnMess = iRequestAction.dealRequest(action, message);
			ConversationClientMessage converMessage = new ConversationClientMessage();
			converMessage.setFrom("SEREVR")
				.setTo(from)
				.setCommand(ENetMessageCommand.RESPONSE.toString())
				.setAction("null")
				.setMessage(returnMess);
			sendMessage(converMessage);
		}
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
	public synchronized void getMessage(String message) {
		communicationMessage = 
				communicationMessage.stringtoCommunicationMessage(message);
		dealCommunicationMessage(communicationMessage);
	}
}
