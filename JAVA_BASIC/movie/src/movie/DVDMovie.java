package movie;

public class DVDMovie extends Movie implements DVD {
	private int regionCode;
	private String[] audioTracks;
	private String[] subtitles;

	public DVDMovie(String initialTitle, String[] initialActors, String initialDirector,
			int initialRegionCode, String[] initialAudioTracks, String[] initialSubtitles){
		super(initialTitle, initialActors, initialDirector);
		this.regionCode = initialRegionCode;
		this.audioTracks = initialAudioTracks;
		this.subtitles = initialSubtitles;
	}
	public int getRegionCode(){ return regionCode; }
	public String[] getAudioTracks(){ return audioTracks; }
	public String[] getSubtitles(){ return subtitles; }
	public String toString(){
		return super.getTitle() + ", " + getRegionCode();
	}
}
