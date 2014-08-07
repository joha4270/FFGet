
package ffget.backend;
 

public class PageRequest {

	PageRequestStatus status;
	public PageRequestStatus GetStatus()
	{
		return status;
	}
	
	StoryPage page;
	public StoryPage GetStoryPage()
	{
		return page;
	}
	
	private String url;
	public String GetURL()
	{
		return url;
		
	}
	
	public String[] subjects;
	PageRequest(String url) {
		// TODO Auto-generated constructor stub
		
		status = PageRequestStatus.Queued;
		if(url.split(":").length > 2)
		{
			System.out.println("url " + url + " Contains embedded metadata");
			subjects = url.substring(url.indexOf(":", url.indexOf(":") + 1)+1).split(":");
			System.out.println("Metadata lenght=" + subjects.length);
			for(String s : subjects)
			{
				System.out.println(s);
			}
			this.url = url.split(":")[0] + ":" + url.split(":")[1];
		}
		else
		{
			this.url = url;
		}
	}
	void SetPage(StoryPage storyPage) {
		if(page == null)
		{
			System.out.println("StoryPage downloaded");
			page = storyPage;
		}
	}

}
