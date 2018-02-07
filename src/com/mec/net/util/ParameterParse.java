package com.mec.net.util;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ParameterParse {
	private JSONObject jsonObject;
	
	public ParameterParse() {
		jsonObject = new JSONObject();
	}
	
	public JSONObject getJSONObject() {
		return this.jsonObject;
	}
	
	String message = "{\"id\": \"03151352\",\"modelList\":"
			+ "[{\"id\":\"123\",\"password\":\"123\"},"
			+ "{\"id\":\"321\",\"password\":\"321\"}],"
			+ "\"modelMap\":"
			+ "[{\"123\":{\"id\":\"123\",\"password\":\"123\"},"
			+ "\"321\":{\"id\":\"321\",\"password\":\"321\"}}]}";
	
	public boolean isBaseData(Class<?> klass) {
		if(klass.equals(Integer.class) 
				|| klass.equals(Short.class) 
				|| klass.equals(Long.class) 
				|| klass.equals(Double.class) 
				|| klass.equals(Float.class) 
				|| klass.equals(Character.class) 
				|| klass.equals(Byte.class) 
				|| klass.equals(Boolean.class) 
				|| klass.equals(String.class)) {
			return true;
		}
		return false;
	}
	
	/*
	 * Map使用JSONObject
	 * List使用JSONArray
	 * */
	public ParameterParse addParameter(String name, Object object) {
		
		Class<?> klass = object.getClass();
		
		if(isBaseData(klass)) {
			jsonObject.accumulate(name, object);
		} else if(object instanceof List) {
			JSONArray jsonArray = JSONArray.fromObject(object);
			jsonObject.accumulate(name, jsonArray.toString());
		} else if(object instanceof Map) {
			JSONArray jsArray = JSONArray.fromObject(object);
			jsonObject.accumulate(name, jsArray.toString());
		} else {
			JSONObject jsObject = JSONObject.fromObject(object);
			jsonObject.accumulate(name, jsObject.toString());
		}
		
		return this;
	}
}
