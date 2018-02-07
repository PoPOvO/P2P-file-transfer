package com.mec.deal_mapping.model;

import java.lang.reflect.Method;

public class ActionDefinition {
	private Object object;
	private Method method;
	
	public ActionDefinition() {
	}

	public Object getObject() {
		return object;
	}

	public ActionDefinition setObject(Object object) {
		this.object = object;
		return this;
	}

	public Method getMethod() {
		return method;
	}

	public ActionDefinition setMethod(Method method) {
		this.method = method;
		return this;
	}

	@Override
	public String toString() {
		return "object=" + object + "\n"
				+ "method=" + method;
	}
	
}
