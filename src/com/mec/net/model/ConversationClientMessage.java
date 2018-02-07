package com.mec.net.model;

public class ConversationClientMessage implements INetMessage {
	private String from;
	private String to;
	private String command;
	private String message;
	private String action;

	@Override
	public INetMessage setFrom(String from) {
		this.from = from;
		return this;
	}

	@Override
	public INetMessage setTo(String to) {
		this.to = to;
		return this;
	}

	@Override
	public INetMessage setCommand(String command) {
		this.command = command;
		return this;
	}

	@Override
	public INetMessage setAction(String action) {
		this.action = action;
		return this;
	}

	@Override
	public INetMessage setMessage(String message) {
		this.message = message;
		return this;
	}

	@Override
	public String getFrom() {
		return this.from;
	}

	@Override
	public String getTo() {
		return this.to;
	}

	@Override
	public String getCommand() {
		return this.command;
	}

	@Override
	public String getAction() {
		return this.action;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

	//  from:to//command:action?&message=message
	@Override
	public INetMessage stringToNetMessage(String message) {
		String[] parts1 = message.split("//");
		
		String[] part1 = parts1[0].split(":");
		setFrom(part1[0]);
		setTo(part1[1]);
//SERVER:192.168.1.211.1512870095//ID:?&message=192.168.1.211.1512870095
		String[] parts2 = parts1[1].split("\\?");
		
		String[] part2 = parts2[0].split(":");
		setCommand(part2[0]);
		setAction(part2[1]);

		String[] parts3 = parts2[1].split("&");
		String[] part4 = parts3[1].split("=");
		setMessage(part4[1]);
		
		return this;
	}

	//  from:to//command:action?&message=message
	@Override
	public String NetMessageToOgnl(INetMessage netMessage) {
		return getFrom() + ":" + getTo() + "//" + getCommand()
				+ ":" + getAction() + "?" + "&message=" + getMessage(); 
	}

	@Override
	public String toString() {
		return getFrom() + ":" + getTo() + "//" + getCommand()
		+ ":" + getAction() + "?" + "&message=" + getMessage();
	}
}
