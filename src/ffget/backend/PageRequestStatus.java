package ffget.backend;

public enum PageRequestStatus {
	Queued,
	Downloading,
	Ready,
	ERROR_GENERAL, 
	ERROR_NULL_STORY,
	ERROR_INVALID_ID
	
}
