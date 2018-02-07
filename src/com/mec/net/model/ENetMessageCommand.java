package com.mec.net.model;

public enum ENetMessageCommand {
	/*下线*/
	OFF_LINE,
	/*网络异常*/
	NET_FAILURE,
	ID,
	/*正常消息*/
	NORMAL_MESSAGE,
	/*一对一发送*/
	TO_ONE,
	/*广播发送*/
	TO_ALL,
	/*一对多发送*/
	TO_OTHER,
	/*发出资源请求*/
	REQUEST,
	/*发出资源响应*/
	RESPONSE
}
