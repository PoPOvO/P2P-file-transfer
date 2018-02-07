package com.mec.deal_mapping.core;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mec.deal_mapping.annotation.AParameter;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 处理方法的参数注入
 * 
 * @author xl
 *
 */
public class ParameterIoC {
	public ParameterIoC() {
	}
	
	/**
	 * 将参数注入method
	 * 
	 * @param object action类
	 * @param method 待执行方法
	 * @param message JSON参数
	 * @return
	 */
	public Object[] addParameter(Object object, Method method, 
			String message) {
		Map<String, String> paraMap = new HashMap<String, String>();
		List<Object> paras = new ArrayList<Object>(); //所有参数值List，复杂形式
		
		JSONObject jsonObject = JSONObject.fromObject(message);
		Iterator<?> iterator = jsonObject.keys();
		while(iterator.hasNext()) {
			String key = (String) iterator.next();
			String value = jsonObject.getString(key);
			paraMap.put(key, value);
		}
		
		Parameter[] parameters = method.getParameters();
		for(Parameter parameter : parameters) {
			Object obj = new Object();
			AParameter aParameter = parameter.getAnnotation(
					AParameter.class);
			String id = aParameter.value();
			String paraValue = paraMap.get(id);
			Type paraType = parameter.getParameterizedType();
			
			//根据参数类型来处理参数
			obj = dealParameter(paraType, paraValue);
			paras.add(obj);
		}
		
		return paras.toArray();
	}
	
	/**
	 * 处理参数，参数为复杂类型，如List<String>等
	 * 
	 * @param type
	 * @param value
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> Object dealParameter(Type type, String value) {
		Object object = null;
		
		if(type instanceof ParameterizedType) { //处理泛型
			Type[] typeArguments = ((ParameterizedType)type).
					getActualTypeArguments(); //得到泛型元素的类型
			Type typeName = 
					((ParameterizedType) type).getRawType(); //得到参数的实际类型，为了处理泛型，如List<String>,这一步得到List
			if(typeName.equals(List.class)) {
				List<T> list = new ArrayList<T>();
				
				for(int i = 0; i < typeArguments.length;i++) {
					JSONArray jsonArray = JSONArray.fromObject(value); //若为List，将参数的json值转化为JSONArray
					Iterator<?> it = jsonArray.iterator();
					while(it.hasNext()) {
						JSONObject obj = (JSONObject) it.next();
						T obje = (T) dealParameter( //递归处理参数，处理如List<List<String>>形式
								 typeArguments[i], obj.toString());
						list.add((T) obje);
					}
				}
				object = list;
			} else if(typeName.equals(Map.class)) {
				Map<T, T> map = new HashMap<T, T>();
				
				JSONArray jsonObject1 = JSONArray.fromObject(value);
				Iterator<?> iterator1 = jsonObject1.iterator();
				while(iterator1.hasNext()) {
					JSONObject jsonObject = (JSONObject) iterator1.next();
					Iterator<?> iterator = jsonObject.keys();
					while(iterator.hasNext()) {
						String key = (String) iterator.next();
						T realKey = (T) dealParameter(typeArguments[0], key);
						String keyValue = jsonObject.getString(key);
						T realValue = (T) dealParameter(typeArguments[1], keyValue);
						map.put(realKey, realValue);
					}
				}
			
				object = map;
			}
		} else if(type instanceof Class) { //处理非泛型
			object = dealBaseData(type, value);
		}
		
		return object;
	}
	
	/**
	 * 处理简单类型
	 * 
	 * @param klass
	 * @param message
	 * @return
	 */
	public Object dealBaseData(Type klass, String message) {
		Object obj = new Object();
		
		if(klass.equals(Integer.class)) {
			obj = Integer.valueOf(message);
		} else if(klass.equals(Short.class)) {
			obj = Short.valueOf(message);
		} else if(klass.equals(Long.class)) {
			obj = Long.valueOf(message);
		} else if(klass.equals(Double.class)) {
			obj = Double.valueOf(message);
		} else if(klass.equals(Float.class)) {
			obj = Float.valueOf(message);
		} else if(klass.equals(Character.class)) {
			obj = message.charAt(0);
		} else if(klass.equals(Byte.class)) {
			obj = Byte.valueOf(message);
		} else if(klass.equals(Boolean.class)) {
			obj = Boolean.valueOf(message);
		} else if(klass.equals(String.class)) {
			obj = message.toString();
		} else {
			obj = dealPOJO(klass, message);
		}
		return obj;
	}

	public Object dealPOJO(Type klass, String message) {
		Object object = null;
		Map<String, String> parameterMap = new HashMap<String, String>();
		JSONObject jsonObject = JSONObject.fromObject(message);
		Iterator<?> iterator = jsonObject.keys();
		while(iterator.hasNext()) {
			String key = (String) iterator.next();
			String name = "set" + key.substring(0, 1).toUpperCase()
								+ key.substring(1);
			String value = jsonObject.getString(key);
			parameterMap.put(name, value);
		}
		
		try {
			Class<?> clazz = Class.forName(klass.getTypeName());
			object = clazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Method[] methods = object.getClass().getDeclaredMethods();
		for(Method method : methods) {
			String methodName = method.getName();
			if(!parameterMap.containsKey(methodName)) {
				continue;
			}
			try {
				method.invoke(object, parameterMap.get(methodName));
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		return object;
	}
}
