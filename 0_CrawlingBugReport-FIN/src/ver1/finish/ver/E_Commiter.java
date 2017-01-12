package ver1.finish.ver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;

import common.Property;


public class E_Commiter {
	String gitPosition = "";
	private Git git;
	private Repository repo;
	private B_DB db;
	private FileReader textFileReader;
	ArrayList<Integer> idArray = new ArrayList<Integer>();
	
	E_Commiter(int[] ids, ArrayList<Integer> dupID, B_DB db) throws Exception {
		System.out.println("START CRAWLING COMMIT INFORMATION");
		gitPosition = Property.getInstance().getGitPosition();
		switch(A_Main.project){
		case "swt":
			gitPosition = gitPosition+"\\eclipse.platform.swt2\\.git";			
			break;
		case "ui":
			gitPosition = gitPosition+"\\eclipse.platform.ui\\.git";			
			break;
		case "aspectj":
			gitPosition = gitPosition+"\\org.aspectj2\\.git";			
			break;
		case "jdt":
			gitPosition = gitPosition+"\\eclipse.jdt.ui\\.git";			
			break;
		case "birt":
			gitPosition = gitPosition+"\\birt\\.git";			
			break;
		}
		git = Git.open(new File(gitPosition));
		System.out.println(git.checkout().setName("master").setForce(true).call());
		repo = git.getRepository();
		this.db=db;
		
		for(int i = 0; i<ids.length; i++)
			idArray.add(ids[i]);
		for(int i = 0; i<dupID.size(); i++)
			idArray.add(dupID.get(i));
		int[] idList = new int[idArray.size()];
		for(int i = 0; i<idArray.size(); i++)
			idList[i] = idArray.get(i);
		System.out.println("TOTAL SIZE : "+idList.length +" "+ids.length+" "+dupID.size());
		getFileName(git,repo,idList);
	}

	public void getFileName(Git git, Repository repo,int[] ids) {
		try {
			LogCommand log = git.log();
			ObjectId lastCommitId = repo.resolve(Constants.HEAD);
			log.add(lastCommitId);

			RevWalk rw = new RevWalk(repo);
			RevCommit parent = rw.parseCommit(lastCommitId);

			rw.sort(RevSort.COMMIT_TIME_DESC);
			rw.markStart(parent);

			Iterable<RevCommit> logMsgs = log.call();

			int m=0;			
			for (RevCommit commit : logMsgs) {
			
				int counter;

				String msg = commit.getFullMessage().toString();
				for(counter=0;counter<ids.length;counter++)
				{					
					if(msg.contains(" "+Integer.toString(ids[counter])+" "))
						break;
					
				}
				if(counter==ids.length)continue;
				m++;
				System.out.println(m+"/"+ids.length+" FINISHED");
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				int time = commit.getCommitTime();
				Long l = time*1000L;
				String commitDate = sdf.format(l);				
				String commitID = commit.getName();
				String commiter = commit.getAuthorIdent().getName();
				String summary = commit.getShortMessage().replace("'", ".");
				String message = commit.getFullMessage().replace("'", ",");
				
				git.checkout().setName(commitID).call();
				
				
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				DiffFormatter df = new DiffFormatter(out); // DisabledOutputStream.INSTANCE
				df.setRepository(repo);
				df.setDiffComparator(RawTextComparator.DEFAULT);
				df.setDetectRenames(true);

				List<DiffEntry> diffs = df.scan(commit.getTree(), commit.getParent(0).getTree()); 
				for (DiffEntry diff : diffs) 
				{
					String filename = diff.getOldPath();
					//System.out.println(filename);
					if(!filename.substring(filename.length()-6,filename.length()).contains("java"))
						continue;										
					String str = gitPosition.substring(0, gitPosition.length()-4)+filename;
					
					db.insertFixedFile(ids[counter],filename,commitDate, commitID, commiter, 
							summary, message, diff.getChangeType().name());					
				}
				out.close();
				parent = commit;

				rw.close();
				df.close();
			}
		} catch (Exception e) {
			System.out.println("no head exception : " + e);
		}
	}
}
