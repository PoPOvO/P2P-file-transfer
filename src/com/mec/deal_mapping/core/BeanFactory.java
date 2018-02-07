package com.mec.deal_mapping.core;

import java.util.HashMap;
import java.util.Map;

public class BeanFactory {
	private static final Map<String, Object> beanFactory;
	
	static {
		beanFactory = new HashMap<>();
	}
	
	public BeanFactory() {
	}
	
	public void addBean(String id, Object object) {
		if(beanFactory.get(id) == null) {
			System.out.println("beanFactory中不存在:" + id);
		}
		beanFactory.put(id, object);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getBean(String id) {
		return (T) beanFactory.get(id);
	}
	
	protected Map<String, Object> getBeanFactory() {
		return beanFactory;
	}
}
