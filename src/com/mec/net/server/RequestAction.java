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

	//messageΪ������Ϊjson��ʽ
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
			//��úͲ������Ͷ�Ӧ�Ĳ���ֵ����
			Object[] paraObject = 
					paraIoC.addParameter(object, method, message);
			
			returnMess = (String) method.invoke(object, paraObject); //���ú�action��Ӧ�ķ���
		}catch (Exception e1) {
			e1.printStackTrace();
		} 
		return returnMess;
	}
}
