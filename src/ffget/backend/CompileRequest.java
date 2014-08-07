package ffget.backend;

import java.nio.file.Path;

public class CompileRequest {
	
	public CompileRequestStatus getStatus()
	{
		
		return Status;
	}
	
	CompileRequestStatus Status;
	StoryPage[] chapters;
	Path path;
	public String[] subjects;
	
	CompileRequest(StoryPage[] c, Path p)
	{
		Status = CompileRequestStatus.QUEUE;
		chapters = c;
		path = p;
	}
	CompileRequest(StoryPage[] c, Path p, String[] s)
	{
		Status = CompileRequestStatus.QUEUE;
		chapters = c;
		path = p;
		subjects = s;
	}
}
