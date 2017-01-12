package ver2.crawdedBR;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;

import common.BugReport;
import common.Commit;
import common.ExtBugReport;
import common.FileInfo;
import common.Method;
import common.Property;

public class Main_NewCommitID {
	static String path;
	static String project; 
	static String gitPosition = "";
	static Git git;
	static Repository repo;
	static String dataPath="";
	
	public static void main(String[] a) throws Exception
	{
		path = Property.getInstance().getOutputPath();
		project = Property.getTargetProduct();
		
		ArrayList<ExtBugReport> bugRepository = XMLReader.parser(path +Property.getInstance().getTargetResolution()+ project+"BugRepository.xml");
		
		switch(project){
		case "swt":
			gitPosition = "D:\\Users\\rose\\git\\eclipse.platform.swt\\.git";
			dataPath ="D:\\Users\\rose\\git\\eclipse.platform.swt";
			break;
		case "ui":
			gitPosition = "D:\\Users\\rose\\git\\eclipse.platform.ui\\.git";
			dataPath ="D:\\Users\\rose\\git\\eclipse.platform.ui";
			break;
		case "aspectj":
			gitPosition = "D:\\Users\\rose\\git\\org.aspectj\\.git";
			dataPath ="D:\\Users\\rose\\git\\org.aspectj";
			break;
		case "jdt":
			gitPosition = "D:\\Users\\rose\\git\\eclipse.jdt.ui\\.git";
			dataPath ="D:\\Users\\rose\\git\\eclipse.jdt.ui";
			break;
		case "birt":
			gitPosition = "D:\\Users\\rose\\git\\birt\\.git";
			dataPath ="D:\\Users\\rose\\git\\birt";
			break;
		case "zxing":
			gitPosition = "D:\\Users\\rose\\git\\zxing\\.git";
			dataPath ="D:\\Users\\rose\\git\\zxing";
			break;
		}
		git = Git.open(new File(gitPosition));	
		System.out.println(git.checkout().setName("master").setForce(true).call());
		repo = git.getRepository();
		for(int i = 0; i<bugRepository.size(); i++){
			ExtBugReport bugReport = bugRepository.get(i);
			int bugID = bugReport.getBugID();
			ArrayList<Commit> commitList = bugReport.getCommitList();
			System.out.println(bugID+" "+commitList.size());
			for(int j = 0; j<commitList.size(); j++){
				String commitID = commitList.get(j).getCommitID();
				LogCommand log = git.log();
				ObjectId lastCommitId = repo.resolve(commitID);
				System.out.println(commitID+" "+lastCommitId);
				log.add(lastCommitId);

				RevWalk rw = new RevWalk(repo);
				RevCommit parent = rw.parseCommit(lastCommitId);

				rw.sort(RevSort.COMMIT_TIME_DESC);
				rw.markStart(parent);

				Iterable<RevCommit> logMsgs = log.call();

				ArrayList<FileInfo> fileList = commitList.get(j).getFileList();
				for(RevCommit commit : logMsgs){
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					DiffFormatter df = new DiffFormatter(out); // DisabledOutputStream.INSTANCE
					df.setRepository(repo);
					df.setDiffComparator(RawTextComparator.DEFAULT);
					df.setDetectRenames(true);
					
					List<DiffEntry> diffs = df.scan(commit.getTree(), commit.getParent(0).getTree());
					int index = 0;
					for (DiffEntry diff : diffs) 
					{
						String filename = diff.getOldPath();
						
						if(!filename.substring(filename.length()-6,filename.length()).contains("java"))
							continue;				
						if(!diff.getChangeType().toString().contains("MODI"))
							continue;						
						System.out.println(bugID+" "+commitID+" "+filename);
						
						FileInfo fileInfo = new FileInfo();						
						fileInfo.setFileName(filename);
						if(index < fileList.size()){
							fileInfo.setMethodList((ArrayList<Method>) fileList.get(index).getMethodList().clone());
							fileList.set(index, fileInfo);
						}
						if(index >= fileList.size()){							
							int len1 = filename.split("/").length;							
							String realSubFileName = filename.split("/")[len1-1];
							String prevSubFileName="";
							for(int k = 0; k<index; k++){
								int len0 = fileList.get(k).getFileName().split("[.]").length;
								prevSubFileName = fileList.get(k).getFileName().split("[.]")[len0-2]+"."+fileList.get(k).getFileName().split("[.]")[len0-1];
							}

							if(realSubFileName.equals(prevSubFileName))
								fileInfo.setMethodList((ArrayList<Method>) fileList.get(index).getMethodList().clone());
							fileList.add(fileInfo);
						}												
						index++;
					}
					break;
				}
				commitList.get(j).setFileList((ArrayList<FileInfo>) fileList.clone());				
			}
			bugReport.setCommitList((ArrayList<Commit>) commitList.clone());
			bugRepository.set(i, bugReport);
			
		}
		
		System.out.println("AST based Fixed File SRC");
		for(int i = 0; i<bugRepository.size(); i++){
			ExtBugReport bugReport = bugRepository.get(i);
			int bugID = bugReport.getBugID();
			ArrayList<Commit> commitList = bugReport.getCommitList();
			for(int j = 0; j<commitList.size(); j++){
				String commitID = commitList.get(j).getCommitID();	
				System.out.println(bugID + " "+commitID+" START!");			
				System.out.println(git.checkout().setName("master").setForce(true).call());
				git.checkout().setName(commitID).call();													
				LogCommand log = git.log();
				Repository repo = git.getRepository();		
				RevWalk walk = new RevWalk(repo);
				ObjectId lastCommitId = repo.resolve(commitID);
				log.add(lastCommitId);
				RevCommit parent = walk.parseCommit(lastCommitId);
				walk.sort(RevSort.COMMIT_TIME_DESC);
				walk.markStart(parent);
				Iterable<RevCommit> logMsgs = log.call();
				String prevCommitID = "";
				int index = 0;
				for(RevCommit commit : logMsgs){
					if(index == 0){
						index++;
						continue;
					}
					prevCommitID = commit.getName();
					break;
				}
				git.checkout().setName(prevCommitID).call();
				
				ArrayList<FileInfo> fileList = commitList.get(j).getFileList();
				index = 0;
				for(int k = 0; k<fileList.size(); k++){					
					String fileName = fileList.get(k).getFileName();
					File file = new File(dataPath+"\\"+fileName);
					FileParser parser = new FileParser(file);
					String fileName2 = parser.getPackageName();
					if (fileName2.trim().equals("")) {
						fileName2 = file.getName();
					} else {
						fileName2 = (new StringBuilder(String.valueOf(fileName2)))
								.append(".").append(file.getName()).toString();
					}
					fileName = fileName.substring(0, fileName.lastIndexOf("."));
					
					FileInfo fileInfo = new FileInfo();						
					System.out.println(bugID+" "+commitID+" "+fileName+" "+fileName2);
					fileInfo.setFileName(fileName2);
					fileInfo.setMethodList((ArrayList<Method>) fileList.get(index).getMethodList().clone());
					fileList.set(index, fileInfo);
					index++;
				}
				commitList.get(j).setFileList((ArrayList<FileInfo>) fileList.clone());
			}
			bugReport.setCommitList((ArrayList<Commit>) commitList.clone());
			bugRepository.set(i, bugReport);
		}
		
		XMLWriter.writeBugRepository(bugRepository, path+"NewCommit"+project+"BugRepository.xml", project);		
	}

}
 