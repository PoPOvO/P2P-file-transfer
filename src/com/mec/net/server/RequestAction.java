package com.mec.net.server;

import java.lang.reflect.Method;
import java.util.Map;

import com.mec.deal_mapping.core.ParameterIoC;
import com.mec.deal_mapping.core.Running;
import com.mec.deal_mapping.model.ActionDefinition;
import com.mec.deal_mapping.model.IRequestAction;

public class RequestAction implements IRequestAction{
	public RequestAction() {
	}

	//message为参数，为json形式
	@Override
	public String dealRequest(String action, String message) {
		String returnMess = "";
		Running annotationScanContext = 
				new Running();
		Map<String, ActionDefinition> map = 
				annotationScanContext.getMap();
		if(!map.containsKey(action)) {
			return "";
		}
		try {
			Object object = map.get(action).getObject();
			Method method = map.get(action).getMethod();
			
			if(message == null) {
				returnMess = (String) method.invoke(object, null);
				return returnMess;
			}
			
			ParameterIoC paraIoC = new ParameterIoC();
			//获得和参数类型对应的参数值数组
			Object[] paraObject = 
					paraIoC.addParameter(object, method, message);
			
			returnMess = (String) method.invoke(object, paraObject); //调用和action对应的方法
		}catch (Exception e1) {
			e1.printStackTrace();
		} 
		return returnMess;
	}
}
