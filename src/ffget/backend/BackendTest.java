package ffget.backend;

public class BackendTest {

	public static void main(String[] args) 
	{
		
		
		TryStory("https://www.fanfiction.net/s/9766566/5/Behind-the-Veil-of-Power");
		TryStory("https://www.fanfiction.net/s/5664828/27/Harry-Potter-and-Future-s-Past");
		TryStory("https://www.fanfiction.net/s/10567416/1/An-Unkown-Connection");
		
		
		
	}
	
	static void TryStory(String URL)
	{
		try
		{
			StoryPage page = new StoryPage(URL);
			System.out.println("Name = " + page.GetStoryName());
			System.out.println("Description = " + page.GetStoryDescription());
			System.out.println("ID = " + page.GetStoryID());
			System.out.println("Author = " +  page.GetAuthor());
			System.out.println("Chapter = " + page.GetChapterName());
			System.out.println("Chapter# = " + page.GetChapterIndex());
			System.out.println("Chapters = " + page.GetChapterCount());
			
			System.out.println("\n" + page.GetStoryUnformated());
			
			
		}
		catch(Exception E)
		{
			System.out.println("Error in URL: " + URL);
			E.printStackTrace();
		}
	
	}

}
