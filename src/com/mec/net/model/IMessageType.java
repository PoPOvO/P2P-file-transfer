package com.mec.net.model;

public interface IMessageType {
	public static final String NORMAL_OFFLINE = "NORMAL_OFFLINE";
	public static final String ABNORMAL_OFFLINE = "ABNORMAL_OFFLINE";
	public static final String ONETOONEMESSAGE = "ONETOONEMESSAGE";
	public static final String ONETOALL = "ONETOALL";
	public static final String BROADCAST = "BROADCAST";
	public static final String COMMAND = "COMMAND";
	public static final String SEND_OK = "OK";
	public static final String SEND_FAILURE = "SEND_FAILURE";
	public static final String FORCE_OFFLINE = "FORCE_OFFLINE";
}
