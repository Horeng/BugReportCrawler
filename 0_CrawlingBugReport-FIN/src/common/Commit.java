package common;



import java.util.ArrayList;

public class Commit {
	String commitID;
	String author;
	String date; 
	String summury;
	String description;
	ArrayList<FileInfo> fileList = new ArrayList<FileInfo>();;
	public String getCommitID() {
		return commitID;
	}
	public void setCommitID(String commitID) {
		this.commitID = commitID;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = ForXML.forXML(author);
	}
	public String getDate() {
		return date;
	}
	public String getSummury() {
		return summury;
	}
	public void setSummury(String summury) {
		this.summury = ForXML.forXML(summury);
	}
	public ArrayList<FileInfo> getFileList() {
		return fileList;
	}
	public void setFileList(ArrayList<FileInfo> fileList) {
		this.fileList = fileList;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public void addFileList(FileInfo fileInfo){
		this.fileList.add(fileInfo);
	}
	@Override
	public String toString() {
		String result =  "Commit [commitID=" + commitID + ", author=" + author + ", date=" + date + ", fileList="; 
		for(int i = 0; i<fileList.size(); i++){
			result = result + fileList.get(i).getFileName()+" ";
		}
		result = result + " ]";
		return result;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = ForXML.forXML(description);
	}
}
