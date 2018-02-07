package org.xli.control;

import org.xli.core.FileSenderCenter;

/**
 * 3个客户端同时发送
 * 
 * @author xl
 *
 */
public class MutilSenderTest {
	public static void main(String[] args) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				new FileSenderCenter().startSendFile(1);
			}
		}).start();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				new FileSenderCenter().startSendFile(2);
			}
		}).start();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				new FileSenderCenter().startSendFile(3);
			}
		}).start();
	}
}
