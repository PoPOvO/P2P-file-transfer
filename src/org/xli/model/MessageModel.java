package org.xli.model;

import org.xli.core.CommandSet;

public class MessageModel {
	private CommandSet command;
	private String message;
	
	public MessageModel() {
	}

	public CommandSet getCommand() {
		return command;
	}

	public MessageModel setCommand(CommandSet command) {
		this.command = command;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public MessageModel setMessage(String message) {
		this.message = message;
		return this;
	}

	@Override
	public String toString() {
		return "MessageModel [command=" + command + ", message=" + message + "]";
	}
}
