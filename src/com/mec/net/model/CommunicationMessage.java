package com.mec.net.model;

public class CommunicationMessage {
	private String id;
	private String command;
	private String message;
	
	public CommunicationMessage() {
	}

	public String getId() {
		return id;
	}


	public CommunicationMessage setId(String id) {
		this.id = id;
		return this;
	}

	public String getCommand() {
		return command;
	}

	public CommunicationMessage setCommand(String command) {
		this.command = command;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public CommunicationMessage setMessage(String message) {
		this.message = message;
		return this;
	}

	@Override
	public String toString() {
		return id + "-" +
			   command + "-" + 
			   message;
	}
	
	public CommunicationMessage 
			stringtoCommunicationMessage(String message) {
		String[] parts = message.split("-");
		
		setId(parts[0]);
		setCommand(parts[1]);
		setMessage(parts[2]);
		
		return this;
	}
}
