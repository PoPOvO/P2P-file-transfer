package com.mec.deal_mapping.core;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.mec.deal_mapping.annotation.AMethod;
import com.mec.deal_mapping.annotation.Component;
import com.mec.deal_mapping.model.ActionDefinition;
import com.mec.deal_mapping.util.PackageScanner;

public class Running {
	private static final Map<String, ActionDefinition> annotationMap;
	
	static {
		annotationMap = new HashMap<>();
		
		new PackageScanner() {
			@Override
			public void dealClass(Class<?> klass) {
				if(klass.isAnnotationPresent(Component.class)) {
					try {
						Object object = klass.newInstance();
						Method[] methods = klass.getMethods();
						for(Method method : methods) {
							if(method.isAnnotationPresent(
									AMethod.class)) {
								ActionDefinition model = 
										new ActionDefinition();
								AMethod aMethod = method.getAnnotation(
										AMethod.class);
								model.setObject(object)
									 .setMethod(method);
								annotationMap.put(aMethod.action(),
										model);
							} 
						}
					} catch (Exception e) {
					}
				}
			}
		}.startScan("com.mec");
	}
	
	public Running() {
	}
	
	public Map<String, ActionDefinition> getMap() {
		return Running.annotationMap;
	}
}
