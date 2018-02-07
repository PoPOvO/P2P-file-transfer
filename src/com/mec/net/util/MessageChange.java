package com.mec.net.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.mec.net.model.CommunicationMessage;

public class MessageChange {
	
	public MessageChange() {
	}
	
	public static CommunicationMessage StringToNetMessageForServer(String message) {
		String[] parts = message.split("&");
		CommunicationMessage netMessage = new CommunicationMessage();
		
		for(String part : parts) {
			String methodName = "set" + part.split(":")[0].substring(0, 1).toUpperCase()
					+ part.split(":")[0].substring(1);
			try {
				Method method = netMessage.getClass().getMethod(methodName, String.class);
				method.invoke(netMessage, part.split(":")[1]);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return netMessage;
	}
	
	public static CommunicationMessage StringToNetMessageForClient(String message) {
		String[] firstparts = message.split("-");
		String[] parts = firstparts[1].split("&");
		CommunicationMessage netMessage = new CommunicationMessage();
		
		for(String part : parts) {
			String methodName = "set" + part.split(":")[0].substring(0, 1).toUpperCase()
					+ part.split(":")[0].substring(1);
			try {
				Method method = netMessage.getClass().getMethod(methodName, String.class);
				method.invoke(netMessage, part.split(":")[1]);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return netMessage;
	}
	
	public static String NetMessageToString(CommunicationMessage netMessage) {
		String message = netMessage.toString();
		
		return message;
	}
}
