package movie;

public interface DVD {	 
	//Returns the region code of the DVD
	public int getRegionCode();
	//Returns an array with the names of the audio tracks on the DVD
	public String[] getAudioTracks();
	//Returns an array with the languages of the subtitles on the DVD
	public String[] getSubtitles();
}
