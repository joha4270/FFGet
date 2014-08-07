package ffget.gui;

class GUIUtil {
	static String genFileName(String name, String extension) {
		return name.replace(' ', '_').replaceAll("\\W|", "") + "." + extension;
	}
	
	static String PageURL(String idOrURL, String pageFormatString)
	{
		
		//System.out.println("Checking \"" +idOrURL +"\"");
		if(idOrURL.matches("http.*|(.*(net|com|org).*)"))  //TODO SETTING FILE
		{
			//if regex matches we asume it is a valid url
			//System.out.printf("String %s matches url regex check\n", idOrURL);
			return idOrURL;
		}
		else
		{
			//System.out.printf("String %s assumed to be id\n", idOrURL);
			return String.format(pageFormatString, idOrURL,1);
		}
	}
	
	static String PageURL(String idOrURL, String pageFormatString, int page)
	{
		if(idOrURL.matches("http.*|(.*(net|com|org).*)"))  //TODO SETTING FILE
		{
			//if regex matches we asume it is a valid url
			//System.out.printf("String %s matches url regex check\n", idOrURL);
			String[] parts = idOrURL.split("/");
			
			//      https:           //               www.ff.nt       /s/              story          chapter
			return parts[0] + "/" + parts[1] + "/" + parts[2] + "/" + parts[3] + "/" + parts[4] + "/" + page;
		}
		else
		{
			//System.out.printf("String %s assumed to be id\n", idOrURL);
			return String.format(pageFormatString, idOrURL,page);
		}
	}
}
