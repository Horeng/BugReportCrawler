package common;

import java.util.ArrayList;

public class FileInfo {
	String fileName;
	ArrayList<Method> methodList = new ArrayList<Method>();
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public ArrayList<Method> getMethodList() {
		return methodList;
	}
	public void setMethodList(ArrayList<Method> methodList) {
		this.methodList = methodList;
	}
	public void addMethod(Method method){
		this.methodList.add(method);
	}
	@Override
	public String toString() {
		return "FileInfo [fileName=" + fileName + ", methodList=" + methodList + "]";
	}
	

}
