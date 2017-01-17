package ver5.struct;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import common.Attachment;
import common.BugReport;
import common.Comment;
import common.Commit;
import common.ExtBugReport;
import common.FileInfo;
import common.Property;

public class G_DAO {
	
private Connection conn = null;
protected PreparedStatement ps = null;
protected ResultSet rs = null;

static String attachUrl = "https://bugs.eclipse.org/bugs/attachment.cgi?id=";

	G_DAO() throws Exception
	{
		Class.forName("org.h2.Driver");
		conn = DriverManager.getConnection("jdbc:h2:./DB/"+Property.getInstance().getTargetResolution()+"/"+F_Main_XML.project,"sa","");
		System.out.println("---Connected..."+Property.getInstance().getTargetResolution()+" "+F_Main_XML.project);		
	}		
	
	public ArrayList<ExtBugReport> getBugIdList(){
		
		ArrayList<ExtBugReport> result = new ArrayList<ExtBugReport>();		
		String sql = "select * from bug_report order by bug_open_date asc";
		
		try{
			ps=conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				String bugID = String.valueOf(rs.getInt("BUG_ID"));
				ExtBugReport bugReport = new ExtBugReport();
				bugReport.setBugID(rs.getInt("BUG_ID"));
				bugReport.setProduct(rs.getString("PRD_NAME"));
				bugReport.setComponent(rs.getString("COMP_NAME"));
				bugReport.setProductVer(rs.getString("PRD_VER"));
				bugReport.setReporter(rs.getString("BUG_AUT"));
				bugReport.setOpenDate(rs.getString("BUG_OPEN_DATE"));
				bugReport.setModifiedDate(rs.getString("BUG_MODIFY_DATE"));
				bugReport.setStatus(rs.getString("BUG_STATUS"));
				bugReport.setSever(rs.getString("BUG_SEVER"));
				bugReport.setSummury(rs.getString("BUG_SUM"));
				bugReport.setDescription(rs.getString("BUG_DES"));
				
				ArrayList<Attachment> attachmentList = new ArrayList<Attachment>();
				String sql2 = "select * from attachment where bug_id = ?";
				PreparedStatement ps2 = conn.prepareStatement(sql2);
				ps2.setInt(1, Integer.parseInt(bugID));
				ResultSet rs2 = ps2.executeQuery();
				int index = 1;
				Attachment attachment = new Attachment();
				String attContent = "";
				while(rs2.next()){					
					attContent = attContent+"\n"+rs2.getString("ATTACH_DESC");
				}
				//System.out.println(attContent);
				attachment.setContent(attContent);
				attachmentList.add(attachment);
				bugReport.setAttachmentList((ArrayList<Attachment>) attachmentList.clone());
				
				ArrayList<Comment> commentList = new ArrayList<Comment>();
				String sql3 = "select * from comment where bug_id = ? order by com_date asc";
				PreparedStatement ps3 = conn.prepareStatement(sql3);
				ps3.setInt(1, Integer.parseInt(bugID));
				ResultSet rs3 = ps3.executeQuery();
				index = 1;
				while(rs3.next()){
					Comment comment = new Comment();
					comment.setCommentID(index);
					index++;
					comment.setDate(rs3.getString("COM_DATE"));
					comment.setAuthor(rs3.getString("COM_AUT"));
					comment.setContent(rs3.getString("COM_DES"));
					commentList.add(comment);
				}
				bugReport.setCommentList((ArrayList<Comment>) commentList.clone());
				
				String sql4 = "SELECT * FROM DUPLICATE where bug_id = ? or dup_bug_id = ?";
				ps2 = conn.prepareStatement(sql4);
				ps2.setInt(1, Integer.parseInt(bugID));
				ps2.setInt(2, Integer.parseInt(bugID));
				rs2 = ps2.executeQuery();
				ArrayList<Integer> dupBugList = new ArrayList<Integer>();
				while(rs2.next()){
					dupBugList.add(rs2.getInt("BUG_ID"));
					if(!dupBugList.contains(rs2.getInt("DUP_BUG_ID")))
						dupBugList.add(rs2.getInt("DUP_BUG_ID"));
				}
				if(Property.getInstance().getTargetResolution().equals("DUPLICATED") && dupBugList.isEmpty())
					continue;
				
				ArrayList<String> commitIDList = new ArrayList<String>();
				for(int i = 0; i<dupBugList.size(); i++){
					String sql5 = "SELECT COMMIT_ID FROM FIXEDFILE where bug_id = ? and change_type='MODIFY'";
					ps2 = conn.prepareStatement(sql5);
					ps2.setInt(1, dupBugList.get(i));
					rs2 = ps2.executeQuery();					
					while(rs2.next()){
						if(!commitIDList.contains(rs2.getString("COMMIT_ID")))
							commitIDList.add(rs2.getString("COMMIT_ID"));
					}
				}
				if(commitIDList.isEmpty())
					continue;
				
				ArrayList<Commit> commitList = new ArrayList<Commit>();				
				for(int i = 0; i<commitIDList.size(); i++){
					String sql5 = "SELECT * FROM FIXEDFILE where COMMIT_ID = ? and change_type='MODIFY'";
					ps2 = conn.prepareStatement(sql5);
					ps2.setString(1, commitIDList.get(i));
					rs2 = ps2.executeQuery();
					
					int first = 0;
					Commit commit = new Commit();
					ArrayList<FileInfo> fileList = new ArrayList<FileInfo>();
					while(rs2.next()){
						FileInfo file = new FileInfo();
						file.setFileName(rs2.getString("FILE_NAME"));
						fileList.add(file);
						if(first== 0){
							commit.setCommitID(rs2.getString("COMMIT_ID"));
							commit.setAuthor(rs2.getString("COMMITER"));
							commit.setDate(rs2.getString("COMMIT_DATE"));
							commit.setSummury(rs2.getString("SUMMARY"));
							commit.setDescription(rs2.getString("MESSAGE"));							
							first++;
						}
					}
					commit.setFileList((ArrayList<FileInfo>) fileList.clone());					
					commitList.add(commit);
				}
				if(commitList.isEmpty()) continue;
				bugReport.setCommitList((ArrayList<Commit>) commitList.clone());
				result.add(bugReport);
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}

}
