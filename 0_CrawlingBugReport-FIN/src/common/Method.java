package common;

public class Method {
	String methodName;
	String returnType;
	String parameters;
	public Method(String methodName2, String returnTypeString, String parameters2) {
		this.methodName = methodName2;
		this.returnType = returnTypeString;
		this.parameters = parameters2;
	}
	public Method() {
		// TODO Auto-generated constructor stub
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public String getReturnType() {
		return returnType;
	}
	public void setReturnType(String returnType) {
		this.returnType = ForXML.forXML(returnType);
	}
	public String getParameters() {
		return parameters;
	}
	public void setParameters(String parameters) {
		this.parameters = ForXML.forXML(parameters);
	}
	@Override
	public String toString() {
		return "Method [methodName=" + methodName + ", returnType=" + returnType + ", parameters=" + parameters + "]";
	}
	
}
