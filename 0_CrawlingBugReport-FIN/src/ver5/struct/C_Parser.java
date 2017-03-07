package ver5.struct;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import java.io.File;
//import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import common.Property;
//import java.util.*;
public class C_Parser {
	private String url;
	private int id[];
	private ArrayList<Integer> dupList = new ArrayList<Integer>();
	private B_DB db;
	static String attachUrl = "https://bugs.eclipse.org/bugs/attachment.cgi?id=";
	HashMap<Integer, String> severityList = new HashMap<Integer,String>();
	C_Parser(String url) throws Exception
	{
		System.out.println("---Parser Created...");
		this.url=url;
		db = new B_DB(); 
	}

	public void parse() throws Exception
	{
		String targetSince = Property.getInstance().getTargetSince();
		String targetUntil = Property.getInstance().getTargetUntil();
		String targetResolution = Property.getInstance().getTargetResolution();
		//search setting
		String url = "";
		if(A_Main.project.equals("SWT") || A_Main.project.equals("UI")){
			url = "https://bugs.eclipse.org/bugs/buglist.cgi?chfieldfrom="+targetSince+"&chfieldto="+targetUntil+"&component="+A_Main.project
					+"&limit=0&order=bug_status%2Cpriority%2Cassigned_to%2Cbug_id&product=platform&query_format=advanced&resolution="+targetResolution;
		}else if(A_Main.project.equals("JDT")){
			url = "https://bugs.eclipse.org/bugs/buglist.cgi?chfieldfrom="+targetSince+"&chfieldto="+targetUntil+"&component=ui"
					+"&limit=0&order=bug_status%2Cpriority%2Cassigned_to%2Cbug_id&query_format=advanced&resolution="+targetResolution+"&product="+A_Main.project;
		}else
			url = "https://bugs.eclipse.org/bugs/buglist.cgi?chfieldfrom="+targetSince+"&chfieldto="+targetUntil+"&"
			+"limit=0&order=bug_status%2Cpriority%2Cassigned_to%2Cbug_id&query_format=advanced&resolution="+targetResolution+"&product="+A_Main.project;
		
		System.out.println(url);
		Document doc = Jsoup.connect(url).maxBodySize(0).timeout(100000).get();
		System.out.println("GET SUCCESS");
		//normal major critical blocker enhancement minor trivial
		String countBug = doc.select("div#bugzilla-body span.bz_result_count").text();		
		for(int i=0;i<countBug.length();i++){
			if(countBug.charAt(i)==' ')
			{
				countBug = countBug.substring(0, i);
				break;
			}
		}
		
		System.out.println(countBug);
		id = new int[Integer.parseInt(countBug)];
		// Normal~Trivial = every severity
		// Get Every Duplicate BugID
		getID(doc,"normal");
		getID(doc,"major");
		getID(doc,"critical");
		getID(doc,"blocker");
		getID(doc,"enhancement");
		getID(doc,"minor");
		getID(doc,"trivial");
		
 
		//Number Of COMMIT
		for(int i=0;i<id.length;i++)
		{
			if(parse(id[i]))
				System.out.println(i +":" + id[i] +" ORIGINAL LIST FINISH");
		}
		//dupList = db.getDupID();
		for(int i=0;i<dupList.size();i++)
		{
			if(parse(dupList.get(i)))
				System.out.println(i +":" + dupList.get(i)+" DUP LIST FINISH // "+dupList.size());
		}
		
		//dupList = db.getBugID();
		//System.out.println(dupList.size());
		
		new E_Commiter(id,dupList,db);
	}		

	private void getID(Document doc, String str) throws Exception
	{
		String selectStr = "div#bugzilla-body table.bz_buglist tbody .bz_bugitem.bz_"+str+ " td.first-child.bz_id_column a";
		String IDs = doc.select(selectStr).text(); // = show_bug.cgi?id=...
		//System.out.println(IDs);
		int w=0;
		for(w=0;w<id.length;w++)
			if(id[w]==0)
				break;
		System.out.print(w);;
		for(int i=w;i<10000;)
		{
			if(IDs.length()<1) break;
			int j;
			String tmpID = null;
			for(j=0;j<10;j++)
			{
				char c;
				if(IDs.length()<j+1) break;
				c = IDs.charAt(j);
				if(c==' ')
				{
					tmpID = IDs.substring(0, j);
					IDs=IDs.substring(j+1);
					id[i]=Integer.parseInt(tmpID);	
					i++;
					severityList.put(Integer.parseInt(tmpID), str);
					break;
				}
			}
			if(!IDs.contains(" "))
			{
				id[i]=Integer.parseInt(IDs);
				severityList.put(Integer.parseInt(tmpID), str);
				break;
			}
		}
		for(w=0;w<id.length;w++)
			if(id[w]==0)break;
		System.out.println(" " + str + " " + w);
	}
	
	String contents = "";
	public boolean parse(int bugID) throws Exception
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar1; Calendar calendar2;
		calendar1 = Calendar.getInstance();
		calendar2 = Calendar.getInstance();
		try{
			Document doc = Jsoup.connect(url+bugID).timeout(5000).get();
			String prdName = doc.select("td#field_container_product").text();
			String compName = doc.select("td#field_container_component").text();		
			Elements table = doc.select("td#bz_show_bug_column_1 table");
			Element tr = table.select("tr").get(5);
			String prodVersion =tr.select("td").text();
			String bugDates = doc.select("td#bz_show_bug_column_2 tr td").text();
			String openDate = bugDates.substring(0,16)+":00";	
			String bugAut = doc.select("td#bz_show_bug_column_2 .vcard").text();
			int k = bugDates.indexOf("(History)");
			String modifiedDate = bugDates.substring(k-21,k-5)+":00";
			String bugStatus="";
			if(doc.select("span#static_bug_status").text().contains("bug")){
				bugStatus = doc.select("span#static_bug_status").text().split(" of bug ")[0];			
			}
			else
				bugStatus = doc.select("span#static_bug_status").text();
			String severity = severityList.get(bugID);
			String bugSum = doc.select("*#short_desc_nonedit_display").text();
			String bugDes = doc.select("div#c0 .bz_comment_text").text();
			
			String thresholdDate = openDate;
			int count = 0;
	    	int j = 0;
			for(j=1;;j++)
			{
				String bugCommentN = "c"+j;
				String query = "div#"+bugCommentN+" .vcard";
				String bugCommentAut = doc.select(query).text();				
				if(bugCommentAut.equals(""))
					break;
		  		query = "div#"+bugCommentN+" .bz_comment_text";
		  		String bugCommentText = doc.select(query).text();
		  		try
		  		{
		  			if(bugCommentText.length()>99999)
		  			{
		  				System.out.println("CUT BUG REPORT");
		  				bugCommentText=bugCommentText.substring(0, 9999);
		  			}
		  		}
		  		catch(Exception e){ System.out.println("TOO BIG COMMENT");} 
		  		
		  		query = "div#"+bugCommentN+" .bz_comment_time";
		  		String bugCommentTime = doc.select(query).text();
		  		if(bugAut.equals(bugCommentAut)){
		  			thresholdDate = bugCommentTime.substring(0, 19)+":00";		  			
				}else
					count++;
		  		db.insertComment(bugID, bugCommentTime.substring(0, 19), bugCommentText.replace("'","."), bugCommentAut.replace("'","."));
			}
			

			// MISOO Stack Trace Regular Expression
		    String stackTrace ="";
		    String tracePattern = "(([a-zA-Z0-9_\\-$]*\\.)*[a-zA-Z_<][a-zA-Z0-9_\\-$>]*" +
		        		"[a-zA-Z_<(][a-zA-Z0-9_\\-$>);/\\[]*" +
		        		"\\(([a-zA-Z_][a-zA-Z0-9_\\-]*\\.java:[0-9]*|[a-zA-Z_][a-zA-Z0-9_\\-]*\\.java\\((?i)inlined compiled code\\)|[a-zA-Z_][a-zA-Z0-9_\\-]*\\.java\\((?i)compiled code\\)|(?i)native method|(?i)unknown source)\\))";
		        
		    Pattern r = Pattern.compile(tracePattern);
		    Matcher m = r.matcher(bugDes);
	        while (m.find()) {
	        	String group = m.group();
	        	stackTrace =  stackTrace+"\n"+group;
	        	bugDes.replace(group, "");
	        }
	        String[] temp = bugDes.split("}");	        
	        
	        // Misoo Source Coe Regular Expression
	       /* String sourcePattern = "\\s*(public|private)\\s+class\\s+(\\w+)\\s+((extends\\s+\\w+)|(implements\\s+\\w+( ,\\w+)*))?\\s*\\{";	        
	        sourcePattern = sourcePattern + "}";
	        r = Pattern.compile(sourcePattern);
	        m = r.matcher(bugDes);
	        String sourceCode = "";
	        while(m.find()){
	        	String group = m.group();
	        	System.out.println(group);
	        	sourceCode =sourceCode +" "+group;
	        	bugDes.replace(group, "");
	        }
	        System.out.println(sourceCode);
	        
	        //sourcePattern = "\\{([^()]|(?R))*\\}";
	        //sourcePattern = "(\\{([^{}]|())*\\})";
	        sourcePattern = "$\\{(.*)\\}";	        
	        r = Pattern.compile(sourcePattern);
	        m = r.matcher(bugDes);
	        
	        while(m.find()){
	        	String group = m.group();
	        	sourceCode =sourceCode +" "+group;
	        	bugDes.replace(group, "");
	        }
	        System.out.println(sourceCode);
	        
	        // Wrong Regular Expression
	        sourcePattern = "^(?![\\s]*\\r?\\n|import|package|[\\s]*}\\r?\\n|[\\s]*\\|[\\s]*\\*|[\\s]*\\*).*\\r?\\n";
	        r = Pattern.compile(sourcePattern);
	        m = r.matcher(bugDes);
	        sourceCode = sourceCode="\n";
	        while(m.find()){
	        	String group = m.group();
	        	sourceCode =sourceCode  +" "+group;
	        	bugDes.replace(group, "");
	        }
	        
	        System.out.println(sourceCode);*/
	        
	        // MISOO STRUCTURE DATA
 			String reproduct = "";
 			String observed = "";
 			String expected ="";
 			if(Property.getInstance().getTargetStruct()){
 				if(! ((bugDes.contains("repro") || bugDes.contains("REPRO") || bugDes.contains("Repro") ||bugDes.contains("step") || bugDes.contains("STEP") || bugDes.contains("Step") ||
 						(bugDes.contains("1.") && bugDes.contains("2."))) 
 						&& (bugDes.contains("EXPECT") || bugDes.contains("expect") || bugDes.contains("Expect") || bugDes.contains("want")) 
 						&& (bugDes.contains("OBSERV") || bugDes.contains("observ") || bugDes.contains("Observ") || bugDes.contains("Actual") || bugDes.contains("actual") || bugDes.contains("ACTUAL")))){
 						//&& (bugDes.contains("result") || bugDes.contains("Result") || bugDes.contains("RESULT")))){
 					//System.out.println(bugDes);
 					System.err.println(bugID+" DOESNOT HAVE STRUCT INFO.");
 					return false;
 				}
 			}
 			
 			int repStartIndex = -1;
 			int obsStartIndex = -1;
 			int expStartIndex = -1;
 			
 			
 			if(bugDes.contains("step"))
 				repStartIndex = bugDes.indexOf("step");
 			else if(bugDes.contains("STEP"))
 				repStartIndex = bugDes.indexOf("STEP");
 			else if(bugDes.contains("Step"))
 				repStartIndex = bugDes.indexOf("Step");
 			else if(bugDes.contains("repro"))
 				repStartIndex = bugDes.indexOf("repro");
 			else if(bugDes.contains("REPRO"))
 				repStartIndex = bugDes.indexOf("REPRO");
 			else if(bugDes.contains("Repro"))
 				repStartIndex = bugDes.indexOf("Repro");
 			else if(bugDes.contains("1.") && bugDes.contains("2. "))
 				repStartIndex = bugDes.indexOf("1. ");
 			
 			if(bugDes.contains("OBSERV"))
 				obsStartIndex = bugDes.indexOf("OBSERV");
 			else if(bugDes.contains("observ"))
 				obsStartIndex = bugDes.indexOf("observ");
 			else if(bugDes.contains("Observ"))
 				obsStartIndex = bugDes.indexOf("Observ");
 			else if(bugDes.contains("Actual"))
 				obsStartIndex = bugDes.indexOf("Actual");
 			else if(bugDes.contains("actual"))
 				obsStartIndex = bugDes.indexOf("actual");
 			else obsStartIndex = bugDes.indexOf("ACTUAL");
 			
 			if(bugDes.contains("EXPECT"))
 				expStartIndex = bugDes.indexOf("EXPECT");
 			else if(bugDes.contains("expect"))
 				expStartIndex = bugDes.indexOf("expect");
 			else if(bugDes.contains("Expect"))
 				expStartIndex = bugDes.indexOf("Expect");
 			else
 				expStartIndex = bugDes.indexOf("want");
 			
 			int repEndIndex = repStartIndex;
 			int obsEndIndex = obsStartIndex;
 			int expEndIndex = expStartIndex;
 			if(repEndIndex > obsEndIndex){
 				if(repEndIndex > expEndIndex){
 					repEndIndex = bugDes.length()-1;
 					if(obsEndIndex > expEndIndex){
 						obsEndIndex = repStartIndex-1;
 						expEndIndex = obsStartIndex-1;
 					}else{
 						expEndIndex = repStartIndex-1;
 						obsEndIndex = expStartIndex-1;
 					}					
 				}else {
 					expEndIndex = bugDes.length()-1;
 					repEndIndex = expStartIndex-1;
 					obsEndIndex = repStartIndex-1;
 				}
 			}else if (obsEndIndex > expEndIndex){
 				if(obsEndIndex > repEndIndex){
 					obsEndIndex = bugDes.length()-1;
 					if(expEndIndex > repEndIndex){
 						expEndIndex = obsStartIndex-1;
 						repEndIndex = expStartIndex-1;
 					}else{
 						expEndIndex = repStartIndex-1;
 						repEndIndex = obsStartIndex-1;
 					}
 				}else{
 					repEndIndex = bugDes.length()-1;
 					obsEndIndex = repStartIndex-1;
 					expEndIndex = obsStartIndex-1;
 				}
 			}else if (expEndIndex > obsEndIndex){
 				if(expEndIndex > repEndIndex){
 					expEndIndex = bugDes.length()-1;
 					if(obsEndIndex > repEndIndex){
 						repEndIndex = obsStartIndex-1;
 						obsEndIndex = expStartIndex-1;
 					}else{
 						obsEndIndex = repStartIndex-1;
 						repEndIndex = expStartIndex-1;
 					}
 				}else{
 					repEndIndex = bugDes.length()-1;
 					obsEndIndex = expStartIndex-1;
 					expEndIndex = repStartIndex-1;
 				}
 			}
	        
	        
			if(bugDes.length()>99999)bugDes=bugDes.substring(0, 9999);			
			
			//MISOO GET HISTORY NUMBER
			Document doc3 = Jsoup.connect("https://bugs.eclipse.org/bugs/show_activity.cgi?id="+bugID).maxBodySize(0).timeout(0).get();
			Elements historyList = doc3.select("div#bugzilla-body table tbody tr");
			
			System.out.println(j+" "+historyList.size());
			
			if(contents.equals(bugDes))
				return false;			
			else{
				db.insertBugReport(bugID, prdName, compName,prodVersion, bugAut.replace("'","."), openDate, modifiedDate, bugStatus, severity, bugSum.replace("'","."), bugDes.replace("'","."), 
						bugDes.substring(repStartIndex,repEndIndex).replace("'", "."), bugDes.substring(obsStartIndex,obsEndIndex).replace("'", "."), 
						bugDes.substring(expStartIndex,expEndIndex).replace("'", "."),"",stackTrace,"","",j,historyList.size()-1);
				contents = bugDes;			
				if(doc.select("span#static_bug_status").text().contains("bug")){				
					String dupID = doc.select("span#static_bug_status").text().split(" of bug ")[1];
					if(!dupList.contains(Integer.parseInt(dupID)))
						dupList.add(Integer.parseInt(dupID));
					if(Property.getInstance().getTargetResolution().equals("DUPLICATE"))
						db.insertDuplicate(bugID,Integer.parseInt(dupID));
				}
			}
			
			
			
			//System.out.println(openDate+" "+thresholdDate);
			Elements attachments = doc.select("a[href][title]");		
			for(j = 1;j<10; j++){
				String bugAttachN = "a"+j;
				String date = "";
				String data = doc.select("table#attachment_table").text();
				if(data.equals("Attachments Add an attachment (proposed patch, testcase, etc.)"))
					break;
				data = data.replace(" ", "");				
				String type = doc.select("table#attachment_table tr#"+bugAttachN+" td span").text();
				//System.out.println(type);
				if(type.contains(","))
					type = type.split(",")[1].replace(")","");
				if(type.contains("text") || type.contains("patch"))	{
					int attID = -1;
					String attach = "";
					try{
						date = doc.select("table#attachment_table tr#"+bugAttachN+" td span a").text();
						Date thresDate = format.parse(thresholdDate);				
						String attDate = date.substring(0,16)+":00:00";
						Date attachDate = format.parse(attDate);					
						calendar1.setTime(thresDate);
						calendar2.setTime(attachDate);
						System.out.println(attDate+" "+thresholdDate);
						if(calendar2.getTimeInMillis() > calendar1.getTimeInMillis())
							break;										
						attach = doc.select("table#attachment_table tr#"+bugAttachN+" td a").attr("href").split("id=")[1];					
						Document doc2 = Jsoup.connect(attachUrl+attach).maxBodySize(0).timeout(10000).get();
						String contents =  doc2.select("body").text();
						type = type.split(" ")[1];
						//System.out.println(bugID+" "+type+" "+attach+" "+contents);
						db.insertAttachment(bugID, Integer.parseInt(attach), type, contents);
					}catch(Exception e){
						e.printStackTrace();
						break;
					}
				}
			}
	    	
		}
		catch(Exception e){
			e.printStackTrace();
		
		}
		return true;
		

	}
	
	public void quit() throws Exception
	{
		db.exit();
	}

}
