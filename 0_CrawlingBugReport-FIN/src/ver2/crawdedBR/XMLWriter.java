package ver2.crawdedBR;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

import common.BugReport;
import common.Comment;
import common.Commit;
import common.ExtBugReport;
import common.FileInfo;
import common.Method;

public class XMLWriter {
	
	public static void writeBugRepository(ArrayList<ExtBugReport> bugRepository,String path, String project) throws Exception
	{
		System.out.println(bugRepository.size());
		
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(path));
		
		bw.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		bw.write("<bugrepository name=\""+project+"\">\n");
		for(int i = 0; i<bugRepository.size(); i++){
			ExtBugReport bug = bugRepository.get(i);
			bw.write("<bug id=\""+bug.getBugID()+"\" opendate=\""+bug.getOpenDate()+"\" fixdate=\""+bug.getModifiedDate()+"\">\n");
			bw.write("<buginformation>\n");
			bw.write("<summary>"+bug.getSummury()+"</summary>\n");
			bw.write("<description>"+bug.getDescription()+"</description>\n");
			if(bug.getAttachmentList().size()>0)
				bw.write("<attachment>"+bug.getAttachmentList().get(0).getContent()+"</attachment>\n");
			else
				bw.write("<attachment></attachment>\n");
			bw.write("<comments>\n");
			ArrayList<Comment> commentList = bug.getCommentList();
			for(int j = 0; j<commentList.size(); j++){
				bw.write("<comment id=\""+commentList.get(j).getCommentID()+"\" date=\""+commentList.get(j).getDate()+"\" author=\""+commentList.get(j).getAuthor()+"\">\n");
				bw.write(commentList.get(j).getContent());
				bw.write("</comment>\n");
			}
			bw.write("</comments>\n");
			bw.write("</buginformation>\n<fixedCommits>\n");
			ArrayList<Commit> commitList = bug.getCommitList();
			for(int j = 0; j<commitList.size(); j++){
				try{
					bw.write("<commit id=\""+bug.getCommitList().get(j).getCommitID()+"\" author=\""+bug.getCommitList().get(j).getAuthor()+"\" date=\""+bug.getCommitList().get(j).getDate()+"\">\n");
					//bw.write("<commit id=\"\" author=\"\" date=\"\">\n");
				}catch(Exception e){
					bw.write("<commit id=\" \" author=\" \" date=\" \">\n");
				}
				ArrayList<FileInfo> fileList = bug.getCommitList().get(j).getFileList();
				for(int k = 0; k<fileList.size(); k++){
					bw.write("<file name=\""+fileList.get(k).getFileName()+"\">\n");
					ArrayList<Method> methodList =fileList.get(k).getMethodList(); 
					for(int l = 0; l<methodList.size(); l++){
						bw.write("<method name=\""+methodList.get(l).getMethodName()+"\" returnType=\""+methodList.get(l).getReturnType()+"\" parameters=\""+methodList.get(l).getParameters()+"\"/>\n");
					}
					bw.write("</file>\n");					
				}
				bw.write("</commit>\n");
			}
			bw.write("</fixedCommits>\n</bug>\n");
		}
		bw.write("</bugrepository>");
		
		bw.close();		
	}

}
