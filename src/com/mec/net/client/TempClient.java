package com.mec.net.client;

import java.util.Scanner;

import com.mec.net.core.ICommunicationListener;

public class TempClient implements Runnable,ICommunicationListener{
	private MecClient mecClient;
	private Scanner in;
	private boolean goon;
	
	public TempClient() {
		mecClient = new MecClient() {
			@Override
			public void dealResponseMessage(String message) {
			}
		};
		in = new Scanner(System.in);
		
		mecClient.addMessageListener(this);
		mecClient.connectionToServer();
		
		if(mecClient.isConnected()) {
			new Thread(this).start();
			goon = true;
		} 
	}

	@Override
	public void getMessage(String message) {
		System.out.println(message);
		if(message.equals("服务器异常宕机")) {
			goon = false;
			in.close();
		}
	}

	@Override
	public void run() {
		String message = "";
		
		while(goon) {
			if(mecClient.isConnected()) {
				System.out.println("[客户端]请输入消息:");
				message = in.next();
				if(message.equals("exit") && mecClient.isConnected()) {
					goon = false;
					mecClient.sendMessage("exit");
				} else if(!mecClient.isConnected()) {
					getMessage("服务已经停止，请充钱!");
				} else {
					mecClient.sendMessage(message);
				} 
			} else {
				in.close();
				return;
				}
			}
		in.close();
	}
}
