/**
 * 
 */
package ffget.backend;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.List;

import org.jsoup.*;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author joha4270
 *
 */
public class StoryPage {
	
	private Boolean OneChapter =  false;
	public String[] getChapters()
	{
		Element e = Story.getElementById("chap_select");
		String[] ret = new String[e.childNodeSize()];
		
		for(int i = 0; i != ret.length; i++)
		{
			ret[i] = e.child(i).text();
		}
		
		return ret;
		
	}
	
	@SuppressWarnings("unused")
	public String getImageURL()
	{
		if(Story != null)
		{
			if(true)
			{
				return getFallBackImageURL();
			}
			else
			{
				Elements imgs = Story.getElementById("profile_top").select("img");
				if(imgs != null)
				{
					if(imgs.first() != null)
					{
						return imgs.first().absUrl("src");
					}
					else
					{
						return getFallBackImageURL();
					}
				}
				else
				{
					
					return getFallBackImageURL();
				}
				//TODO Change content between last three /  (/x/y/) to bigger values, contain pixel size
			}
		}
		else
		{
			return null;
		}
	}
	
	private String getFallBackImageURL() {
		if(Story != null)
		{
			
			return "https://ffcdn2012-fictionpressllc.netdna-ssl.com/imageu/" +  Story.getElementById("profile_top").select("a").first().absUrl("href").split("/")[4] + "/180/269/";
		}
		else
		{
			return null;
		}
	}

	private String StoryName;
	public String GetStoryName() 
	{
		try{
			
			if(Story == null)
			{
				System.out.println("NullStorry");
				return "NULL";
			}
						
			if(StoryName == null)
			{
				StoryName = Story
					.getElementById("profile_top")
					.select("b")
					.html();
				
			}
			
		
			return StoryName;
			
		}
		catch(Exception E)
		{
			if(checkValidID())
			{
				StoryName = "Story Not Existing";
			}
			else
			{
				dumpStorySingleFile();
				StoryName = "Exception";
				E.printStackTrace();
				return "Exception";
			}
		}
		
		return StoryName;
	}

	void dumpStorySingleFile() {
		
		System.out.println("Dumping Story with ID=" + extractIDFromURL());
		FileManager.Dump("StoryExceptionDump_" + System.currentTimeMillis() + ".html",Story.html().getBytes());
	}

	private Boolean checkValidID() {
		
		
		Boolean valid = !Story.getElementsByClass("gui_warning").isEmpty();
		if(valid)
		{
			System.out.println("non existing story with id=" + extractIDFromURL());
		}
		
		
		
		return valid;
		
	}
	
	private String extractIDFromURL() {
		return Story.location().split("/")[4];
	}
	private static String extractIDFromURL(String URL)
	{
		return URL.split("/")[4];
	}
	private String ChapterName;
	
	
	public String GetChapterName()
	{
		if(ChapterName == null)
		{
			int titleLenght = GetStoryName().length();
			String[] Split = Story.title().substring(titleLenght).split(",");
			
			if(Split[0].length() == 0)
			{
				OneChapter = true;
				ChapterName = "Chapter1";
			}
			else
			{
				ChapterName = Split[0];
			}
		}
		
		return ChapterName;
	}
	
	private String StoryID;
	public String GetStoryID()
	{
		try{
			if(StoryID == null)
			{
				Element elmn = Story.getElementById("profile_top");
				
				String TempName = elmn.children().last().html().toString();
						
						
				int idIndex = TempName.lastIndexOf("id:") + 4;
				StoryID = TempName.substring(idIndex);
			}
		
			return StoryID;
		}
		catch(Exception e)
		{
			
			e.printStackTrace();
			dumpStorySingleFile();
			return null;
		}
	}
	
	private String StoryDescription;
	public String GetStoryDescription()
	{ 
		if(StoryDescription == null)
		{
			StoryDescription = Story.getElementById("profile_top").child(7).html();
		}
		
		return StoryDescription;
	}
	
	private String Author;
	public String GetAuthor()
	{
		if(Author == null)
		{
			Author = Story.getElementById("profile_top").child(4).html();
		}
		return Author;
	}
	
	private int ChapterCount = -1;
	public int GetChapterCount()
	{
		if(ChapterCount == -1)
		{
			//A bi product of calling this function is setting "Boolean OneChapter" to true if there is only on
			GetChapterName();
			if(OneChapter)
			{
				ChapterCount = 1;
			}
			else
			{
				Element e = Story.getElementById("chap_select");
				
				ChapterCount = e.childNodeSize();
			}
		}
		return ChapterCount;
	}
	
	private int ChapterIndex = -1;
	public int GetChapterIndex()
	{
		if(ChapterIndex == -1)
		{
			ChapterIndex = Integer.parseInt(Story.location().split("/")[5]);
			
		}
		
		return ChapterIndex;
	}
	
	public String GetStoryHtml()
	{
		return Story.getElementById("storytext").html();
	}
	public String GetStoryUnformated()
	{
		StringBuilder sb = new StringBuilder();
		List<Element> Lines = Story.getElementById("storytext").children();
		
		for(Element line : Lines)
		{
			sb.append(line.text());
			sb.append("\n");
		}
		
		return sb.toString();
		
	}
		
	Document Story;
	public StoryPage(String URL) throws HttpStatusException, MalformedURLException
	{
		String ID = extractIDFromURL(URL);
		if(ID.trim().isEmpty() || !ID.matches("\\d+"))
		{
			System.out.println("Story Malformatted");
			throw new MalformedURLException("no storyID found in url");
		}
		else
		{
			try
			{
				//System.out.print("Starting Jsoup.parse()... ");
				//Story = Jsoup.parse(URL);
				
				//System.out.print("Starting Jsoup.connect()... ");
				Connection con = Jsoup.connect(URL);
				con.timeout(10000).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.125 Safari/537.3610567416");
				//System.out.print("execute()... ");
				Response re = con.execute();
				//System.out.print("parse()... ");
				Story = re.parse();
				
				
				System.out.println("Download of \"" + URL + "\" finished succesfully\n performing Check");
				if(testStory())
				{
					dumpStorySingleFile();
					Story = null;
					
				}
				
				if(DownloadManager.dumpData)
				{
					System.out.print("Dumping downloaded HTML...");
					FileManager.Dump("Dumpfile.txt",Story.html().getBytes(Charset.defaultCharset()));
					System.out.println("Finished successfully");
				}
			}
			catch(HttpStatusException HTTPE)
			{
				throw HTTPE;
			}
			catch(Exception E)
			{
				E.printStackTrace();
			}
		}
	}

	private boolean testStory() {
		if(checkValidID())
			return true;
		
		
		return false;
		
	}

	public String getFandom() {

		if(Story != null)
		{
			
			String[] part = Story.title().split("(, a )|( fanfic )");
			
			return part[1];
		}
		else
		{
			return null;
		}
		
	}
	
}
