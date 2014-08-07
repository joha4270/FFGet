package ffget.backend;


import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.concurrent.*;

import org.jsoup.HttpStatusException;


public class DownloadManager extends Thread{
	
	
	private static final boolean CacheEnabled = false;
	private static HashMap<String, PageRequest> Cache;
	private static LinkedBlockingQueue<PageRequest> DownloadQue;
	private static LinkedBlockingQueue<CompileRequest> CompileQue;
	private static DownloadManager[] threads;
	public static boolean dumpData = false;
	private WorkerThreadStatus Status;
	public WorkerThreadStatus GetStatus()
	{
		return Status;
	}
	
	private String workStoryID;
	public String getWorkingID()
	{
		return workStoryID;
	}
	static int downloadThreads = 8;
	static
	{
		Cache = new HashMap<String, PageRequest>();
		DownloadQue = new LinkedBlockingQueue<PageRequest>();
		CompileQue = new LinkedBlockingQueue<CompileRequest>();
		System.out.println("Stating Autothread");
		threads = new DownloadManager[downloadThreads];
		for(int i = 0; i != downloadThreads; i++)
		{
			System.out.println("Staring thread");
			threads[i] = new DownloadManager();
			threads[i].setName("Download thread "+ (i + 1));
			threads[i].start();
		}
		
		
	}
	
	static public int Slow = 0;
	public void run()
	{
		
		PageRequest workset;
		CompileRequest book;
		while(true)
		{
			try{
				
				
				if((book = CompileQue.poll()) != null)
				{
					System.out.println("Starting Compiling in " + Thread.currentThread().getName() + 
						"   Queue lenght is (" + DownloadQue.size() + "," + CompileQue.size() + ")");
					workStoryID = book.chapters[0].GetStoryID();
					Status = WorkerThreadStatus.COMPILING;
					try
					{
						EPubCompiler.Compile(book);
					}
					catch(Exception E)
					{
						E.printStackTrace();
					}
					
				}
				else if((workset = DownloadQue.poll()) != null)
				{
					
					
					workStoryID = workset.GetURL().split("/")[4];
					Status = WorkerThreadStatus.DOWNLOADING;
					System.out.println("Starting Download in " + Thread.currentThread().getName() + 
							"   Queue lenght is (" + DownloadQue.size() + "," + CompileQue.size() + ")");
					workset.status = PageRequestStatus.Downloading;
					try
					{
						System.out.println("Fetching " + workset.GetURL());
						workset.SetPage(new StoryPage(workset.GetURL()));
						if(workset.page.Story == null)
						{
							workset.status = PageRequestStatus.ERROR_NULL_STORY;
						}
						else
						{
							workset.status = PageRequestStatus.Ready;
						}
						if(workset.page.getImageURL() == null)
						{
							System.err.println("img url = null");
							workset.page.dumpStorySingleFile();
						}
						
					}
					catch (HttpStatusException E)
					{
						System.err.println(E.toString());
						workset.status = PageRequestStatus.ERROR_INVALID_ID;
					}
					catch (MalformedURLException E)
					{
						System.err.println(E.toString());
						workset.status = PageRequestStatus.ERROR_INVALID_ID;
					}
					catch(Exception E)
					{
						workset.status = PageRequestStatus.ERROR_GENERAL;
						E.printStackTrace();
					}
					
					if(Slow > 0)
					{
						System.out.println("Slowmode pause!");
						Thread.sleep(1);
						Slow--;
						System.out.println("resumed");
					}
				}
				else
				{
					workStoryID = null;
					Status = WorkerThreadStatus.IDLE;
					try
					{
						Thread.sleep(250);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			catch(Exception e)
			{
				System.err.println("Error in fetching thread");
				e.printStackTrace();
			}
		}
	}
	
	public static PageRequest AddUrlToQue(String url)
	{
		//System.out.println("New Url Added to queue " + url);
		
		PageRequest result;
		if(CacheEnabled)
		{
			if((result = Cache.get(url)) != null)
			{
				System.out.println("Cached hit for " + url);
				return result;
			}
		}
		
		result = new PageRequest(url);
		
		if(CacheEnabled)
		{
		Cache.put(url, result);
		}
		DownloadQue.add(result);
		
		return result;
		
		
	}
	public static PageRequest AddUrlToFrontQueue(String url)
	{
		PageRequest result;
		if(CacheEnabled)
		{
			if((result = Cache.get(url)) != null)
			{
				System.out.println("Cached hit for " + url);
				return result;
			}
		}
		
		result = new PageRequest(url);
		if(CacheEnabled)
		{
			Cache.put(url, result);
		}
		
		//god have mercy, im sorry
		//nasty hack to add to front of que
		//[intended] use is to fetch a single page from a story quickly to get stats
		
		/*PageRequest[] queData = new PageRequest[DownloadQue.size()]; 
		DownloadQue.toArray(queData);
		DownloadQue.clear();
		DownloadQue.add(result);
		for(PageRequest r : queData)
		{
			DownloadQue.add(r);
		}*/
		
		int queLen = DownloadQue.size();
		DownloadQue.add(result);
		for(int i = 0; i != queLen; i++)
		{
			DownloadQue.add(DownloadQue.poll());
		}
		
		return result;
		
	}

	public static CompileRequest QueueCompiling(StoryPage[] chapters, Path path, String[] subjects) {
		
		CompileRequest req = new CompileRequest(chapters,path,subjects);
		CompileQue.add(req);
		
		return req;
	}

	public static boolean WorksAt(String getStoryID) {
		for(DownloadManager m: threads)
		{
			if(m.workStoryID.equals(getStoryID))
				return true;
		}
		
		return false;
	}
	
	 
	
	
}
