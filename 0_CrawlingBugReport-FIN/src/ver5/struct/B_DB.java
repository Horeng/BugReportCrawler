package ver5.struct;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import common.ForXML;
import common.Property;

public class B_DB {
	private Connection conn = null;
	
	B_DB() throws Exception
	{
		Class.forName("org.h2.Driver");
		conn = DriverManager.getConnection("jdbc:h2:./DB/"+Property.getInstance().getTargetResolution()+"/"+A_Main.project,"sa","");
		System.out.println("-------- CONNECT WITH "+Property.getInstance().getTargetResolution()+" "+A_Main.project+" DB ----------");;
		
		if(conn!=null) createTable();
	}		
	
	public Connection getConn()
	{
		return conn;
	}

	private void createTable() throws Exception
	{
		Statement q = conn.createStatement();
		try
		{
			q.execute("Create Table BUG_REPORT("
					+ "BUG_ID int PRIMARY KEY,"
					+ "PRD_NAME varchar(255),"
					+ "COMP_NAME varchar(255),"
					+ "PRD_VER varchar(50),"
					+ "BUG_AUT VARCHAR(255),"
					+ "BUG_OPEN_DATE DATETIME,"
					+ "BUG_MODIFY_DATE DATETIME,"
					+ "BUG_STATUS VARCHAR(255),"
					+ "BUG_SEVER VARCHAR(255),"
					+ "BUG_SUM VARCHAR(255),"
					+ "BUG_DES VARCHAR(99999),"
					+ "BUG_DES_STEP VARCHAR(9999),"
					+ "BUG_DES_OBS VARCHAR(9999),"
					+ "BUG_DES_EXP VARCHAR(9999),"
					+ "BUG_DES_PATCH VARCHAR(99999),"
					+ "BUG_DES_STRACE VARCHAR(9999),"
					+ "BUG_DES_CODE_EX VARCHAR(9999),"
					+ "BUG_DES_ENUM VARCHAR(9999),"
					+ "NUM_COMMENT INT,"
					+ "NUM_HISTORY INT);");
			
			System.out.println("---BUG REPORT TABLE CREATED...");
		}
		catch(Exception e)
		{
			System.out.println("---BUG REPORT TABLE CREATION ERROR...");
			//e.printStackTrace();
		}

		try
		{
			q.execute("CREATE TABLE COMMENT("
					+ "BUG_ID int,"
					+ "COM_DATE DATETIME,"
					+ "COM_DES VARCHAR(99999),"
					+ "COM_AUT VARCHAR(255),"
					+ "PRIMARY KEY(BUG_ID,COM_DATE));");
			System.out.println("---COMMENT TABLE CREATED...");
		}
		catch(Exception e)
		{
			System.out.println("---COMMENT TABLE CREATION ERROR...");
		}
		

		try
		{
			q.execute("CREATE TABLE ATTACHMENT("
					+ "BUG_ID int,"
					+ "ATTACH_ID int,"
					+ "ATTACh_TYPE VARCHAR(255),"
					+ "ATTACH_DESC VARCHAR(999999),"
					+ "PRIMARY KEY(BUG_ID,ATTACH_ID));");
			System.out.println("---ATTACHMENT TABLE CREATED...");
		}
		catch(Exception e)
		{
			System.out.println("---ATTACHMENT TABLE CREATION ERROR...");
		}
		
		try
		{
			q.execute("CREATE TABLE DUPLICATE("
					+ "BUG_ID int,"
					+ "DUP_BUG_ID int,"
					+ "PRIMARY KEY(BUG_ID,DUP_BUG_ID));");
			System.out.println("---DUPLICATE TABLE CREATED...");
		}
		catch(Exception e)
		{
			System.out.println("---DUPLICATE TABLE CREATION ERROR...");
		}
		
		try
		{
			q.execute("CREATE TABLE FIXEDFILE("
					+ "BUG_ID int,"
					+ "FILE_NAME VARCHAR(255),"
					+ "Commit_Date DATETIME,"
					+ "COMMIT_ID VARCHAR(255),"
					+ "COMMITER VARCHAR(255),"
					+ "SUMMARY VARCHAR(9999),"
					+ "MESSAGE VARCHAR(9999),"
					+ "CHANGE_TYPE VARCHAR(255),"
					+ "count int);");
					//+ "PRIMARY KEY(BUG_ID,COMMIT_ID));");
			System.out.println("---FIXED FILE TABLE CREATED...");
		}
		catch(Exception e)
		{
			System.out.println("---FIXED FILE TABLE CREATION ERROR...");
			System.out.println(e.getMessage());
		}
		
		
		try
		{
			q.execute("CREATE TABLE COSINESIMILARITY("
				+	"BUG_ID int,"
				+	"FILENAME VARCHAR(255),"
				+	"PRD_NAME_CS double,"
				+	"BUG_OPEN_DATE_CS double,"
				+	"BUG_FIX_DATE_CS double,"
				+	"BUG_STA_CS double,"
				+	"BUG_SUM_CS double,"
				+	"BUG_DES_CS double,"
				+	"BUG_AUT_CS double,"
				+	"PRIMARY KEY(BUG_ID,FILENAME));");
			System.out.println("---COSINE SIMILARITY TABLE CREATED...");
		}
		catch(Exception e)
		{
			System.out.println("---COSINE SIMILARITY TABLE CREATION ERROR...");
			System.out.println(e.getMessage());
		}
		
	}

	public void dropTable() throws Exception
	{
		Statement q = conn.createStatement();
		q.execute("DROP TABLE BUG_REPORT;");
		System.out.println("---DELETE BUG REPORT TABLE...");
		q.execute("DROP TABLE COMMENT;");
		System.out.println("---DELETE COMMENT TABLE...");
		q.execute("DROP TABLE ATTACHMENT;");
		System.out.println("---DELETE ATTACHMENT TABLE...");
		q.execute("DROP TABLE DUPLICATE;");
		System.out.println("---DELETE DUPLICATE TABLE...");
		q.execute("DROP TABLE FIXEDFILE;");
		System.out.println("---DELETE FIXEDFILE TABLE...");
		q.execute("DROP TABLE COSINESIMILARITY;");
		System.out.println("---DELETE COSINE SIMILARITY TABLE...");
	}
		
	public void insertBugReport(int id,String prdName, String compName, String prodVersion, String author, String openDate, String modifiedDate, 
			String status, String severity, String summary, String description, String reproduct, String observed, String expected,String patch, String sTrace,
			String sourceCode,String enumer,int numComment,int numHistory) throws Exception
	{
		try
		{
		Statement q = conn.createStatement();
		q.execute("INSERT INTO BUG_REPORT VALUES ("+ id + ",'"+prdName+"','"+compName+"','"+prodVersion+"','"+author+"','"+openDate+"','"+modifiedDate+"','"+status
					+"','"+severity+"','"+summary+"','"+description+"','"+reproduct+"','"+observed+"','"+expected+"','"+patch+"','"+sTrace+"','"+sourceCode+"','"+enumer+"',"
							+ numComment+","+numComment+")");
		}
		catch(Exception e)
		{
			System.out.println("BUG REPORT #"+id+" INSERT ERROR");
			System.err.println(e);
		}
	}
	
	public void insertComment(int id,String date, String des, String aut) throws Exception
	{
		try
		{
			Statement q = conn.createStatement();
			q.execute("INSERT INTO COMMENT VALUES ("+ id + ",'"+date+"','"+des+"','"+aut+"');");
		}
		catch(Exception e)
		{
			//System.out.println("COMMENT IN BUG #"+id+" INSERT ERROR");
			//System.err.println(e);
		}
	}
	
	
	public void insertAttachment(int id,int attachID,String attachType, String desc) throws Exception
	{
		try
		{
			Statement q = conn.createStatement();
			q.execute("INSERT INTO ATTACHMENT VALUES ("+ id + ","+attachID+ ",'"+attachType+"','"+ForXML.forXML(desc)+"');");
		}
		catch(Exception e)
		{
			//System.out.println("Attachment IN BUG #"+id+"INSERT ERROR");
			//System.err.println(e);
			e.printStackTrace();
		}
	}

	
	public void insertDuplicate(int id,int dupID) throws Exception
	{
		try
		{
			Statement q = conn.createStatement();
			q.execute("INSERT INTO DUPLICATE VALUES ("+ id + ",'"+dupID+"');");
		}
		catch(Exception e)
		{
			//System.out.println("DUPLICATE IN BUG #"+id+"INSERT ERROR");
			//System.err.println(e);
		}
	}
	
	
	
	public void exit() throws Exception
	{
		if(conn!=null)
		{
			conn.close();
			System.out.println("---CONNECTION CLOSED...");
		}
	}

	public ArrayList<Integer> getBugID() {
		ArrayList<Integer> bugID = new ArrayList<Integer>();
		try
		{
			Statement q = conn.createStatement();
			ResultSet rs = q.executeQuery("SELECT bug_id from BUG_REPORT");
			while(rs.next()){
				bugID.add(rs.getInt("BUG_ID"));
			}
			System.out.println(bugID.size());
			rs = q.executeQuery("SELECT dup_bug_id from DUPLICATE");
			while(rs.next()){
				int dupBugID =rs.getInt("DUP_BUG_ID"); 
				if(!bugID.contains(dupBugID))
					bugID.add(dupBugID);
			}
			
			
		}
		catch(Exception e)
		{
			System.out.println("ERROR: GET BUG ID LIST");
			System.out.println(e.getMessage());
			
		}
		return bugID;
	}

	public void insertFixedFile(int id, String filename, String commitDate, String commitID, String commiter,
			String summary, String message, String changeType) {
		try
		{
			Statement q = conn.createStatement();
			q.execute("INSERT INTO FIXEDFILE VALUES ("+ id + ",'"+ filename + "','"+ commitDate+"','"+ commitID
					+"','"+ commiter+"','"+ summary+"','"+ message+"','"+ changeType+"',1);");
			System.out.println("SUCCESS "+id+" "+filename);
		}
		catch(Exception e)
		{
			//System.out.println("INSERT FIXEDFILE VALUES");
			/*//System.out.println("FIXED FILE IN BUG #"+id+"WITH SOURCE INSERT ERROR "+filename);			
			try {
				Statement q = conn.createStatement();
				int count = 0;
				ResultSet rs = q.executeQuery("SELECT COUNT FROM FIXEDFILE WHERE BUG_ID="+id+" and file_name='"+filename+"'");
				while(rs.next()){
					System.out.println("SAME");
					count = rs.getInt("COUNT");
				}
				q.execute("UPDATE FIXEDFILE SET COUNT = "+ (count+1) +"WHERE bug_id = "+id); 
				System.out.println("SUCCESS UPDATE "+count+" "+id+" "+filename);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}			
			System.err.println(e);*/
		}		
	}

	public ArrayList<Integer> getDupID() {
		ArrayList<Integer> bugID = new ArrayList<Integer>();
		try
		{
			Statement q = conn.createStatement();
			ResultSet rs = q.executeQuery("SELECT dup_bug_id from DUPLICATE");
			while(rs.next()){
				int dupBugID =rs.getInt("DUP_BUG_ID"); 
				if(!bugID.contains(dupBugID))
					bugID.add(dupBugID);
			}
			
		}
		catch(Exception e)
		{
			//System.out.println("ERROR: GET DUP BUG ID LIST");
		}
		return bugID;
	}

}
