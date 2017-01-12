package ver4.bugLocator;

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
						}
					}		
					ArrayList<Commit> commitList = new ArrayList<Commit>();
					Commit commit = new Commit();
					ArrayList<FileInfo> fileList = new ArrayList<FileInfo>();
					if(bugChild.getNodeName().equals(String.valueOf("fixedFiles"))){						
						NodeList bugInfoChild = bugChild.getChildNodes();
						for(int k = 0; k<bugInfoChild.getLength(); k++){
							Node bugInfo = bugInfoChild.item(k);							
							FileInfo fileInfo = new FileInfo();
							if(bugInfo.getNodeName()==String.valueOf("file")){
								fileInfo.setFileName(bugInfo.getChildNodes().item(0).getNodeValue());
								fileList.add(fileInfo);
							}
						}						
					}
					commit.setFileList((ArrayList<FileInfo>) fileList.clone());
					commitList.add(commit);
					bug.setCommitList((ArrayList<Commit>) commitList.clone());
				}
			}
			bugRepository.add(bug);
		}			
		return (ArrayList<ExtBugReport>) bugRepository.clone();					
	}
	
}
