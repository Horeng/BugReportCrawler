package ver4.bugLocator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class C_DAO {
	
private Connection conn = null;
protected PreparedStatement ps = null;
protected ResultSet rs = null;
	
	C_DAO() throws Exception
	{
		Class.forName("org.h2.Driver");
		conn = DriverManager.getConnection("jdbc:h2:./DB/"+A_Main.type+"/attach/"+A_Main.project,"sa","");
		System.out.println("---Connected...");;		
	}		
	
	public void insertStructuredBug(int bugID, int attachID, String type, String desc) {
		String sql = "INSERT INTO test (bug_id, attach_id, type, desc)" +
					 " VALUES (?, ?, ?, ?)";
		
		try {
			ps=conn.prepareStatement(sql);
			ps.setInt(1, bugID);
			ps.setInt(2, attachID);
			ps.setString(3, type);
			ps.setString(4, desc);
			ps.executeUpdate();
		}catch(Exception e){
			System.out.println(e);
		}
	}
}
