package org.xli.core.Interface;

import org.xli.model.MessageModel;

//���ڻ�����Ϣ�Ĵ���
public interface IPublisher {
	void attachListener(IListener listener);
	void removeListener(IListener listener);
	void postMessage(MessageModel mess);
}
