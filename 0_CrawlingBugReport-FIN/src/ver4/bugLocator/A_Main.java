package ver4.bugLocator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import common.Attachment;
import common.ExtBugReport;
import common.Comment;

public class A_Main {
	static String type = "BLIA"; // BLIA , els
	static String project ="eclipse"; // swt, ui, aspectj, jdt, birt 
	static String brUrl = "https://bugs.eclipse.org/bugs/show_bug.cgi?id=";	
	static String attachUrl = "https://bugs.eclipse.org/bugs/attachment.cgi?id=";
	static String path = "E:\\0_Research\\2017-1_DuplicatedBugReport\\";
	static int struct = 1;//Structural
	static int attach = 1; 
	public static void main(String[] a) throws Exception
	{
		ArrayList<Integer> bugID = new ArrayList<Integer>();
		ArrayList<Integer> textbugID = new ArrayList<Integer>();
		ArrayList<ExtBugReport> bugRepository = B_XMLReader.parser(path+project+"BugRepository.xml");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar1; Calendar calendar2; Calendar calendar3;
		
		//C_DAO dao = new C_DAO();
		for(int i = 0; i<bugRepository.size(); i++){
			ExtBugReport bugreport = bugRepository.get(i);
			String description = bugreport.getDescription()+" ";		
			System.out.println(bugreport.getBugID());
			Document doc;
			try{
				doc = Jsoup.connect(brUrl+bugreport.getBugID()).maxBodySize(0).timeout(1000).get();
			}catch(Exception e){
				continue;
			}
			String author = doc.select("td#bz_show_bug_column_2 .vcard").text();
			Date openDate = format.parse(bugreport.getOpenDate());
			calendar1 = Calendar.getInstance();
			calendar1.setTime(openDate);
			Date thresholdDate = openDate;
			calendar2 = Calendar.getInstance();
			calendar2.setTime(thresholdDate);
			
			ArrayList<Comment> commentList = new ArrayList<Comment>();
			for(int j=1;;j++)
			{
				Comment comment = new Comment();
				String bugCommentN = "c"+j;
				String query = "div#"+bugCommentN+" .vcard";
				String bugCommentAut = doc.select(query).text();				
				if(bugCommentAut.equals(""))
					break;
		  		query = "div#"+bugCommentN+" .bz_comment_text";
		  		String bugCommentText = doc.select(query).text();
		  		try{
		  			if(bugCommentText.length()>99999)
		  			{
		  				System.out.println("CUT BUG REPORT");
		  				bugCommentText=bugCommentText.substring(0, 9999);
		  			}
		  		}
		  		catch(Exception e){ System.out.println("TOO BIG COMMENT");} 		  		
		  		query = "div#"+bugCommentN+" .bz_comment_time";
		  		String bugCommentTime = doc.select(query).text();
		  		comment.setAuthor(bugCommentAut);
		  		comment.setCommentID(j);
		  		comment.setContent(bugCommentText.replace("'","."));
		  		comment.setDate(bugCommentTime);
		  		commentList.add(comment);
			}			
			
			bugreport.setCommentList((ArrayList<Comment>) commentList.clone());
			
			for(int j = 0; j<commentList.size(); j ++){
				String reporter = commentList.get(j).getAuthor();
				//System.out.println(reporter+" "+author);
				if(reporter.equals(author))
					continue;
				else{
					thresholdDate =format.parse(commentList.get(j).getDate());
					calendar2 = Calendar.getInstance();
					calendar2.setTime(thresholdDate);
					break;
				}
			}
			//System.out.println(openDate+" "+thresholdDate);
			//System.out.println(calendar1.getTimeInMillis()+" "+calendar2.getTimeInMillis());
			HashMap<Integer, String> attachmentList = new HashMap<Integer, String>();
			if(attach == 1){
				Elements attachments = doc.select("a[href][title]");
				
				
				for(int j = 1;j<10; j++){
					String bugAttachN = "a"+j;
					String data = doc.select("table#attachment_table").text();
					if(data.equals("Attachments Add an attachment (proposed patch, testcase, etc.)"))
						break;
					data = data.replace(" ", "");
					String type = data.split(",")[1].split("\\)")[0];								
					try{
						if(!bugID.contains(bugreport.getBugID()))
							bugID.add(bugreport.getBugID());
						if(type.contains("text/plain") || type.contains("patch")){						
							String date = doc.select("table#attachment_table tr#"+bugAttachN+" td span a").text();
							//System.out.println(date);
							Date attachDate = format.parse(date.substring(0, 16)+":00");
							calendar3 = Calendar.getInstance();
							calendar3.setTime(attachDate);
							System.out.println(type);
							System.out.println(date+" "+format.format(thresholdDate)+" "+format.format(openDate));
							if(!(calendar3.getTimeInMillis() <= calendar2.getTimeInMillis()))
								break;
							attachmentList.put(Integer.parseInt(doc.select("table#attachment_table tr#"+bugAttachN+" td a").attr("href").split("id=")[1]),type);						
							if(!textbugID.contains(bugreport.getBugID()))
								textbugID.add(bugreport.getBugID());
						}
					}catch(Exception e){
						System.err.println(e.toString());
					}
				}
				if(attachmentList.isEmpty()) continue;
				System.out.println(attachmentList.size());
				ArrayList<Attachment> attachList = new ArrayList<Attachment>();
				String attachContent = "";
				Iterator iter = attachmentList.keySet().iterator();
				while(iter.hasNext()){
					int id = (int) iter.next();
					try{
						doc = Jsoup.connect(attachUrl+id).maxBodySize(0).timeout(10000).get();
						String contents = doc.select("body").text();
						System.out.println(attachmentList.size()+" "+bugreport.getBugID()+" "+id+" "+attachmentList.get(id)+" "+contents);
						/*Attachment attachment = new Attachment();
						attachment.setAttachmentID(id);
						attachment.setType(attachmentList.get(id));
						attachment.setContent(contents);
						attachList.add(attachment);*/
						if(struct == 1)
							attachContent = attachContent +"\n"+ contents;
						else
							description = description +"\n"+ contents;
						//dao.insertStructuredBug(bugreport.getBugID(), id, attachmentList.get(id), contents);
					}catch(Exception e){
						System.out.println(e.getMessage());
					}
				}
				
				bugreport.setDescription(description);
				if(attachContent.length() >= 5){
					Attachment attachment = new Attachment();
					attachment.setContent(attachContent);
					attachList.add(attachment);
					bugreport.setAttachmentList(attachList);
				}
			}
			bugRepository.set(i, bugreport);
		}
		
		if(attach == 1 && struct == 1)
			XMLWriter.writeBugRepository(bugRepository, path+"StructuralAttachExtended" +project+"BugRepository.xml", project, struct);
		else if(attach == 1 && struct == 0)
			XMLWriter.writeBugRepository(bugRepository, path+"AttachExtended" +project+"BugRepository.xml", project, struct);
		else if(attach == 0 && struct == 0)
			XMLWriter.writeBugRepository(bugRepository, path+"Extended" +project+"BugRepository.xml", project, struct);
		
		System.out.println(bugID.size()+" "+textbugID.size());
		for(int i = 0 ; i<textbugID.size(); i++)
			System.out.println(textbugID.get(i));
		
	}

}
