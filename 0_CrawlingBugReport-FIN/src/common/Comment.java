package common;


public class Comment {
	int commentID;
	String date;
	String author; 
	String content;
	public int getCommentID() {
		return commentID;
	}
	public void setCommentID(int commentID) {
		this.commentID = commentID;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = ForXML.forXML(author);
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = ForXML.forXML(content);
	}
	@Override
	public String toString() {
		return "Comment [commentID=" + commentID + ", date=" + date + ", author=" + author + ", content=" + content
				+ "]";
	}
	
}
