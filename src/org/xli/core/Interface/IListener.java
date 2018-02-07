package org.xli.core.Interface;

import org.xli.model.MessageModel;

//基本消息获取后处理
public interface IListener {
	void getMessage(MessageModel mess);
}
