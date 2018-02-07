package com.mec.net.core;

import com.mec.net.model.CommunicationMessage;

public interface ICommunicationPublisher {
	void addMessageListener(ICommunicationListener listener);
	void removeMessageListener(ICommunicationListener listener);
	void postMessage(String message);
}
