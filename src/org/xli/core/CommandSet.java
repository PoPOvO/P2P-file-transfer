package org.xli.core;

public enum CommandSet {
	/**
	 * 所有文件的总字节数
	 */
	ALL_FILE_BYTE_LEN,
	/**
	 * 所有文件的剩余字节数
	 */
	ALL_FILE_REMAIN_BYTE_LEN,
	/**
	 * 一个发送者发送的所有文件的总字节数
	 */
	ONE_SENDER_RECEIVE_ALL_FILE_BYTE_LEN,
	/**
	 * 一个发送方发送的剩余文件的长度
	 */
	ONE_SENDER_RECEIVE_FILE_REMAIN_BYTE_LEN,
	/**
	 * 字符串信息
	 */
	STRING_MESSAGE,
	/**
	 * 发送者发送的文件信息，用于显示进度条
	 */
	SENDER_FILE_INFO,
	/**
	 * 去合并文件
	 */
	TO_FILE_MERGE
}
