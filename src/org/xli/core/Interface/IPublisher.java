package org.xli.core.Interface;

import org.xli.model.MessageModel;

//用于基本消息的处理
public interface IPublisher {
	void attachListener(IListener listener);
	void removeListener(IListener listener);
	void postMessage(MessageModel mess);
}
