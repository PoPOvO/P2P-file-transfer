package com.mec.deal_mapping.information;

import com.mec.deal_mapping.annotation.Component;
import com.mec.deal_mapping.model.StudentModel;

import java.util.List;
import java.util.Map;

import com.mec.deal_mapping.annotation.AMethod;
import com.mec.deal_mapping.annotation.AParameter;

@Component
public class Information {
	public Information() {
	}
	
	@AMethod(action = "getStudent")
	public void getStudentInfo(@AParameter(value = "id")String id,
							   @AParameter(value = "modelList")
									List<StudentModel> modelList,
							   @AParameter(value = "modelMap")
									Map<String, StudentModel> modelMap) {
		System.out.println("id:" + id + "\n");
		System.out.println("modellist:");
		for(int i = 0;i < modelList.size();i++) {
			System.out.println(modelList.get(i));
		}
		System.out.println("\nmodelMap:");
		for(String string : modelMap.keySet()) {
			System.out.println("key:" + string + "\n" + "value:"
								+ modelMap.get(string));
		}
	}
	
	@AMethod(action = "getTeacher")
	public void getTeacherInfo(@AParameter(value = "mdoelList")List<
									List<StudentModel>> modelList) {
		for(int i = 0; i < modelList.size(); i++) {
			System.out.println("大list第" + i + "层" + " " + modelList.get(i));
			for(int j = 0; j < modelList.get(i).size(); j++) {
				System.out.println("小list第" + j + " " 
			+ modelList.get(i).get(j));
			}
		}
	}
	
	@AMethod(action = "getMessage")
	public String getMessageInfo(@AParameter(value = "id")String id) {
		String message = "已经处理了action";
		return message;
	}
}
