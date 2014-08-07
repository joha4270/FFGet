package ffget.backend;

import java.nio.file.*;
public class FileManager {
	public static void Dump(String fileName, byte[] Data)
	{
		
		
		try
		{
			if(!Files.exists(FileSystems.getDefault().getPath(System.getProperty("user.dir"), "Dump")))
			{
				System.out.print("Creating Dump Folder... ");
				Files.createDirectory(FileSystems.getDefault().getPath(System.getProperty("user.dir"), "Dump"));
				System.out.println("Done");
			}
			
			
			Path path = FileSystems.getDefault().getPath(System.getProperty("user.dir"), "Dump", fileName);
			System.out.println("Dumping data to file " + path.toString());
			Files.write(path, Data, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
		}
		catch(Exception e)
		{
			System.out.println("Error in dumping error data, Your screwed son");
			e.printStackTrace();
		}
		
		
	}
	
	public static CompileRequest QueueCompiling(StoryPage[] chapters, Path path, String[] subjects)
	{
		return DownloadManager.QueueCompiling(chapters,path, subjects);
	}
}
