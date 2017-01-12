package ver1.finish.ver;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

//import java.io.File;
//import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
		if(A_Main.project.equals("swt") || A_Main.project.equals("ui")){
			url = "https://bugs.eclipse.org/bugs/buglist.cgi?chfieldfrom="+targetSince+"&chfieldto="+targetUntil+"&component="+A_Main.project
					+"&limit=0&order=bug_status%2Cpriority%2Cassigned_to%2Cbug_id&product=platform&query_format=advanced&resolution="+targetResolution;
		}else if(A_Main.project.equals("jdt")){
			url = "https://bugs.eclipse.org/bugs/buglist.cgi?chfieldfrom="+targetSince+"&chfieldto="+targetUntil+"&component=ui"
					+"&limit=0&order=bug_status%2Cpriority%2Cassigned_to%2Cbug_id&query_format=advanced&resolution="+targetResolution+"&product="+A_Main.project;
		}else
			url = "https://bugs.eclipse.org/bugs/buglist.cgi?chfieldfrom="+targetSince+"&chfieldto="+targetUntil+"&"
			+"limit=0&order=bug_status%2Cpriority%2Cassigned_to%2Cbug_id&query_format=advanced&resolution="+targetResolution+"&product="+A_Main.project;
		
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
			parse(id[i]);
			System.out.println(i +":" + id[i] +" ORIGINAL LIST FINISH");
		}
		//dupList = db.getDupID();
		for(int i=0;i<dupList.size();i++)
		{
			parse(dupList.get(i));
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
	public void parse(int bugID) throws Exception
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar1; Calendar calendar2;
		calendar1 = Calendar.getInstance();
		calendar2 = Calendar.getInstance();
		try{
			Document doc = Jsoup.connect(url+bugID).timeout(0).get();
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
			if(bugDes.length()>99999)bugDes=bugDes.substring(0, 9999);
			
			if(contents.equals(bugDes))
				return;
			else{
				db.insertBugReport(bugID, prdName, compName,prodVersion, bugAut.replace("'","."), openDate, modifiedDate, bugStatus, severity, bugSum.replace("'","."), bugDes.replace("'","."));
				contents = bugDes;			
				if(doc.select("span#static_bug_status").text().contains("bug")){				
					String dupID = doc.select("span#static_bug_status").text().split(" of bug ")[1];
					if(!dupList.contains(Integer.parseInt(dupID)))
						dupList.add(Integer.parseInt(dupID));
					if(Property.getInstance().getTargetResolution().equals("DUPLICATE"))
						db.insertDuplicate(bugID,Integer.parseInt(dupID));
				}
			}
			
			String thresholdDate = openDate;
			int count = 0;
	    	
			for(int j=1;;j++)
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
			
			//System.out.println(openDate+" "+thresholdDate);
			Elements attachments = doc.select("a[href][title]");		
			for(int j = 1;j<10; j++){
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
		

	}
	
	public void quit() throws Exception
	{
		db.exit();
	}

}
