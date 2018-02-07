package com.mec.net.model;

public interface INetMessage {
	INetMessage setFrom(String from);
	INetMessage setTo(String to);
	INetMessage setCommand(String command);
	INetMessage setAction(String action);
	INetMessage setMessage(String message);
	String getFrom();
	String getTo();
	String getCommand();
	String getAction();
	String getMessage();
	INetMessage stringToNetMessage(String message);
	String NetMessageToOgnl(INetMessage netMessage);
}
