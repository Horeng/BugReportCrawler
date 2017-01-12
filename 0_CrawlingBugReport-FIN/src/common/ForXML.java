package common;

public class ForXML {

	public static String forXML(String content){
		String result="";
		result = content.replace("&", "&amp;");
		result = result.replace("<", "&lt;");
		result = result.replace(">", "&gt;");
		result = result.replace("'", "&apos;");
		result = result.replace("\"", "&quot;");
		return result;
	}
}
