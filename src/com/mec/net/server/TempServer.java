package com.mec.net.server;

import java.util.Scanner;

import com.mec.net.core.ICommunicationListener;

public class TempServer implements Runnable,ICommunicationListener{
	private MecServer mecServer;
	private Scanner keyIn;
	
	public TempServer() {
		mecServer = new MecServer();
		keyIn = new Scanner(System.in);
		mecServer.addMessageListener(this);
		
		new Thread(this).start();
	}

	@Override
	public void getMessage(String message) {
		System.out.println(message);
	}

	@Override
	public void run() {
		boolean goon = true;
		String command = "";
		
		while(goon) {
			System.out.println("[服务器]请输入命令:");
			command = keyIn.next();
			if(command.equals("start") || command.equals("st")) {
				mecServer.startServer();
			} else if(command.equals("shutdown") || command.equals("sd")) {
				if(mecServer.shutdown()) {
					getMessage("您已经关闭服务器");
					goon = false;
					keyIn.close();
				}
			} else if(command.equals("getall")) {
				getMessage(mecServer.getClientList());
			} else {
				getMessage("无效的命令");
			}
		}
		keyIn.close();
	}
}
