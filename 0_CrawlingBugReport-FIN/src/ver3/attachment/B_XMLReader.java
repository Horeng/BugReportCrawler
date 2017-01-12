package ver3.attachment;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.ExtBugReport;
import common.Comment;
import common.Commit;
import common.FileInfo;
import common.Method;

public class B_XMLReader {
	
	
	public static ArrayList<ExtBugReport> parser(String path) throws Exception {
		
		System.out.println(path);
		ArrayList<ExtBugReport> bugRepository = new ArrayList<ExtBugReport>();
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(path);
		
		Element root = doc.getDocumentElement();
		
		NodeList bugNodes = root.getElementsByTagName("bug");
		
		for(int i = 0; i<bugNodes.getLength(); i++){
			int bugID;
			Node bugNode = bugNodes.item(i);
			NamedNodeMap bugNodeAttrs = bugNode.getAttributes();
			ExtBugReport bug = new ExtBugReport();
			bug.setBugID(Integer.parseInt(bugNodeAttrs.getNamedItem("id").getNodeValue()));
			bugID = Integer.parseInt(bugNodeAttrs.getNamedItem("id").getNodeValue());
			bug.setOpenDate(String.valueOf(bugNodeAttrs.getNamedItem("opendate").getNodeValue()));
			bug.setModifiedDate(String.valueOf(bugNodeAttrs.getNamedItem("fixdate").getNodeValue()));			
			
			NodeList bugNodeChildNodes=bugNode.getChildNodes();
			for(int j = 0; j<bugNodeChildNodes.getLength(); j++){
				Node bugChild = bugNodeChildNodes.item(j);
				if(bugChild.getNodeType() ==Node.ELEMENT_NODE){
					if(bugChild.getNodeName().equals(String.valueOf("buginformation"))){
						NodeList bugInfoChild = bugChild.getChildNodes();
						for(int k = 0; k<bugInfoChild.getLength(); k++){
							Node bugInfo = bugInfoChild.item(k);
							if(bugInfo.getNodeName()==String.valueOf("summary"))
								bug.setSummury(bugInfo.getChildNodes().item(0).getNodeValue());
							if(bugInfo.getNodeName().equals(String.valueOf("description"))){
								try{
									bug.setDescription(bugInfo.getChildNodes().item(0).getNodeValue());
								}catch(Exception e){
									bug.setDescription("");
								}								
							}
							if(bugInfo.getNodeName().equals(String.valueOf("comments"))){
								NodeList bugCommentChild = bugInfo.getChildNodes();
								for(int l = 0; l<bugCommentChild.getLength(); l ++){
									Node comment = bugCommentChild.item(l);
									if(comment.getNodeName().equals(String.valueOf("comment"))){	
										NamedNodeMap commentAttrs = comment.getAttributes();
										Comment com = new Comment();										
										com.setCommentID(Integer.parseInt(commentAttrs.getNamedItem("id").getNodeValue()));
										com.setDate(String.valueOf(commentAttrs.getNamedItem("date").getNodeValue()));
										com.setAuthor(String.valueOf(commentAttrs.getNamedItem("author").getNodeValue()));											
										try{
											com.setContent(comment.getChildNodes().item(0).getNodeValue());
										}catch (NullPointerException e){											
											com.setContent("");
										}	
										bug.addComment(com);										
									}
								}
							}
						}
					}		
					if(bugChild.getNodeName().equals(String.valueOf("fixedCommits"))){
						NodeList comitChild = bugChild.getChildNodes();
						for(int k = 0; k<comitChild.getLength(); k++){							
							Node comt = comitChild.item(k);
							if(comt.getNodeName().equals(String.valueOf("commit"))){
								Commit commit = new Commit();
								NamedNodeMap commitAttrs = comt.getAttributes(); 
								commit.setCommitID(String.valueOf(commitAttrs.getNamedItem("id").getNodeValue()));
								commit.setAuthor(String.valueOf(commitAttrs.getNamedItem("author").getNodeValue()));
								//Temporally
								if(String.valueOf(commitAttrs.getNamedItem("date").getNodeValue())=="")
									commit.setDate(bug.getModifiedDate());
								else
									commit.setDate(String.valueOf(commitAttrs.getNamedItem("date").getNodeValue()));
								NodeList fileChild = comt.getChildNodes();
								ArrayList<FileInfo> fileList = new ArrayList<FileInfo>();
								for(int l = 0; l<fileChild.getLength(); l++){
									Node file = fileChild.item(l);
									if(file.getNodeName().equals(String.valueOf("file"))){
										if(file.getAttributes().getNamedItem("name").getNodeValue().toString()!=""){
											FileInfo fileInfo = new FileInfo();
											fileInfo.setFileName(file.getAttributes().getNamedItem("name").getNodeValue().toString());
											NodeList fileInfoChild = file.getChildNodes();
											ArrayList<Method> methodList = new ArrayList<Method>();
											for(int m=0; m<fileInfoChild.getLength(); m++){
												Node methodNode = fileInfoChild.item(m);
												Method method = new Method();
												if(methodNode.getNodeName().equals(String.valueOf("method"))){													
													method.setMethodName(methodNode.getAttributes().getNamedItem("name").getNodeValue().toString());
													if(String.valueOf(methodNode.getAttributes().getNamedItem("returnType").getNodeValue())=="")
														method.setReturnType("");
													else
														method.setReturnType(methodNode.getAttributes().getNamedItem("returnType").getNodeValue().toString());
													
													if(String.valueOf(methodNode.getAttributes().getNamedItem("parameters").getNodeValue())=="")
														method.setParameters("");
													else
														method.setParameters(methodNode.getAttributes().getNamedItem("parameters").getNodeValue().toString());
													methodList.add(method);
													
													
												}											
											}
											fileInfo.setMethodList((ArrayList<Method>) methodList.clone());
											fileList.add(fileInfo);
										}
									}
								}								
								commit.setFileList((ArrayList<FileInfo>) fileList.clone());								
								bug.addCommit(commit);
							}			
						}
					}
				}
			}
			bugRepository.add(bug);
		}			
		return (ArrayList<ExtBugReport>) bugRepository.clone();					
	}
	
}
